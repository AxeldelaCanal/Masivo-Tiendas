package com.axel.masivo_tiendas.service;

import com.axel.masivo_tiendas.model.BaseRow;
import com.axel.masivo_tiendas.util.TimeUtils;
import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BaseFileService {

    // Lista con TODAS las filas del Excel (convertidas a BaseRow)
    private final List<BaseRow> allRows = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(BaseFileService.class);

    // Mapa: "Baez" -> lista de filas de Baez
    private Map<String, List<BaseRow>> rowsByTienda = new HashMap<>();

    // getter para usar desde el controller
    public Map<String, List<BaseRow>> getRowsByTienda() {
        return rowsByTienda;
    }

    // Devuelve solo los nombres de las tiendas padre (ordenados)
    public Set<String> getTiendaPadres() {
        return new TreeSet<>(rowsByTienda.keySet());
    }

    // =====================================================================
    // ESTE MÉTODO SE EJECUTA AUTOMÁTICAMENTE CUANDO ARRANCA LA APLICACIÓN
    // =====================================================================
    @PostConstruct
    public void loadBaseFile() {

        try {
            // 1) Le decimos a Spring que queremos abrir el archivo que está en:
            // src/main/resources/data/masivo_tiendas.xlsx
            ClassPathResource resource =
                    new ClassPathResource("data/masivo_tiendas.xlsx");

            // 2) Abrimos el archivo como InputStream
            try (InputStream is = resource.getInputStream();
                 Workbook workbook = new XSSFWorkbook(is)) {

                // 3) Tomamos la primera hoja del Excel (índice 0)
                Sheet sheet = workbook.getSheetAt(0);

                // 4) La primer fila suele ser el header (nombres de columnas)
                Row headerRow = sheet.getRow(0);

                // 5) Mapeamos columnas de forma flexible:
                //    store_id -> columna X, days -> columna Y, etc.
                Map<String, Integer> colIndex = mapColumns(headerRow);

                if (colIndex.isEmpty()) {
                    log.error("No se encontraron columnas esperadas en el archivo base");
                    return;
                }

                // 6) Recorremos todas las filas (desde la 1, la 0 era el header)
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue; // fila vacía, la salteamos

                    // 7) Leemos cada celda según la columna que detectamos
                    String storeId     = getCellString(row, colIndex.get("store_id"));
                    String day         = getCellString(row, colIndex.get("days"));
                    LocalTime startTime = null;
                    LocalTime endTime = null;
                    Integer startIdx = colIndex.get("start_time");
                    Integer endIdx = colIndex.get("end_time");
                    if (startIdx != null) {
                        Cell c = row.getCell(startIdx);
                        startTime = TimeUtils.fromCell(c);
                    }
                    if (endIdx != null) {
                        Cell c = row.getCell(endIdx);
                        endTime = TimeUtils.fromCell(c);
                    }
                    String tiendaPadre = getCellString(row, colIndex.get("tienda_padre"));
                    String type        = getCellString(row, colIndex.get("type"));

                        // Si faltan datos importantes, no la usamos
                        if (storeId == null || day == null || tiendaPadre == null || type == null) {
                        continue;
                    }

                    // 8) Creamos un objeto BaseRow con los datos de esa fila
                        BaseRow baseRow = BaseRow.builder()
                            .storeId(storeId.trim())
                            .day(day.trim())
                            .startTime(startTime)
                            .endTime(endTime)
                            .tiendaPadre(tiendaPadre.trim())
                            .type(type.trim())
                            .build();

                    allRows.add(baseRow);
                }

                // 9) Agrupamos todas las filas por tienda padre
                rowsByTienda = allRows.stream()
                        .collect(Collectors.groupingBy(BaseRow::getTiendaPadre));

                log.info("Archivo base cargado. Filas totales: {}", allRows.size());
                log.info("Tiendas padre encontradas: {}", rowsByTienda.keySet());
            }

        } catch (Exception e) {
            log.error("Error leyendo archivo masivo_tiendas.xlsx", e);
        }
    }

    // =====================================================================
    // FUNCIONES AUXILIARES
    // =====================================================================

    // Mapea las columnas según el texto del header.
    // Ej: si encuentra "store_id" o "Store Id" lo normaliza a "storeid"
    // y guarda el índice de esa columna.
    private Map<String, Integer> mapColumns(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        if (headerRow == null) return map;

        for (Cell cell : headerRow) {
            String raw = cell.getStringCellValue();
            if (raw == null) continue;

            // normalizamos el texto (minúsculas, sin espacios, sin acentos)
            String normalized = normalize(raw);

            if (normalized.startsWith("storeid")) {
                map.put("store_id", cell.getColumnIndex());
            } else if (normalized.startsWith("days") || normalized.startsWith("dia")) {
                map.put("days", cell.getColumnIndex());
            } else if (normalized.startsWith("starttime") || normalized.startsWith("inicio")) {
                map.put("start_time", cell.getColumnIndex());
            } else if (normalized.startsWith("endtime") || normalized.startsWith("fin")) {
                map.put("end_time", cell.getColumnIndex());
            } else if (normalized.startsWith("tiendapadre")) {
                map.put("tienda_padre", cell.getColumnIndex());
            } else if (normalized.equals("type") || normalized.equals("tipo")) {
                map.put("type", cell.getColumnIndex());
            }
        }
        return map;
    }

    // Normaliza texto: lo pasa a minúsculas, saca espacios, guiones bajos y acentos
    private String normalize(String s) {
        return s.toLowerCase()
                .replace(" ", "")
                .replace("_", "")
                .replace("á","a").replace("é","e")
                .replace("í","i").replace("ó","o").replace("ú","u");
    }

    // Convierte una celda cualquiera a String (tanto si es número como texto)
    private String getCellString(Row row, Integer idx) {
        if (idx == null) return null;
        Cell cell = row.getCell(idx);
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            // Por si store_id viene numérico
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return cell.toString();
    }
}
