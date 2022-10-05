package common;

import constant.Status;
import manager.Managers;
import manager.task.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static constant.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private static TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    public void shouldCheckEpicStatusWithoutSubTasks() {
        Epic testEpic = new Epic("Test Epic", "Description Test Epic");
        int testEpicId = manager.createEpic(testEpic);

        Status epicStatus = manager.getEpic(testEpicId).getStatus();

        assertEquals(NEW, epicStatus);
    }

    @Test
    public void shouldCheckEpicStatusWithNewSubTasks() {
        Epic testEpic = new Epic("Test Epic", "Description Test Epic");
        int testEpicId = manager.createEpic(testEpic);

        SubTask testSubTask1 = new SubTask("Test SubTask1", "Description Test SubTask1");
        SubTask testSubTask2 = new SubTask("Test SubTask2", "Description Test SubTask2");

        manager.createSubTask(testEpic, testSubTask1);
        manager.createSubTask(testEpic, testSubTask2);

        Status epicStatus = manager.getEpic(testEpicId).getStatus();

        assertEquals(NEW, epicStatus);
    }

    @Test
    public void shouldCheckEpicStatusWithDoneSubTasksStatus() {
        Epic testEpic = new Epic("Test Epic", "Description Test Epic");
        int testEpicId = manager.createEpic(testEpic);

        SubTask testSubTask1 = new SubTask("Test SubTask1", "Description Test SubTask1");
        SubTask testSubTask2 = new SubTask("Test SubTask2", "Description Test SubTask2");

        manager.createSubTask(testEpic, testSubTask1);
        manager.createSubTask(testEpic, testSubTask2);

        testSubTask1.setStatus(DONE);
        testSubTask2.setStatus(DONE);

        manager.updateSubTask(testSubTask1);
        manager.updateSubTask(testSubTask2);

        Status epicStatus = manager.getEpic(testEpicId).getStatus();

        assertEquals(DONE, epicStatus);
    }

    @Test
    public void shouldCheckEpicInProgressStatus() {
        Epic testEpic = new Epic("Test Epic", "Description Test Epic");
        int testEpicId = manager.createEpic(testEpic);

        SubTask testSubTask1 = new SubTask("Test SubTask1", "Description Test SubTask1");
        SubTask testSubTask2 = new SubTask("Test SubTask2", "Description Test SubTask2");

        manager.createSubTask(testEpic, testSubTask1);
        manager.createSubTask(testEpic, testSubTask2);

        testSubTask2.setStatus(DONE);

        manager.updateSubTask(testSubTask2);

        Status epicStatus = manager.getEpic(testEpicId).getStatus();

        assertEquals(IN_PROGRESS, epicStatus);
    }

    @Test
    public void shouldCheckEpicInProgressStatus2() {
        Epic testEpic = new Epic("Test Epic", "Description Test Epic");
        int testEpicId = manager.createEpic(testEpic);

        SubTask testSubTask1 = new SubTask("Test SubTask1", "Description Test SubTask1");
        SubTask testSubTask2 = new SubTask("Test SubTask1", "Description Test SubTask1");

        manager.createSubTask(testEpic, testSubTask1);
        manager.createSubTask(testEpic, testSubTask2);

        testSubTask1.setStatus(IN_PROGRESS);
        testSubTask2.setStatus(IN_PROGRESS);

        manager.updateSubTask(testSubTask1);
        manager.updateSubTask(testSubTask2);

        Status epicStatus = manager.getEpic(testEpicId).getStatus();

        assertEquals(IN_PROGRESS, epicStatus);
    }
}