package zoo;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {
    public static void main(String[] args) throws IOException {
        int port = 8085; // Puerto cambiado a 8085
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("🚀 Servidor corriendo en http://localhost:" + port);

        // Sirve archivos desde src/main/resources/public/
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null); // Usa el executor por defecto
        server.start();
    }

    static class StaticFileHandler implements HttpHandler {
        private static final String BASE_DIR = "src/main/java/zoo/public";

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html"; // Redirige a index.html por defecto
            }

            Path filePath = Paths.get(BASE_DIR + path);
            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                send404(exchange);
                return;
            }

            String contentType = getContentType(path);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, Files.size(filePath));

            OutputStream os = exchange.getResponseBody();
            Files.copy(filePath, os);
            os.close();
        }

        private void send404(HttpExchange exchange) throws IOException {
            String response = "404 Not Found";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html";
            if (path.endsWith(".css")) return "text/css";
            if (path.endsWith(".js")) return "application/javascript";
            return "application/octet-stream";
        }
    }
}
