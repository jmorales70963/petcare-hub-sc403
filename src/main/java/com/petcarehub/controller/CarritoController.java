package com.petcarehub.controller;

import com.petcarehub.dao.ProductoDao;
import com.petcarehub.domain.Producto;
import com.petcarehub.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoDao productoDao;

    @ModelAttribute("totalCarrito")
    public int totalCarrito() {
        return carritoService.getCantidadTotalItems();
    }

    @GetMapping("")
    public String verCarrito(Model model) {
        model.addAttribute("items", carritoService.getItems());
        model.addAttribute("total", carritoService.getTotal());
        model.addAttribute("iva", carritoService.getIvaIncluido());
        return "carrito/vista";
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(
            @RequestParam("idProducto") Long idProducto,
            @RequestParam(value = "cantidad", defaultValue = "1") int cantidad) {
        Producto prod = productoDao.findById(idProducto).orElse(null);
        if (prod != null) {
            carritoService.agregarProducto(prod, cantidad);
        }
        return "redirect:/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizarCantidad(
            @RequestParam("idProducto") Long idProducto,
            @RequestParam("delta") int delta) {
        carritoService.actualizarCantidad(idProducto, delta);
        return "redirect:/carrito";
    }

    @PostMapping("/confirmar")
    public String confirmarCompra(RedirectAttributes flash) {
        boolean exito = carritoService.procesarCheckout();
        if (exito) {
            flash.addFlashAttribute("exito", "¡Orden #PCH-202689 procesada con éxito! El stock ha sido actualizado en MySQL.");
        } else {
            flash.addFlashAttribute("error", "Stock insuficiente en bodega para completar la transacción.");
        }
        return "redirect:/carrito";
    }
}
