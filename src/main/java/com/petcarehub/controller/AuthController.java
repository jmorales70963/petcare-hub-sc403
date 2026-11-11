package com.petcarehub.controller;

import com.petcarehub.dao.RolDao;
import com.petcarehub.dao.UsuarioDao;
import com.petcarehub.domain.Rol;
import com.petcarehub.domain.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private RolDao rolDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String formularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    @PostMapping("/registro/guardar")
    public String registrarCliente(@ModelAttribute Usuario usuario, RedirectAttributes flash, Model model) {
        if (usuarioDao.existsByCorreo(usuario.getCorreo())) {
            model.addAttribute("error", "El correo electrónico ya se encuentra registrado.");
            return "auth/registro";
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setActivo(true);
        Usuario guardado = usuarioDao.save(usuario);

        Rol rolCliente = new Rol();
        rolCliente.setNombre("ROLE_CLIENTE");
        rolCliente.setIdUsuario(guardado.getIdUsuario());
        rolDao.save(rolCliente);

        flash.addFlashAttribute("exito", "Cuenta creada con éxito. Ahora puedes iniciar sesión.");
        return "redirect:/login";
    }
}
