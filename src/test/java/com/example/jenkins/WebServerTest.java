package com.example.jenkins;

import static org.junit.jupiter.api.Assertions.*;
import com.sun.net.httpserver.HttpServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebServerTest {
    private HttpServer server;
    private HttpClient client;
    private String baseUrl;

    @BeforeEach void start() throws Exception {
        server = WebServer.create("127.0.0.1", 0);
        server.start();
        client = HttpClient.newHttpClient();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterEach void stop() {
        client.close();
        server.stop(0);
    }

    private HttpResponse<String> get(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(baseUrl + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test void servesWebsiteAndAssets() throws Exception {
        var page = get("/");
        assertEquals(200, page.statusCode());
        assertTrue(page.body().contains("Hello from Maven"));
        assertTrue(page.headers().firstValue("Content-Type").orElseThrow().startsWith("text/html"));
        assertEquals(200, get("/style.css").statusCode());
        assertEquals(200, get("/app.js").statusCode());
    }

    @Test void returnsHealth() throws Exception {
        var result = get("/health");
        assertEquals(200, result.statusCode());
        assertEquals("{\"status\":\"ok\",\"stack\":\"maven\"}", result.body());
    }

    @Test void greetsThroughHttp() throws Exception {
        assertEquals("Hello, World!", get("/api/greet").body());
        assertEquals("Hello, World!", get("/api/greet?name").body());
        assertEquals("Hello, World!", get("/api/greet?other=value").body());
        assertEquals("Hello, Ada Lovelace!", get("/api/greet?other=1&name=Ada+Lovelace").body());
        assertEquals(400, get("/api/greet?name=" + "a".repeat(81)).statusCode());
    }

    @Test void rejectsUnknownRoutesAndMethods() throws Exception {
        assertEquals(404, get("/missing").statusCode());
        assertEquals(404, get("/health/extra").statusCode());
        var request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/greet"))
                .POST(HttpRequest.BodyPublishers.noBody()).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
        assertEquals("GET", response.headers().firstValue("Allow").orElseThrow());
    }
}
