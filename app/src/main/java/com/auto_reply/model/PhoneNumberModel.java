package com.auto_reply.model;

import java.io.Serializable;

public class PhoneNumberModel implements Serializable {
    private String number;
    private long timeMillis;
    private String inComingMessage = "";
    private String outGoingMessage = "";
    private String missedMessage = "";

    private boolean isInComingMessage;
    private boolean isOutGoingMessage;
    private boolean isMissedMessage;

    public String getInComingMessage() {
        return inComingMessage;
    }

    public void setInComingMessage(String inComingMessage) {
        this.inComingMessage = inComingMessage;
    }

    public String getOutGoingMessage() {
        return outGoingMessage;
    }

    public void setOutGoingMessage(String outGoingMessage) {
        this.outGoingMessage = outGoingMessage;
    }

    public String getMissedMessage() {
        return missedMessage;
    }

    public void setMissedMessage(String missedMessage) {
        this.missedMessage = missedMessage;
    }

    public boolean isInComingMessage() {
        return isInComingMessage;
    }

    public void setInComingMessage(boolean inComingMessage) {
        isInComingMessage = inComingMessage;
    }

    public boolean isOutGoingMessage() {
        return isOutGoingMessage;
    }

    public void setOutGoingMessage(boolean outGoingMessage) {
        isOutGoingMessage = outGoingMessage;
    }

    public boolean isMissedMessage() {
        return isMissedMessage;
    }

    public void setMissedMessage(boolean missedMessage) {
        isMissedMessage = missedMessage;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }
}
