package com.petcarehub.dao;

import com.petcarehub.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaDao extends JpaRepository<Categoria, Long> {
    List<Categoria> findByActivoTrue();
}