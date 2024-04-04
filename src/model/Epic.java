package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;


public class Epic extends Task {

    protected LocalDateTime endTime = null;

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
        subtaskIDs.remove(Integer.valueOf(id));
    }

    public void removeAllSubtaskFromEpic() {
        subtaskIDs.clear();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", progress=" + progress +
                ", subtaskIds=" + subtaskIDs +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, progress, subtaskIDs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return id == epic.id
                && Objects.equals(name, epic.name)
                && Objects.equals(description, epic.description)
                && progress == epic.progress
                && Objects.equals(subtaskIDs, epic.subtaskIDs)
                && Objects.equals(startTime, epic.startTime)
                && Objects.equals(duration, epic.duration)
                && Objects.equals(endTime, epic.endTime);
    }
}

