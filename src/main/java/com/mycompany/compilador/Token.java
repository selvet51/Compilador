/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compilador;


public class Token {
	private int numeroToken;
	private String lexema;
	private int linea;
	
	
	public Token(int numeroToken, String lexema, int linea) {
		super();
		this.numeroToken = numeroToken;
		this.lexema = lexema;
		this.linea = linea;
	}


	public int getNumeroToken() {
		return numeroToken;
	}


	public void setNumeroToken(int numeroToken) {
		this.numeroToken = numeroToken;
	}


	public String getLexema() {
		return lexema;
	}


	public void setLexema(String lexema) {
		this.lexema = lexema;
	}


	public int getLinea() {
		return linea;
	}


	public void setLinea(int linea) {
		this.linea = linea;
	}


	@Override
	public String toString() {
		return "\nTOKEN numeroToken: [" + numeroToken + "], lexema: [" + lexema + "], linea: [" + linea + "]";
	}
	
	
	

}

