package com.changeguard.change.repository;

import com.changeguard.change.domain.Change;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeRepository extends JpaRepository<Change, Long> {}