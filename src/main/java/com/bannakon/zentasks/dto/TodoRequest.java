package com.bannakon.zentasks.dto;

import lombok.Data;

@Data
public class TodoRequest {
    private String title;
    private boolean completed;
}
