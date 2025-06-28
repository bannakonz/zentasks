package com.bannakon.zentasks.entity;

import jakarta.persistence.*; // JPA annotations
import lombok.Getter; // lombox
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
    private boolean completed; // ไม่จำเป็นต้องครบตาม table DB

    public Todo() {
        // No-args constructor needed by JPA
    }

    public Todo(Long id, String title, boolean completed) {
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
