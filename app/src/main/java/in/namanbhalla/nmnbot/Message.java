package in.namanbhalla.nmnbot;

import java.util.Date;

public class Message {
    private String message;
    private Date createdAt;
    private String type;

    public Date getCreatedAt() {
        return createdAt;
    }


    public String getSender() {
        return type;
    }


    public String getMessage() {
        return message;
    }

    public Message(String message, String type){
        this.createdAt = new Date();
        this.message = message;
        this.type = type;
    }

}
