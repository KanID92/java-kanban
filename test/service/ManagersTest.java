package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    void shouldGaveTaskManagerInstance() {
        Assertions.assertEquals((new InMemoryTaskManager(new InMemoryHistoryManager())).getClass(), taskManager.getClass());
    }


}