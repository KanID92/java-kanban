package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    void beforeEach() {
        taskManager.createEpic(new Epic("Эпик № 1", "Эпик № 1 для теста"));
        taskManager.createTask(new Epic("Эпик № 2", "Эпик № 2 для теста"));
        taskManager.createTask(new Epic("Эпик № 3", "Эпик № 3 для теста"));
        taskManager.createSubtask(new SubTask("Подзадача №2.1", "Подзадача №2.1 для теста", 2));
        taskManager.createSubtask(new SubTask("Подзадача №2.2", "Подзадача №2.2 для теста", 2));
    }

    @Test
    void shouldBeEqualsEpicsWithTheSameIds() {
        Epic epic1 =  taskManager.getEpicByID(1);
        Epic epic2 =  taskManager.getEpicByID(1);
        Assertions.assertEquals(epic1 , epic2);
    }
    @Test
    void shouldSetName() {
        taskManager.getEpicByID(3).setName("Задача 3а");
        Assertions.assertEquals("Задача 3а", taskManager.getEpicByID(3).getName());
    }

    @Test
    void shouldNotAddEpicForTheSameEpicLikeASubtask() {
        taskManager.getEpicByID(2).addSubtaskIdToEpic(2);
        Assertions.assertFalse(taskManager.getEpicSubtasks(2).contains(taskManager.getEpicByID(2)));
    }

}