package com.bannakon.zentasks.dto;

import lombok.Data;

@Data
public class TodoResponse {
    private Long id;
    private String title;
    private boolean completed;

    public TodoResponse(Long id, String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }
}
