/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compilador;

public class Error {
	private int numeroError;
	private int linea;
	private String descripcion;
	private String lexema;
	private TipoError tipoError;
	
	public Error(int numeroError, int linea, String lexema, TipoError tipoError) {
		super();
		this.numeroError = numeroError;
		switch(numeroError) {
			case 500: descripcion = "Caracter no reconocido."; break;
			case 501: descripcion = "No se permiten saltos de líneas entre cadenas."; break;
			case 502: descripcion = "Se espera un caracter [BbOoLl]."; break;
			case 503: descripcion = "Se esperan números binarios."; break;
			case 504: descripcion = "Se esperan números octales."; break;
			case 505: descripcion = "Se esperan números hexadecimales."; break;
			case 506: descripcion = "Se espera un número después del punto decimal."; break;
			case 507: descripcion = "Se espera un exponente después del circunflejo (^)."; break;
			case 508: descripcion = "Se espera caracter alfanumérico."; break;
			case 509: descripcion = "Se espera caracter alfanumérico o '_'."; break;
			case 510: descripcion = "Se espera caracter [BDOX]."; break;
			case 511: descripcion = "Se espera una letra después del '.'."; break;
			case 512: descripcion = "Palabra reservada no encontrada."; break;
                        case 514: descripcion = "Comentario sin cerrar."; break;
                        case 515: descripcion = "Se espera reg, var, def, id o main."; break;
                        case 516: descripcion = "Se espera apertura de paréntesis."; break;
                        case 517: descripcion = "Se espera id, apertura de paréntesis, +, -, constante, operador postfijo, !, ~ o función."; break;
                        case 518: descripcion = "Se espera +, - o constante."; break;
                        case 519: descripcion = "Se espera constante."; break;
                        case 520: descripcion = "Se espera constante numérica."; break;
                        case 521: descripcion = "Se espera id, apertura de paréntesis, +, -, constante, operador postfijo, !, ~, función, palabra reservada, llaves o ;."; break;
                        case 522: descripcion = "Se espera apertura de corchetes."; break;
                        case 523: descripcion = "Se espera una función."; break;
                        case 524: descripcion = "Se espera operador de asignación (=, +=, -=, /=, *=)."; break;
                        case 525: descripcion = "Se espera cierre de llaves o ;."; break;
                        case 526: descripcion = "Se espera cierre de paréntesis, cierre de llaves o ,."; break;
                        case 527: descripcion = "Se espera reg o id."; break;
                        case 528: descripcion = "Se espera cierre de corchetes o ,."; break;
                        case 529: descripcion = "Se espera apertura de corchetes, ; o ,."; break;
                        case 530: descripcion = "Se espera , o :."; break;
                        case 531: descripcion = "Se espera ), }, ], +, -, while, else, elseif, break, :, ;, ,, operador relacional, lógico, lógico binario, ternario, matemático, o de turno."; break;
                        case 532: descripcion = "Se esperan paréntesis, +, -, cierre de llaves, while, else, elseif, break, :, ;, corchetes, operadores de asignación, relacionales, lógicos, lógicos binarios, ternario, matemáticos, de turno o ,. "; break;
                        case 533: descripcion = "Se espera id, paréntesis, +, -, constantes, operadores postfijo, !, ~, o función."; break;
                        case 534: descripcion = "Se espera cierre de paréntesis, ;, :, cierre de corchetes o ,."; break;
                        case 535: descripcion = "Se espera cierre de paréntesis, +, -, cierre de llaves, while, else, elseif, break, ;, :, cierre de corchetes, operadores de asignación, relacionales, lógicos, lógicos binarios, ternario, matemático, de turno o ,."; break;
                        case 536: descripcion = "Se espera cierre de corchetes, while, else, elseif, break o ;."; break;
                        case 537: descripcion = "Se espera : o ;."; break;
                        case 538: descripcion = "Se espera break o ;."; break;
                        case 539: descripcion = "Se espera cierre de corchetes, default o case."; break;
                        case 540: descripcion = "No se ha encontrado el símbolo declarado."; break;
                        case 541: descripcion = "El símbolo no se encuentra dentro del ámbito correspondiente."; break;
                        case 542: descripcion = "El símbolo ya estaba declarado anteriormente."; break;
                        case 547: descripcion = "Tipos incompatibles en la suma."; break;
                        case 548: descripcion = "Tipos incompatibles en la resta."; break;
                        case 549: descripcion = "Tipos incompatibles en la multiplicación."; break;
                        case 550: descripcion = "Tipos incompatibles en la división."; break;
                        case 551: descripcion = "Tipos incompatibles en la comparación relacional."; break;
                        case 552: descripcion = "Tipos incompatibles en la operación lógica."; break;
                        case 553: descripcion = "Tipos incompatibles en la comparación de igualdad."; break;
                        case 554: descripcion = "Tipos incompatibles en la operación de resto."; break;
                        case 555: descripcion = "El resultado de la expresión no cabe en la variable."; break;
			default:  descripcion = "Error no identificado."; break;
		}
		this.linea = linea;
		this.lexema = lexema;
		this.tipoError = tipoError;
	}

	public int getNumeroError() {
		return numeroError;
	}

	public void setNumeroError(int numeroError) {
		this.numeroError = numeroError;
	}

	public int getLinea() {
		return linea;
	}

	public void setLinea(int linea) {
		this.linea = linea;
	}

	public String getLexema() {
		return lexema;
	}

	public void setCadena(String lexema) {
		this.lexema = lexema;
	}

	public String getTipoError() {
		switch (tipoError) {
			case LÉXICO: return "Léxico";
			case SINTÁXIS: return "Sintáxis";
                        case AMBITO: return "Ámbitos";
                        case SEMANTICA: return "Semántica";
			default: return null;
		}
	}

	public void setTipoError(TipoError tipoError) {
		this.tipoError = tipoError;
	}
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public String toString() {
		return "\nERROR numeroError: [" + numeroError + "], linea: [" + linea + "], lexema: [" + lexema + "], tipoError: [" + tipoError + "]";
	}
	
	
	
}

