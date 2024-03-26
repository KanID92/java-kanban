package service;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    void setBeforeEach() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        super.setBeforeEach();
    }

}