package org.andres.cliente.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.ArrayList;

public class ClientePrincipalArchivo extends JFrame implements ActionListener {

    private final String titulo = "Cliente principal";
    private final String letra = "Arial";
    private final String error = "Error";

    private DatagramSocket socketCliente;
    private int miPuertoCliente;
    private int miIdCliente;

    private ArrayList<Integer> listaServidores = new ArrayList<>();
    private File[] archivo = new File[1];

    private String carpetaCliente;

    private JLabel labelIconoCliente;
    private JLabel tituloCliente;
    private JLabel tituloServidores;
    private JLabel tituloMensajes;
    private JLabel tituloFuncionesMensajes;
    private JLabel tituloArchivos;


    private JTextField textoArchivo;

    private JComboBox comboListaServidores;
    private JComboBox comboListaFunciones;

    private JButton botonConexSer;
    private JButton botonDesconexSer;
    private JButton botonBuscarServidores;
    private JButton botonSelecArch;
    private JButton botonEnviarArch;

    private JFileChooser escogerArchivo;

    private JScrollPane barraAreaMensajes;
    private JTextArea areaMensajes;

    private ImageIcon imagenCliente;

    public ClientePrincipalArchivo(){
        iniciarlizarComponentes();
        dimensionar();
        adicionar();
        visualizar();
        accionar();
    }

    public void iniciarlizarComponentes(){

        this.labelIconoCliente = new JLabel("", JLabel.CENTER);
        this.tituloCliente = new JLabel("Cliente UDP: DFRACK");
        this.tituloServidores = new JLabel("lista de Servidores");
        this.tituloMensajes = new JLabel("Mensajes");
        this.tituloFuncionesMensajes = new JLabel("Enviar Archivo a:");
        this.tituloArchivos = new JLabel("Seleccionar Archivos");

        this.textoArchivo = new JTextField();
        textoArchivo.setEditable(false);

        this.comboListaServidores = new JComboBox<>();
        this.comboListaFunciones = new JComboBox<>();

        this.botonBuscarServidores = new JButton("Buscar ■");
        this.botonBuscarServidores.setBackground(Color.decode("#BAC6E8"));
        this.botonBuscarServidores.setFont(new Font(letra, Font.BOLD, 13));

        this.botonConexSer = new JButton("Conectar ▲");
        this.botonConexSer.setBackground(Color.decode("#DFEDDD"));
        this.botonConexSer.setFont(new Font(letra, Font.BOLD, 12));
        this.botonConexSer.setEnabled(false);

        this.botonDesconexSer = new JButton("Desconectar ▼");
        this.botonDesconexSer.setBackground(Color.decode("#EDDDDD"));
        this.botonDesconexSer.setFont(new Font(letra, Font.BOLD, 12));
        this.botonDesconexSer.setEnabled(false);

        this.botonSelecArch = new JButton("Seleccionar ♦");
        this.botonSelecArch.setBackground(Color.decode("#B4B6CC"));
        this.botonSelecArch.setFont(new Font(letra, Font.BOLD, 12));
        this.botonSelecArch.setEnabled(false);

        this.botonEnviarArch = new JButton("Enviar ►");
        this.botonEnviarArch.setBackground(Color.decode("#B0C2BF"));
        this.botonEnviarArch.setFont(new Font(letra, Font.BOLD, 12));
        this.botonEnviarArch.setEnabled(false);

        this.areaMensajes = new JTextArea("");

        this.imagenCliente = new ImageIcon("iconos/iconoCliente.png");
        this.labelIconoCliente.setIcon(this.imagenCliente);

        this.areaMensajes = new JTextArea(20,30);
        this.areaMensajes.setEditable(false);

        this.barraAreaMensajes = new JScrollPane();
        this.barraAreaMensajes.setViewportView(this.areaMensajes);

        this.escogerArchivo = new JFileChooser();
        escogerArchivo.setDialogTitle("Escoger un archivo a enviar");
    }

