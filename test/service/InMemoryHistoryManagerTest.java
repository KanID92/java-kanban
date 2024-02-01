package service;

import java.util.List;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();
    int taskTest1Id;
    int taskTest2Id;
    Task taskTest1;
    Task taskTest2;


    @BeforeEach
    void beforeEach() {
        taskTest1Id = taskManager.createTask(new Task("Задача №1","Задача №1 на проверку сохранения"));
        taskTest1 = taskManager.getTaskByID(taskTest1Id);
        taskTest2Id = taskManager.createTask(new Task("Задача №1.1", "Задача №1.1 на проверку сохранения"));
        taskTest2 = taskManager.getTaskByID(taskTest2Id);
        taskTest2.setId(taskTest1Id);
        taskManager.updateTask(taskTest2);
    }

    @Test
    void shouldSavePreviousAndNewTasksVersions() {

        assertTrue(taskManager.getHistory().contains(taskTest1),
                "История содержит предыдущую версию задачи");
        assertTrue(taskManager.getHistory().contains(taskTest2),
                "История содержит текущую версию задачи");

    }

    @Test
    void shouldAddToHistory() {
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history,
                "История не пустая.");
        assertEquals(2, history.size(),
                "История не пустая.");
        assertTrue(taskManager.getHistory().contains(taskTest1), "История содержит добавленную задачу");
    }

}

