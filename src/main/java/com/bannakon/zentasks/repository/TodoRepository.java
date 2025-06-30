package com.bannakon.zentasks.repository;

import com.bannakon.zentasks.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByCompleted(boolean completed);
}
