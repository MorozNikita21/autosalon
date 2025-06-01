package com.autosalon.backend.repository;

import com.autosalon.backend.entity.TestDrive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestDriveRepository extends JpaRepository<TestDrive, Long> {
}
