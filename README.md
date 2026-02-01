# ⚡ Enterprise Data Loader API (Excel-to-Rest Adapter)

> **Microservicio de transformación y orquestación de datos operativos.**
> Ingesta configuraciones complejas en Excel (.xlsx) y las expone como una API REST de alta velocidad (In-Memory Access) para sistemas de Quick-Commerce.

![Java 21](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.x-green?logo=springboot)
![Apache POI](https://img.shields.io/badge/Excel_Processing-Apache_POI-blue)
![Security](https://img.shields.io/badge/Spring_Security-Configured-red)

---

## 💡 El Problema de Negocio
En operaciones de Quick-Commerce, los equipos comerciales gestionan horarios y configuraciones de tiendas ("Dark Stores") utilizando hojas de cálculo masivas (Excel).
Sincronizar estos Excels con las plataformas tecnológicas suele ser un proceso manual propenso a errores.

## 🛠️ La Solución Técnica
Este servicio actúa como un **Middleware de Adaptación**:
1.  **Ingesta Automática:** Lee y procesa archivos `.xlsx` complejos al iniciar el servicio.
2.  **Normalización Dinámica:** Un algoritmo inteligente detecta columnas independientemente de variaciones en el nombre (ej: "Store ID" vs "store_id").
3.  **High-Performance Serving:** Almacena los datos procesados en memoria (Heap), permitiendo tiempos de respuesta de **<10ms** para consultas de operación en tiempo real.

---

## 🏗️ Arquitectura del Sistema

### Tech Stack
* **Core:** Java 21 (LTS) & Spring Boot 3.
* **Data Processing:** Apache POI 5.x (Para parsing avanzado de Office Open XML).
* **API Layer:** Spring Web MVC.
* **Security:** Spring Security (CSRF disabled para APIs internas).
* **Tools:** Lombok (Boilerplate reduction), Maven.

### Patrones de Diseño Detectados
* **Singleton Service:** `BaseFileService` mantiene el estado único de los datos en memoria.
* **Strategy / Normalizer:** Lógica de mapeo de columnas flexible en `mapColumns()` para tolerar errores de entrada humana en los Excels.
* **Eager Loading:** Uso de `@PostConstruct` para garantizar que los datos estén validados y listos antes de aceptar la primera petición HTTP.

---

## 🚀 Instalación y Ejecución

### Prerrequisitos
* JDK 21 instalado.
* Maven 3.8+.

### 1. Clonar

`git clone [https://github.com/AxeldelaCanal/Masivo-Tiendas.git](https://github.com/AxeldelaCanal/Masivo-Tiendas.git)
cd masivo-tiendas`

### 2. Ejecutar con Docker (Recomendado)
El proyecto incluye un docker-compose.yml que levanta la API y la base de datos MySQL automáticamente.

`docker-compose up --build`

### 3. Ejecutar manualmente (Dev Mode)
Si prefieres correrlo localmente:

1. Configura tu base de datos en `application.properties`.
2. Ejecuta:
   `mvn spring-boot:run`
