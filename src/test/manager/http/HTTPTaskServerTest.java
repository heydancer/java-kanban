package manager.http;

import com.google.gson.Gson;
import common.Epic;
import common.SubTask;
import common.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HTTPTaskServerTest {

    private KVServer kvServer;
    private HTTPTaskServer httpTaskServer;

    private static Gson gson;
    private static Task task1;
    private static Epic epic1;
    private static SubTask subTask1;
    private static SubTask subTask2;
    private static HttpClient client;

    private static final URI url = URI.create("http://localhost:8080");
    private static final String FAKE_URL = "/fake_url";
    private static final int FAKE_ID = -999;

    @BeforeAll
    public static void beforeAll() {
        gson = new Gson();
        client = HttpClient.newBuilder().build();

        task1 = new Task("Task1", "Description Task1");
        epic1 = new Epic("Epic1", "Description Epic1");
        subTask1 = new SubTask("SubTask1", "Description SubTask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        subTask1.setEpicId(2);
        subTask2 = new SubTask("SubTask2", "Description SubTask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));
        subTask2.setEpicId(2);
    }

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        httpTaskServer = new HTTPTaskServer();
        httpTaskServer.start();
    }

    @AfterEach
    public void afterEach() {
        kvServer.stop();
        httpTaskServer.stop();
    }


    @Test
    void shouldCheckPostMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask1)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + FAKE_URL))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask2)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldCheckGetMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/task"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/epic"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/subtask"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + FAKE_URL))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldCheckGetMethodById() throws IOException, InterruptedException {
        createAllTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/task?id=1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/epic?id=2"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/subtask?id=3"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/subtask/epic?id=2"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/task?id=" + FAKE_ID))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldCheckGetMethodFromHistory() throws IOException, InterruptedException {
        createHistory();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }

    @Test
    void shouldCheckDeleteMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/task"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/epic"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/subtask"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + FAKE_URL))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldCheckDeleteMethodById() throws IOException, InterruptedException {
        createAllTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/task?id=1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/subtask?id=3"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/subtask?id=4"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/epic?id=2"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/task?id=" + FAKE_ID))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }


    private void createAllTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask1)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask2)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void createHistory() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks/task?id=1"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}