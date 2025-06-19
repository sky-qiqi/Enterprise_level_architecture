package com.project.travel.domain;

import java.util.List;

public class ChatRequest {
    private String model = "doubao-1-5-thinking-vision-pro-250428";
    private List<Message> messages;

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
}

class Message {
    private String role = "user";
    private List<Content> content;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public List<Content> getContent() { return content; }
    public void setContent(List<Content> content) { this.content = content; }
}

class Content {
    private String type;
    private String text;
    private String image_url; // Changed from ImageUrl to String

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }
}