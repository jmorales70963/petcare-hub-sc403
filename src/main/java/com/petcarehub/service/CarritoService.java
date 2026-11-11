package com.petcarehub.service;

import com.petcarehub.dao.ProductoDao;
import com.petcarehub.domain.ItemCarrito;
import com.petcarehub.domain.Producto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

@Service
@SessionScope
public class CarritoService {

    private final List<ItemCarrito> items = new ArrayList<>();

    @Autowired
    private ProductoDao productoDao;

    public List<ItemCarrito> getItems() {
        return items;
    }

    public void agregarProducto(Producto producto, int cantidad) {
        for (ItemCarrito item : items) {
            if (item.getProducto().getIdProducto().equals(producto.getIdProducto())) {
                int nuevaCantidad = Math.min(item.getCantidad() + cantidad, producto.getExistencias());
                item.setCantidad(nuevaCantidad);
                return;
            }
        }
        if (producto.getExistencias() >= cantidad && cantidad > 0) {
            items.add(new ItemCarrito(producto, cantidad));
        }
    }

    public void actualizarCantidad(Long idProducto, int delta) {
        items.removeIf(item -> {
            if (item.getProducto().getIdProducto().equals(idProducto)) {
                int nueva = item.getCantidad() + delta;
                if (nueva <= 0) return true;
                if (nueva <= item.getProducto().getExistencias()) {
                    item.setCantidad(nueva);
                }
            }
            return false;
        });
    }

    public void eliminarProducto(Long idProducto) {
        items.removeIf(i -> i.getProducto().getIdProducto().equals(idProducto));
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getIvaIncluido() {
        return getTotal().multiply(new BigDecimal("0.13")).setScale(0, RoundingMode.HALF_UP);
    }

    public int getCantidadTotalItems() {
        return items.stream().mapToInt(ItemCarrito::getCantidad).sum();
    }

    // HU-09 y HU-10: Rebaja transaccional ACID de stock
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean procesarCheckout() {
        for (ItemCarrito item : items) {
            Producto bdProd = productoDao.findById(item.getProducto().getIdProducto()).orElse(null);
            if (bdProd == null || bdProd.getExistencias() < item.getCantidad()) {
                return false;
            }
            bdProd.setExistencias(bdProd.getExistencias() - item.getCantidad());
            productoDao.save(bdProd);
        }
        items.clear();
        return true;
    }
}
