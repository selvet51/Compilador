package com.mycompany.compilador;

/**
 * Una verificación de regla semántica: es una fila del archivo reglas.txt.
 */
public class ResultadoRegla {
	private int linea;
	private int regla;
	private int clave;
	private String descripcion;
	private boolean aceptada;
	private String lexema;
	private int ambito;

	public ResultadoRegla(int linea, int regla, int clave, String descripcion, boolean aceptada, String lexema, int ambito) {
		super();
		this.linea = linea;
		this.regla = regla;
		this.clave = clave;
		this.descripcion = descripcion;
		this.aceptada = aceptada;
		this.lexema = lexema;
		this.ambito = ambito;
	}

	public int getLinea() {
		return linea;
	}

	public int getRegla() {
		return regla;
	}

	public int getClave() {
		return clave;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public boolean isAceptada() {
		return aceptada;
	}

	public String getLexema() {
		return lexema;
	}

	public int getAmbito() {
		return ambito;
	}

	/** "ACEPT" o "ERROR", tal como va en reglas.txt. */
	public String getResultado() {
		return aceptada ? "ACEPT" : "ERROR";
	}

	@Override
	public String toString() {
		return "\nREGLA linea: [" + linea + "], regla: [" + regla + "], clave: [" + clave + "], resultado: [" + getResultado()
				+ "], lexema: [" + lexema + "], ambito: [" + ambito + "]";
	}
}
