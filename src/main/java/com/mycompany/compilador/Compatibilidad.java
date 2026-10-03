package com.mycompany.compilador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

/**
 * Matriz de compatibilidad de tipos (resources/Matriz Compatibilidad.csv).
 *
 * Los tipos se indexan igual que las columnas del CSV:
 * 0 Bin, 1 Dec, 2 Oct, 3 Hex, 4 Real, 5 Exp, 6 Cadena, 7 Boolean, 8 Variant.
 *
 * Valores de la matriz: mayor a 500 = error, negativo = tipo resultante
 * (-1 Bin ... -8 Boolean), 0 = Variant.
 */
public class Compatibilidad {

    public static final int VARIANT = 8;

    private static final String[] NOMBRES = {"Bin", "Dec", "Oct", "Hex", "Real", "Exp", "Cad", "Bool", "Var"};
    private static final int[] TOKEN_TEMPORAL = {-110, -111, -112, -113, -114, -115, -116, -117, -118};

    private final HashMap<String, int[]> tablas = new HashMap<>();

    public Compatibilidad() {
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/Matriz Compatibilidad.csv"))) {
            reader.readLine(); // encabezado
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] partes = line.split(",");
                int[] fila = new int[9];
                for (int i = 0; i < 9; i++) {
                    fila[i] = Integer.parseInt(partes[2 + i].trim());
                }
                // la clave es TABLA + índice del tipo izquierdo
                tablas.put(partes[0].trim() + indiceDeNombre(partes[1].trim()), fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int indiceDeNombre(String nombre) {
        switch (nombre) {
            case "Bin": return 0;
            case "Dec": return 1;
            case "Oct": return 2;
            case "Hex": return 3;
            case "Real": return 4;
            case "Exp": return 5;
            case "Cadena": return 6;
            case "Boolean": return 7;
            default: return VARIANT;
        }
    }

    /** Valor crudo de la matriz para (tabla, izquierdo, derecho). */
    public int consultar(String tabla, int izquierdo, int derecho) {
        int[] fila = tablas.get(tabla + izquierdo);
        return fila == null ? 0 : fila[derecho];
    }

    /** Nombre de la tabla que le toca a un operador, o null si no hay tabla para él. */
    public static String tablaDeOperador(int tokenOperador) {
        switch (tokenOperador) {
            case -11: return "SUMA";                                   // +
            case -12: return "RESTA";                                  // -
            case -13: case -6: case -72: return "MULT";                // *, ^, #
            case -14: case -15: return "DIV";                          // /, %
            case -17: case -18: case -19: return "MULT";               // <<, >>, >>>
            case -20: case -21: case -22: case -23: case -24: case -25:
                return "REL";                                          // <, >, <=, >=, ==, !=
            case -30: case -31: case -4: case -5: return "LOG";        // &&, ||, |, &
            default: return null;
        }
    }

    /** Número de error que corresponde a una tabla (547 a 552). */
    public static int errorDeTabla(String tabla) {
        switch (tabla) {
            case "SUMA": return 547;
            case "RESTA": return 548;
            case "MULT": return 549;
            case "DIV": return 550;
            case "REL": return 551;
            default: return 552;
        }
    }

    /** Índice de tipo (0 a 8) de un token operando; Variant si no se reconoce. */
    public static int tipoDeToken(int numeroToken) {
        switch (numeroToken) {
            case -54: case -61: case -110: return 0;
            case -55: case -62: case -111: return 1;
            case -56: case -63: case -112: return 2;
            case -57: case -64: case -113: return 3;
            case -58: case -65: case -114: return 4;
            case -59: case -66: case -115: return 5;
            case -53: case -60: case -116: return 6;
            case -69: case -70: case -67: case -117: return 7;
            default: return VARIANT;
        }
    }

    /** Convierte el valor de la matriz (negativo o 0) a índice de tipo. */
    public static int tipoDeResultado(int valor) {
        return valor < 0 ? -valor - 1 : VARIANT;
    }

    public static int tokenTemporal(int tipo) {
        return TOKEN_TEMPORAL[tipo];
    }

    /** "TempDec", "TempReal", etc. */
    public static String prefijoTemporal(int tipo) {
        return "Temp" + (tipo == 1 ? "Dec" : NOMBRES[tipo]);
    }
}
