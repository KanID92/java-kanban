package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InMemoryTaskManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    void shouldTakeTask() {
        taskManager.createTask(new Task("Задача №1", "Задача №1 для проверки"));
        taskManager.createEpic(new Epic("Эпик №2", "Эпик №2 для проверки"));
        taskManager.createSubtask(new SubTask("Подзадача №3", "Подзадача №3 для проверки", 2));

        Assertions.assertTrue(taskManager.getTaskList().contains(taskManager.getTaskByID(1)),
                "Задача получена");
        Assertions.assertTrue(taskManager.getEpicList().contains(taskManager.getEpicByID(2)),
                "Эпик получен");
        Assertions.assertTrue(taskManager.getSubtaskList().contains(taskManager.getSubtaskByID(3)),
                "Подзадача получена");
    }


}