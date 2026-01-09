import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.*;

public class ChatbotEngine {

    private static final String OLLAMA_URL =
            "http://localhost:11434/api/chat";

    private final HttpClient client = HttpClient.newHttpClient();
    private final JsonArray messages = new JsonArray();

    /* =========================
       TEXT STREAMING
    ========================= */
    public void askStream(String userInput, StreamListener listener) {
        try {
            // add user message
            JsonObject userMsg = new JsonObject();
            userMsg.addProperty("role", "user");
            userMsg.addProperty("content", userInput);
            messages.add(userMsg);

            JsonObject body = new JsonObject();
            body.addProperty("model", "mistral");
            body.add("messages", messages);
            body.addProperty("stream", true);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

            HttpResponse<java.io.InputStream> response =
                client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            BufferedReader reader =
                new BufferedReader(new InputStreamReader(response.body()));

            StringBuilder assistantReply = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                JsonObject json =
                    JsonParser.parseString(line).getAsJsonObject();

                if (json.has("message")) {
                    JsonObject msg = json.getAsJsonObject("message");
                    if (msg.has("content")) {
                        String token = msg.get("content").getAsString();
                        assistantReply.append(token);
                        listener.onToken(token);
                    }
                }
            }

            // save assistant reply for context
            JsonObject assistantMsg = new JsonObject();
            assistantMsg.addProperty("role", "assistant");
            assistantMsg.addProperty("content", assistantReply.toString());
            messages.add(assistantMsg);

            listener.onComplete();
        } catch (Exception e) {
            listener.onError(e.getMessage());
            e.printStackTrace();
        }
    }

    /* =========================
       IMAGE STREAMING (LLAVA)
    ========================= */
    public void askWithImage(
            String prompt,
            String imageBase64,
            StreamListener listener
    ) {

        try {
            JsonObject body = new JsonObject();
            body.addProperty("model", "llava");
            body.addProperty("prompt", prompt);

            JsonArray images = new JsonArray();
            images.add(imageBase64);
            body.add("images", images);

            body.addProperty("stream", true);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<java.io.InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(response.body()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                JsonObject json =
                        JsonParser.parseString(line).getAsJsonObject();

                if (json.has("response")) {
                    listener.onToken(json.get("response").getAsString());
                }
            }

            listener.onComplete();

        } catch (Exception e) {
            listener.onError(e.getMessage());
            e.printStackTrace();
        }
    }
}