package com.mycompany.compilador;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class FrameLexico extends JFrame implements ActionListener {

    private static final Font FUENTE_TABLA = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    private static final Color FILA_PAR = Color.WHITE;
    private static final Color FILA_IMPAR = new Color(242, 245, 250);
    private static final String[] TIPOS_ERROR = {"Léxico", "Sintáxis", "Ámbitos", "Semántica 1", "Semántica 2"};

    JPanel panelPrincipal;

    JButton botonArchivo, botonCompilar, botonGuardar;

    JTextArea textArea;

    DefaultTableModel modeloTokens, modeloErrores;
    JTable tablaTokens, tablaErrores;
    TitledBorder bordeTokens, bordeErrores;

    JScrollPane scrollCodigo;

    File file = null;

    Lexico analizador;

    public FrameLexico(String title) {

        this.setTitle(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setSize(1300, 750);

        analizador = new Lexico();

        panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelCodigo = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("Editor de Código");
        panelCodigo.add(titulo, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(true);

        JTextArea lineNumbers = new JTextArea("1");
        lineNumbers.setEditable(false);
        lineNumbers.setBackground(Color.LIGHT_GRAY);

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i <= 500; i++) {
            sb.append(i).append("\n");
        }

        lineNumbers.setText(sb.toString());

        scrollCodigo = new JScrollPane(
                textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        );

        scrollCodigo.setRowHeaderView(lineNumbers);

        panelCodigo.add(scrollCodigo, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();

        botonArchivo = new JButton("Escoger Archivo");
        botonArchivo.addActionListener(this);

        botonCompilar = new JButton("Compilar");
        botonCompilar.addActionListener(this);

        botonGuardar = new JButton("Guardar Excel");
        botonGuardar.addActionListener(this);
        botonGuardar.setEnabled(false); // hasta que se compile por primera vez

        panelBotones.add(botonArchivo);
        panelBotones.add(botonCompilar);
        panelBotones.add(botonGuardar);

        panelCodigo.add(panelBotones, BorderLayout.SOUTH);

        // TOKENS
        modeloTokens = crearModelo("Token", "Lexema", "Línea");
        tablaTokens = crearTabla(modeloTokens);
        tablaTokens.getColumnModel().getColumn(0).setPreferredWidth(70);
        tablaTokens.getColumnModel().getColumn(1).setPreferredWidth(220);
        tablaTokens.getColumnModel().getColumn(2).setPreferredWidth(60);
        bordeTokens = BorderFactory.createTitledBorder("Tokens (0)");
        JScrollPane scrollTokens = new JScrollPane(tablaTokens);
        scrollTokens.setBorder(bordeTokens);

        // ERRORES
        modeloErrores = crearModelo("Error", "Descripción", "Lexema", "Línea", "Tipo");
        tablaErrores = crearTabla(modeloErrores);
        tablaErrores.setDefaultRenderer(Object.class, new RendererErrores());
        tablaErrores.getColumnModel().getColumn(0).setPreferredWidth(55);
        tablaErrores.getColumnModel().getColumn(1).setPreferredWidth(330);
        tablaErrores.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaErrores.getColumnModel().getColumn(3).setPreferredWidth(50);
        tablaErrores.getColumnModel().getColumn(4).setPreferredWidth(85);
        bordeErrores = BorderFactory.createTitledBorder("Errores (0)");
        JScrollPane scrollErrores = new JScrollPane(tablaErrores);
        scrollErrores.setBorder(bordeErrores);

        JSplitPane panelResultados = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTokens, scrollErrores);
        panelResultados.setResizeWeight(0.5);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                panelCodigo,
                panelResultados
        );

        splitPane.setDividerLocation(600);

        panelPrincipal.add(splitPane, BorderLayout.CENTER);

        this.add(panelPrincipal);

        this.setVisible(true);
        panelResultados.setDividerLocation(0.5);
    }

    /** Modelo de tabla con esas columnas, sin celdas editables. */
    private static DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    /** Tabla de resultados: fuente legible, encabezado en negrita y filas alternadas. */
    private static JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setFont(FUENTE_TABLA);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setFont(FUENTE_TABLA.deriveFont(Font.BOLD));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tabla.setFillsViewportHeight(true);
        tabla.setDefaultRenderer(Object.class, new RendererFilas());
        return tabla;
    }

    /** Pinta las filas alternadas y muestra el texto completo de la celda como tooltip. */
    private static class RendererFilas extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
            if (!seleccionada) {
                setBackground(colorFila(tabla, fila));
            }
            setToolTipText(valor == null ? null : valor.toString());
            setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            return this;
        }

        protected Color colorFila(JTable tabla, int fila) {
            return fila % 2 == 0 ? FILA_PAR : FILA_IMPAR;
        }
    }

    /** En la tabla de errores cada fila lleva un color suave según su tipo. */
    private static class RendererErrores extends RendererFilas {
        @Override
        protected Color colorFila(JTable tabla, int fila) {
            Object tipo = tabla.getModel().getValueAt(tabla.convertRowIndexToModel(fila), 4);
            Color base;
            switch (tipo == null ? "" : tipo.toString()) {
                case "Léxico": base = new Color(255, 236, 236); break;
                case "Sintáxis": base = new Color(255, 244, 226); break;
                case "Ámbitos": base = new Color(232, 242, 255); break;
                case "Semántica 1": base = new Color(240, 234, 255); break;
                case "Semántica 2": base = new Color(230, 246, 234); break;
                default: return super.colorFila(tabla, fila);
            }
            // Las filas impares un poco más oscuras para que se distingan entre sí.
            return fila % 2 == 0 ? base
                    : new Color(Math.max(base.getRed() - 12, 0), Math.max(base.getGreen() - 12, 0), Math.max(base.getBlue() - 12, 0));
        }
    }

    /** Abre la ventana nativa del sistema (en Windows, la del Explorador) para cargar un archivo de código. */
    private void escogerArchivo() {
        FileDialog dialogo = new FileDialog(this, "Escoger archivo", FileDialog.LOAD);
        dialogo.setVisible(true);
        if (dialogo.getFile() == null) {
            return; // cancelado
        }

        file = new File(dialogo.getDirectory(), dialogo.getFile());
        textArea.setText("");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                textArea.append(line + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "No se pudo leer el archivo:\n" + ex.getMessage(),
                    "Escoger archivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre la ventana nativa de "Guardar como" y copia ahí el Excel que se generó en la carpeta del
     * proyecto. Si se cancela, el Excel sigue solo en la carpeta del proyecto.
     */
    private void guardarExcel() {
        File generado = new File(Lexico.NOMBRE_EXCEL);
        if (!generado.exists()) {
            JOptionPane.showMessageDialog(this, "Primero compila el código para generar el Excel.",
                    "Guardar Excel", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FileDialog dialogo = new FileDialog(this, "Guardar Excel", FileDialog.SAVE);
        dialogo.setFile(Lexico.NOMBRE_EXCEL);
        dialogo.setVisible(true);
        if (dialogo.getFile() == null) {
            return; // cancelado
        }

        String nombre = dialogo.getFile();
        if (!nombre.toLowerCase().endsWith(".xlsx")) {
            nombre += ".xlsx";
        }
        guardarExcelEn(new File(dialogo.getDirectory(), nombre));
    }

    /** Copia el Excel generado a destino y avisa el resultado. */
    void guardarExcelEn(File destino) {
        try {
            File generado = new File(Lexico.NOMBRE_EXCEL).getAbsoluteFile();
            if (!generado.equals(destino.getAbsoluteFile())) {
                try (InputStream entrada = new FileInputStream(generado);
                        OutputStream salida = new FileOutputStream(destino)) {
                    entrada.transferTo(salida);
                }
            }
            JOptionPane.showMessageDialog(this, "Excel guardado en:\n" + destino.getAbsolutePath(),
                    "Guardar Excel", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "No se pudo guardar el Excel:\n" + ex.getMessage(),
                    "Guardar Excel", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Compila el código del editor y llena las tablas de tokens y errores. */
    private void compilar() {
        try {
            analizador.consultar(textArea.getText());
        } catch (IOException ex) {
            System.getLogger(FrameLexico.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        modeloTokens.setRowCount(0);
        for (Token token : analizador.getListaTokens()) {
            modeloTokens.addRow(new Object[]{token.getNumeroToken(), token.getLexema(), token.getLinea()});
        }

        modeloErrores.setRowCount(0);
        for (Error error : analizador.getListaErrores()) {
            modeloErrores.addRow(new Object[]{error.getNumeroError(), error.getDescripcion(), error.getLexema(),
                error.getLinea(), error.getTipoError()});
        }

        bordeTokens.setTitle("Tokens (" + analizador.getListaTokens().size() + ")");
        int[] porTipo = new int[TIPOS_ERROR.length];
        for (Error error : analizador.getListaErrores()) {
            for (int i = 0; i < TIPOS_ERROR.length; i++) {
                if (TIPOS_ERROR[i].equals(error.getTipoError())) {
                    porTipo[i]++;
                }
            }
        }
        StringBuilder titulo = new StringBuilder("Errores (" + analizador.getListaErrores().size() + "):");
        for (int i = 0; i < TIPOS_ERROR.length; i++) {
            titulo.append(i == 0 ? "  " : "  ·  ").append(TIPOS_ERROR[i]).append(" ").append(porTipo[i]);
        }
        bordeErrores.setTitle(titulo.toString());
        repaint();

        botonGuardar.setEnabled(true);
        guardarExcel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == botonArchivo) {
            escogerArchivo();
        }

        if (e.getSource() == botonCompilar) {
            compilar();
        }

        if (e.getSource() == botonGuardar) {
            guardarExcel();
        }
    }
}
