package com.mycompany.compilador;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class FrameLexico extends JFrame implements ActionListener {

    JPanel panelPrincipal;

    JButton botonArchivo, botonCompilar;

    JTextArea textArea;

    JTextArea areaTokens;
    JTextArea areaErrores;

    JLabel labelTokens;
    JLabel labelErrores;

    JScrollPane scrollCodigo;

    File file = null;

    Lexico analizador;

    public FrameLexico(String title) {

        this.setTitle(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setSize(1200, 700);

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
        

        panelBotones.add(botonArchivo);
        panelBotones.add(botonCompilar);

        panelCodigo.add(panelBotones, BorderLayout.SOUTH);

        JPanel panelResultados = new JPanel();
        panelResultados.setLayout(new GridLayout(4, 1));

        // TOKENS
        JPanel panelTokens = new JPanel(new BorderLayout());

        labelTokens = new JLabel("Tokens: 0");

        areaTokens = new JTextArea();
        areaTokens.setEditable(false);

        JScrollPane scrollTokens = new JScrollPane(areaTokens);

        panelTokens.add(labelTokens, BorderLayout.NORTH);
        panelTokens.add(scrollTokens, BorderLayout.CENTER);

        // ERRORES
        JPanel panelErrores = new JPanel(new BorderLayout());

        labelErrores = new JLabel("Errores: 0");

        areaErrores = new JTextArea();
        areaErrores.setEditable(false);

        JScrollPane scrollErrores = new JScrollPane(areaErrores);

        panelErrores.add(labelErrores, BorderLayout.NORTH);
        panelErrores.add(scrollErrores, BorderLayout.CENTER);

        panelResultados.add(panelTokens);
        panelResultados.add(panelErrores);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                panelCodigo,
                panelResultados
        );

        splitPane.setDividerLocation(700);

        panelPrincipal.add(splitPane, BorderLayout.CENTER);

        this.add(panelPrincipal);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == botonArchivo) {

            JFileChooser fileChooser = new JFileChooser();

            int respuesta = fileChooser.showOpenDialog(null);

            if (respuesta == JFileChooser.APPROVE_OPTION) {

                textArea.setText("");

                file = new File(fileChooser.getSelectedFile().getAbsolutePath());

                try {

                    String line = "";

                    BufferedReader br = new BufferedReader(new FileReader(file));

                    while ((line = br.readLine()) != null) {
                        textArea.append(line + "\n");
                    }

                    br.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (e.getSource() == botonCompilar) {

            try {
                analizador.consultar(textArea.getText());
            } catch (IOException ex) {
                System.getLogger(FrameLexico.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }

            areaTokens.setText("");

            for (Token token : analizador.getListaTokens()) {

                areaTokens.append(
                        "Token: " + token.getNumeroToken()
                        + " | Lexema: " + token.getLexema()
                        + " | Línea: " + token.getLinea()
                        + "\n"
                );
            }
            areaErrores.setText("");

            for (Error error : analizador.getListaErrores()) {

                areaErrores.append(
                        "Error: " + error.getNumeroError()
                        + " | Desc: " + error.getDescripcion()
                        + " | Lexema: " + error.getLexema()
                        + " | Línea: " + error.getLinea()
                        + " | Tipo: " + error.getTipoError()
                        + "\n"
                );
            }
            labelTokens.setText(
                    "Tokens: " + analizador.getListaTokens().size()
            );

            labelErrores.setText(
                    "Errores: " + analizador.getListaErrores().size()
            );
        }
        
        
    }
}