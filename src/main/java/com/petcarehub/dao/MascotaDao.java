package com.petcarehub.dao;

import com.petcarehub.domain.Mascota;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MascotaDao extends JpaRepository<Mascota, Long> {
    List<Mascota> findByActivoTrue();
}
