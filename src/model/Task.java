package model;

import java.util.Objects;

public class Task {

    protected String name;
    protected String description;
    protected int id;
    protected Progress progress;



    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.progress = Progress.NEW;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && progress == task.progress;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, progress);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", progress=" + progress +
                '}';
    }
}


