package manager.http;

import common.Epic;
import common.SubTask;
import common.Task;
import manager.Managers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HTTPTaskManagerTest {

    private Task task1;
    private Task task2;
    private Epic epic1;
    private SubTask subTask1;
    private SubTask subTask2;
    private HTTPTaskManager manager;
    private static KVServer kvServer;

    @BeforeAll
    public static void beforeAll() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @BeforeEach
    public void beforeEach() {
        manager = (HTTPTaskManager) Managers.getDefault();

        task1 = new Task("Task1", "Description Task1");
        task2 = new Task("Task2", "Description Task2");
        epic1 = new Epic("Epic1", "Description Epic1");
        subTask1 = new SubTask("SubTask1", "Description SubTask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        subTask2 = new SubTask("SubTask2", "Description SubTask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));
    }

    @Test
    public void shouldSaveAndRestoreAnEmptyTaskList() {
        manager.createTask(task1);

        assertEquals(1, manager.getTaskList().size());

        manager.removeTask(task1.getId());

        manager = (HTTPTaskManager) Managers.getDefault();

        assertEquals(List.of(), manager.getTaskList());
    }

    @Test
    public void shouldSaveAndRestoreEpicWithoutSubtasks() {
        List<Task> epicList = new ArrayList<>(List.of(epic1));

        manager.createEpic(epic1);

        manager = (HTTPTaskManager) Managers.getDefault();
        manager.loadFromServer();

        assertEquals(epicList.size(), manager.getEpicList().size());
        assertEquals(List.of(), manager.getSubTaskList());
    }

    @Test
    public void shouldSaveAndRestoreAnEmptyHistoryList() {

        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTask(task1.getId());
        manager.getTask(task2.getId());

        assertEquals(2, manager.getTaskList().size());
        assertEquals(2, manager.getHistory().size());

        manager.removeAllTask();
        manager = (HTTPTaskManager) Managers.getDefault();

        assertEquals(0, manager.getTaskList().size());
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void shouldSaveAndLoadTasks() {
        manager.createTask(task1);
        manager.createEpic(epic1);
        manager.createSubTask(epic1, subTask1);
        manager.createSubTask(epic1, subTask2);

        assertFalse(manager.getTaskList().isEmpty());
        assertFalse(manager.getEpicList().isEmpty());
        assertFalse(manager.getSubTaskList().isEmpty());

        manager = (HTTPTaskManager) Managers.getDefault();

        assertTrue(manager.getTaskList().isEmpty());
        assertTrue(manager.getEpicList().isEmpty());
        assertTrue(manager.getSubTaskList().isEmpty());

        manager.loadFromServer();

        assertFalse(manager.getTaskList().isEmpty());
        assertFalse(manager.getEpicList().isEmpty());
        assertFalse(manager.getSubTaskList().isEmpty());
    }

    @AfterAll
    public static void afterAll() {
        kvServer.stop();
    }
}