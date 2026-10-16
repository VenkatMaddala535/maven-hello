package com.example.jenkins;

public final class Main {
    private Main() { }

    public static void main(String[] args) throws Exception {
        String host = System.getenv().getOrDefault("HOST", "0.0.0.0");
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8081"));

        var server = WebServer.create(host, port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(0)));

        server.start();

        System.out.printf(
            "Maven Hello is running at http://%s:%d%n",
            host,
            server.getAddress().getPort()
        );
    }
}
