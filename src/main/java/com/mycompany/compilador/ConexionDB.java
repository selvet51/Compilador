


package com.mycompany.compilador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author goops
 */
public class ConexionDB {
    String url = "jdbc:mysql://localhost:3306/A23130417";
    String usuario = "root";
    String contrasena = "root";

    public void conectar(){
        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena)) {
        System.out.println("¡Conexión exitosa!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Simbolo> mostrarSimbolos(){
        List<Simbolo> lista = new ArrayList<>();
    String sql = "{CALL mostrarSimbolos()}";

    try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
         CallableStatement cstmt = conn.prepareCall(sql);
         ResultSet rs = cstmt.executeQuery()) {

        while (rs.next()) {
            Simbolo s = new Simbolo();
            s.setId(rs.getString("id"));
            s.setTipo(rs.getString("tipo"));
            s.setClase(rs.getString("clase"));
            s.setAmbito(rs.getInt("ambito"));
            s.setTamaño_arreglo(rs.getString("tamaño_arreglo"));
            s.setDimension_arreglo(rs.getInt("dimension_arreglo"));
            s.setNumero_parametros(rs.getInt("numero_parametros"));
            s.setPertenece_funcion(rs.getString("pertenece_funcion"));
            s.setLinea(rs.getInt("linea"));
            lista.add(s);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return lista;
    }
    
    public boolean insertarVariable(String id, String tipo, String clase, int ambito, int linea){
        String sql = "{CALL insertarVariable(?, ?, ?, ?, ?)}";

    try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
         CallableStatement cstmt = conn.prepareCall(sql)) {

        cstmt.setString(1, id);
        cstmt.setString(2, tipo);
        cstmt.setString(3, clase);
        cstmt.setInt(4, ambito);
        cstmt.setInt(5, linea);

        cstmt.execute();
        return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean borrarTodo(){
        String sql = "{CALL borrarTodo()}";

    try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
         CallableStatement cstmt = conn.prepareCall(sql)) {

        

        cstmt.execute();
        return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean aumentarArreglo(String id, String tamaño_arreglo, int dimension_arreglo){
        String sql = "{CALL aumentarArreglo(?, ?, ?)}";

    try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
         CallableStatement cstmt = conn.prepareCall(sql)) {

        cstmt.setString(1, id);
        cstmt.setString(2, tamaño_arreglo);
        cstmt.setInt(3, dimension_arreglo);

        cstmt.execute();
        return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public boolean insertarParametro(String id, String tipo, String clase, int ambito, String pertenece_funcion, int numero_parametros, int linea){
        String sql = "{CALL insertarParametro(?, ?, ?, ?, ?, ?, ?)}";

    try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
         CallableStatement cstmt = conn.prepareCall(sql)) {

        cstmt.setString(1, id);
        cstmt.setString(2, tipo);
        cstmt.setString(3, clase);
        cstmt.setInt(4, ambito);
        cstmt.setString(5, pertenece_funcion);
        cstmt.setInt(6, numero_parametros);
        cstmt.setInt(7, linea);

        cstmt.execute();
        return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean actualizarAmbitoFuncion(String id, String pertenece_funcion){
        String sql = "{CALL actualizarAmbitoFuncion(?, ?)}";

    try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
         CallableStatement cstmt = conn.prepareCall(sql)) {

        cstmt.setString(1, id);
        cstmt.setString(2, pertenece_funcion);
        cstmt.execute();
        return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean asignarRegistro(String id, int ambito, String registro){
        String sql = "{CALL asignarRegistro(?, ?, ?)}";

    try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
         CallableStatement cstmt = conn.prepareCall(sql)) {

        cstmt.setString(1, id);
        cstmt.setInt(2, ambito);
        cstmt.setString(3, registro);
        cstmt.execute();
        return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Integer> verificarSimbolo(String id) {
        List<Integer> lista = new ArrayList<>();
        String sql = "{CALL verificarSimbolo(?)}";

        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
            CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, id); 

            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getInt("ambito"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    public List<Integer> buscarPorId(String id) {
        List<Integer> lista = new ArrayList<>();
        String sql = "{CALL buscarPorId(?)}";

        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
            CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, id); 

            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getInt("ambito"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    
    public int[][] tablaAmbitos(int noAmbitos){
        int[][] arreglo = new int[noAmbitos][9];
        for(int k = 0; k < noAmbitos; k++){
            arreglo[k][0] = k;
        }
    String sql = "{CALL tablaAmbitos()}";
    int i = 0;

    try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
         CallableStatement cstmt = conn.prepareCall(sql);
         ResultSet rs = cstmt.executeQuery()) {

        while (rs.next()) {
            i = rs.getInt(1);
            arreglo[i][0] = Integer.valueOf(rs.getInt(1));
            arreglo[i][1] = Integer.valueOf(rs.getInt(2));
            arreglo[i][2] = Integer.valueOf(rs.getInt(3));
            arreglo[i][3] = Integer.valueOf(rs.getInt(4));
            arreglo[i][4] = Integer.valueOf(rs.getInt(5));
            arreglo[i][5] = Integer.valueOf(rs.getInt(6));
            arreglo[i][6] = Integer.valueOf(rs.getInt(7));
            arreglo[i][7] = Integer.valueOf(rs.getInt(8));
            arreglo[i][8] = Integer.valueOf(rs.getInt(9));
        }
        

    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return arreglo;
    }
    
    
    public List<Integer> tablaTotales(){
        List<Integer> lista = new ArrayList<>();
    String sql = "{CALL tablaTotales()}";

    try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
         CallableStatement cstmt = conn.prepareCall(sql);
         ResultSet rs = cstmt.executeQuery()) {

        while (rs.next()) {
            lista.add(Integer.valueOf(rs.getInt(1)));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return lista;
    }
    
    public List<Simbolo> mostrarFinal(){
        List<Simbolo> lista = new ArrayList<>();
    String sql = "{CALL mostrarSimbolos()}";

    try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
         CallableStatement cstmt = conn.prepareCall(sql);
         ResultSet rs = cstmt.executeQuery()) {

        while (rs.next()) {
            Simbolo s = new Simbolo();
            s.setId(rs.getString("id"));
            s.setTipo(rs.getString("tipo"));
            s.setClase(rs.getString("clase"));
            s.setAmbito(rs.getInt("ambito"));
            s.setTamaño_arreglo(rs.getString("tamaño_arreglo"));
            s.setDimension_arreglo(rs.getInt("dimension_arreglo"));
            s.setNumero_parametros(rs.getInt("numero_parametros"));
            s.setPertenece_funcion(rs.getString("pertenece_funcion"));
            lista.add(s);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return lista;
    }
    
    public List<Integer> ObtenerTotalesSImbolos(){
        List<Integer> lista = new ArrayList<>();
    String sql = "{CALL ObtenerTotalesSimbolos()}";

    try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
         CallableStatement cstmt = conn.prepareCall(sql);
         ResultSet rs = cstmt.executeQuery()) {

        while (rs.next()) {
            lista.add(Integer.valueOf(rs.getInt(1)));
            lista.add(Integer.valueOf(rs.getInt(2)));
            lista.add(Integer.valueOf(rs.getInt(3)));
            lista.add(Integer.valueOf(rs.getInt(4)));
            lista.add(Integer.valueOf(rs.getInt(5)));
            lista.add(Integer.valueOf(rs.getInt(6)));
            lista.add(Integer.valueOf(rs.getInt(7)));
            lista.add(Integer.valueOf(rs.getInt(8)));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return lista;
    }
    
    
    public int contarSimbolos() {
    String sql = "{CALL ContarSimbolos()}";

    try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
         CallableStatement cstmt = conn.prepareCall(sql);
         ResultSet rs = cstmt.executeQuery()) {

        if (rs.next()) {
            return rs.getInt("Total");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return 0; // o -1 si prefieres distinguir "error" de "tabla vacía"
}
}
