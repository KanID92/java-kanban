package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> subtaskList = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public ArrayList<SubTask> getAllEpicSubtasks() {
        return subtaskList;
    }

    public void addSubtask(SubTask subTask) {
        subtaskList.add(subTask);
    }

    public void setSubtasksList(ArrayList<SubTask> subTasks) {
        this.subtaskList = subTasks;
    }

}
