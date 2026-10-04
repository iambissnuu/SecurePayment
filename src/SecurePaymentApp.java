import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SecurePaymentApp {

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/payment", SecurePaymentApp::handlePayment);

        server.setExecutor(null);
        server.start();

        System.out.println("SecurePayment server is running.");
        System.out.println("http://localhost:8080");
    }

    private static void handlePayment(HttpExchange exchange) throws IOException {
        
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "http://127.0.0.1:5500"
        );

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed.");
            return;
        }

        String requestBody = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        Map<String, String> formData = parseFormData(requestBody);

        String customerName = formData.getOrDefault("customerName", "").trim();
        String amountText = formData.getOrDefault("amount", "").trim();

        // Server-side validation is required because browser checks can be bypassed.
        if (customerName.isEmpty() || customerName.length() > 50) {
            sendResponse(exchange, 400, "Invalid customer name.");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountText);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                sendResponse(exchange, 400, "Invalid payment amount.");
                return;
            }

            DatabaseManager.savePayment(customerName, amount);

            sendResponse(exchange, 200, "Payment saved successfully.");

        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid payment amount.");

        } catch (SQLException e) {
            // Don't expose database details to the browser.
            System.err.println("Database operation failed.");
            sendResponse(exchange, 500, "Unable to process payment.");
        }
    }

    private static Map<String, String> parseFormData(String body) {

        Map<String, String> values = new HashMap<>();

        for (String pair : body.split("&")) {

            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(
                    parts[0],
                    StandardCharsets.UTF_8
            );

            String value = parts.length > 1
                    ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                    : "";

            values.put(key, value);
        }

        return values;
    }

    private static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response
    ) throws IOException {

        byte[] responseBytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(responseBytes);
        }
    }
}