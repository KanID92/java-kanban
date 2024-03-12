package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();
    Task taskTest1;
    Task taskTest2;
    Task taskTest3;

    Epic epicTest1;
    int subTaskTest1Id;
    SubTask subTaskTest1;


    @BeforeEach
    void beforeEach() {
        taskTest1 = taskManager.getTaskByID(taskManager.createTask(new Task("Задача №1", "Для тестов")));
        // вызов 1 задачи

        taskTest2 = taskManager.getTaskByID(taskManager.createTask(new Task("Задача №2", "Для тестов")));
        // вызов 2 задачи

        taskTest3 = taskManager.getTaskByID(taskManager.createTask(new Task("Задача №3", "Для тестов")));
        // вызов 3 задачи

        epicTest1 = taskManager.getEpicByID(taskManager.createEpic(new Epic("Задача №4", "Для тестов")));
        // вызов 4 задачи

        subTaskTest1 = taskManager.getSubtaskByID((taskManager.createSubtask(
                new SubTask("Задача №5", "Для тестов", epicTest1.getId()))));
        // вызов 5 задачи

        taskManager.getTaskByID(1); // вызов 1 задачи
        taskManager.getTaskByID(3); // вызов 3 задачи
        taskManager.getTaskByID(2); // вызов 2 задачи
        taskManager.getTaskByID(1); // вызов 1 задачи

    }

    @Test
    void shouldSavePreviousAndNewTasksVersions() {

        taskTest2.setId(taskTest1.getId());
        taskManager.updateTask(taskTest2);

        assertTrue(taskManager.getHistory().contains(taskTest1),
                "История содержит предыдущую версию задачи");
        assertTrue(taskManager.getHistory().contains(taskTest2),
                "История содержит текущую версию задачи");

    }

    @Test
    void shouldGetHistory() {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        assertEquals(taskArrayList.getClass(), taskManager.getHistory().getClass(),
                "Проверка правильности возвращаемого класса");

        assertEquals(5, taskManager.getHistory().size(),
                "Проверка правильности размера истории");


        assertTrue(taskManager.getHistory().contains(taskTest1) &&
                        taskManager.getHistory().contains(taskTest2) &&
                        taskManager.getHistory().contains(taskTest3) &&
                        taskManager.getHistory().contains(epicTest1) &&
                        taskManager.getHistory().contains(subTaskTest1),
                "Содержит все вызванные задачи");
    }

    @Test
    void shouldAddToHistory() {
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history,
                "История не пустая.");
        assertEquals(5, history.size(),
                "История не пустая.");
        assertTrue(taskManager.getHistory().contains(taskTest1), "История содержит добавленную задачу");
        assertTrue(taskManager.getHistory().contains(taskTest2), "История содержит добавленную задачу");
        assertTrue(taskManager.getHistory().contains(taskTest3), "История содержит добавленную задачу");
    }

    @Test
    void shouldLinkLast() {
        List<Task> historyList = taskManager.getHistory();
        assertTrue(historyList.contains(subTaskTest1),
                "Проверка на наличие последней задачи");
        assertEquals(historyList.get(historyList.size() - 1), taskTest1,
                "Проверка записи как последнего значения");
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        taskManager.deleteSubtaskByID(subTaskTest1.getId());
        List<Task> tasksHistory = taskManager.getHistory();
        assertFalse(tasksHistory.contains(subTaskTest1));
        assertEquals(tasksHistory.get(3), taskTest1, "Соседний справа элемент истории занял " +
                "откорректированное положение");
        assertEquals(tasksHistory.get(0), epicTest1, "Соседний слева элемент истории остался в том же положении");
    }

    @Test
    void shouldNotContainInEpicDeletedSubtask() {
        int epicId = subTaskTest1.getEpicId();
        taskManager.deleteSubtaskByID(subTaskTest1Id);
        assertFalse(taskManager.getEpicByID(epicId).getAllEpicSubtasksIds().contains(subTaskTest1Id));
    }

    @Test
    void shouldNotContainInDeletedSubtaskEpicID() {
        taskManager.deleteSubtaskByID(subTaskTest1.getId());
        assertEquals(-1, subTaskTest1.getEpicId());

    }


}

