package com.axel.masivo_tiendas.controller;

import com.axel.masivo_tiendas.model.BaseRow;
import com.axel.masivo_tiendas.service.BaseFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tienda")
public class TiendaController {

    private final BaseFileService baseFileService;

    @GetMapping("/{nombre}")
    public Map<String, List<BaseRow>> getTienda(@PathVariable String nombre) {

        Map<String, List<BaseRow>> all = baseFileService.getRowsByTienda();

        // Buscamos EXACTAMENTE la tienda
        List<BaseRow> rows = all.getOrDefault(nombre, new ArrayList<>());

        // Agrupamos por type (turbo, turbo_express, etc.)
        Map<String, List<BaseRow>> porType = new TreeMap<>();

        for (BaseRow r : rows) {
            porType.computeIfAbsent(r.getType(), k -> new ArrayList<>()).add(r);
        }

        return porType; // JSON: type → lista de días/horarios
    }
}
