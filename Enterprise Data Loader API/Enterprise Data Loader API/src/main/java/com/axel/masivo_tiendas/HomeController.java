package com.axel.masivo_tiendas;

import com.axel.masivo_tiendas.service.BaseFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// @Controller -> le dice a Spring que esta clase maneja requests web
@Controller
// @RequiredArgsConstructor -> genera un constructor con los "final"
// así Spring puede inyectar BaseFileService sin @Autowired
@RequiredArgsConstructor
public class HomeController {

    // Inyectamos nuestro servicio que lee el Excel
    private final BaseFileService baseFileService;

    // Cuando alguien entra a "/" (http://localhost:8080/)
    @GetMapping("/")
    public String home(Model model) {
        // Le pasamos al HTML (dashboard.html) la lista de tiendas padre
        // "tiendas" va a ser una variable disponible en la plantilla
        model.addAttribute("tiendas", baseFileService.getTiendaPadres());

        // Retornamos el nombre de la vista Thymeleaf (dashboard.html)
        return "dashboard";
    }
}
