package com.bannakon.zentasks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoRequest {
    @NotBlank(message = "{todo.title.notblank}")
    @Size(min = 2, max = 255, message = "{todo.title.size}")
    private String title;
    private boolean completed;
}
