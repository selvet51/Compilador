/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compilador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Stack;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author selvet
 */
public class Sintaxis {
    
    private ConexionDB conexionDB;
    private boolean enArreglo = false;
    private int numeroDimensiones = 0, numeroDeParametros = 0;
    private String tamañoArreglo;
    private String ultimaVar, ultimaConst, ultimaFunc, ultimoPar;
    private String registroDeVar;
    private int enIndice = 0;
    private HashMap<Integer, List<Integer>> producciones;
    private HashMap<Integer, String> valorNoTerminales;
    private HashMap<Integer, String> valorTokens;
    private HashMap<Integer, Integer> valorColumna;
    private BufferedReader reader = null;
    //private BufferedWriter areasWriter = null;
    private BufferedWriter prefijoWriter = null;
    private String line = "";
    private boolean declaracion = true, ejecucion = false;
    static private int numFilas = 39;
    static private int numColumnas = 86;
    static private String[][] matriz = new String[numFilas][numColumnas];
    private Stack<Integer> pilaSintactica, pilaAmbitos;
    private int contadorAmbitos;
    private LinkedList<Integer> listaNoTokens;
    private LinkedList<Error> listaErrores;
    private List<Integer> ambitos, totales;
    private int erroresAmbitosCont;
    
    private boolean insertandoPrefijo = false;
    private Stack<Token> pilaPrefijo, pilaOperadores;
    private Stack<Token> pilaOperandos;
    private Compatibilidad compatibilidad;
    private int[] contadoresTemporales = new int[9];
    private List<String> cuadruplos = new LinkedList<>();
    Token tokenAsignado, tokenIgual;
    
