package com.axel.masivo_tiendas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Para tu caso (form + fetch simple), es práctico desactivar CSRF.
            // Más adelante podemos activarlo si querés hacerlo más estricto.
            .csrf(csrf -> csrf.disable())

            // Permitir acceso a todas las rutas sin autenticación
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // Desactivar formulario de login y auth por defecto
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable());

        return http.build();
    }
}
