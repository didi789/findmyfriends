package com.sindj.findmyfriends.chat;

/**
 * Created by nirel on 05/09/2017.
 */

public class FriendlyMessage {

    private String text;
    private String key;
    private String displayName;
    private Long time;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String displayName, String key ,String text, Long time) {
        this.displayName = displayName;
        this.text = text;
        this.time = time;
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
