/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compilador;

/**
 *
 * @author goops
 */
public class Simbolo {
    private String id;
    private String tipo;
    private String clase;
    private int ambito;
    private String tamaño_arreglo;
    private int dimension_arreglo;
    private int numero_parametros;
    private String pertenece_funcion;
    private int linea;
    
    public Simbolo(){
        
    }
    
    public Simbolo(String id, String tipo, String clase, int ambito, int linea){
        this.id = id;
        this.tipo = tipo;
        this.clase = clase;
        this.ambito = ambito;
        this.linea = linea;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public int getAmbito() {
        return ambito;
    }

    public void setAmbito(int ambito) {
        this.ambito = ambito;
    }

    public String getTamaño_arreglo() {
        return tamaño_arreglo;
    }

    public void setTamaño_arreglo(String tamaño_arreglo) {
        this.tamaño_arreglo = tamaño_arreglo;
    }

    public int getDimension_arreglo() {
        return dimension_arreglo;
    }

    public void setDimension_arreglo(int dimension_arreglo) {
        this.dimension_arreglo = dimension_arreglo;
    }

    public int getNumero_parametros() {
        return numero_parametros;
    }

    public void setNumero_parametros(int numero_parametros) {
        this.numero_parametros = numero_parametros;
    }

    public String getPertenece_funcion() {
        return pertenece_funcion;
    }

    public void setPertenece_funcion(String pertenece_funcion) {
        this.pertenece_funcion = pertenece_funcion;
    }
    
    public int getLinea(){
        return linea;
    }
    
    public void setLinea(int linea){
        this.linea = linea;
    }

    @Override
    public String toString() {
        return "Simbolo{" + "id=" + id + ", tipo=" + tipo + ", clase=" + clase + ", ambito=" + ambito + ", tama\u00f1o_arreglo=" + tamaño_arreglo + ", dimension_arreglo=" + dimension_arreglo + ", numero_parametros=" + numero_parametros + ", pertenece_funcion=" + pertenece_funcion + ", linea=" + linea + '}';
    }

    
    
    
}
