import common.*;
import manager.Managers;
import manager.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Java", "Сделать домашнее задание");
        Task task2 = new Task("Английский", "Поехать на занятие");

        Epic epic1 = new Epic("Переезд", "Покупка новой квартиры");
        SubTask subTask1 = new SubTask("Собрать вещи", "Сложить все вещи по коробкам");
        SubTask subTask2 = new SubTask("Собрать игрушки собаки", "Найти все игрушки");
        SubTask subTask3 = new SubTask("Купить крупу", "Найти рис");

        Epic epic2 = new Epic("Решить задачу", "долго думать над линкед листом");

        //-----------------Создание--------------------
        //Создаем Tasks
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        //Создаем Epic и три SubTasks
        taskManager.createEpic(epic1);
        taskManager.createSubTask(epic1, subTask1);
        taskManager.createSubTask(epic1, subTask2);
        taskManager.createSubTask(epic1, subTask3);
        //Создаем Epic без SubTasks
        taskManager.createEpic(epic2);

        //-----------------Просмотры--------------------
        //Просмотрим epic2
        taskManager.getEpic(epic2.getId());
        //Просмотрим task1
        taskManager.getTask(task1.getId());
        //Просмотрим task2
        taskManager.getTask(task2.getId());
        //Просмотрим task1
        taskManager.getTask(task1.getId());
        //Просмотрим subTask3
        taskManager.getSubTask(subTask3.getId());
        //Просмотрим subTask2
        taskManager.getSubTask(subTask2.getId());
        //Просмотрим subTask1
        taskManager.getSubTask(subTask1.getId());
        //Просмотрим epic1
        taskManager.getEpic(epic1.getId());
        //Просмотрим subTask2
        taskManager.getSubTask(subTask2.getId());

        //Проверяем историю просмотров на повторы
        System.out.println("История просмотров без повторов: ");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        //---------------Проверка метода remove---------------
        //Удаляем task1
        taskManager.removeTask(task1.getId());

        //Удаляем epic1
        taskManager.removeEpic(epic1.getId());

        //Проверяем историю просмотров после вызова методов removeTask и removeEpic
        System.out.println();
        System.out.println("История просмотров после удаления task1 и epic1: ");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
