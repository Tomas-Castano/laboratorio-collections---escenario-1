package org.modelo;

import java.util.*;
import java.text.SimpleDateFormat;

public class SistemaRegistroPacientes {

    private LinkedHashMap<String, Paciente> pacientes;
    private HashSet<String> documentosRegistrados;

    public SistemaRegistroPacientes() {
        this.pacientes = new LinkedHashMap<>();
        this.documentosRegistrados = new HashSet<>();
    }

    public boolean registrarPaciente(String documento, String nombre) {
        if (documentosRegistrados.contains(documento)) {
            return false; // duplicado
        }
        Paciente nuevo = new Paciente(documento, nombre);
        pacientes.put(documento, nuevo);
        documentosRegistrados.add(documento);
        return true;
    }

    public Paciente buscarPorDocumento(String documento) {
        return pacientes.get(documento);
    }

    public void listarPacientes() {
        if (pacientes.isEmpty()) {
            System.out.println("No hay pacientes registrados.");
            return;
        }
        System.out.println("\n--- Lista de pacientes (orden de llegada) ---");
        int turno = 1;
        for (Paciente p : pacientes.values()) {
            System.out.println(turno++ + ". " + p);
        }
    }

    public int totalPacientes() {
        return pacientes.size();
    }

    // FASE 4 — Medición de rendimiento
    static void medirRendimiento(int cantidad) {
        SistemaRegistroPacientes sistema = new SistemaRegistroPacientes();
        Runtime runtime = Runtime.getRuntime();

        // Forzar GC antes de medir para una lectura más limpia
        runtime.gc();
        long memoriaAntes = runtime.totalMemory() - runtime.freeMemory();
        long tiempoInicio = System.nanoTime();

        // --- Inserción ---
        for (int i = 0; i < cantidad; i++) {
            sistema.registrarPaciente("DOC" + i, "Paciente " + i);
        }

        // --- Búsqueda (últimos 100 registros) ---
        int busquedas = Math.min(100, cantidad);
        for (int i = cantidad - busquedas; i < cantidad; i++) {
            sistema.buscarPorDocumento("DOC" + i);
        }

        // --- Intento de duplicado ---
        sistema.registrarPaciente("DOC0", "Duplicado");

        long tiempoFin     = System.nanoTime();
        long memoriaDespues = runtime.totalMemory() - runtime.freeMemory();

        double tiempoMs  = (tiempoFin - tiempoInicio) / 1_000_000.0;
        long   memoriaMB = Math.max(0, (memoriaDespues - memoriaAntes) / (1024 * 1024));

        System.out.printf("  %-10s | Tiempo: %8.2f ms | Memoria aprox: %4d MB | Registros: %d%n",
                formatear(cantidad), tiempoMs, memoriaMB, sistema.totalPacientes());
    }

    static String formatear(int n) {
        if (n >= 1_000_000) return (n / 1_000_000) + "M";
        if (n >= 1_000)     return (n / 1_000) + ".000";
        return String.valueOf(n);
    }

    // ---------------------------------------------------------------
    // Main
    // ---------------------------------------------------------------
    public static void main(String[] args) {

        // --- Demostración funcional ---
        System.out.println("+------------------------------------------+");
        System.out.println("|   SISTEMA DE REGISTRO DE PACIENTES        |");
        System.out.println("+------------------------------------------+");

        SistemaRegistroPacientes sistema = new SistemaRegistroPacientes();

        sistema.registrarPaciente("1001", "Ana Garcia");
        sistema.registrarPaciente("1002", "Carlos Perez");
        sistema.registrarPaciente("1003", "Maria Lopez");

        System.out.println("\n[Intento de duplicado]");
        boolean ok = sistema.registrarPaciente("1001", "Intento duplicado");
        System.out.println("Resultado: " + (ok ? "Registrado" : "Rechazado — documento ya existe"));

        sistema.listarPacientes();

        System.out.println("\n[Busqueda por documento]");
        Paciente p = sistema.buscarPorDocumento("1002");
        System.out.println("Buscando doc 1002: " + (p != null ? p : "No encontrado"));
        p = sistema.buscarPorDocumento("9999");
        System.out.println("Buscando doc 9999: " + (p != null ? p : "No encontrado"));

        // --- FASE 4: Medición con distintos volúmenes ---
        System.out.println("\n+--------------------------------------------------------------+");
        System.out.println("|         FASE 4 — Medicion de rendimiento                     |");
        System.out.println("+--------------------------------------------------------------+");
        System.out.printf("  %-10s | %-22s | %-20s | %s%n",
                "Volumen", "Tiempo total", "Memoria usada", "Registros");
        System.out.println("  ----------|------------------------|----------------------|----------");

        medirRendimiento(100);
        medirRendimiento(1_000);
        medirRendimiento(10_000);
        medirRendimiento(100_000);
    }

    // ---------------------------------------------------------------
    // Clase interna Paciente
    // ---------------------------------------------------------------
    static class Paciente {
        String documento;
        String nombre;
        String horaLlegada;

        public Paciente(String documento, String nombre) {
            this.documento    = documento;
            this.nombre       = nombre;
            this.horaLlegada  = new SimpleDateFormat("HH:mm:ss").format(new Date());
        }

        @Override
        public String toString() {
            return nombre + " | Doc: " + documento + " | Llegada: " + horaLlegada;
        }
    }
}