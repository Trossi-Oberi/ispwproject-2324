package logic.model;

import java.io.ObjectOutputStream;

public abstract class ObserverClass {
    protected int id; //generico, sia per User che per Organizer
    protected ObjectOutputStream out;

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }
    public void setObsID(Integer id){
        this.id = id;
    }

    public int getObsID() {
        return this.id;
    }

    public abstract void update(Object o);

}
