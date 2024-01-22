package model;

import java.util.ArrayList;


public class Epic extends Task {

    protected ArrayList<Integer> subtaskIDs = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getAllEpicSubtasksIds() {
        return subtaskIDs;
    }

    public void addSubtaskIdToEpic(int id) {
        subtaskIDs.add(id);
    }

    public void removeSubtaskFromEpic(int id) {
        for (int subtaskId : subtaskIDs) {
            if (subtaskId == id){
                subtaskIDs.remove(id);
            }
        }

    }

    public void removeAllSubtaskFromEpic() {
        subtaskIDs.clear();
    }

}

