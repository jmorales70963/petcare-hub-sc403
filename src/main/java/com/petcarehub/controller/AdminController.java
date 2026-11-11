package com.petcarehub.controller;

import com.petcarehub.dao.CategoriaDao;
import com.petcarehub.dao.ProductoDao;
import com.petcarehub.domain.Producto;
import com.petcarehub.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

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

    @GetMapping({"/dashboard", "/productos"})
    public String dashboard(Model model) {
        model.addAttribute("productos", productoDao.findAll());
        model.addAttribute("categorias", categoriaDao.findByActivoTrue());
        model.addAttribute("stockCritico", productoDao.contarStockCritico());
        model.addAttribute("nuevoProducto", new Producto());
        return "admin/dashboard";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(@ModelAttribute Producto producto, RedirectAttributes flash) {
        producto.setActivo(true);
        productoDao.save(producto);
        flash.addFlashAttribute("mensaje", "Inventario actualizado correctamente en MySQL.");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/productos/estado/{id}")
    public String cambiarEstado(@PathVariable("id") Long idProducto) {
        Producto p = productoDao.findById(idProducto).orElse(null);
        if (p != null) {
            p.setActivo(!p.isActivo());
            productoDao.save(p);
        }
        return "redirect:/admin/dashboard";
    }
}
