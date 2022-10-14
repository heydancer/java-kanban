package manager.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    public HttpClient client;
    private String token;
    private final URI url;

    public KVTaskClient(String url) {
        this.client = HttpClient.newHttpClient();
        this.url = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/register"))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                this.token = response.body();
            } else {
                System.out.println("Ошибка при регистрации код запроса - " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Ошибка при регистрации");
        }
    }

    public void put(String key, String json) {
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/save/%s?API_TOKEN=%s", url, key, token)))
                .POST(body)
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Метод put успешно отработал");
            } else {
                System.out.println("Ошибка в методе put: " + response.statusCode());
            }
        } catch (NullPointerException | InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String load(String key) {
        String managerStatusToLoad = null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/load/%s?API_TOKEN=%s", url, key, token)))
                .GET()
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                managerStatusToLoad = response.body();
            } else {
                System.out.println("Ошибка в методе load: " + response.statusCode());
            }
        } catch (NullPointerException | InterruptedException | IOException e) {
            System.out.println("Ошибка в методе load");
        }
        return managerStatusToLoad;
    }
}
