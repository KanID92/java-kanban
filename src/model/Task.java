package model;

public class Task {

    private String name;


    private String description;
    private final int id;
    private Progress progress;



    public Task(int id, String name, String description) {
        this.id = id;
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

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

}


