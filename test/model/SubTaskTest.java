package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

class SubTaskTest {
    TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    void beforeEach() {
        taskManager.createEpic(new Epic("Эпик 1", "Эпик 1 для теста подзадач"));
        taskManager.createEpic(new Epic("Эпик 2", "Эпик 2 для теста подзадач"));

        taskManager.createSubtask(new SubTask("Подзадача 1.1", "Подзадача 1.1 для теста подзадач", 1));
        taskManager.createSubtask(new SubTask("Подзадача 1.2", "Подзадача 1.2 для теста подзадач", 1));
        taskManager.createSubtask(new SubTask("Подзадача 2.1", "Подзадача 2.1 для теста подзадач", 2));

    }

    @Test
    void shouldBeEqualsSubtaskWithTheSameIds() {

        SubTask subtask1 = taskManager.getSubtaskByID(3);
        SubTask subtask2 = taskManager.getSubtaskByID(3);
        Assertions.assertEquals(subtask1, subtask2);
    }

    void shouldNotBeAddSubtaskIdLikeEpicIdWitchBelongs() {
        taskManager.createSubtask(new SubTask("Подзадача 2.1", "Подзадача 2.1 для теста подзадач", 6));
        Assertions.assertNotEquals(6, taskManager.getSubtaskByID(6).getEpicId());
    }

    @Test
    void shouldGetId() {
        Assertions.assertEquals(3, taskManager.getSubtaskByID(3).getId());
    }

    @Test
    void shouldGetName() {
        Assertions.assertEquals("Подзадача 2.1", taskManager.getSubtaskByID(5).getName());
    }

    @Test
    void shouldSetId() {
        taskManager.getSubtaskByID(4).setId(10);
        Assertions.assertEquals(10, taskManager.getSubtaskByID(4).getId());
    }

    @Test
    void shouldGetDescription() {

        Assertions.assertEquals("Подзадача 1.2 для теста подзадач", taskManager.getSubtaskByID(4).getDescription());
    }

    @Test
    void shouldSetName() {
        taskManager.getSubtaskByID(5).setName("Подзадача 2.1а");
        Assertions.assertEquals("Подзадача 2.1а", taskManager.getSubtaskByID(5).getName());
    }

}