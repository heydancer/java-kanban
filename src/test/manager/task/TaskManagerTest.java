package manager.task;

import common.Epic;
import common.SubTask;
import common.Task;
import constant.Status;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected static final int FAKE_ID = -999;
    protected T manager;

    @Test
    void shouldReturnTaskList() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> taskList1 = new ArrayList<>(List.of(task1, task2));
        List<Task> taskList2 = new ArrayList<>(List.of(task2, task1, task2));

        assertEquals(taskList1.size(), manager.getTaskList().size());
        assertNotEquals(taskList2.size(), manager.getTaskList().size());

        assertEquals(taskList1, manager.getTaskList());
        assertNotEquals(taskList2, manager.getTaskList());
    }

    @Test
    void shouldReturnEpicList() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");
        SubTask subTask1 = new SubTask("SubTask1", "Description SubTask1");

        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubTask(epic2, subTask1);

        List<Task> epicList1 = new ArrayList<>(List.of(epic1, epic2));
        List<Task> epicList2 = new ArrayList<>(List.of(epic2, epic1, epic2));

        List<Task> subTaskList1 = new ArrayList<>(List.of(subTask1));

        assertEquals(epicList1.size(), manager.getEpicList().size());
        assertNotEquals(epicList2.size(), manager.getEpicList().size());

        assertEquals(epicList1, manager.getEpicList());
        assertNotEquals(epicList2, manager.getEpicList());

        assertEquals(subTaskList1, manager.getSubTaskList());

        List<Task> emptyEpic = new ArrayList<>();
        List<Task> emptySubTaskByEpic = new ArrayList<>();
        manager.removeAllEpic();

        assertEquals(emptyEpic, manager.getEpicList());
        assertEquals(emptySubTaskByEpic, manager.getSubTaskList());
    }

    @Test
    void shouldReturnSubTaskList() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        SubTask subTask1 = new SubTask("SubTask1", "Description SubTask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        SubTask subTask2 = new SubTask("SubTask2", "Description SubTask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createEpic(epic1);
        manager.createSubTask(epic1, subTask1);
        manager.createSubTask(epic1, subTask2);

        List<Task> subTaskList = new ArrayList<>(List.of(subTask1, subTask2));

        assertEquals(subTaskList.size(), manager.getSubTaskList().size());
        assertEquals(subTaskList, manager.getSubTaskList());
        assertFalse(manager.getSubTaskList().isEmpty());
    }

    @Test
    void shouldRemoveAllTask() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTask(task1.getId());

        manager.removeAllTask();
        assertTrue(manager.getTaskList().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
        assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void shouldRemoveAllEpic() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");
        SubTask subTask1 = new SubTask("SubTask1", "Description SubTask1");

        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubTask(epic2, subTask1);

        manager.getEpic(epic1.getId());
        manager.getEpic(epic2.getId());

        manager.removeAllEpic();
        assertTrue(manager.getEpicList().isEmpty());
        assertTrue(manager.getSubTaskList().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
        assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void shouldRemoveAllSubTask() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        SubTask subTask1 = new SubTask("SubTask1", "Description SubTask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        SubTask subTask2 = new SubTask("SubTask2", "Description SubTask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createEpic(epic1);
        manager.createSubTask(epic1, subTask1);
        manager.createSubTask(epic1, subTask2);

        manager.getSubTask(subTask1.getId());
        manager.getSubTask(subTask2.getId());

        manager.removeAllSubTask();
        assertFalse(manager.getEpicList().isEmpty());
        assertTrue(manager.getSubTaskList().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
        assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void shouldReturnTask() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        int taskId = manager.createTask(task1);

        assertEquals(task1, manager.getTask(taskId));
        assertNotEquals(task2, manager.getTask(taskId));
    }

    @Test
    void shouldReturnGetEpic() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");

        manager.createEpic(epic1);

        assertEquals(epic1, manager.getEpic(epic1.getId()));
        assertNotEquals(epic2, manager.getEpic(epic1.getId()));
    }

    @Test
    void shouldReturnSubTask() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        SubTask subTask1 = new SubTask("SubTask1", "Description SubTask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        SubTask subTask2 = new SubTask("SubTask2", "Description SubTask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createEpic(epic1);
        manager.createSubTask(epic1, subTask1);
        manager.createSubTask(epic1, subTask2);

        assertEquals(subTask1, manager.getSubTask(subTask1.getId()));
        assertNotEquals(subTask1, manager.getSubTask(subTask2.getId()));
    }

    @Test
    void shouldCreateTask() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        manager.createTask(task1);
        manager.createTask(task2);

        assertEquals(task1, manager.getTask(task1.getId()));
        assertNotEquals(FAKE_ID, task2.getId());
        assertFalse(manager.getTaskList().isEmpty());
        assertEquals(2, manager.getTaskList().size());
    }

    @Test
    void shouldCreateEpic() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        assertEquals(epic1, manager.getEpic(epic1.getId()));
        assertNotEquals(FAKE_ID, epic2.getId());
        assertFalse(manager.getEpicList().isEmpty());
        assertEquals(2, manager.getEpicList().size());
    }

    @Test
    void shouldCreateSubTask() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        SubTask subTask1 = new SubTask("SubTask1", "Description SubTask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        SubTask subTask2 = new SubTask("SubTask2", "Description SubTask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createEpic(epic1);
        manager.createSubTask(epic1, subTask1);
        manager.createSubTask(epic1, subTask2);

        assertEquals(subTask1, manager.getSubTask(subTask1.getId()));
        assertNotEquals(FAKE_ID, subTask2.getId());
        assertFalse(manager.getSubTaskList().isEmpty());
        assertEquals(2, manager.getSubTaskList().size());
    }

    @Test
    void shouldUpdateTask() {
        Task task1 = new Task("Task1", "Description Task1");

        int taskId = manager.createTask(task1);

        task1 = new Task("New Task", "Description Task1");
        task1.setId(taskId);
        task1.setStatus(Status.IN_PROGRESS);

        manager.updateTask(task1);

        assertEquals("New Task", manager.getTask(1).getName());
        assertEquals(Status.IN_PROGRESS, manager.getTask(1).getStatus());

        manager.getPrioritizedTasks().forEach(System.out::println);
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");

        int epicId = manager.createEpic(epic1);

        epic1 = new Epic("New Epic", "Description Epic1");
        epic1.setId(epicId);
        epic1.setStatus(Status.IN_PROGRESS);

        manager.updateEpic(epic1);

        assertEquals("New Epic", manager.getEpic(1).getName());
        assertEquals(Status.IN_PROGRESS, manager.getEpic(1).getStatus());
    }

    @Test
    void shouldUpdateSubTask() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        SubTask subTask1 = new SubTask("SubTask1", "Description SubTask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        SubTask subTask2 = new SubTask("SubTask2", "Description SubTask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        int epicId = manager.createEpic(epic1);
        int subTask1Id = manager.createSubTask(epic1, subTask1);
        int subTask2Id = manager.createSubTask(epic1, subTask2);

        assertEquals(Status.NEW, epic1.getStatus());
        assertEquals(Status.NEW, subTask1.getStatus());
        assertEquals(Status.NEW, subTask2.getStatus());

        subTask1 = new SubTask("New SubTask1", "Description SubTask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        subTask1.setId(subTask1Id);
        subTask1.setEpicId(epicId);
        subTask1.setStatus(Status.DONE);

        manager.updateSubTask(subTask1);

        subTask2 = new SubTask("New SubTask2", "Description SubTask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));
        subTask2.setId(subTask2Id);
        subTask2.setEpicId(epicId);
        subTask2.setStatus(Status.IN_PROGRESS);

        manager.updateSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
        assertEquals(Status.DONE, subTask1.getStatus());
        assertEquals(Status.IN_PROGRESS, subTask2.getStatus());
    }

    @Test
    void shouldRemoveTask() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTask(task1.getId());

        manager.removeTask(task1.getId());

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.removeTask(FAKE_ID));

        assertNull(exception.getMessage());

        manager.removeTask(task2.getId());

        assertTrue(manager.getTaskList().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
        assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void shouldRemoveEpic() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        manager.getEpic(epic1.getId());

        manager.removeEpic(epic1.getId());

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.removeEpic(FAKE_ID));

        assertNull(exception.getMessage());

        manager.removeEpic(epic2.getId());

        assertTrue(manager.getEpicList().isEmpty());
        assertTrue(manager.getSubTaskList().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldRemoveSubTask() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        SubTask subTask1 = new SubTask("SubTask1", "Description SubTask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        SubTask subTask2 = new SubTask("SubTask2", "Description SubTask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createEpic(epic1);
        manager.createSubTask(epic1, subTask1);
        manager.createSubTask(epic1, subTask2);

        manager.getSubTask(subTask1.getId());
        manager.getSubTask(subTask2.getId());

        manager.removeSubTask(subTask1.getId());

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.removeSubTask(FAKE_ID));

        assertNull(exception.getMessage());

        manager.removeSubTask(subTask2.getId());

        assertFalse(manager.getEpicList().isEmpty());
        assertTrue(manager.getSubTaskList().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldReturnAllSubTaskOfEpic() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        SubTask subTask1 = new SubTask("SubTask1", "Description SubTask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        SubTask subTask2 = new SubTask("SubTask2", "Description SubTask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createEpic(epic1);

        assertTrue(manager.getAllSubTaskOfEpic(epic1).isEmpty());

        manager.createSubTask(epic1, subTask1);
        manager.createSubTask(epic1, subTask2);

        List<Task> subTaskList = new ArrayList<>(List.of(subTask1, subTask2));

        assertEquals(subTaskList.size(), manager.getAllSubTaskOfEpic(epic1).size());
        assertEquals(subTaskList, manager.getAllSubTaskOfEpic(epic1));
    }

    @Test
    void shouldReturnHistory() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTask(task1.getId());
        manager.getTask(task2.getId());

        List<Task> taskList1 = new ArrayList<>(List.of(task1, task2));
        List<Task> taskList2 = new ArrayList<>(List.of(task2, task1, task2));

        assertEquals(taskList1.size(), manager.getHistory().size());
        assertNotEquals(taskList2.size(), manager.getHistory().size());

        assertEquals(taskList1, manager.getHistory());
        assertNotEquals(taskList2, manager.getHistory());
    }

    @Test
    void shouldReturnPrioritizedTasks() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        Epic epic1 = new Epic("Epic1", "Description Epic1");
        SubTask subTask1 = new SubTask("SubTask1", "Description SubTask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        SubTask subTask2 = new SubTask("SubTask2", "Description SubTask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createTask(task1);
        manager.createTask(task2);

        manager.createEpic(epic1);
        manager.createSubTask(epic1, subTask1);
        manager.createSubTask(epic1, subTask2);


        List<Task> prioritizedTasks = new ArrayList<>();

        prioritizedTasks.add(task2);
        prioritizedTasks.add(subTask2);
        prioritizedTasks.add(subTask1);
        prioritizedTasks.add(task1);

        int size = prioritizedTasks.size();

        assertEquals(size, manager.getPrioritizedTasks().size());

        int indexCounter = 0;
        for (Task task : manager.getPrioritizedTasks()) {
            assertEquals(task, prioritizedTasks.get(indexCounter++));
        }
    }

    @Test
    void shouldUpdateTaskAndReturnPrioritizedTasks() {

        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        Epic epic1 = new Epic("Epic1", "Description Epic1");
        SubTask subTask1 = new SubTask("SubTask1", "Description SubTask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        SubTask subTask2 = new SubTask("SubTask2", "Description SubTask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        int task1Id = manager.createTask(task1);
        manager.createTask(task2);

        manager.createEpic(epic1);
        manager.createSubTask(epic1, subTask1);
        manager.createSubTask(epic1, subTask2);

        List<Task> expectedOrderList = new ArrayList<>();

        task1 = new Task("Updated task1", "Task1 was update",
                Duration.ofMinutes(120), LocalDateTime.of(2021, 1, 1, 0, 0));
        task1.setId(task1Id);

        expectedOrderList.add(task1);
        expectedOrderList.add(task2);
        expectedOrderList.add(subTask2);
        expectedOrderList.add(subTask1);

        manager.updateTask(task1);

        int indexCounter = 0;

        for (Task task : manager.getPrioritizedTasks()) {
            assertEquals(task, expectedOrderList.get(indexCounter++));
        }

        assertEquals(LocalDateTime.of(2021, 1, 1, 2, 0), task1.getEndTime());
    }
}