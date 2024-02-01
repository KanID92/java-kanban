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
        taskManager.createEpic(new Epic("Эпик № 2", "Эпик № 2 для теста"));
        taskManager.createEpic(new Epic("Эпик № 3", "Эпик № 3 для теста"));
        taskManager.createSubtask(new SubTask("Подзадача №2.1", "Подзадача №2.1 для теста", 2));
        taskManager.createSubtask(new SubTask("Подзадача №2.2", "Подзадача №2.2 для теста", 2));
    }

    @Test
    void shouldBeEqualsEpicsWithTheSameIds() {
        Epic epic1 =  taskManager.getEpicByID(1);
        Epic epic2 =  taskManager.getEpicByID(1);
        assertEquals(epic1 , epic2, "Равны задачи с одинаковым ID");
    }
    @Test
    void shouldSetName() {
        taskManager.getEpicByID(3).setName("Задача 3а");
        assertEquals("Задача 3а", taskManager.getEpicByID(3).getName(), "Задача переименована");
    }

    @Test
    void shouldNotAddEpicForTheSameEpicLikeASubtask() {
        taskManager.getEpicByID(2).addSubtaskIdToEpic(2);
        assertFalse(taskManager.getEpicSubtasks(2).contains(taskManager.getEpicByID(2)),
                "Эпик нельзя добавить как подзадачу самому себе");
    }

}