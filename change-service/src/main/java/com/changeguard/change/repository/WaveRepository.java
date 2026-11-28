package com.changeguard.change.repository;

import com.changeguard.change.domain.Wave;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WaveRepository extends JpaRepository<Wave, Long> {
    List<Wave> findByChangeIdOrderByPercentageAsc(Long changeId);
}