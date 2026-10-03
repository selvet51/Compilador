/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compilador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Lexico {
	
	static private int numFilas = 104;
    static private int numColumnas = 54;
    static private String[][] matriz = new String[numFilas][numColumnas];
    private LinkedList<Token> listaTokens;
    private LinkedList<Error> listaErrores;
    private Set<String> palabrasReservadas;
	private String[] actuales;
	private String lexemaActual = "";
	private int estadoActual = 0, lineaActual = 1;
	private Character caracterActual = null;
	private BufferedReader reader = null;
    private String line = "";
    private HashMap<Character, Integer> caracteres;
    private Workbook wb;
    private Sheet sheetTokens;
	private Sheet sheetErrores;
	private Sheet sheetContadores;
	private int rowActual = 1;
        private boolean enComentario = false;
        private Sintaxis sintaxis;
    
    
	public Lexico(){
		
        
        caracteres = new HashMap<>();
        caracteres.put('+', 2);
        caracteres.put('-', 3);
        caracteres.put('~', 4);
        caracteres.put('*', 5);
        caracteres.put('|', 6);
        caracteres.put('&', 7);
        caracteres.put('^', 8);
        caracteres.put(',', 9);
        caracteres.put('.', 10);
        caracteres.put(';', 11);
        caracteres.put(':', 12);
        caracteres.put('/', 13);
        caracteres.put('%', 14);
        caracteres.put('<', 15);
        caracteres.put('>', 16);
        caracteres.put('=', 17);
        caracteres.put('!', 18);
        caracteres.put('?', 19);
        caracteres.put('{', 20);
        caracteres.put('}', 21);
        caracteres.put('[', 22);
        caracteres.put(']', 23);
        caracteres.put('(', 24);
        caracteres.put(')', 25);
        caracteres.put('"', 26);
        caracteres.put('\'', 27);
        caracteres.put('@', 28);
        caracteres.put('#', 29);
        caracteres.put('$', 30);
        caracteres.put('¿', 31);
        caracteres.put('¡', 32);
        caracteres.put('_', 33);
        caracteres.put('\n', 34);
        caracteres.put('\t', 35);
        caracteres.put(' ', 36);
        caracteres.put('A', 37);
        caracteres.put('a', 37);
        caracteres.put('á', 48);
        caracteres.put('Á', 48);
        caracteres.put('B', 38);
        caracteres.put('b', 39);
        caracteres.put('C', 40);
        caracteres.put('c', 40);
        caracteres.put('D', 41);
        caracteres.put('d', 42);
        caracteres.put('E', 42);
        caracteres.put('e', 42);
        caracteres.put('é', 48);
        caracteres.put('É', 48);
        caracteres.put('F', 42);
        caracteres.put('f', 42);
        caracteres.put('G', 43);
        caracteres.put('g', 43);
        caracteres.put('H', 43);
        caracteres.put('h', 43);
        caracteres.put('I', 43);
        caracteres.put('i', 43);
        caracteres.put('í', 48);
        caracteres.put('Í', 48);
        caracteres.put('J', 43);
        caracteres.put('j', 43);
        caracteres.put('K', 43);
        caracteres.put('k', 43);
        caracteres.put('L', 44);
        caracteres.put('l', 44);
        caracteres.put('M', 45);
        caracteres.put('m', 45);
        caracteres.put('N', 45);
        caracteres.put('n', 45);
        caracteres.put('Ñ', 45);
        caracteres.put('ñ', 45);
        caracteres.put('O', 46);
        caracteres.put('o', 47);
        caracteres.put('ó', 48);
        caracteres.put('Ó', 48);
        caracteres.put('P', 48);
        caracteres.put('p', 48);
        caracteres.put('Q', 48);
        caracteres.put('q', 48);
        caracteres.put('R', 48);
        caracteres.put('r', 48);
        caracteres.put('S', 48);
        caracteres.put('s', 48);
        caracteres.put('T', 48);
        caracteres.put('t', 48);
        caracteres.put('U', 48);
        caracteres.put('u', 48);
        caracteres.put('ú', 48);
        caracteres.put('Ú', 48);
        caracteres.put('V', 48);
        caracteres.put('v', 48);
        caracteres.put('W', 48);
        caracteres.put('w', 48);
        caracteres.put('X', 49);
        caracteres.put('x', 50);
        caracteres.put('Y', 51);
        caracteres.put('y', 51);
        caracteres.put('Z', 51);
        caracteres.put('z', 51);
        caracteres.put('0', 52);
        caracteres.put('1', 52);
        caracteres.put('2', 53);
        caracteres.put('3', 53);
        caracteres.put('4', 53);
        caracteres.put('5', 53);
        caracteres.put('6', 53);
        caracteres.put('7', 53);
        caracteres.put('8', 54);
        caracteres.put('9', 54);
        caracteres.put('–', 51);
        caracteres.put('—', 51);
        
        
        
        String palabrasArray[] = {"if", "else", "switch", "for", "do", "while", "console.log", "forEach", "break", "continue", "let", "const", "undefined",
        		"interface", "typeof", "any", "set", "get", "class", "toLowerCase", "toUpperCase", "length", "trim", "charAt", "startsWith", "endsWith",
        		"indexOf", "includes", "slice", "replace", "split", "push", "shift", "in", "of", "splice", "concat", "find", "findIndex", "filter", "map",
        		"sort", "reverse", "true", "false", "null", "Includes", "reg", "var", "def", "main", "CLEAR", "SQRT", "POW", "SQRTV", "STRLEN", "copy", "val",
                        "str", "sin", "cos", "tan", "chr", "pred", "succ", "inc", "dec", "sqr", "Console.read", "Console.log", "console.read", "return", "elseif",
                        "default", "case"};
        
        palabrasReservadas = new HashSet<>();
        for(int i = 0; i < palabrasArray.length; i++) {
        	palabrasReservadas.add(palabrasArray[i]);
        }
        
        
        int i = 0;

        try {
            reader = new BufferedReader(new FileReader("resources/Matriz Java.csv"));
            while((line = reader.readLine()) != null){
            	matriz[i] = line.split(",");
            	i++;
            }
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
        

        
        
	}
	
	public void consultar(String texto) throws IOException {
		try {
        	reader = new BufferedReader(new StringReader(texto));
        	listaTokens = new LinkedList<>();
        	listaErrores = new LinkedList<>();
        	
        	
        	int contIdCadena = 0, contIdBin = 0, contIdDec = 0, contIdOct = 0, contIdHex = 0, contIdReal = 0, contIdExp = 0, contIdBool = 0, contComt = 0;
            int contReserv = 0, contConstCadena = 0, contConstBin = 0, contConstDec = 0, contConstOct = 0, contConstHex = 0, contConstReal = 0, contConstExp = 0, contConstBool = 0;
            int contConstNul = 0, contOpPost = 0, contOpLogBin = 0, contOpCtrl = 0, contOpMat = 0, contOpExp = 0, contOpTurno = 0, contOpRel = 0, contOpSinIg = 0, contOpLog = 0;
            int contOpTer = 0, contOpAsig = 0, contOpAgrup = 0, contIdReg = 0;
            lineaActual = 1;
        	
        	while((line = reader.readLine()) != null) {
        		actuales = (line + "\n").split("");
        		for (int j = 0; j < actuales.length; j++) {
        			caracterActual = actuales[j].charAt(0);
        			
        			//System.out.print("Caracter actual: " + caracterActual);
        			//System.out.print(", estado incial: " + estadoActual);
        			
        			if(caracteres.containsKey(caracterActual)) {
        				estadoActual = consultarMatriz(estadoActual, caracteres.get(caracterActual));
        			}else {
        				//System.out.println("ERROR 500: Caracter no reconocido.");
        				listaErrores.add(new Error(500, lineaActual, lexemaActual.concat(caracterActual.toString()), TipoError.LÉXICO));
        			}
        			
        			//System.out.println(", estado actual: " + estadoActual);
        			
        			
        			if(estadoActual >= 500) {
        				switch(estadoActual) {
        					case 501: System.out.print("ERROR 501: No se permiten saltos de líneas entre cadenas."); break;
        					case 502: System.out.print("ERROR 502: Se espera un caracter [BbOoLl]."); break;
        					case 503: System.out.print("ERROR 503: Se esperan números binarios."); break;
        					case 504: System.out.print("ERROR 504: Se esperan números octales."); break;
        					case 505: System.out.print("ERROR 505: Se esperan números hexadecimales."); break;
        					case 506: System.out.print("ERROR 506: Se espera un número después del punto decimal."); break;
        					case 507: System.out.print("ERROR 507: Se espera un exponente después del circunflejo (^)."); break;
        					case 508: System.out.print("ERROR 508: Se espera caracter alfanumérico."); break;
        					case 509: System.out.print("ERROR 509: Se espera caracter alfanumérico o '_'."); break;
        					case 510: System.out.print("ERROR 510: Se espera caracter [BDOX]."); break;
        					case 511: System.out.print("ERROR 511: Se espera una letra después del '.'."); break;
        				default: //System.out.println("ERROR NO IDENTIFICADO.");
        					break;
        				}
        				listaErrores.add(new Error(estadoActual, lineaActual, lexemaActual.concat(caracterActual.toString()), TipoError.LÉXICO));
        				//System.out.println(" Se tiene [" + lexemaActual.concat(caracterActual.toString()) + "]");
        				estadoActual = 0;
    					lexemaActual = "";
                                        if(caracterActual != '_'){
                                            j--;
                                        }
    					
                                        enComentario = false;
        				
        			}
                                if(estadoActual == 25 || estadoActual == 26){
                                    enComentario = true;
                                }
        			if(estadoActual < 0) {
        				if(!(estadoActual == -68)) {
        					
                                            if(estadoActual != -51 && estadoActual != -52){
                                                //System.out.println("Se formó el token no: " + estadoActual + ", con lexema: [" + lexemaActual + "]");
        					
                                                
        					listaTokens.add(new Token(estadoActual, lexemaActual, lineaActual));
                                            }else{
                                                contComt++;
                                            }	
        				}else { //PALABRAS
        					
        					if(palabrasReservadas.contains(lexemaActual)) {
                                                    
        						switch(lexemaActual){
                                                            
                                                            case "true": estadoActual = -69; break;
                                                            case "false": estadoActual = -70; break;
                                                            case "null": estadoActual = -71; break;
                                                            case "reg": estadoActual = -73; break;
                                                            case "var": estadoActual = -74; break;
                                                            case "def": estadoActual = -75; break;
                                                            case "main": estadoActual = -77; break;
                                                            case "CLEAR": estadoActual = -78; break;
                                                            case "SQRT": estadoActual = -79; break;
                                                            case "POW": estadoActual = -80; break;
                                                            case "SQRTV": estadoActual = -81; break;
                                                            case "STRLEN": estadoActual = -82; break;
                                                            case "concat": estadoActual = -83; break;
                                                            case "copy": estadoActual = -84; break;
                                                            case "val": estadoActual = -85; break;
                                                            case "str": estadoActual = -86; break;
                                                            case "sin": estadoActual = -87; break;
                                                            case "cos": estadoActual = -88; break;
                                                            case "tan": estadoActual = -89; break;
                                                            case "chr": estadoActual = -90; break;
                                                            case "pred": estadoActual = -91; break;
                                                            case "succ": estadoActual = -92; break;
                                                            case "inc": estadoActual = -93; break;
                                                            case "dec": estadoActual = -94; break;
                                                            case "sqr": estadoActual = -95; break;
                                                            case "Console.read": estadoActual = -96; break;
                                                            case "console.read": estadoActual = -96; break;
                                                            case "Console.log": estadoActual = -97; break;
                                                            case "console.log": estadoActual = -97; break;
                                                            case "if": estadoActual = -98; break;
                                                            case "while": estadoActual = -99; break;
                                                            case "do": estadoActual = -100; break;
                                                            case "return": estadoActual = -101; break;
                                                            case "for": estadoActual = -102; break;
                                                            case "switch": estadoActual = -103; break;
                                                            case "else": estadoActual = -104; break;
                                                            case "elseif": estadoActual = -105; break;
                                                            case "break": estadoActual = -106; break;
                                                            case "default": estadoActual = -107; break;
                                                            case "case": estadoActual = -108; break;
                                                            
                                                            default: break;
                                                        }
        						listaTokens.add(new Token(estadoActual, lexemaActual, lineaActual));
        						//System.out.println("La palabra fue encontrada.");
                                                        if(!lexemaActual.equals("true") && !lexemaActual.equals("false") && !lexemaActual.equals("null")){
                                                            contReserv++;
                                                            
                                                        }
                                                        
        					}else {
                                                    estadoActual = -109;
                                                    listaTokens.add(new Token(estadoActual, lexemaActual, lineaActual));
        						//System.out.println("No se encontró palabra (id registro).");
        						//System.out.println("ERROR 512: Palabra reservada no encontrada.");
        						//listaErrores.add(new Error(512, lineaActual, lexemaActual, TipoError.LÉXICO));
        					}
        				}
        				estadoActual = 0;
    					lexemaActual = "";
    					j--;
                                        enComentario = false;
        				
        			}else if(estadoActual != 0){
        				lexemaActual += caracterActual;
        			}
				}
        		lineaActual++;
        	}
                
                if(enComentario){
                    listaErrores.add(new Error(514, lineaActual, lexemaActual, TipoError.LÉXICO));
                }
        	
        	crearExcel();
        	
        	
        	rowActual = 1;
        	for(Token token: listaTokens) {
        		int numeroToken = token.getNumeroToken();
        		
        		Row newRow = sheetTokens.createRow(rowActual++);
        		newRow.createCell(0).setCellValue(numeroToken);
        		newRow.createCell(1).setCellValue(token.getLexema());
        		newRow.createCell(2).setCellValue(token.getLinea());
        		

        		switch(numeroToken) {
        			case -1: contOpPost++; break;
        			case -2: contOpPost++; break;
        			case -3: contOpLogBin++; break;
        			case -4: contOpLogBin++; break;
        			case -5: contOpLogBin++; break;
        			case -6: contOpLogBin++; break;
        			case -7: contOpCtrl++; break;
        			case -8: contOpCtrl++; break;
        			case -9: contOpCtrl++; break;
        			case -10: contOpCtrl++; break;
        			case -11: contOpMat++; break;
        			case -12: contOpMat++; break;
        			case -13: contOpMat++; break;
        			case -14: contOpMat++; break;
        			case -15: contOpMat++; break;
        			case -16: contOpExp++; break;
        			case -17: contOpTurno++; break;
        			case -18: contOpTurno++; break;
        			case -19: contOpTurno++; break;
        			case -20: contOpRel++; break;
        			case -21: contOpRel++; break;
        			case -22: contOpRel++; break;
        			case -23: contOpRel++; break;
        			case -24: contOpRel++; break;
        			case -25: contOpRel++; break;
        			case -26: contOpRel++; break;
        			case -27: contOpSinIg++; break;
        			case -28: contOpSinIg++; break;
        			case -29: contOpLog++; break;
        			case -30: contOpLog++; break;
        			case -31: contOpLog++; break;
        			case -32: contOpTer++; break;
        			case -33: contOpAsig++; break;
        			case -34: contOpAsig++; break;
        			case -35: contOpAsig++; break;
        			case -36: contOpAsig++; break;
        			case -37: contOpAsig++; break;
        			case -38: contOpAsig++; break;
        			case -39: contOpAsig++; break;
        			case -40: contOpAsig++; break;
        			case -41: contOpAsig++; break;
        			case -42: contOpAsig++; break;
        			case -43: contOpAsig++; break;
        			case -44: contOpAsig++; break;
        			case -45: contOpAgrup++; break;
        			case -46: contOpAgrup++; break;
        			case -47: contOpAgrup++; break;
        			case -48: contOpAgrup++; break;
        			case -49: contOpAgrup++; break;
        			case -50: contOpAgrup++; break;
        			case -53: contConstCadena++; break;
        			case -54: contConstBin++; break;
        			case -55: contConstDec++; break;
        			case -56: contConstOct++; break;
        			case -57: contConstHex++; break;
        			case -58: contConstReal++; break;
        			case -59: contConstExp++; break;
        			case -60: contIdCadena++; break;
        			case -61: contIdBin++; break;
        			case -62: contIdDec++; break;
        			case -63: contIdOct++; break;
        			case -64: contIdHex++; break;
        			case -65: contIdReal++; break;
        			case -66: contIdExp++; break;
        			case -67: contIdBool++; break;
        			case -69: contConstBool++; break;
                                case -70: contConstBool++; break;
        			case -71: contConstNul++; break;
                                case -72: contOpMat++; break;
                                case -109: contIdReg++; break;
        			default: break;
        		}
                        
        	}
        	
        	int rowActual = 1;
        	for(Error error: listaErrores) {
        		Row newRow = sheetErrores.createRow(rowActual++);
        		newRow.createCell(0).setCellValue(error.getNumeroError());
        		newRow.createCell(1).setCellValue(error.getDescripcion());
        		newRow.createCell(2).setCellValue(error.getLexema());
        		newRow.createCell(3).setCellValue(error.getTipoError());
        		newRow.createCell(4).setCellValue(error.getLinea());
        		
        	}
        	
        	Row rowContadores = sheetContadores.createRow(2);
        	rowContadores.createCell(0).setCellValue(listaErrores.size());
        	rowContadores.createCell(1).setCellValue(contIdCadena);
        	rowContadores.createCell(2).setCellValue(contIdBin);
        	rowContadores.createCell(3).setCellValue(contIdDec);
        	rowContadores.createCell(4).setCellValue(contIdOct);
        	rowContadores.createCell(5).setCellValue(contIdHex);
        	rowContadores.createCell(6).setCellValue(contIdReal);
        	rowContadores.createCell(7).setCellValue(contIdExp);
        	rowContadores.createCell(8).setCellValue(contIdBool);
                rowContadores.createCell(9).setCellValue(contIdReg);
        	rowContadores.createCell(10).setCellValue(contComt);
        	rowContadores.createCell(11).setCellValue(contReserv);
        	rowContadores.createCell(12).setCellValue(contConstCadena);
        	rowContadores.createCell(13).setCellValue(contConstBin);
        	rowContadores.createCell(14).setCellValue(contConstDec);
        	rowContadores.createCell(15).setCellValue(contConstOct);
        	rowContadores.createCell(16).setCellValue(contConstHex);
        	rowContadores.createCell(17).setCellValue(contConstReal);
        	rowContadores.createCell(18).setCellValue(contConstExp);
        	rowContadores.createCell(19).setCellValue(contConstBool);
        	rowContadores.createCell(20).setCellValue(contConstNul);
        	rowContadores.createCell(21).setCellValue(contOpPost);
        	rowContadores.createCell(22).setCellValue(contOpLogBin);
        	rowContadores.createCell(23).setCellValue(contOpCtrl);
        	rowContadores.createCell(24).setCellValue(contOpMat);
        	rowContadores.createCell(25).setCellValue(contOpExp);
        	rowContadores.createCell(26).setCellValue(contOpTurno);
        	rowContadores.createCell(27).setCellValue(contOpRel);
        	rowContadores.createCell(28).setCellValue(contOpSinIg);
        	rowContadores.createCell(29).setCellValue(contOpLog);
        	rowContadores.createCell(30).setCellValue(contOpTer);
        	rowContadores.createCell(31).setCellValue(contOpAsig);
        	rowContadores.createCell(32).setCellValue(contOpAgrup);
        	

        	
        	
        	
        }catch (Exception e){
        	e.printStackTrace();
        }finally {
        	try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		
		
		
		System.out.println(listaTokens);
        System.out.println(listaErrores);

        
        JOptionPane.showMessageDialog(null, "Hay un total de " + listaTokens.size() + " token(s). \nHay un total de " + listaErrores.size() + " error(es) en léxico. \nSe ha creado archivo de contadores.");
        
        sintaxis = new Sintaxis(wb, sheetErrores);
        
        LinkedList<Error> erroresSintaxis = sintaxis.analizarSintaxis(new LinkedList<Token>(listaTokens));
        for(Error error: erroresSintaxis){
            listaErrores.add(error);
        }
        
        int rowActual = 1;
        	for(Error error: listaErrores) {
        		Row newRow = sheetErrores.createRow(rowActual++);
        		newRow.createCell(0).setCellValue(error.getNumeroError());
        		newRow.createCell(1).setCellValue(error.getDescripcion());
        		newRow.createCell(2).setCellValue(error.getLexema());
        		newRow.createCell(3).setCellValue(error.getTipoError());
        		newRow.createCell(4).setCellValue(error.getLinea());
        		
        	}
        
        try {
        	FileOutputStream fileOut = new FileOutputStream("Carlos Tamayo - Semántica 1 Avance 1.xlsx");
            wb.write(fileOut);
            fileOut.close();
            wb.close();
        }catch(Exception e) {
        	e.printStackTrace();
        }
        
    }
	
	
	private void crearExcel() {
		wb = new XSSFWorkbook();
		sheetTokens = wb.createSheet("Tokens");
		sheetErrores = wb.createSheet("Errores");
		sheetContadores = wb.createSheet("Contadores");
		
		Row row1 = sheetTokens.createRow(0);
		row1.createCell(0).setCellValue("Estado");
		row1.createCell(1).setCellValue("Lexema");
		row1.createCell(2).setCellValue("Linea");
		
		sheetTokens.setColumnWidth(1, 15000);
		
		
		Row row2 = sheetErrores.createRow(0);
		row2.createCell(0).setCellValue("Token");
		row2.createCell(1).setCellValue("Descripcion");
		row2.createCell(2).setCellValue("Lexema");
		row2.createCell(3).setCellValue("Tipo de Error");
		row2.createCell(4).setCellValue("Línea");
		
		sheetErrores.setColumnWidth(1, 12000);
		sheetErrores.setColumnWidth(2, 6000);
		sheetErrores.setColumnWidth(3, 4000);
		
		Row row3 = sheetContadores.createRow(0);
		
		
		sheetContadores.setColumnWidth(2, 4000);
		sheetContadores.setColumnWidth(3, 4000);
		sheetContadores.setColumnWidth(4, 4000);
		sheetContadores.setColumnWidth(5, 5000);
		sheetContadores.setColumnWidth(7, 4000);
		sheetContadores.setColumnWidth(8, 4000);
                sheetContadores.setColumnWidth(9, 4000);
		sheetContadores.setColumnWidth(10, 4000);
		sheetContadores.setColumnWidth(11, 5000);
		sheetContadores.setColumnWidth(13, 4000);
		sheetContadores.setColumnWidth(14, 4000);
		sheetContadores.setColumnWidth(15, 4000);
		sheetContadores.setColumnWidth(16, 5000);
		sheetContadores.setColumnWidth(18, 4000);
		sheetContadores.setColumnWidth(19, 4000);
		sheetContadores.setColumnWidth(21, 5000);
		sheetContadores.setColumnWidth(22, 5000);
		sheetContadores.setColumnWidth(23, 5000);
		sheetContadores.setColumnWidth(24, 5000);
		sheetContadores.setColumnWidth(25, 5000);
		sheetContadores.setColumnWidth(26, 5000);
		sheetContadores.setColumnWidth(27, 5000);
		sheetContadores.setColumnWidth(28, 6000);
		sheetContadores.setColumnWidth(29, 5000);
		sheetContadores.setColumnWidth(30, 5000);
		sheetContadores.setColumnWidth(31, 5000);
		sheetContadores.setColumnWidth(32, 5000);
		
		//(filaInicio, filaFin, colInicio, colFin)
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 0, 0)); //Errores
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 0, 1, 9)); //Identificadores
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 11, 11)); //Palabras Reservadas
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 0, 12, 20)); //Constantes
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 21, 21)); // Operadores Postfix
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 22, 22)); // Operadores Lógicos Binarios
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 23, 23)); // Operadores de Control
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 24, 24)); // Operadores Matemáticos
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 25, 25)); // Operadores Exponente
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 26, 26)); // Operadores de Turno
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 27, 27)); // Operadores Relacionales
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 28, 28)); // Operadores sin Igualdad de Conversión
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 29, 29)); // Operadores Lógicos
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 30, 30)); // Operadores Ternarios
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 31, 31)); // Operadores de Asignación
		sheetContadores.addMergedRegion(new CellRangeAddress(0, 1, 32, 32)); // Operadores de Agrupamiento
		
		
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		
		Cell cell1 = row3.createCell(0);
		cell1.setCellStyle(style);
		cell1.setCellValue("Errores");
		
		Cell cell2 = row3.createCell(1);
		cell2.setCellStyle(style);
		cell2.setCellValue("Identificadores");
		
		Cell cell3 = row3.createCell(11);
		cell3.setCellStyle(style);
		cell3.setCellValue("Palabras Reservadas");
		
		Cell cell4 = row3.createCell(12);
		cell4.setCellStyle(style);
		cell4.setCellValue("Constantes");
		
		Cell cell5 = row3.createCell(21);
		cell5.setCellStyle(style);
		cell5.setCellValue("Operadores de Postfix");
		
		Cell cell6 = row3.createCell(22);
		cell6.setCellStyle(style);
		cell6.setCellValue("Operadores Lógicos Binarios");
		
		Cell cell7 = row3.createCell(23);
		cell7.setCellStyle(style);
		cell7.setCellValue("Operadores de Control");
		
		Cell cell8 = row3.createCell(24);
		cell8.setCellStyle(style);
		cell8.setCellValue("Operadores Matemáticos");
		
		Cell cell9 = row3.createCell(25);
		cell9.setCellStyle(style);
		cell9.setCellValue("Operadores Exponente");
		
		Cell cell10 = row3.createCell(26);
		cell10.setCellStyle(style);
		cell10.setCellValue("Operadores de Turno");
		
		Cell cell11 = row3.createCell(27);
		cell11.setCellStyle(style);
		cell11.setCellValue("Operadores Relacionales");
		
		Cell cell12 = row3.createCell(28);
		cell12.setCellStyle(style);
		cell12.setCellValue("Operadores sin Igualdad de Conversión");
		
		Cell cell13 = row3.createCell(29);
		cell13.setCellStyle(style);
		cell13.setCellValue("Operadores Lógicos");
		
		Cell cell14 = row3.createCell(30);
		cell14.setCellStyle(style);
		cell14.setCellValue("Operadores Ternarios");
		
		Cell cell15 = row3.createCell(31);
		cell15.setCellStyle(style);
		cell15.setCellValue("Operadores de Asignación");
		
		Cell cell16 = row3.createCell(32);
		cell16.setCellStyle(style);
		cell16.setCellValue("Operadores de Agrupamiento");
		
		Row row4 = sheetContadores.createRow(1);
		
		row4.createCell(1).setCellValue("Cadena");
		row4.createCell(2).setCellValue("Numérica Binario");
		row4.createCell(3).setCellValue("Numérica Decimal");
		row4.createCell(4).setCellValue("Numérica Octal");
		row4.createCell(5).setCellValue("Numérica Hexadecimal");
		row4.createCell(6).setCellValue("Real");
		row4.createCell(7).setCellValue("Exponencial");
		row4.createCell(8).setCellValue("Booleanas");
                row4.createCell(9).setCellValue("Registros");
		row4.createCell(10).setCellValue("Comentarios");
		
		row4.createCell(12).setCellValue("Cadena");
		row4.createCell(13).setCellValue("Numérica Binario");
		row4.createCell(14).setCellValue("Numérica Decimal");
		row4.createCell(15).setCellValue("Numérica Octal");
		row4.createCell(16).setCellValue("Numérica Hexadecimal");
		row4.createCell(17).setCellValue("Real");
		row4.createCell(18).setCellValue("Exponencial");
		row4.createCell(19).setCellValue("Booleanas");
		row4.createCell(20).setCellValue("Nula");
	}
	
	private String filtrarCSV(String lexema) {
		if(lexema.contains(",") || lexema.contains("\"") || lexema.contains("\n") || lexema.contains(";")) {
			lexema = lexema.replace("\"", "\"\"");
			return "\"" + lexema + "\"";
		}
		return lexema;
	}
	
	private static int consultarMatriz(int fila, int columna) {
		return Integer.parseInt(matriz[fila][columna - 1]);
	}
        
        public LinkedList<Token> getListaTokens() {
            return listaTokens;
        }

        public LinkedList<Error> getListaErrores() {
            return listaErrores;
        }
}