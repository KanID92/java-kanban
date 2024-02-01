package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    void beforeEach() {
        taskManager.createTask(new Task("Задача 1", "Задача 1 для теста"));
        taskManager.createTask(new Task("Задача 2", "Задача 2 для теста"));
        taskManager.createTask(new Task("Задача 3", "Задача 3 для теста"));
        taskManager.getTaskByID(3).setName("Задача 3а");
    }

    @Test
    void shouldBeEqualsTasksWithTheSameIds() {

        Task task1 =  taskManager.getTaskByID(1);
        Task task2 =  taskManager.getTaskByID(1);
        Assertions.assertEquals(task1, task2,
                "Объекты Таск, вызванные по одинаковому ID - одинаковые");
    }

    @Test
    void shouldGetId() {
        Assertions.assertEquals(3, taskManager.getTaskByID(3).getId(),
                "Правильное ID Задачи возвращено");
    }

    @Test
    void shouldGetName() {
        Assertions.assertEquals("Задача 2", taskManager.getTaskByID(2).getName(),
                "Правильное имя задачи возвращено");
    }

    @Test
    void shouldSetId() {
        taskManager.getTaskByID(3).setId(11);
        Assertions.assertEquals(11,taskManager.getTaskByID(3).getId(),
                "Id назначено");
    }

    @Test
    void shouldGetDescription() {

        Assertions.assertEquals("Задача 2 для теста", taskManager.getTaskByID(2).getDescription(),
                "Описание задачи возвращено");
    }

    @Test
    void shouldSetName() {
        taskManager.getTaskByID(3).setName("Задача 3а");
        Assertions.assertEquals("Задача 3а", taskManager.getTaskByID(3).getName(),
                "Задача перенаименована");
    }

}