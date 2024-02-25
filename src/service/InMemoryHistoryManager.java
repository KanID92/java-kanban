package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task data;
        Node next;
        Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    private final Map<Integer, Node> history = new HashMap<>();

    Node first;
    Node last;

    void linkLast(Task task) {
        final Node node = last;
        final Node newNode = new Node(node, task, null);
        last = newNode;
        if (node == null) {
            first = newNode;
        } else {
            node.next = newNode;
        }
        history.put(task.getId(), newNode);
    }

    @Override
    public void add(Task task) {
        Integer taskID = task.getId();
        Node node = history.get(taskID);
        if (node != null) {
            removeNode(node);
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        removeNode(node);
    }

    public List<Task> getAll() {
        ArrayList<Task> historyList = new ArrayList<>();
        Node current = first;
        while (current != null) {
            historyList.add(current.data);
            current = current.next;
        }
        return historyList;
    }

    public List<Task> getHistory() {
        return getAll();
    }


    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                last = node.prev;
            }
        } else {
            node.next.prev = null;
            first = node.next;
        }
        history.remove(node.data.getId());
    }

    public Task getLast() {
        return last.data;
    }

}


