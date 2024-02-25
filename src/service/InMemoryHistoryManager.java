package service;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> lastTasksList = new LinkedList<>();

    @Override
    public void add(Task task) { // добавление задачи в лист истории запросов задач (в конец)
        if (task == null) {
            return;
        } else if (lastTasksList.size() == 10) {
            lastTasksList.removeFirst();
        }
        lastTasksList.addLast(task);

    }


    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(lastTasksList);
    }

}
