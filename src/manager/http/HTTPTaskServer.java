package manager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import common.Epic;
import common.SubTask;
import common.Task;
import manager.Managers;
import manager.task.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HTTPTaskServer {
    private static final int PORT = 8080;
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;

    private final TaskManager manager;
    private final HttpServer server;

    public HTTPTaskServer() throws IOException {
        gson = new Gson().newBuilder().setPrettyPrinting().create();
        manager = Managers.getDefault();
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler());
    }

    public TaskManager getManager() {
        return manager;
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1);
    }

    class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    handlesGetProcessing(exchange);
                    break;

                case "POST":
                    handlesPostProcessing(exchange);
                    break;

                case "DELETE":
                    handlesDeleteProcessing(exchange);
                    break;
            }
        }

        private void handlesGetProcessing(HttpExchange exchange) throws IOException {
            String response = "";
            int statusCode = 404;

            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            if (path.endsWith("/tasks") && query == null) {
                statusCode = 200;
                response = gson.toJson(manager.getPrioritizedTasks());
            }

            if (path.endsWith("tasks/subtask/epic") && query != null) {
                statusCode = 200;
                int taskId = getIdFromQuery(query);
                response = gson.toJson(manager.getAllSubTaskOfEpic(manager.getEpic(taskId)));
            }

            if (path.endsWith("tasks/task")) {
                if (query != null) {
                    Task taskForReturn = null;
                    int taskId = getIdFromQuery(query);

                    for (Task task : manager.getTaskList()) {
                        if (taskId == task.getId()) {
                            taskForReturn = manager.getTask(taskId);
                            statusCode = 200;
                            response = gson.toJson(taskForReturn);
                        }
                    }

                    if (taskForReturn == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, task c " + taskId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    response = gson.toJson(manager.getTaskList());
                }
            }

            if (path.endsWith("tasks/history") && query == null) {
                statusCode = 200;
                response = gson.toJson(manager.getHistory());
            }

            if (path.endsWith("tasks/epic")) {
                if (query != null) {
                    Epic epicForReturn = null;
                    int epicId = getIdFromQuery(query);

                    for (Epic epic : manager.getEpicList()) {
                        if (epicId == epic.getId()) {
                            epicForReturn = manager.getEpic(epicId);
                            statusCode = 200;
                            response = gson.toJson(epicForReturn);
                        }
                    }

                    if (epicForReturn == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, epic c " + epicId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    response = gson.toJson(manager.getEpicList());
                }
            }

            if (path.endsWith("tasks/subtask")) {
                if (query != null) {
                    SubTask subTaskForReturn = null;
                    int subTaskId = getIdFromQuery(query);

                    for (SubTask subTask : manager.getSubTaskList()) {
                        if (subTaskId == subTask.getId()) {
                            subTaskForReturn = manager.getSubTask(subTaskId);
                            statusCode = 200;
                            response = gson.toJson(subTaskForReturn);
                        }
                    }

                    if (subTaskForReturn == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, SubTask c " + subTaskId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    response = gson.toJson(manager.getSubTaskList());
                }
            }

            exchange.sendResponseHeaders(statusCode, response.getBytes(CHARSET).length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes(CHARSET));
            }
        }

        private void handlesPostProcessing(HttpExchange exchange) throws IOException {
            String response = "";
            int statusCode = 404;

            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            InputStream inputStream = exchange.getRequestBody();
            if (inputStream != null) {
                String body = new String(inputStream.readAllBytes(), CHARSET);
                if (query == null) {
                    if (path.endsWith("tasks/task")) {
                        statusCode = 201;
                        Task newTask = gson.fromJson(body, Task.class);
                        manager.createTask(newTask);
                        response = "Задача типа Task создана";

                    } else if (path.endsWith("tasks/epic")) {
                        statusCode = 201;
                        Epic newEpic = gson.fromJson(body, Epic.class);
                        manager.createEpic(newEpic);
                        response = "Задача типа Epic создана";

                    } else if (path.endsWith("tasks/subtask")) {
                        SubTask newSubTask = gson.fromJson(body, SubTask.class);
                        Epic epicForSubTask = null;

                        for (Epic epic : manager.getEpicList()) {
                            if (epic.getId() == newSubTask.getEpicId()) {
                                epicForSubTask = epic;
                                statusCode = 201;
                                manager.createSubTask(epic, newSubTask);
                                manager.updateSubTask(newSubTask);
                                response = "Задача типа SubTask создана";
                            }
                        }

                        if (epicForSubTask == null) {
                            statusCode = 400;
                            response = "Необходимо указать корректный id Epic в теле Subtask";
                        }
                    }
                }
            }

            exchange.sendResponseHeaders(statusCode, response.getBytes(CHARSET).length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes(CHARSET));
            }
        }

        private void handlesDeleteProcessing(HttpExchange exchange) throws IOException {

            String response = "";
            int statusCode = 404;

            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            if (path.endsWith("/tasks") && query == null) {
                statusCode = 200;
                manager.removeAllTask();
                manager.removeAllEpic();
                manager.getPrioritizedTasks().clear();
                response = "Все задачи удалены";
            }

            if (path.endsWith("tasks/task")) {
                if (query != null) {
                    Task taskForRemove = null;
                    int taskId = getIdFromQuery(query);

                    for (Task task : manager.getTaskList()) {
                        if (taskId == task.getId()) {
                            taskForRemove = task;
                            statusCode = 200;
                            manager.removeTask(taskId);
                            response = "Задача типа TASK с ID " + taskId + " удалена";
                        }
                    }

                    if (taskForRemove == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, task c " + taskId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    manager.removeAllTask();
                    response = "удалены все задачи типа TASK";
                }
            }

            if (path.endsWith("tasks/epic")) {
                if (query != null) {
                    Epic epicForRemove = null;
                    int epicId = getIdFromQuery(query);

                    for (Epic epic : manager.getEpicList()) {
                        if (epicId == epic.getId()) {
                            epicForRemove = epic;
                            statusCode = 200;
                            manager.getAllSubTaskOfEpic(epicForRemove).clear();
                            manager.removeEpic(epicId);
                            response = "Задача типа EPIC с ID " + epicId + " удалена";
                        }
                    }

                    if (epicForRemove == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, epic c " + epicId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    manager.removeAllEpic();
                    response = "удалены все задачи типа EPIC";
                }
            }

            if (path.endsWith("tasks/subtask")) {
                if (query != null) {
                    SubTask subTaskForRemove = null;
                    int subTaskId = getIdFromQuery(query);

                    for (SubTask subTask : manager.getSubTaskList()) {
                        if (subTaskId == subTask.getId()) {
                            subTaskForRemove = subTask;
                            statusCode = 200;
                            manager.removeSubTask(subTaskId);
                            response = "Задача типа SubTask с ID " + subTaskId + " удалена";
                        }
                    }

                    if (subTaskForRemove == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, SubTask c " + subTaskId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    manager.removeAllSubTask();
                    response = "удалены все задачи типа SUBTASK";
                }
            }

            exchange.sendResponseHeaders(statusCode, response.getBytes(CHARSET).length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes(CHARSET));
            }
        }

        private int getIdFromQuery(String query) {
            String[] splitQuery = query.split("=");
            return Integer.parseInt(splitQuery[1]);
        }
    }
}





