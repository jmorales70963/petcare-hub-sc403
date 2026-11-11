package com.petcarehub.controller;

import com.petcarehub.dao.MascotaDao;
import com.petcarehub.dao.ServicioDao;
import com.petcarehub.domain.Mascota;
import com.petcarehub.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MascotaController {

    @Autowired
    private MascotaDao mascotaDao;

    @Autowired
    private ServicioDao servicioDao;

    @Autowired
    private CarritoService carritoService;

    @ModelAttribute("totalCarrito")
    public int totalCarrito() {
        return carritoService.getCantidadTotalItems();
    }

    @GetMapping({"/servicios", "/cliente/mascotas"})
    public String misMascotasYCitas(Model model) {
        model.addAttribute("mascotas", mascotaDao.findByActivoTrue());
        model.addAttribute("servicios", servicioDao.findByActivoTrue());
        model.addAttribute("nuevaMascota", new Mascota());
        return "mascotas/mis-mascotas";
    }

    @PostMapping("/cliente/mascotas/guardar")
    public String guardarMascota(@ModelAttribute Mascota mascota, RedirectAttributes flash) {
        mascota.setIdUsuario(2L);
        mascota.setActivo(true);
        mascotaDao.save(mascota);
        flash.addFlashAttribute("mensaje", "Mascota registrada exitosamente en el expediente digital.");
        return "redirect:/servicios";
    }
}
