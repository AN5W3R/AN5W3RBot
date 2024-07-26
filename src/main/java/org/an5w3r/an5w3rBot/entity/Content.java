package org.an5w3r.an5w3rBot.entity;

import lombok.Data;

@Data
public class Content {
    private String role;
    private String text;

    public Content() {
    }

    public Content(String role, String text) {
        this.role = role;
        this.text = text;
    }
}
