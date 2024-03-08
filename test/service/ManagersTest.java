package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    void shouldGaveTaskManagerInstance() {
        Assertions.assertEquals((new InMemoryTaskManager(new InMemoryHistoryManager())).getClass(), taskManager.getClass());
    }


}