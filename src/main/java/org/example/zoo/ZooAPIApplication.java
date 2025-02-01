package org.example.zoo;

import com.sun.net.httpserver.*;
import jakarta.xml.ws.Endpoint;
import org.example.zoo.service.XQueryService;
import org.example.zoo.service.ZooService;
import org.example.zoo.service.AnimalService;
import org.example.zoo.service.ConservationService;
import java.io.IOException;
import java.net.InetSocketAddress;

public class ZooAPIApplication {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);

        // Publicar ZooService
        Endpoint zooEndpoint = Endpoint.create(new ZooService());
        HttpContext zooContext = server.createContext("/ws/zoo");
        zooEndpoint.publish(zooContext);

        // Publicar AnimalService
        Endpoint animalEndpoint = Endpoint.create(new AnimalService());
        HttpContext animalContext = server.createContext("/ws/animal");
        animalEndpoint.publish(animalContext);

        // Publicar ConservationService
        Endpoint conservationEndpoint = Endpoint.create(new ConservationService());
        HttpContext conservationContext = server.createContext("/ws/conservation");
        conservationEndpoint.publish(conservationContext);

        // Registrar el servicio de XQuery
        Endpoint xqueryEndpoint = Endpoint.create(new XQueryService());
        HttpContext xqueryContext = server.createContext("/ws/xquery");
        xqueryEndpoint.publish(xqueryContext);

        // ✅ Configurar CORS manualmente para cada servicio
        configureCORS(zooContext);
        configureCORS(animalContext);
        configureCORS(conservationContext);
        configureCORS(xqueryContext);


        server.start();
        System.out.println("📡 API SOAP corriendo en:");
        System.out.println("🔗 Zoológicos: http://localhost:8082/ws/zoo?wsdl");
        System.out.println("🔗 Animales: http://localhost:8082/ws/animal?wsdl");
        System.out.println("🔗 Conservación: http://localhost:8082/ws/conservation?wsdl");
        System.out.println("🔗 XQuery API: http://localhost:8082/ws/xquery?wsdl");


    }

    // 🔥 Método para configurar CORS en un contexto específico
    private static void configureCORS(HttpContext context) {
        context.getFilters().add(new Filter() {
            @Override
            public String description() {
                return "Filtro CORS";
            }

            @Override
            public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, SOAPAction");

                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                chain.doFilter(exchange);
            }
        });
    }
}
