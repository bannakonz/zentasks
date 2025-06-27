package com.bannakon.zentasks.repository;

import com.bannakon.zentasks.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

}
