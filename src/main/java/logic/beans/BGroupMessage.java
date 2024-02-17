package logic.beans;

public class BGroupMessage {
    private Integer senderID;
    private String message;

    //SETTER
    public void setSenderID(Integer senderID) {
        this.senderID = senderID;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //GETTER
    public Integer getSenderID() {
        return senderID;
    }

    public String getMessage() {
        return message;
    }
}