    private HashMap<Integer, Integer> erroresAmbitos;
    
    
    private Workbook wb;
    private Sheet sheetSintaxis;
    private Sheet sheetErrores;
    private Sheet sheetAmbito;
    private Sheet sheetSimbolos;
    private Sheet sheetSemantica;
    private Map<Integer, int[]> temporalesPorLinea = new TreeMap<>();
    private Map<Integer, List<String>> asignacionesPorLinea = new TreeMap<>();
    
    
    private int contPROGRAMA = 0, contLISTADEPARAMETROS = 0, contEXP_PAS = 0, contDECLARACIONCONSTANTES = 0, contOR = 0, contAND = 0, contFACTOR = 0, contCONSTSINSIGNO = 0, contCONSTNUMERICA = 0,
                contELEVACION = 0, contTERMINOPASCAL = 0, contSIMPLEPASCAL = 0, contSTATU = 0, contARR = 0, contFUNCION = 0, contASIG = 0;
    
    
    
    
    public Sintaxis(Workbook wb, Sheet sheetErrores){
        int i = 0;
        this.wb = wb;
        this.sheetErrores = sheetErrores;

        try {
            reader = new BufferedReader(new FileReader("resources/Matriz Sintaxis.csv"));
            while((line = reader.readLine()) != null){
            	matriz[i] = line.split(",");
            	i++;
            }
            prefijoWriter = new BufferedWriter(new FileWriter("temporales.txt"));


        	
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        try {
            reader.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
        }
        
        erroresAmbitos = new HashMap<>();
        erroresAmbitosCont = 0;
        listaErrores = new LinkedList<>();
        totales = new LinkedList<>();
        
        valorTokens = new HashMap<>();
        
        conexionDB = new ConexionDB();
        conexionDB.conectar();
        
        if(conexionDB.borrarTodo()){
            System.out.println("Se han borrado todos los símbolos previos.");
        }
        
        //DEBUGGING
        
        valorTokens.put(-1, "++");
        valorTokens.put(-2, "--");
        valorTokens.put(-3, "~");
        valorTokens.put(-4, "|");
        valorTokens.put(-5, "&");
        valorTokens.put(-6, "^");
        valorTokens.put(-7, ",");
        valorTokens.put(-9, ";");
        valorTokens.put(-10, ":");
        valorTokens.put(-11, "+");
        valorTokens.put(-12, "-");
        valorTokens.put(-13, "*");
        valorTokens.put(-14, "/");
        valorTokens.put(-15, "%");
        valorTokens.put(-17, "<<");
        valorTokens.put(-18, ">>");
        valorTokens.put(-19, ">>>");
        valorTokens.put(-20, "<");
        valorTokens.put(-21, ">");
        valorTokens.put(-22, "<=");
        valorTokens.put(-23, ">=");
        valorTokens.put(-24, "==");
        valorTokens.put(-25, "!=");
        valorTokens.put(-29, "!");
        valorTokens.put(-30, "&&");
        valorTokens.put(-31, "||");
        valorTokens.put(-32, "?");
        valorTokens.put(-33, "=");
        valorTokens.put(-34, "+=");
        valorTokens.put(-35, "-=");
        valorTokens.put(-36, "*=");
        valorTokens.put(-37, "/=");
        valorTokens.put(-45, "{");
        valorTokens.put(-46, "}");
        valorTokens.put(-47, "[");
        valorTokens.put(-48, "]");
        valorTokens.put(-49, "(");
        valorTokens.put(-50, ")");
        valorTokens.put(-53, "Const cadena");
        valorTokens.put(-54, "Binario");
        valorTokens.put(-55, "Const Decimal");
        valorTokens.put(-56, "Const Octal");
        valorTokens.put(-57, "Const Hexadecimal");
        valorTokens.put(-58, "Const Real");
        valorTokens.put(-59, "Const Exponencial");
        valorTokens.put(-60, "id");
        valorTokens.put(-61, "Id Binario");
        valorTokens.put(-62, "Id Decimal");
        valorTokens.put(-63, "Id Octal");
        valorTokens.put(-64, "Id Hexadecimal");
        valorTokens.put(-65, "Id Real");
        valorTokens.put(-66, "Id Exponencial");
        valorTokens.put(-67, "Id Booleano");
        valorTokens.put(-68, "Palabra Reservada");
        valorTokens.put(-69, "true");
        valorTokens.put(-70, "false");
        valorTokens.put(-71, "null");
        valorTokens.put(-72, "#");
        valorTokens.put(-73, "reg");
        valorTokens.put(-74, "var");
        valorTokens.put(-75, "def");
        valorTokens.put(-77, "main");
        valorTokens.put(-78, "CLEAR");
        valorTokens.put(-79, "SQRT");
        valorTokens.put(-80, "POW");
        valorTokens.put(-81, "SQRTV");
        valorTokens.put(-82, "STRLEN");
        valorTokens.put(-83, "concat");
        valorTokens.put(-84, "copy");
        valorTokens.put(-85, "val");
        valorTokens.put(-86, "str");
        valorTokens.put(-87, "sin");
        valorTokens.put(-88, "cos");
        valorTokens.put(-89, "tan");
        valorTokens.put(-90, "chr");
        valorTokens.put(-91, "pred");
        valorTokens.put(-92, "succ");
        valorTokens.put(-93, "inc");
        valorTokens.put(-94, "dec");
        valorTokens.put(-95, "sqr");
        valorTokens.put(-96, "Console.read");
        valorTokens.put(-97, "Console.log");
        valorTokens.put(-98, "if");
        valorTokens.put(-99, "while");
        valorTokens.put(-100, "do");
        valorTokens.put(-101, "return");
        valorTokens.put(-102, "for");
        valorTokens.put(-103, "switch");
        valorTokens.put(-104, "else");
        valorTokens.put(-105, "elseif");
        valorTokens.put(-106, "break");
        valorTokens.put(-107, "default");
        valorTokens.put(-108, "case");
        valorTokens.put(-109, "registro");
        
        
        valorTokens.put(-110, "Temporal Binario");
        valorTokens.put(-111, "Temporal Decimal");
        valorTokens.put(-112, "Temporal Octal");
        valorTokens.put(-113, "Temporal Hexadecimal");
        valorTokens.put(-114, "Temporal Real");
        valorTokens.put(-115, "Temporal Exponencial");
        valorTokens.put(-116, "Temporal Cadena");
        valorTokens.put(-117, "Temporal Booleano");
        valorTokens.put(-118, "Temporal Variant");
        valorTokens.put(-999, "$");
        
        valorNoTerminales = new HashMap<>();
        
        
        //DEBUGGING
        valorNoTerminales.put(1, "PROGRAMA");
        valorNoTerminales.put(2, "LISTA DE PARAMETROS");
        valorNoTerminales.put(3, "EXP PAS");
        valorNoTerminales.put(4, "DECLARACIÓN CONSTANTES");
        valorNoTerminales.put(5, "OR");
        valorNoTerminales.put(6, "AND");
        valorNoTerminales.put(7, "FACTOR");
        valorNoTerminales.put(8, "CONST S/SIGNO");
        valorNoTerminales.put(9, "CONST NUMERICA");
        valorNoTerminales.put(10, "ELEVACION");
        valorNoTerminales.put(11, "TERMINO PASCAL");
        valorNoTerminales.put(12, "SIMPLE EXP PASCAL");
        valorNoTerminales.put(13, "STATU");
        valorNoTerminales.put(14, "ARR");
        valorNoTerminales.put(15, "FUNCION");
        valorNoTerminales.put(16, "ASIG");
        valorNoTerminales.put(17, "A");
        valorNoTerminales.put(18, "B");
        valorNoTerminales.put(19, "C");
        valorNoTerminales.put(20, "D");
        valorNoTerminales.put(21, "E");
        valorNoTerminales.put(22, "F");
        valorNoTerminales.put(23, "G");
        valorNoTerminales.put(24, "H");
        valorNoTerminales.put(25, "I");
        valorNoTerminales.put(26, "K");
        valorNoTerminales.put(27, "L");
        valorNoTerminales.put(28, "N");
        valorNoTerminales.put(29, "O");
        valorNoTerminales.put(30, "P");
        valorNoTerminales.put(31, "Q");
        valorNoTerminales.put(32, "R");
        valorNoTerminales.put(33, "U");
        valorNoTerminales.put(34, "V");
        valorNoTerminales.put(35, "W");
        valorNoTerminales.put(36, "Y");
        valorNoTerminales.put(37, "Z");
        valorNoTerminales.put(38, "A2");
        valorNoTerminales.put(39, "B2");
        valorNoTerminales.put(800, "@"); //CERRAR ZONA DECLARACIÓN
        valorNoTerminales.put(801, "@"); //ABRIR ZONA DECLARACIÓN
        valorNoTerminales.put(802, "@"); //ABRIR ÁMBITO
        valorNoTerminales.put(803, "@"); //CERRAR ÁMBITO
        
        
        valorNoTerminales.put(804, "@"); //FINAL DE DECLARACIÓN VAR
        
        valorNoTerminales.put(805, "@"); //SALIDA DE ARREGLO
        valorNoTerminales.put(806, "@"); //CONTADOR DE DIMENSIONES
        
        valorNoTerminales.put(807, "@"); //DECLARACIÓN DE CONSTANTES
        
        valorNoTerminales.put(808, "@"); //DECLARACIÓN DE FUNCIÓN
        
        valorNoTerminales.put(809, "@"); //AÑADIR PARÁMETRO DE FUNCIÓN
        
        valorNoTerminales.put(810, "@"); //FINAL DE FUNCIÓN
        
        valorNoTerminales.put(811, "@"); //VERIFICACIÓN DE VARIABLE
        
        valorNoTerminales.put(812, "@"); //INICIAR PREFIJO A POSTFIJO
        
        valorNoTerminales.put(813, "@"); //TERMINAR PREFIJO A POSTFIJO
        
        valorNoTerminales.put(814, "@"); //INSERTAR OPERANDO
        
        valorNoTerminales.put(815, "@"); //INSERTAR OPERADOR
        
        valorNoTerminales.put(816, "@"); //INSERTAR OPERADOR
        
        valorNoTerminales.put(817, "@"); //INSERTAR OPERADOR

        valorNoTerminales.put(818, "@"); //INSERTAR ID DE REGISTRO

        valorNoTerminales.put(819, "@"); //VERIFICAR TIPO REGISTRO DE UNA VARIABLE

        valorNoTerminales.put(820, "@"); //ABRIR ÍNDICE DE ARREGLO

        valorNoTerminales.put(821, "@"); //CERRAR ÍNDICE DE ARREGLO
        
        
        //AÑADIR A LA PILA
        producciones = new HashMap<>();
        
        producciones.put(1, Arrays.asList(17, -77, -49, -50, -45, 800, 13, 18, 801, -46));
        producciones.put(2, Arrays.asList());
        producciones.put(3, Arrays.asList(-73, 818, -60, 802, -45, 809, -60, 19, 810, -46, 803, 17));
        producciones.put(4, Arrays.asList());
        producciones.put(5, Arrays.asList(-7, 809, -60, 19));
        producciones.put(6, Arrays.asList(-74, 20, 804, -60, 22, 23, 813, -9, 17));
        producciones.put(7, Arrays.asList());
        producciones.put(8, Arrays.asList(-73, 819, -60));
        producciones.put(9, Arrays.asList());
        producciones.put(10, Arrays.asList(-7, 806, -55, 21));
        producciones.put(11, Arrays.asList());
        producciones.put(12, Arrays.asList(-47, 806, -55, 21, 805, -48));
        producciones.put(13, Arrays.asList());
        producciones.put(14, Arrays.asList(-7, 20, 804, -60, 22, 23));
        producciones.put(15, Arrays.asList(-75, 808, -60, 802, 2, 1, 803, 813, -9, 17));
        producciones.put(16, Arrays.asList(807, -60, -33, 814, 4, 24, 813, -9, 17));
        producciones.put(17, Arrays.asList());
        producciones.put(18, Arrays.asList(-7, 807, -60, -33, 814, 4, 24));
        producciones.put(19, Arrays.asList());
        producciones.put(20, Arrays.asList(813, -9, 13, 18));
        producciones.put(21, Arrays.asList(-49, 809, -60, 19, 810, -50));
        producciones.put(22, Arrays.asList(12, 25));
        producciones.put(23, Arrays.asList());
        producciones.put(24, Arrays.asList(815, -20, 12, 816, 25));
        producciones.put(25, Arrays.asList(815, -23, 12, 816, 25));
        producciones.put(26, Arrays.asList(815, -22, 12, 816, 25));
        producciones.put(27, Arrays.asList(815, -25, 12, 816, 25));
        producciones.put(28, Arrays.asList(815, -24, 12, 816, 25));
        producciones.put(29, Arrays.asList(815, -21, 12, 816, 25));
        producciones.put(30, Arrays.asList(8));
        producciones.put(31, Arrays.asList(815, -11, 8));
        producciones.put(32, Arrays.asList(815, -12, 8));
        producciones.put(33, Arrays.asList(6, 26));
        producciones.put(34, Arrays.asList());
        producciones.put(35, Arrays.asList(815, -31, 6, 816, 26));
        producciones.put(36, Arrays.asList(815, -4, 6, 816, 26));
        producciones.put(37, Arrays.asList(3, 27));
        producciones.put(38, Arrays.asList());
        producciones.put(39, Arrays.asList(815, -30, 3, 816, 27));
        producciones.put(40, Arrays.asList(815, -5, 3, 816, 27));
        producciones.put(41, Arrays.asList(814, 4));
        producciones.put(42, Arrays.asList(811, -60, 28));
        producciones.put(43, Arrays.asList(-49, 29, -50));
        producciones.put(44, Arrays.asList(5, 30));
        producciones.put(45, Arrays.asList());
        producciones.put(46, Arrays.asList(-7, 5, 30));
        producciones.put(47, Arrays.asList());
        producciones.put(48, Arrays.asList(16, 5, 32));
        producciones.put(49, Arrays.asList());
        producciones.put(50, Arrays.asList(14, 31));
        producciones.put(51, Arrays.asList());
        producciones.put(52, Arrays.asList(16, 5, 32));
        producciones.put(53, Arrays.asList());
        producciones.put(54, Arrays.asList(-32, 5, -10, 5));
        producciones.put(55, Arrays.asList(-1, 811, -60, 28));
        producciones.put(56, Arrays.asList(-2, 811, -60, 28));
        producciones.put(57, Arrays.asList(-49, 5, -50));
        producciones.put(58, Arrays.asList(-29, -49, 5, -50));
        producciones.put(59, Arrays.asList(-3, -49, 5, -50));
        producciones.put(60, Arrays.asList(15));
        producciones.put(61, Arrays.asList(-58));
        producciones.put(62, Arrays.asList(-53));
        producciones.put(63, Arrays.asList(9));
        producciones.put(64, Arrays.asList(-69));
        producciones.put(65, Arrays.asList(-70));
        producciones.put(66, Arrays.asList(-59));
        producciones.put(67, Arrays.asList(-71));
        producciones.put(68, Arrays.asList(-54));
        producciones.put(69, Arrays.asList(-55));
        producciones.put(70, Arrays.asList(-56));
        producciones.put(71, Arrays.asList(-57));
        producciones.put(72, Arrays.asList(7, 33));
        producciones.put(73, Arrays.asList());
        producciones.put(74, Arrays.asList(815, -6, 7, 816, 33));
        producciones.put(75, Arrays.asList(10, 34));
        producciones.put(76, Arrays.asList());
        producciones.put(77, Arrays.asList(815, -13, 10, 816, 34));
        producciones.put(78, Arrays.asList(815, -14, 10, 816, 34));
        producciones.put(79, Arrays.asList(815, -72, 10, 816, 34));
        producciones.put(80, Arrays.asList(815, -15, 10, 816, 34));
        producciones.put(81, Arrays.asList(11, 35));
        producciones.put(82, Arrays.asList());
        producciones.put(83, Arrays.asList(815, -12, 11, 816, 35));
        producciones.put(84, Arrays.asList(815, -11, 11, 816, 35));
        producciones.put(85, Arrays.asList(815, -17, 11, 816, 35));
        producciones.put(86, Arrays.asList(815, -18, 11, 816, 35));
        producciones.put(87, Arrays.asList(815, -19, 11, 816, 35));
        producciones.put(88, Arrays.asList());
        producciones.put(89, Arrays.asList(-96, -49, 5, 30, -50));
        producciones.put(90, Arrays.asList(-97, -49, 5, -50));
        producciones.put(91, Arrays.asList(-98, -49, 5, -50, 13, 36));
        producciones.put(92, Arrays.asList());
        producciones.put(93, Arrays.asList(-105, -49, 5, -50, 13, 36));
        producciones.put(94, Arrays.asList(-104, 13));
        producciones.put(95, Arrays.asList(5));
        producciones.put(96, Arrays.asList(-45, 13, 18, -46));
        producciones.put(97, Arrays.asList(-99, -49, 5, -50, 13));
        producciones.put(98, Arrays.asList(-100, 13, -99, -49, 5, -50));
        producciones.put(99, Arrays.asList(-101, 5));
        producciones.put(100, Arrays.asList(-102, -49, 802, 5, 30, 37, -50, 13, 803));
        producciones.put(101, Arrays.asList(-10, 5));
        producciones.put(102, Arrays.asList(813, -9, 13, 813, -9, 5, 30));
        producciones.put(103, Arrays.asList(-103, -49, 5, -50, -45, -108, 5, -10, 13, 38, -46));
        producciones.put(104, Arrays.asList(813, -9, 13, 38));
        producciones.put(105, Arrays.asList(-106, 39));
        producciones.put(106, Arrays.asList());
        producciones.put(107, Arrays.asList(-107, -10, 13, 18));
        producciones.put(108, Arrays.asList(-108, 5, -10, 13, 38));
        producciones.put(109, Arrays.asList(820, -47, 5, 30, -48, 821));
        producciones.put(110, Arrays.asList(-78));
        producciones.put(111, Arrays.asList(-79, -49, 5, -50));
        producciones.put(112, Arrays.asList(-80, -49, 5, -7, 5, -50));
        producciones.put(113, Arrays.asList(-81, -49, 5, -7, 5, -50));
        producciones.put(114, Arrays.asList(-82, -49, 5, -50));
        producciones.put(115, Arrays.asList(-83, -49, 5, -50));
        producciones.put(116, Arrays.asList(-84, -49, 5, -7, 5, -50));
        producciones.put(117, Arrays.asList(-85, -49, 5, -7, 5, -7, 5, -50));
        producciones.put(118, Arrays.asList(-86, -49, 5, -7, 5, -50));
        producciones.put(119, Arrays.asList(-87, -49, 5, -50));
        producciones.put(120, Arrays.asList(-88, -49, 5, -50));
        producciones.put(121, Arrays.asList(-89, -49, 5, -50));
        producciones.put(122, Arrays.asList(-90, -49, 5, -50));
        producciones.put(123, Arrays.asList(-91, -49, 5, -50));
        producciones.put(124, Arrays.asList(-92, -49, 5, -50));
        producciones.put(125, Arrays.asList(-93, -49, 5, -50));
        producciones.put(126, Arrays.asList(-94, -49, 5, -50));
        producciones.put(127, Arrays.asList(-95, -49, 5, -50));
        producciones.put(128, Arrays.asList(817, -33, 812));
        producciones.put(129, Arrays.asList(817, -34, 812));
        producciones.put(130, Arrays.asList(817, -35, 812));
        producciones.put(131, Arrays.asList(817, -37, 812));
        producciones.put(132, Arrays.asList(817, -36, 812));
        
        /*System.out.println("PRODUCCIONES");
        
        for(int j = 1; j <= 132; j++){
            List<Integer> prod = producciones.get(j);
            
            if(!prod.isEmpty()){
                System.out.print(j + " -> [");
            for(int valor: prod){
                if(valor < 0){
                    System.out.print(" " + valorTokens.get(valor) + " ");
                }else{
                    System.out.print(" " + valorNoTerminales.get(valor) + " ");
                }
            }
            System.out.println("]");
            }else{
                System.out.println(j + " -> ε");
            }
        }*/
        
        
        
        valorColumna = new HashMap<>();
        
        valorColumna.put(-73, 1);
        valorColumna.put(-74, 2);
        valorColumna.put(-75, 3);
        valorColumna.put(-60, 4);
        valorColumna.put(-61, 4);
        valorColumna.put(-62, 4);
        valorColumna.put(-63, 4);
        valorColumna.put(-64, 4);
        valorColumna.put(-65, 4);
        valorColumna.put(-66, 4);
        valorColumna.put(-67, 4);
        valorColumna.put(-109, 4);
        valorColumna.put(-77, 5);
        valorColumna.put(-49, 6);
        valorColumna.put(-50, 7);
        valorColumna.put(-11, 8);
        valorColumna.put(-12, 9);
        valorColumna.put(-58, 10);
        valorColumna.put(-53, 11);
        valorColumna.put(-69, 12);
        valorColumna.put(-70, 13);
        valorColumna.put(-59, 14);
        valorColumna.put(-71, 15);
        valorColumna.put(-54, 16);
        valorColumna.put(-55, 17);
        valorColumna.put(-56,18);
        valorColumna.put(-57,19);
        valorColumna.put(-1,20);
        valorColumna.put(-2,21);
        valorColumna.put(-29,22);
        valorColumna.put(-3,23);
        valorColumna.put(-78,24);
        valorColumna.put(-79,25);
        valorColumna.put(-80,26);
        valorColumna.put(-81,27);
        valorColumna.put(-82,28);
        valorColumna.put(-83,29);
        valorColumna.put(-84,30);
        valorColumna.put(-85,31);
        valorColumna.put(-86,32);
        valorColumna.put(-87,33);
        valorColumna.put(-88,34);
        valorColumna.put(-89,35);
        valorColumna.put(-90,36);
        valorColumna.put(-91,37);
        valorColumna.put(-92,38);
        valorColumna.put(-93,39);
        valorColumna.put(-94,40);
        valorColumna.put(-95,41);
        valorColumna.put(-96,42);
        valorColumna.put(-97,43);
        valorColumna.put(-98,44);
        valorColumna.put(-45,45);
        valorColumna.put(-46,46);
        valorColumna.put(-99,47);
        valorColumna.put(-100,48);
        valorColumna.put(-101,49);
        valorColumna.put(-102,50);
        valorColumna.put(-103,51);
        valorColumna.put(-104,52);
        valorColumna.put(-105,53);
        valorColumna.put(-106,54);
        valorColumna.put(-10,55);
        valorColumna.put(-9,56);
        valorColumna.put(-47,57);
        valorColumna.put(-48,58);
        valorColumna.put(-33,59);
        valorColumna.put(-34,60);
        valorColumna.put(-35,61);
        valorColumna.put(-37,62);
        valorColumna.put(-36,63);
        valorColumna.put(-7,64);
        valorColumna.put(-20,65);
        valorColumna.put(-23,66);
        valorColumna.put(-22,67);
        valorColumna.put(-25,68);
        valorColumna.put(-24,69);
        valorColumna.put(-21,70);
        valorColumna.put(-30,71);
        valorColumna.put(-5,72);
        valorColumna.put(-31,73);
        valorColumna.put(-4,74);
        valorColumna.put(-32,75);
        valorColumna.put(-6,76);
        valorColumna.put(-13,77);
        valorColumna.put(-14,78);
        valorColumna.put(-72,79);
        valorColumna.put(-15,80);
        valorColumna.put(-17,81);
        valorColumna.put(-18,82);
        valorColumna.put(-19,83);
        valorColumna.put(-107,84);
        valorColumna.put(-108,85);
        valorColumna.put(-999,86);
        
        pilaSintactica = new Stack<>();
        pilaAmbitos = new Stack<>();
        pilaPrefijo = new Stack<>();
        pilaOperandos = new Stack<>();
        compatibilidad = new Compatibilidad();
        pilaOperadores = new Stack<>();
        
        pilaAmbitos.add(contadorAmbitos);
        erroresAmbitos.put(contadorAmbitos, 0);
        
        sheetSintaxis = wb.createSheet("Sintaxis");
        
        Row row1 = sheetSintaxis.createRow(0);
	row1.createCell(0).setCellValue("Errores");
	row1.createCell(1).setCellValue("PROGRAMA");
	row1.createCell(2).setCellValue("LISTA DE PARAMETROS");
        row1.createCell(3).setCellValue("EXP PAS");
        row1.createCell(4).setCellValue("CONSTANTE S/SIGNO");
        row1.createCell(5).setCellValue("CONST NUMÉRICA");
        row1.createCell(6).setCellValue("OR");
        row1.createCell(7).setCellValue("AND");
        row1.createCell(8).setCellValue("DECLARACIÓN CONSTANTES");
        row1.createCell(9).setCellValue("FACTOR");
        row1.createCell(10).setCellValue("ELEVACION");
        row1.createCell(11).setCellValue("TERMINO PASCAL");
        row1.createCell(12).setCellValue("Simple Exp Pascal");
        row1.createCell(13).setCellValue("STATU");
        row1.createCell(14).setCellValue("Funcion");
        row1.createCell(15).setCellValue("ASIG");
        row1.createCell(16).setCellValue("ARR");
        
        
        //SHEET DE ÁMBITO
        sheetAmbito = wb.createSheet("Ámbito");
        
        Row row3 = sheetAmbito.createRow(0);
        row3.createCell(0).setCellValue("Ámbito");
	row3.createCell(1).setCellValue("Bin");
	row3.createCell(2).setCellValue("Dec");
        row3.createCell(3).setCellValue("Oct");
        row3.createCell(4).setCellValue("Hex");
        row3.createCell(5).setCellValue("Real");
        row3.createCell(6).setCellValue("Exp");
        row3.createCell(7).setCellValue("Cadena");
        row3.createCell(8).setCellValue("Boolean");
        row3.createCell(9).setCellValue("Errores");
        row3.createCell(10).setCellValue("Total");
        
        
        
        
        
        
        //SHEET DE TABLA DE SÍMBOLOS
        sheetSimbolos = wb.createSheet("Tabla de Símbolos");
        
        Row row5 = sheetSimbolos.createRow(0);
        row5.createCell(0).setCellValue("id");
	row5.createCell(1).setCellValue("Tipo");
	row5.createCell(2).setCellValue("Clase");
        row5.createCell(3).setCellValue("Amb");
        row5.createCell(4).setCellValue("Tarr");
        row5.createCell(5).setCellValue("DimArr");
        row5.createCell(6).setCellValue("NoPar");
        row5.createCell(7).setCellValue("TParr");
        
        
        //SHEET DE SEMÁNTICA 1
        sheetSemantica = wb.createSheet("Semántica 1");
        
        String[] encabezadoSemantica = {"Linea", "TBin", "TDec", "TOct", "THex", "TReal", "Texp", "TCadena", "TBoolean", "TVariant", "Asignaciones", "Errores"};
        Row rowSemantica = sheetSemantica.createRow(0);
        for(int c = 0; c < encabezadoSemantica.length; c++){
            rowSemantica.createCell(c).setCellValue(encabezadoSemantica[c]);
        }
        sheetSemantica.setColumnWidth(10, 40 * 256);
        
    }
    
    public LinkedList<Error> analizarSintaxis(LinkedList<Token> listaTokens) throws IOException{
        int contPROGRAMA = 0, contLISTADEPARAMETROS = 0, contEXP_PAS = 0, contDECLARACIONCONSTANTES = 0, contOR = 0, contAND = 0, contFACTOR = 0, contCONSTSINSIGNO = 0, contCONSTNUMERICA = 0,
                contELEVACION = 0, contTERMINOPASCAL = 0, contSIMPLEPASCAL = 0, contSTATU = 0, contARR = 0, contFUNCION = 0, contASIG = 0;
        
        
        
        
        listaTokens.add(new Token(-999, "$", 0));
        pilaSintactica.clear();
        pilaSintactica.push(-999);
        pilaSintactica.push(1);
        List<Integer> prod;
        
        //System.out.println(listaTokens);
        
        //areasWriter.write("Línea " + 1 + " ---> Área Declaración = TRUE");
        //areasWriter.newLine();

        
        
        while(!pilaSintactica.isEmpty()){
            int topePila = pilaSintactica.peek();
            //System.out.print("La pila es actualmente: [");
            for(int valor: pilaSintactica){
                if(valor < 0){
                    //System.out.print("  " + valorTokens.get(valor) + "  ");
                }else{
                    //System.out.print("  " + valorNoTerminales.get(valor) + "  ");
                }
            }
            //System.out.println("]");
            int estadoActual;
            
            
            Token tokenActual = listaTokens.get(0);
            int noTokenActual = tokenActual.getNumeroToken();
            
            //System.out.println("Lexema actual: " + tokenActual.getLexema());
            //System.out.println("Linea: " + tokenActual.getLinea());
            //System.out.println("El token Actual es: " + valorTokens.get(noTokenActual));
            
            if(topePila == 800){
                //System.out.println("Area de ejecución abierta en la línea " + tokenActual.getLinea());
                //areasWriter.write("Línea " + (tokenActual.getLinea()-1) + " ---> Área Declaración = FALSE");
                //areasWriter.newLine();
                //areasWriter.write("Línea " + (tokenActual.getLinea()-1) + " ---> Área Ejecución = TRUE");
                //areasWriter.newLine();
                pilaSintactica.pop();
            }else if(topePila == 801){
                //System.out.println("Area de ejecución cerrada en la línea " + tokenActual.getLinea());
                //areasWriter.write("Línea " + tokenActual.getLinea() + " ---> Área Ejecución = FALSE");
                //areasWriter.newLine();
                //areasWriter.write("Línea " + tokenActual.getLinea() + " ---> Área Declaración = TRUE");
                //areasWriter.newLine();
                pilaSintactica.pop();
            }else if(topePila == 802){
                
                pilaAmbitos.add(++contadorAmbitos);
                erroresAmbitos.put(contadorAmbitos, 0);

                //System.out.println("SE HA ABIERTO EL ÁMBITO " + contadorAmbitos);
                //System.out.println("PILA DE ÁMBITOS = " + pilaAmbitos);
                pilaSintactica.pop();
            }else if(topePila == 803){
                //System.out.println("SE HA CERRADO EL ÁMBITO " + contadorAmbitos);

                pilaAmbitos.pop();
                //System.out.println("PILA DE ÁMBITOS = " + pilaAmbitos);
                pilaSintactica.pop();
            }else if (topePila == 804){ //INSERTAR VARIABLE EN TABLA
                pilaSintactica.pop();
                String tipo = "?";
                switch(tokenActual.getNumeroToken()){
                    case -60: tipo = "Cadena"; break;
                    case -61: tipo = "Binario"; break;
                    case -62: tipo = "Entero"; break;
                    case -63: tipo = "Octal"; break;
                    case -64: tipo = "Hexadecimal"; break;
                    case -65: tipo = "Real"; break;
                    case -66: tipo = "Exponencial"; break;
                    case -67: tipo = "Booleano"; break;
                    default: break;
                }
                if(registroDeVar != null){
                    tipo = "Registro";
                }
                boolean insertada = false;
                ambitos = conexionDB.buscarPorId(tokenActual.getLexema());
                if(ambitos.isEmpty()){
                    if(conexionDB.insertarVariable(tokenActual.getLexema(), tipo, "Variable", pilaAmbitos.peek(), tokenActual.getLinea())){
                        //System.out.println("Se ha insertado correctamente la variable.");
                        ultimaVar = tokenActual.getLexema();
                        insertada = true;
                    }else{
                        //System.out.println("No se ha insertado nada.");
                    }
                }else{
                    boolean encontrado = false;
                    
                    for(Integer ambito: ambitos){
                        if(ambito == pilaAmbitos.peek()){
                            encontrado = true;
                            break;
                        }
                    }
                    if(encontrado){
                        erroresAmbitosCont++;
                        erroresAmbitos.put(pilaAmbitos.peek(), erroresAmbitos.get(pilaAmbitos.peek()) + 1);
                        listaErrores.add(new Error(542, tokenActual.getLinea(), tokenActual.getLexema(), TipoError.AMBITO));
                    }else{
                        if(conexionDB.insertarVariable(tokenActual.getLexema(), tipo, "Variable", pilaAmbitos.peek(), tokenActual.getLinea())){
                            //System.out.println("Se ha insertado correctamente la variable.");
                            ultimaVar = tokenActual.getLexema();
                            insertada = true;
                        }else{
                            //System.out.println("No se ha insertado nada.");
                        }
                    }
                }
                
                
                
                
                if(registroDeVar != null){
                    if(insertada){
                        conexionDB.asignarRegistro(tokenActual.getLexema(), pilaAmbitos.peek(), registroDeVar);
                    }
                    registroDeVar = null;
                }

                //System.out.println("PEEK PILA SINTACTICA: " + pilaSintactica.peek());
                //System.out.println(tokenActual.getLexema());
            }
            else if (topePila == 805){
                enArreglo = false;
                numeroDimensiones = 0;
                tamañoArreglo = "";
                
                pilaSintactica.pop();
                
            }
            else if (topePila == 806){ //AUMENTAR NUMERO DE DIMENSIONES
                enArreglo = true;
                numeroDimensiones++;
                
                if(numeroDimensiones == 1){
                    tamañoArreglo = tokenActual.getLexema();
                    conexionDB.aumentarArreglo(ultimaVar, tamañoArreglo, numeroDimensiones);
                    
                }else{
                    tamañoArreglo += ", " + tokenActual.getLexema();
                    conexionDB.aumentarArreglo(ultimaVar, tamañoArreglo, numeroDimensiones);
                }
                pilaSintactica.pop();
                
            }else if (topePila == 807){ //INSERTAR CONSTANTE EN TABLA
                pilaSintactica.pop();
                String tipo = "?";
                switch(tokenActual.getNumeroToken()){
                    case -60: tipo = "Cadena"; break;
                    case -61: tipo = "Binario"; break;
                    case -62: tipo = "Entero"; break;
                    case -63: tipo = "Octal"; break;
                    case -64: tipo = "Hexadecimal"; break;
                    case -65: tipo = "Real"; break;
                    case -66: tipo = "Exponencial"; break;
                    case -67: tipo = "Booleano"; break;
                    default: break;
                }
                ambitos = conexionDB.buscarPorId(tokenActual.getLexema());
                if(ambitos.isEmpty()){
                    if(conexionDB.insertarVariable(tokenActual.getLexema(), tipo, "Constante", pilaAmbitos.peek(), tokenActual.getLinea())){
                        //System.out.println("Se ha insertado correctamente la constante.");
                        ultimaConst = tokenActual.getLexema();
                    }else{
                        //System.out.println("No se ha insertado nada.");
                    }
                }else{
                    boolean encontrado = false;
                    
                    for(Integer ambito: ambitos){
                        if(ambito == pilaAmbitos.peek()){
                            encontrado = true;
                            break;
                        }
                    }
                    if(encontrado){
                        erroresAmbitosCont++;
                        erroresAmbitos.put(pilaAmbitos.peek(), erroresAmbitos.get(pilaAmbitos.peek()) + 1);
                        listaErrores.add(new Error(542, tokenActual.getLinea(), tokenActual.getLexema(), TipoError.AMBITO));
                    }else{
                        if(conexionDB.insertarVariable(tokenActual.getLexema(), tipo, "Constante", pilaAmbitos.peek(), tokenActual.getLinea())){
                            //System.out.println("Se ha insertado correctamente la constante.");
                            ultimaConst = tokenActual.getLexema();
                        }else{
                            //System.out.println("No se ha insertado nada.");
                        }
                    }
                }
                
                
                //System.out.println("PEEK PILA SINTACTICA: " + pilaSintactica.peek());
                //System.out.println(tokenActual.getLexema());
            }else if (topePila == 808){ //INSERTAR ID FUNCIÓN
                pilaSintactica.pop();
                String tipo = "?";
                switch(tokenActual.getNumeroToken()){
                    case -60: tipo = "Cadena"; break;
                    case -61: tipo = "Binario"; break;
                    case -62: tipo = "Entero"; break;
                    case -63: tipo = "Octal"; break;
                    case -64: tipo = "Hexadecimal"; break;
                    case -65: tipo = "Real"; break;
                    case -66: tipo = "Exponencial"; break;
                    case -67: tipo = "Booleano"; break;
                    default: break;
                }
                ambitos = conexionDB.buscarPorId(tokenActual.getLexema());
                if(ambitos.isEmpty()){
                    if(conexionDB.insertarVariable(tokenActual.getLexema(), tipo, "Función", pilaAmbitos.peek(), tokenActual.getLinea())){
                        //System.out.println("Se ha insertado correctamente la función.");
                        ultimaFunc = tokenActual.getLexema();
                    }else{
                        //System.out.println("No se ha insertado nada.");
                    }
                }else{
                    boolean encontrado = false;
                    
                    for(Integer ambito: ambitos){
                        if(ambito == pilaAmbitos.peek()){
                            encontrado = true;
                            break;
                        }
                    }
                    if(encontrado){
                        listaErrores.add(new Error(542, tokenActual.getLinea(), tokenActual.getLexema(), TipoError.AMBITO));
                    }else{
                        if(conexionDB.insertarVariable(tokenActual.getLexema(), tipo, "Función", pilaAmbitos.peek(), tokenActual.getLinea())){
                            //System.out.println("Se ha insertado correctamente la función.");
                            ultimaFunc = tokenActual.getLexema();
                        }else{
                            //System.out.println("No se ha insertado nada.");
                        }
                    }
                }
                
                
                
                //System.out.println("PEEK PILA SINTACTICA: " + pilaSintactica.peek());
                //System.out.println(tokenActual.getLexema());
            }else if (topePila == 809){ //INSERTAR PARÁMETRO DE FUNCIÓN
                pilaSintactica.pop();
                String tipo = "?";
                switch(tokenActual.getNumeroToken()){
                    case -60: tipo = "Cadena"; break;
                    case -61: tipo = "Binario"; break;
                    case -62: tipo = "Entero"; break;
                    case -63: tipo = "Octal"; break;
                    case -64: tipo = "Hexadecimal"; break;
                    case -65: tipo = "Real"; break;
                    case -66: tipo = "Exponencial"; break;
                    case -67: tipo = "Booleano"; break;
                    default: break;
                }
                if(conexionDB.insertarParametro(tokenActual.getLexema(), tipo, "Parametro", pilaAmbitos.peek(), ultimaFunc, ++numeroDeParametros, tokenActual.getLinea())){
                    //System.out.println("Se ha insertado correctamente el parámetro.");
                    ultimoPar = tokenActual.getLexema();
                }else{
                    //System.out.println("No se ha insertado nada.");
                }
                //System.out.println("PEEK PILA SINTACTICA: " + pilaSintactica.peek());
                //System.out.println(tokenActual.getLexema());
            }else if (topePila == 810){ //FINAL DE PARÁMETROS DE FUNCION
                numeroDeParametros = 0;
                if(conexionDB.actualizarAmbitoFuncion(ultimaFunc, pilaAmbitos.peek().toString())){
                    //System.out.println("Se ha actualizado la función (se agregó ámbito que crea).");
                }else{
                    //System.out.println("No se ha podido actualizar la función.");
                }
                
                pilaSintactica.pop();
                
            }else if (topePila == 811){ //VERFICAR QUE EXISTE ID
                //System.out.println("TOKEN ACTUAL = " + tokenActual.getLexema());
                boolean declarado = verificarSimbolo(tokenActual);
                // Un id no declarado (o fuera de ámbito) se trata como Variant para no encadenar errores de tipo.
                Token operando = declarado ? tokenActual
                        : new Token(Compatibilidad.tokenTemporal(Compatibilidad.VARIANT), tokenActual.getLexema(), tokenActual.getLinea());
                if(insertandoPrefijo){
                    pilaOperandos.push(operando);
                }else{
                    if(enIndice == 0){
                        tokenAsignado = operando;
                    }
                }
                
                pilaSintactica.pop();
                
            }else if(topePila == 812){ //ACTIVAR ZONA DE INFIJO A PREFIJO
                insertandoPrefijo = true;
                pilaOperandos.clear();
                pilaOperadores.clear();
                cuadruplos.clear();
                pilaSintactica.pop();
            }else if(topePila == 813){ //DESACTIVAR ZONA DE INFIJO A POSTFIJO
                insertandoPrefijo = false;

                if(!pilaOperandos.isEmpty()){
                    Token resultado = pilaOperandos.pop();
                    verificarAsignacion(resultado);

                    prefijoWriter.write("Linea " + tokenAsignado.getLinea() + ":");
                    prefijoWriter.newLine();
                    for(String cuadruplo: cuadruplos){
                        prefijoWriter.write(cuadruplo);
                        prefijoWriter.newLine();
                    }
                    prefijoWriter.write(tokenIgual.getLexema() + ", " + tokenAsignado.getLexema() + ", " + resultado.getLexema());
                    prefijoWriter.newLine();
                }
                cuadruplos.clear();

                pilaOperandos.clear();
                pilaOperadores.clear();
                pilaSintactica.pop();
            }
            else if(topePila == 814){ //INSERTAR OPERANDO A PILA PREFIJO
                if(insertandoPrefijo){
                    pilaOperandos.push(tokenActual);
                }
                
                
                pilaSintactica.pop();
            }else if(topePila == 815){ //INSERTAR OPERADOR A PILA PREFIJO
                if(insertandoPrefijo){
                    pilaOperadores.push(tokenActual);
                }
                
                
                pilaSintactica.pop();
            }else if(topePila == 816){
                if(insertandoPrefijo && pilaOperandos.size() >= 2 && !pilaOperadores.isEmpty()){
                    Token derecho = pilaOperandos.pop();
                    Token izquierdo = pilaOperandos.pop();
                    Token operador = pilaOperadores.pop();
                    pilaOperandos.push(generarTemporal(operador, izquierdo, derecho));
                }
                pilaSintactica.pop();
            }else if(topePila == 817){ //CAPTURAR TOKEN DE ASIGNACIÓN
                tokenIgual = tokenActual;
                pilaSintactica.pop();
            }else if(topePila == 820){ //ABRIR ÍNDICE DE ARREGLO
                enIndice++;
                pilaSintactica.pop();
            }else if(topePila == 821){ //CERRAR ÍNDICE DE ARREGLO
                enIndice--;
                pilaSintactica.pop();
            }else if(topePila == 818){ //INSERTAR ID DE REGISTRO
                pilaSintactica.pop();
                if(insertarSimbolo(tokenActual, "Registro", "Registro")){
                    ultimaFunc = tokenActual.getLexema();
                }
            }else if(topePila == 819){ //VERIFICAR QUE EL REGISTRO DE UNA VARIABLE EXISTE
                pilaSintactica.pop();
                verificarSimbolo(tokenActual);
                registroDeVar = tokenActual.getLexema();
            }
            else if(topePila > 0){ //ES NO TERMINAL
                
                estadoActual = verificarMatriz(topePila, valorColumna.get(noTokenActual));
                //System.out.println("El estado Actual es: " + estadoActual);
                
                if(estadoActual > 500){
                    listaErrores.add(new Error(estadoActual, tokenActual.getLinea(), tokenActual.getLexema(), TipoError.SINTÁXIS));
                    listaTokens.remove(0);
                }else if(estadoActual > 0 && estadoActual < 500 && !producciones.get(estadoActual).isEmpty()){
                    switch(pilaSintactica.pop()){
                        case 1: contPROGRAMA++; break;
                        case 2: contLISTADEPARAMETROS++; break;
                        case 3: contEXP_PAS++; break;
                        case 4: contDECLARACIONCONSTANTES++; break;
                        case 5: contOR++; break;
                        case 6: contAND++; break;
                        case 7: contFACTOR++; break;
                        case 8: contCONSTSINSIGNO++; break;
                        case 9: contCONSTNUMERICA++; break;
                        case 10: contELEVACION++; break;
                        case 11: contTERMINOPASCAL++; break;
                        case 12: contSIMPLEPASCAL++; break;
                        case 13: contSTATU++; break;
                        case 14: contARR++; break;
                        case 15: contFUNCION++; break;
                        case 16: contASIG++; break;
                        default: break;
                    }
                    
                    prod = producciones.get(estadoActual);
                    for(int i = prod.size()- 1; i >= 0; i--){
                        pilaSintactica.push(prod.get(i));
                    }
                }else if(producciones.get(estadoActual).isEmpty()){
                    switch(pilaSintactica.pop()){
                        case 1: contPROGRAMA++; break;
                        case 2: contLISTADEPARAMETROS++; break;
                        case 3: contEXP_PAS++; break;
                        case 4: contDECLARACIONCONSTANTES++; break;
                        case 5: contOR++; break;
                        case 6: contAND++; break;
                        case 7: contFACTOR++; break;
                        case 8: contCONSTSINSIGNO++; break;
                        case 9: contCONSTNUMERICA++; break;
                        case 10: contELEVACION++; break;
                        case 11: contTERMINOPASCAL++; break;
                        case 12: contSIMPLEPASCAL++; break;
                        case 13: contSTATU++; break;
                        case 14: contARR++; break;
                        case 15: contFUNCION++; break;
                        case 16: contASIG++; break;
                        default: break;
                    }
                }
            }else if(topePila < 0){ //ES TERMINAL
                if((noTokenActual <= -60 && noTokenActual >= -67) || noTokenActual == -109){
                    noTokenActual = -60;
                }
                if(topePila == noTokenActual){
                    if(topePila == -999){
                        System.out.println("Sintáxis Completada.");
                        JOptionPane.showMessageDialog(null, "Verificación de sintaxis completada.");
                        break;
                    }
                    pilaSintactica.pop();
                    listaTokens.remove(0);
                }else{
                    //System.out.println("ERROR DE FUERZA BRUTA");
                    JOptionPane.showMessageDialog(null, "ERROR DE FUERZA BRUTA.\nEl programa se ha detenido.");
                    break;
                }
            }
            
            
            

            
        }
        
        List<Simbolo> simbolos = conexionDB.mostrarSimbolos();
        //System.out.println("Simbolos: " + simbolos);
        //System.out.println(listaErrores);
        
        int[][] arregloTabla = conexionDB.tablaAmbitos(contadorAmbitos + 1);
        
        totales = new LinkedList<>();
        for(int i = 0; i < contadorAmbitos + 1; i++){
            totales.add(0);
        }
        for(Simbolo simbolo: simbolos){
            totales.set(simbolo.getAmbito(), totales.get(simbolo.getAmbito()) + 1);
        }
        
         List<Integer> filaTotal = conexionDB.ObtenerTotalesSImbolos();
        
        for(int i = 0; i < contadorAmbitos + 1; i++){
            Row rowAmbito = sheetAmbito.createRow(i+1);
            for(int j = 0; j < 9; j++){
                //System.out.println("SE INSERTARA EL NUMERO " + arregloTabla[i][j] + " i = " + i + " j = " + j);
                rowAmbito.createCell(j).setCellValue(arregloTabla[i][j]);
            }
            rowAmbito.createCell(9).setCellValue(erroresAmbitos.get(i));
            rowAmbito.createCell(10).setCellValue(erroresAmbitos.get(i) + totales.get(i));
        }
        Row rowFinal = sheetAmbito.createRow(contadorAmbitos + 2);
        
        rowFinal.createCell(0).setCellValue("Totales");
        rowFinal.createCell(1).setCellValue(filaTotal.get(0));
        rowFinal.createCell(2).setCellValue(filaTotal.get(1));
        rowFinal.createCell(3).setCellValue(filaTotal.get(2));
        rowFinal.createCell(4).setCellValue(filaTotal.get(3));
        rowFinal.createCell(5).setCellValue(filaTotal.get(4));
        rowFinal.createCell(6).setCellValue(filaTotal.get(5));
        rowFinal.createCell(7).setCellValue(filaTotal.get(6));
        rowFinal.createCell(8).setCellValue(filaTotal.get(7));
        rowFinal.createCell(9).setCellValue(erroresAmbitosCont);
        rowFinal.createCell(10).setCellValue(conexionDB.contarSimbolos());
        
        
        //System.out.println("AMBITOS TOTALES = " + contadorAmbitos);
        
        prefijoWriter.close();
        
        int erroresSintaxis = 0, erroresAmbito = 0, erroresSemantica = 0;
        for(Error error: listaErrores){
            switch(error.getTipoError()){
                case "Sintáxis": erroresSintaxis++; break;
                case "Ámbitos": erroresAmbito++; break;
                case "Semántica": erroresSemantica++; break;
                default: break;
            }
        }
        JOptionPane.showMessageDialog(null, "Hay un total de " + erroresSintaxis + " error(es) de sintáxis.\n"
                + "Hay un total de " + erroresAmbito + " error(es) de ámbito.\n"
                + "Hay un total de " + erroresSemantica + " error(es) de semántica.");
        
        System.out.println("Pila de prefijo: " + pilaPrefijo);
        
        List<Simbolo> simbolosFinales = conexionDB.mostrarFinal();
        
        int contador = 0;
        for(Simbolo simbolo: simbolosFinales){
            Row rowSimbolo = sheetSimbolos.createRow(++contador);
            rowSimbolo.createCell(0).setCellValue(simbolo.getId());
            rowSimbolo.createCell(1).setCellValue(simbolo.getTipo());
            rowSimbolo.createCell(2).setCellValue(simbolo.getClase());
            rowSimbolo.createCell(3).setCellValue(simbolo.getAmbito());
            rowSimbolo.createCell(4).setCellValue(simbolo.getTamaño_arreglo());
            rowSimbolo.createCell(5).setCellValue(simbolo.getDimension_arreglo());
            rowSimbolo.createCell(6).setCellValue(simbolo.getNumero_parametros());
            rowSimbolo.createCell(7).setCellValue(simbolo.getPertenece_funcion());
        }
        


        
        llenarSemantica();

        Row row2 = sheetSintaxis.createRow(1);
        row2.createCell(0).setCellValue(listaErrores.size());
	row2.createCell(1).setCellValue(contPROGRAMA);
	row2.createCell(2).setCellValue(contLISTADEPARAMETROS);
        row2.createCell(3).setCellValue(contEXP_PAS);
        row2.createCell(4).setCellValue(contCONSTSINSIGNO);
        row2.createCell(5).setCellValue(contCONSTNUMERICA);
        row2.createCell(6).setCellValue(contOR);
        row2.createCell(7).setCellValue(contAND);
        row2.createCell(8).setCellValue(contDECLARACIONCONSTANTES);
        row2.createCell(9).setCellValue(contFACTOR);
        row2.createCell(10).setCellValue(contELEVACION);
        row2.createCell(11).setCellValue(contTERMINOPASCAL);
        row2.createCell(12).setCellValue(contSIMPLEPASCAL);
        row2.createCell(13).setCellValue(contSTATU);
        row2.createCell(14).setCellValue(contFUNCION);
        row2.createCell(15).setCellValue(contASIG);
        row2.createCell(16).setCellValue(contARR);
        
        
        return listaErrores;
        
    }
    
    public void prueba(){
        System.out.println("2 y 2" + "regresa: " + verificarMatriz(2, 2));
    }
    
    public int verificarMatriz(int fila, int columna){
        return Integer.parseInt(matriz[fila - 1][columna - 1]);
    }

    /** Revisa que el id esté declarado en un ámbito visible; si no, agrega el error 540 o 541 y regresa false. */
    private boolean verificarSimbolo(Token token) {
        ambitos = conexionDB.verificarSimbolo(token.getLexema());

        if(!ambitos.isEmpty()){
            boolean coincide = false;
            for(Integer ambito: ambitos){
                if(pilaAmbitos.contains(ambito)){
                    coincide = true;
                    break;
                }
            }
            if(!coincide){
                erroresAmbitosCont++;
                erroresAmbitos.put(pilaAmbitos.peek(), erroresAmbitos.get(pilaAmbitos.peek()) + 1);
                listaErrores.add(new Error(541, token.getLinea(), token.getLexema(), TipoError.AMBITO));
                return false;
            }
        }else{
            erroresAmbitosCont++;
            erroresAmbitos.put(pilaAmbitos.peek(), erroresAmbitos.get(pilaAmbitos.peek()) + 1);
            listaErrores.add(new Error(540, token.getLinea(), token.getLexema(), TipoError.AMBITO));
            return false;
        }
        return true;
    }

    /** Inserta el id en el ámbito actual; si ya existe en él agrega el error 542. */
    private boolean insertarSimbolo(Token token, String tipo, String clase) {
        ambitos = conexionDB.buscarPorId(token.getLexema());
        if(ambitos.contains(pilaAmbitos.peek())){
            erroresAmbitosCont++;
            erroresAmbitos.put(pilaAmbitos.peek(), erroresAmbitos.get(pilaAmbitos.peek()) + 1);
            listaErrores.add(new Error(542, token.getLinea(), token.getLexema(), TipoError.AMBITO));
            return false;
        }
        return conexionDB.insertarVariable(token.getLexema(), tipo, clase, pilaAmbitos.peek(), token.getLinea());
    }

    /**
     * Llena la hoja "Semántica 1": una fila por línea con sus temporales, asignaciones y
     * errores semánticos (547 a 555), y la fila de totales pegada a la última línea.
     */
    private void llenarSemantica() {
        Map<Integer, Integer> erroresPorLinea = new TreeMap<>();
        for(Error error: listaErrores){
            if(error.getNumeroError() >= 547 && error.getNumeroError() <= 555){
                erroresPorLinea.merge(error.getLinea(), 1, Integer::sum);
            }
        }

        TreeSet<Integer> lineas = new TreeSet<>(temporalesPorLinea.keySet());
        lineas.addAll(asignacionesPorLinea.keySet());
        lineas.addAll(erroresPorLinea.keySet());

        int[] totalTemporales = new int[9];
        int totalAsignaciones = 0, totalErrores = 0;
        int fila = 0;

        for(int linea: lineas){
            Row row = sheetSemantica.createRow(++fila);
            row.createCell(0).setCellValue(linea);

            int[] temporales = temporalesPorLinea.getOrDefault(linea, new int[9]);
            for(int i = 0; i < 9; i++){
                row.createCell(i + 1).setCellValue(temporales[i]);
                totalTemporales[i] += temporales[i];
            }

            List<String> asignaciones = asignacionesPorLinea.getOrDefault(linea, new LinkedList<>());
            row.createCell(10).setCellValue(String.join("; ", asignaciones));
            totalAsignaciones += asignaciones.size();

            int errores = erroresPorLinea.getOrDefault(linea, 0);
            row.createCell(11).setCellValue(errores);
            totalErrores += errores;
        }

        Row rowTotales = sheetSemantica.createRow(++fila);
        rowTotales.createCell(0).setCellValue("Totales");
        for(int i = 0; i < 9; i++){
            rowTotales.createCell(i + 1).setCellValue(totalTemporales[i]);
        }
        rowTotales.createCell(10).setCellValue(totalAsignaciones);
        rowTotales.createCell(11).setCellValue(totalErrores);
    }

    /**
     * Revisa que el resultado de la expresión quepa en la variable asignada (error 555).
     * En asignaciones compuestas se evalúa como x = x op expresión.
     */
    private void verificarAsignacion(Token resultado) {
        int tipoVariable = Compatibilidad.tipoDeToken(tokenAsignado.getNumeroToken());
        int tipoValor = Compatibilidad.tipoDeToken(resultado.getNumeroToken());
        asignacionesPorLinea.computeIfAbsent(tokenAsignado.getLinea(), k -> new LinkedList<>())
                .add(tokenAsignado.getLexema() + " -> " + Compatibilidad.nombreTipo(tipoValor));
        // La asignación cuenta como un temporal Variant en la hoja "Semántica 1".
        temporalesPorLinea.computeIfAbsent(tokenAsignado.getLinea(), k -> new int[9])[Compatibilidad.VARIANT]++;

        int tabla = -1;
        switch(tokenIgual.getNumeroToken()){
            case -34: tabla = Compatibilidad.SUMA; break;
            case -35: tabla = Compatibilidad.RESTA; break;
            case -36: tabla = Compatibilidad.MULT; break;
            case -37: tabla = Compatibilidad.DIV; break;
            default: break;
        }
        if(tabla >= 0){
            int valor = compatibilidad.consultar(tabla, tipoVariable, tipoValor);
            if(valor > 500){
                listaErrores.add(new Error(valor, tokenAsignado.getLinea(), tokenIgual.getLexema(), TipoError.SEMANTICA));
                tipoValor = Compatibilidad.VARIANT;
            }else{
                tipoValor = Compatibilidad.tipoDeResultado(valor);
            }
        }

        if(!Compatibilidad.cabe(tipoVariable, tipoValor)){
            listaErrores.add(new Error(555, tokenAsignado.getLinea(), tokenAsignado.getLexema(), TipoError.SEMANTICA));
        }
    }

    /**
     * Consulta la matriz de compatibilidad con (izquierdo, derecho), registra el
     * cuádruplo "operador, izquierdo, derecho, temporal" y regresa el temporal.
     * Si los tipos son incompatibles se reporta el error y el temporal es Variant.
     */
    private Token generarTemporal(Token operador, Token izquierdo, Token derecho) {
        int tabla = Compatibilidad.tablaDeOperador(operador.getNumeroToken());
        int tipo = Compatibilidad.VARIANT;

        if(tabla >= 0){
            int valor = compatibilidad.consultar(tabla,
                    Compatibilidad.tipoDeToken(izquierdo.getNumeroToken()),
                    Compatibilidad.tipoDeToken(derecho.getNumeroToken()));
            if(valor > 500){
                listaErrores.add(new Error(valor, operador.getLinea(), operador.getLexema(), TipoError.SEMANTICA));
            }else{
                tipo = Compatibilidad.tipoDeResultado(valor);
            }
        }

        String nombre = Compatibilidad.prefijoTemporal(tipo) + (++contadoresTemporales[tipo]);
        Token temporal = new Token(Compatibilidad.tokenTemporal(tipo), nombre, operador.getLinea());
        temporalesPorLinea.computeIfAbsent(operador.getLinea(), k -> new int[9])[tipo]++;
        cuadruplos.add(operador.getLexema() + ", " + izquierdo.getLexema() + ", " + derecho.getLexema() + ", " + nombre);
        return temporal;
    }
}
