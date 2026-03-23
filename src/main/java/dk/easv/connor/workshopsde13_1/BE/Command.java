package dk.easv.connor.workshopsde13_1.BE;

public class Command {
    private int id;
    private String name;
    private String description;

    public Command(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
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
}
