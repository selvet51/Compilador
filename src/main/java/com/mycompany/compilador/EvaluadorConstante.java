package com.mycompany.compilador;

import java.util.List;

/**
 * Evalúa una expresión entera formada solo por constantes decimales (token -55), + - * / %,
 * menos/más unario y paréntesis. Si aparece cualquier otra cosa (un id, otro tipo de constante,
 * una llamada, un corchete...) la expresión no es constante y evaluar() regresa null.
 *
 * Es un descenso recursivo: expresión -> término -> unario -> primario.
 */
public class EvaluadorConstante {

	private final List<Token> tokens;
	private int posicion;
	private boolean constante;

	private EvaluadorConstante(List<Token> tokens) {
		this.tokens = tokens;
	}

	/** Valor de la expresión, o null si no es una constante entera evaluable. */
	public static Long evaluar(List<Token> tokens) {
		if (tokens == null || tokens.isEmpty()) {
			return null;
		}
		EvaluadorConstante evaluador = new EvaluadorConstante(tokens);
		evaluador.constante = true;
		long valor = evaluador.expresion();
		if (!evaluador.constante || evaluador.posicion != tokens.size()) {
			return null;
		}
		return valor;
	}

	private int tokenActual() {
		return posicion < tokens.size() ? tokens.get(posicion).getNumeroToken() : 0;
	}

	private long expresion() {
		long valor = termino();
		while (constante && (tokenActual() == -11 || tokenActual() == -12)) {
			boolean suma = tokenActual() == -11;
			posicion++;
			long derecho = termino();
			valor = suma ? valor + derecho : valor - derecho;
		}
		return valor;
	}

	private long termino() {
		long valor = unario();
		while (constante && (tokenActual() == -13 || tokenActual() == -14 || tokenActual() == -15)) {
			int operador = tokenActual();
			posicion++;
			long derecho = unario();
			if (operador == -13) {
				valor = valor * derecho;
			} else if (derecho == 0) {
				constante = false; // división o resto entre cero: no se puede decidir
			} else {
				valor = operador == -14 ? valor / derecho : valor % derecho;
			}
		}
		return valor;
	}

	private long unario() {
		if (tokenActual() == -12) {
			posicion++;
			return -unario();
		}
		if (tokenActual() == -11) {
			posicion++;
			return unario();
		}
		return primario();
	}

	private long primario() {
		int token = tokenActual();
		if (token == -55) {
			String lexema = tokens.get(posicion++).getLexema();
			try {
				return Long.parseLong(lexema);
			} catch (NumberFormatException e) {
				constante = false;
				return 0;
			}
		}
		if (token == -49) { // (
			posicion++;
			long valor = expresion();
			if (tokenActual() != -50) { // )
				constante = false;
				return 0;
			}
			posicion++;
			return valor;
		}
		constante = false;
		return 0;
	}
}
