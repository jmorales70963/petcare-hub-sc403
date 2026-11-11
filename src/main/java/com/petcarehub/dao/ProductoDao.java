package com.petcarehub.dao;

import com.petcarehub.domain.Producto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoDao extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    // HU-05: Filtrado multicriterio por especie, categoria y precio maximo
    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "AND (:especie IS NULL OR :especie = '' OR p.especie = :especie OR p.especie = 'Ambos') " +
           "AND (:idCategoria IS NULL OR p.categoria.idCategoria = :idCategoria) " +
           "AND (:precioMax IS NULL OR p.precio <= :precioMax)")
    List<Producto> filtrarCatalogo(
            @Param("especie") String especie,
            @Param("idCategoria") Long idCategoria,
            @Param("precioMax") BigDecimal precioMax);

    // HU-06: Busqueda de articulos por nombre, marca o SKU
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.marca) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Producto> buscarPorTermino(@Param("q") String q);

    // HU-18: Conteo de productos con stock critico (< 5 unidades)
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true AND p.existencias < 5")
    long contarStockCritico();
}