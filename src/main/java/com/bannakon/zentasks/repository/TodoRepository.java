package com.bannakon.zentasks.repository;

import com.bannakon.zentasks.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // native query
    // มีการ query ซ้ำซ้อนขึ้น
    // ลองเพิ่ม entity
}
