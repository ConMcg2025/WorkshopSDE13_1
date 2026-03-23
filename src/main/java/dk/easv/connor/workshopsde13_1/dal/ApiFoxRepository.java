package dk.easv.connor.workshopsde13_1.dal;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class ApiFoxRepository implements IFoxRepository {

    private static final String BASE_URL = "http://10.5.10.10:8080";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void sendCommand(int groupNumber, String command, String value) throws Exception {
        String url = BASE_URL + "/fox/commands/send/" + groupNumber;

        String json = mapper.writeValueAsString(new ReceivedCommandDto(command, value));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new Exception("API error " + response.statusCode() + ": " + response.body());
    }

    @Override
    public List<PredefinedCommand> getCommands() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/fox/commands/get"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new Exception("API error " + response.statusCode() + ": " + response.body());

        return Arrays.asList(mapper.readValue(response.body(), PredefinedCommand[].class));
    }

    @Override
    public List<ReceivedCommand> getConfig(int groupNumber) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/fox/configs/get/" + groupNumber))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new Exception("API error " + response.statusCode() + ": " + response.body());

        return Arrays.asList(mapper.readValue(response.body(), ReceivedCommand[].class));
    }
}
