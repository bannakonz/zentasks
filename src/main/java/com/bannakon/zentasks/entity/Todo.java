package com.bannakon.zentasks.entity;

import jakarta.persistence.*; // JPA annotations
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity // Marks this class as a JPA entity
@Table(name = "todos")
public class Todo {

    @Id // Marks 'id' as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;
    private String title;
    private boolean completed;

    public Todo() { // *D
        // No-args constructor needed by JPA,
    }

    public Todo(Long id, String title, boolean completed) { // ถ้าเพิ่ม constructor ใหม่แล้ว, default constructor จะหายไป *D
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                '}';
    }
}
