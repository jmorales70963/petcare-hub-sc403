package com.petcarehub.service;

import com.petcarehub.dao.UsuarioDao;
import com.petcarehub.domain.Rol;
import com.petcarehub.domain.Usuario;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioDao usuarioDao;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioDao.findByCorreo(correo);
        if (usuario == null || !usuario.isActivo()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + correo);
        }
        List<GrantedAuthority> roles = new ArrayList<>();
        for (Rol rol : usuario.getRoles()) {
            roles.add(new SimpleGrantedAuthority(rol.getNombre()));
        }
        return new User(usuario.getCorreo(), usuario.getPassword(), roles);
    }
}