    public void dimensionar(){

        setLayout(null);

        this.labelIconoCliente.setBounds(136,20,64,64);
        this.tituloCliente.setBounds(106,100, 140,10);

        this.tituloServidores.setBounds(30,130,140,10);
        this.comboListaServidores.setBounds(30,150,140,25);
        this.botonBuscarServidores.setBounds(200, 145, 90,30);

        this.botonConexSer.setBounds(40,190, 110,30);
        this.botonDesconexSer.setBounds(170,190, 125,30);

        this.tituloFuncionesMensajes.setBounds(30, 250, 100, 10);
        this.comboListaFunciones.setBounds(30,270, 130, 25);

        this.tituloArchivos.setBounds(30, 315, 150, 10);
        this.textoArchivo.setBounds(30, 335, 250, 25);

        this.botonSelecArch.setBounds(30, 370, 100, 30);
        this.botonEnviarArch.setBounds(170, 370, 100,30);

        this.tituloMensajes.setBounds(30,425, 100,10);
        this.barraAreaMensajes.setBounds(30, 450, 275, 130);
    }

    public void adicionar(){

        this.add(labelIconoCliente);
        this.add(tituloCliente);
        this.add(tituloServidores);
        this.add(tituloMensajes);
        this.add(tituloFuncionesMensajes);
        this.add(tituloArchivos);

        this.add(textoArchivo);

        this.add(comboListaServidores);
        this.add(comboListaFunciones);

        this.add(botonBuscarServidores);
        this.add(botonConexSer);
        this.add(botonDesconexSer);
        this.add(botonSelecArch);
        this.add(botonEnviarArch);

        this.add(barraAreaMensajes);
    }

