package dk.easv.connor.workshopsde13_1.dal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiFoxRepository implements IFoxRepository {

    private static final String BASE_URL = "http://10.5.10.10:8080/";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void sendCommand(int groupNumber, String command, String value) throws Exception {
        String url = BASE_URL + "fox?group=" + groupNumber
                + "&command=" + command.replace(" ", "%20")
                + "&value=" + value;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new Exception("API error " + response.statusCode() + ": " + response.body());
    }
}
