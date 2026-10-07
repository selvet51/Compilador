package com.mycompany.compilador;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Matriz de compatibilidad de tipos (resources/COMPATIBILIDAD.csv).
 *
 * El archivo trae 8 tablas de 9 filas cada una, en este orden: SUMA, RESTA, MULT,
 * DIV, REL, REL2, LOG y RESTO. Cada fila es: tabla, tipo izquierdo y el resultado
 * para cada tipo derecho. Los tipos se indexan igual que las columnas del CSV:
 * 0 Bin, 1 Dec, 2 Oct, 3 Hex, 4 Real, 5 Exp, 6 Cadena, 7 Boolean, 8 Variant.
 *
 * Valores de la matriz: mayor a 500 = error, negativo = tipo resultante
 * (-1 Bin ... -8 Boolean), 0 = Variant.
 */
public class Compatibilidad {

    public static final int VARIANT = 8;

    public static final int SUMA = 0, RESTA = 1, MULT = 2, DIV = 3, REL = 4, REL2 = 5, LOG = 6, RESTO = 7;
    private static final String[] NOMBRES_TABLAS = {"SUMA", "RESTA", "MULT", "DIV", "REL", "REL2", "LOG", "RESTO"};

    private static final String[] NOMBRES = {"Bin", "Dec", "Oct", "Hex", "Real", "Exp", "Cad", "Bool", "Var"};
    private static final int[] TOKEN_TEMPORAL = {-110, -111, -112, -113, -114, -115, -116, -117, -118};

    static private int numTipos = 9;
    static private int numFilas = NOMBRES_TABLAS.length * numTipos;
    static private int numColumnas = 11;
    static private String[][] matriz = new String[numFilas][numColumnas];

    public Compatibilidad() {
        int i = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/COMPATIBILIDAD.csv"))) {
            reader.readLine(); // encabezado
            String line;
            while ((line = reader.readLine()) != null && i < numFilas) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                matriz[i] = line.split(",");
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Si alguien cambia el orden de las tablas en el CSV, se avisa en lugar de leer mal.
        for (int tabla = 0; tabla < NOMBRES_TABLAS.length; tabla++) {
            String[] fila = matriz[tabla * numTipos];
            if (fila == null || !NOMBRES_TABLAS[tabla].equals(fila[0].trim())) {
                System.out.println("COMPATIBILIDAD.csv: se esperaba la tabla " + NOMBRES_TABLAS[tabla]
                        + " en la fila " + (tabla * numTipos + 2) + " del archivo.");
            }
        }
    }

    /** Valor crudo de la matriz para (tabla, izquierdo, derecho). */
    public int consultar(int tabla, int izquierdo, int derecho) {
        return Integer.parseInt(matriz[tabla * numTipos + izquierdo][2 + derecho].trim());
    }

    /** Tabla que le toca a un operador, o -1 si no hay tabla para él. */
    public static int tablaDeOperador(int tokenOperador) {
        switch (tokenOperador) {
            case -11: return SUMA;                                     // +
            case -12: return RESTA;                                    // -
            case -13: return MULT;                                     // *
            case -14: return DIV;                                      // /
            case -20: case -21: case -22: case -23:
                return REL;                                            // <, >, <=, >=
            case -24: case -25: return REL2;                           // ==, !=
            case -30: case -31: case -4: case -5: return LOG;          // &&, ||, |, &
            case -15: case -6: case -72:
            case -17: case -18: case -19: return RESTO;                // %, ^, #, <<, >>, >>>
            default: return -1;
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

    /**
     * Si un valor de tipo tipoValor cabe en una variable de tipo tipoVariable.
     * Solo se permite el mismo tipo o ensanchar (Dec a Real o Exp, Real a Exp);
     * Variant nunca da error para no repetir errores en cascada.
     */
    public static boolean cabe(int tipoVariable, int tipoValor) {
        if (tipoVariable == VARIANT || tipoValor == VARIANT || tipoVariable == tipoValor) {
            return true;
        }
        if (tipoValor == 1) {                       // Dec
            return tipoVariable == 4 || tipoVariable == 5;
        }
        if (tipoValor == 4) {                       // Real
            return tipoVariable == 5;
        }
        return false;
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
