package logic.controllers;

import java.io.ObjectOutputStream;

public class ObserverClass {
    protected int id; //generico, sia per User che per Organizer
    protected ObjectOutputStream out;

    public ObserverClass(int id, ObjectOutputStream out) {
        this.id = id;
        this.out = out;
    }
    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public int getObsID() {
        return this.id;
    }

}
