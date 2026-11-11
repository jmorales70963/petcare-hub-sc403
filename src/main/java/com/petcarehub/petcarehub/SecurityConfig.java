package com.petcarehub.petcarehub;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ComponentScan(basePackages = "com.petcarehub")
@EntityScan(basePackages = "com.petcarehub.domain")
@EnableJpaRepositories(basePackages = "com.petcarehub.dao")
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/", "/home", "/productos/**", "/servicios/**",
                                 "/carrito/**", "/registro/**", "/css/**", "/js/**", "/img/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/especialista/**").hasAnyRole("ESPECIALISTA", "ADMIN")
                .requestMatchers("/cliente/**").hasAnyRole("CLIENTE", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                .loginPage("/login")
                .usernameParameter("correo")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }
    
    @Bean
    public org.springframework.boot.CommandLineRunner sincronizarClavesSemilla(
            com.petcarehub.dao.UsuarioDao usuarioDao,
            PasswordEncoder passwordEncoder) {
        return args -> {
            String hashReal = passwordEncoder.encode("12345678A");
            for (com.petcarehub.domain.Usuario u : usuarioDao.findAll()) {
                if (u.getCorreo().endsWith("@petcarehub.cr") || u.getCorreo().endsWith("@ufide.ac.cr")) {
                    u.setPassword(hashReal);
                    usuarioDao.save(u);
                }
            }
        };
    }
}