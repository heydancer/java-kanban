package server;

import com.google.gson.Gson;
import common.Task;
import manager.Managers;
import manager.http.HTTPTaskManager;
import manager.http.HTTPTaskServer;
import manager.task.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        new KVServer().start();
        new HTTPTaskServer().start();


    }
}
