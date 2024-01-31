package service;
import model.Task;
import java.util.List;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> last10TasksList = new LinkedList<>();

    @Override
    public void add(Task task) { // добавление задачи в лист истории запросов задач (в конец)
            if (last10TasksList.size() == 10) {
                last10TasksList.removeFirst();
            }
            last10TasksList.addLast(task);
        }


    @Override
    public List<Task> getHistory() {
        return last10TasksList;
    }

}