    public void visualizar(){
        this.setTitle(titulo);
        this.setVisible(true);
        this.setSize(350,640);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.WHITE);
    }

    public void accionar(){
        this.botonConexSer.addActionListener(this);
        this.botonDesconexSer.addActionListener(this);
        this.botonBuscarServidores.addActionListener(this);
        this.botonSelecArch.addActionListener(this);
        this.botonEnviarArch.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.botonConexSer){
            int capturaPort = capturarPuerto();
            if (capturaPort != 0) conectar(capturaPort);
        }
        else if(e.getSource() == this.botonDesconexSer){
            desconectar(Integer.parseInt(String.valueOf(comboListaServidores.getSelectedItem())));
        }
        else if(e.getSource() == this.botonBuscarServidores){
            actualizarServidoresCombo();

            if(!listaServidores.isEmpty()){
                this.botonConexSer.setEnabled(true);
            }
        }
        else if(e.getSource() == this.botonSelecArch){
            seleccionarArchivo();
            this.botonEnviarArch.setEnabled(true);
        }
        else if(e.getSource() == this.botonEnviarArch){
            enviarArchivoUDP(capturarPuerto());
        }

    }

    private int capturarPuerto(){
        return (int) comboListaServidores.getSelectedItem();
    }

    public void conectar(int puerto){
        JOptionPane.showMessageDialog(this,
                "Conectando con servidor",
                "Verificando ...",
                JOptionPane.INFORMATION_MESSAGE);

        try {

            socketCliente = new DatagramSocket(); // puerto aleatorio válido
            miPuertoCliente = socketCliente.getLocalPort();

            InetAddress direccion = InetAddress.getByName("localhost");

            String mensaje = "CONNECT:"+ miPuertoCliente;
            byte[] buffer = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, direccion, puerto);
            socketCliente.send(paquete);

            areaMensajes.append("Conexión con servidor UDP realizada \n");
            System.out.println("hola" + miPuertoCliente);
            escucharServidor();
            configurarBotones(1);
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo realizar la conexión al servidor indicado",
                    error,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void desconectar(int puerto) {
        try {

            InetAddress direccion = InetAddress.getByName("localhost");

            String mensaje = "DESCONECTAR:" + miIdCliente;
            byte[] buffer = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, direccion, puerto);
            socketCliente.send(paquete);

            areaMensajes.setText("Se ha desconectado del servidor\n");

            configurarBotones(2);
            comboListaFunciones.removeAllItems();

            // cerrar socket
            socketCliente.close();
            socketCliente = null;

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al intentar desconectarse del servidor",
                    error,
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    public void visualizarServidoresUDP() {
        listaServidores.clear();
        int timeout = 1; // ms

        for (int puerto = 15000; puerto <= 15100; puerto++) {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(timeout);

                byte[] sendData = "ping".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(
                        sendData, sendData.length, InetAddress.getByName("localhost"), puerto);
                socket.send(sendPacket);

                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                socket.receive(receivePacket);
                listaServidores.add(puerto);

                break;

            } catch (IOException e) {
                // No hay servidor en este puerto
            }
        }
    }

    public void actualizarServidoresCombo(){
        eliminarListaServidores();
        visualizarServidoresUDP();
        SwingUtilities.invokeLater(() -> {
            comboListaServidores.removeAllItems();
            for (Integer servidor : listaServidores) {
                comboListaServidores.addItem(servidor);
            }
        });
    }

    public void eliminarListaServidores(){
        listaServidores = new ArrayList<>();
        comboListaServidores.removeAllItems();
    }

    public void configurarBotones(int decision){

        switch (decision){
            case 1:
                this.botonConexSer.setEnabled(false);
                this.botonDesconexSer.setEnabled(true);
                this.botonBuscarServidores.setEnabled(false);
                this.comboListaServidores.setEnabled(false);
                this.botonSelecArch.setEnabled(true);
                break;
            case 2:
                this.botonDesconexSer.setEnabled(false);
                this.botonConexSer.setEnabled(true);
                this.botonBuscarServidores.setEnabled(true);
                this.comboListaServidores.setEnabled(true);
                this.botonSelecArch.setEnabled(false);
                this.botonEnviarArch.setEnabled(false);
                break;
            default:
                // no acciona nada
        }
    }

    public void seleccionarArchivo(){
        if(escogerArchivo.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            archivo[0] = escogerArchivo.getSelectedFile();
            textoArchivo.setText("Archivo: " + archivo[0].getName());
        }
    }

    public void enviarArchivoUDP(int puerto) {
        try {
            if (archivo[0] == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un archivo primero", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            InetAddress direccion = InetAddress.getByName("localhost");
            String destino = (String) comboListaFunciones.getSelectedItem();

            // Enviar encabezado
            String encabezado = "FILE:" + destino + ":" + archivo[0].getName() + ":" + archivo[0].length() + ":" + miIdCliente;
            DatagramPacket headerPacket = new DatagramPacket(encabezado.getBytes(), encabezado.getBytes().length, direccion, puerto);
            socketCliente.send(headerPacket);

            // Enviar contenido en bloques
            byte[] buffer = new byte[1024];
            try (FileInputStream fis = new FileInputStream(archivo[0])) {
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    DatagramPacket filePacket = new DatagramPacket(buffer, bytesRead, direccion, puerto);
                    socketCliente.send(filePacket);
                }
            }

            // Enviar EOF
            byte[] fin = "EOF".getBytes();
            DatagramPacket finPacket = new DatagramPacket(fin, fin.length, direccion, puerto);
            socketCliente.send(finPacket);

            areaMensajes.append("Archivo enviado a " + destino + ": " + archivo[0].getName() + "\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void escucharServidor() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);

            while (true) {
                try{
                    socketCliente.receive(paquete);
                    String msg = new String(paquete.getData(), 0, paquete.getLength());

                    if(msg.startsWith("ID:")){
                        miIdCliente = Integer.parseInt(msg.substring(3).trim());
                        this.carpetaCliente = "archivos/clientes";

                    } else if (msg.startsWith("LIST:")) {
                        String lista = msg.substring(5);
                        String[] elementos = lista.split(",");
                        SwingUtilities.invokeLater(() -> {
                            comboListaFunciones.removeAllItems();
                            for (String elem : elementos) {
                                if (!elem.isBlank()) comboListaFunciones.addItem(elem);
                            }
                        });

                    } else if (msg.startsWith("FILE:")) {
                        recibirArchivo(msg);
                    }

                } catch (SocketException e) {
                    if (socketCliente.isClosed()) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void recibirArchivo(String encabezado) {
        try {
            String[] partes = encabezado.split(":");
            String nombreArchivo = partes[2];

            File archivoDestino = new File(carpetaCliente, nombreArchivo);
            try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                long total = 0;
                byte[] buffer = new byte[1024];

                while (true) {
                    DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                    socketCliente.receive(dp);

                    String msg = new String(dp.getData(), 0, dp.getLength());
                    if ("EOF".equals(msg)) break;

                    fos.write(dp.getData(), 0, dp.getLength());
                    total += dp.getLength();
                }

                areaMensajes.append("Archivo recibido: " + nombreArchivo + " (" + total + " bytes)\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run(){
                new ClientePrincipalArchivo();
            }
        });
    }

}
