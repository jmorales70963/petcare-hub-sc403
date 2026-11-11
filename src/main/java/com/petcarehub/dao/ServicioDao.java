package com.petcarehub.dao;

import com.petcarehub.domain.Servicio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioDao extends JpaRepository<Servicio, Long> {
    List<Servicio> findByActivoTrue();
}
