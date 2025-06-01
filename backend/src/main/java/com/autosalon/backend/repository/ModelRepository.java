package com.autosalon.backend.repository;

import com.autosalon.backend.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<Model, Long> {
}
