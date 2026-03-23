package dk.easv.fox.repository;

import dk.easv.fox.model.FoxConfig;
import dk.easv.fox.model.FoxParameter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Repository implementation that communicates with the Fox emulator REST API.
 *
 * POST /fox/commands/send/{group_id}  — send a command
 *  GET /fox/configs/get/{group_id}    — read current config (returns array)
 */
public class ApiRepository implements IFoxRepository {

    private static final String BASE_URL = "http://10.5.10.10:8080";

    // Force HTTP/1.1 — HTTP/2 causes body to arrive as null on some FastAPI servers
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    @Override
    public void sendCommand(int groupId, String command, String value) {
        String url  = BASE_URL + "/fox/commands/send/" + groupId;
        String body = String.format("{\"command\":\"%s\",\"value\":\"%s\"}", command, value);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status < 200 || status >= 300) {
                throw new RuntimeException(
                        "API error " + status + " for command '" + command + " " + value + "'" +
                        "\nBody: " + body +
                        "\nResponse: " + response.body());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send command: " + e.getMessage(), e);
        }
    }

    /**
     * Fetch the current fox config.
     * Returns an array of ReceivedCommand objects:
     * [{"id":1,"command":"wr freq","value":"434750"}, ...]
     */
    @Override
    public FoxConfig getConfig(int groupId) {
        String url = BASE_URL + "/fox/configs/get/" + groupId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status < 200 || status >= 300) {
                throw new RuntimeException("API error " + status + " fetching config");
            }
            return parseConfigArray(response.body());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch config: " + e.getMessage(), e);
        }
    }

    /**
     * Parses the array response: [{"id":1,"command":"wr freq","value":"434750"}, ...]
     * Extracts each "command"/"value" pair and maps to FoxParameter.
     */
    private FoxConfig parseConfigArray(String json) {
        FoxConfig config = new FoxConfig();
        // Match each {"command":"...","value":"..."} object in the array
        Pattern objPattern = Pattern.compile("\\{[^}]+\\}");
        Pattern cmdPattern = Pattern.compile("\"command\"\\s*:\\s*\"([^\"]+)\"");
        Pattern valPattern = Pattern.compile("\"value\"\\s*:\\s*\"([^\"]+)\"");

        Matcher objMatcher = objPattern.matcher(json);
        while (objMatcher.find()) {
            String obj = objMatcher.group();
            Matcher cmdMatcher = cmdPattern.matcher(obj);
            Matcher valMatcher = valPattern.matcher(obj);
            if (!cmdMatcher.find()) continue;

            String command = cmdMatcher.group(1).trim();
            String value   = valMatcher.find() ? valMatcher.group(1).trim() : "";

            for (FoxParameter param : FoxParameter.values()) {
                if (param.getCommand().equalsIgnoreCase(command)) {
                    config.set(param, value);
                    break;
                }
            }
        }
        return config;
    }
}
