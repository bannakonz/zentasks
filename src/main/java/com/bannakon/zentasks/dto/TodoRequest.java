package com.bannakon.zentasks.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class TodoRequest {

    @NonNull
    @NotBlank(message = "Title is not empty")
    private String title;
    private boolean completed;
}
