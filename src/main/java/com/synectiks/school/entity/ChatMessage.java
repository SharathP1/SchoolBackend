package com.synectiks.school.entity;

public class ChatMessage {

    private MessageType type;
    private String sender;
    private String content;
    private String role;

    public enum MessageType {
        JOIN, LEAVE, CHAT
    }

    private ChatMessage(Builder builder) {
        this.type = builder.type;
        this.sender = builder.sender;
        this.content = builder.content;
    }

    public static class Builder {
        private MessageType type;
        private String sender;
        private String content;

        public Builder type(MessageType type) {
            this.type = type;
            return this;
        }

        public Builder sender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public ChatMessage build() {
            return new ChatMessage(this);
        }
    }

    // Getters and setters
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

	public Object getRole() {
		// TODO Auto-generated method stub
		return null;
	}
}
