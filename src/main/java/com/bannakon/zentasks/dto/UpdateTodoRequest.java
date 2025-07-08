package com.bannakon.zentasks.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UpdateTodoRequest {
    @Size(min = 1, max = 255, message = "Title cannot be empty")
    private String title;

    private Boolean completed;

    public UpdateTodoRequest(String title, Boolean completed) {
        this.title = title;
        this.completed = completed;
    }
}
