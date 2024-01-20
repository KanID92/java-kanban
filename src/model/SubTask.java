package model;;

public class SubTask extends Task {


    private Epic epicAffilation;

    public SubTask(int id, String name, String description) {
        super(id, name, description);
    }

    public Epic getEpicAffilation() {
        return epicAffilation;
    }

    public void setEpicAffilation(Epic epic) {
        this.epicAffilation = epic;
    }

}
