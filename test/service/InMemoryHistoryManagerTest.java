package service;

import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    void shouldSavePreviousTaskVersion() {
        int taskTest1Id;
        int taskTest2Id;
        taskTest1Id = taskManager.createTask(new Task("Задача №1","Задача №1 на проверку сохранения"));
        Task taskTest1 = taskManager.getTaskByID(taskTest1Id);
        taskTest2Id = taskManager.createTask(new Task("Задача №1.1", "Задача №1.1 на проверку сохранения"));
        Task taskTest2 = taskManager.getTaskByID(taskTest2Id);
        taskTest2.setId(taskTest1Id);
        taskManager.updateTask(taskTest2);

        Assertions.assertTrue(taskManager.getHistory().contains(taskTest1));


    }

}