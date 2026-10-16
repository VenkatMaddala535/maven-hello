package com.example.jenkins;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class WebServer {
    private static final Map<String, String> CONTENT_TYPES = Map.of(
            "/", "text/html; charset=utf-8",
            "/style.css", "text/css; charset=utf-8",
            "/app.js", "text/javascript; charset=utf-8");

    private WebServer() { }

    public static HttpServer create(String host, int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
        server.createContext("/", WebServer::handle);
        return server;
    }

    private static void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "GET");
                respond(exchange, 405, "text/plain; charset=utf-8", "Method not allowed");
                return;
            }
            String path = exchange.getRequestURI().getPath();
            if ("/health".equals(path)) {
                respond(exchange, 200, "application/json", "{\"status\":\"ok\",\"stack\":\"maven\"}");
            } else if ("/api/greet".equals(path)) {
                try {
                    respond(exchange, 200, "text/plain; charset=utf-8",
                            Greeting.message(queryName(exchange.getRequestURI().getRawQuery())));
                } catch (IllegalArgumentException error) {
                    respond(exchange, 400, "text/plain; charset=utf-8", "Please use a valid name with 80 characters or fewer.");
                }
            } else if (CONTENT_TYPES.containsKey(path)) {
                String resource = "/public" + ("/".equals(path) ? "/index.html" : path);
                try (var stream = WebServer.class.getResourceAsStream(resource)) {
                    if (stream == null) {
                        throw new IOException("Missing packaged website resource: " + resource);
                    }
                    respond(exchange, 200, CONTENT_TYPES.get(path),
                            new String(stream.readAllBytes(), StandardCharsets.UTF_8));
                }
            } else {
                respond(exchange, 404, "text/plain; charset=utf-8", "Not found");
            }
        }
    }

    private static String queryName(String query) {
        if (query != null) {
            for (String pair : query.split("&")) {
                String[] parts = pair.split("=", 2);
                if ("name".equals(URLDecoder.decode(parts[0], StandardCharsets.UTF_8))) {
                    return parts.length == 2 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
                }
            }
        }
        return "";
    }

    private static void respond(HttpExchange exchange, int status, String type, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", type);
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }
}
