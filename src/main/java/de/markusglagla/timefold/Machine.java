package de.markusglagla.timefold;

public class Machine {

    private final String id;
    private final String type;

    public Machine(String id, String type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Machine{id='" + id + "', type='" + type + "'}";
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

}
