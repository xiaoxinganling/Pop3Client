package com.mail;

public class Mail {
    String receivedFrom;
    String subject;
    String content;

    public String getReceivedFrom() {
        return receivedFrom;
    }

    public void setReceivedFrom(String receivedFrom) {
        this.receivedFrom = receivedFrom;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "From: "+receivedFrom+"\n"+
                "Subject: "+subject+"\n"+
                "Content: \n"+content;
    }
}
