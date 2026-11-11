package com.petcarehub.controller;

import com.petcarehub.dao.CategoriaDao;
import com.petcarehub.dao.ProductoDao;
import com.petcarehub.domain.Producto;
import com.petcarehub.service.CarritoService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CatalogoController {

    @Autowired
    private ProductoDao productoDao;

    @Autowired
    private CategoriaDao categoriaDao;

    @Autowired
    private CarritoService carritoService;

    @ModelAttribute("totalCarrito")
    public int totalCarrito() {
        return carritoService.getCantidadTotalItems();
    }

    @GetMapping({"/", "/home"})
    public String inicio(Model model) {
        model.addAttribute("categorias", categoriaDao.findByActivoTrue());
        model.addAttribute("productos", productoDao.findByActivoTrue());
        return "index";
    }

    @GetMapping("/productos")
    public String listarCatalogo(
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) String q,
            Model model) {

        List<Producto> productos;
        if (q != null && !q.trim().isEmpty()) {
            productos = productoDao.buscarPorTermino(q.trim());
            model.addAttribute("q", q);
        } else {
            productos = productoDao.filtrarCatalogo(especie, idCategoria, precioMax);
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaDao.findByActivoTrue());
        model.addAttribute("especieSeleccionada", especie);
        model.addAttribute("categoriaSeleccionada", idCategoria);
        model.addAttribute("precioMax", precioMax != null ? precioMax : new BigDecimal("50000"));
        return "catalogo/listado";
    }

    @GetMapping("/productos/{id}")
    public String detalleProducto(@PathVariable("id") Long idProducto, Model model) {
        Producto producto = productoDao.findById(idProducto).orElse(null);
        if (producto == null) {
            return "redirect:/productos";
        }
        model.addAttribute("producto", producto);
        return "catalogo/detalle";
    }
}
