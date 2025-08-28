package org.andres.servidor.gui;

import org.andres.conexion.ClienteUDP;
import org.andres.dto.MiDatagrama;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorPrincipalArchivo extends JFrame implements ActionListener {

    private final String titulo = "Servidor";
    private final String letra = "Arial";
    private final String error = "Error";
    private DatagramSocket socketudp;
    private volatile boolean ejecucion = false;

    private final ArrayList<ClienteUDP> clientesServ = new ArrayList<>();
    private final ArrayList<Integer> idEnUso = new ArrayList<>();
    private JLabel labelIconoServidor;
    private JLabel tituloServidor;
    private JLabel tituloEjecucion;
    private JLabel tituloNumPuerto;
    private JLabel tituloClienConec;
    private JLabel tituloMensajes;

    private JTextField campoTextEjecEn;
    private JTextField campoTextPuerto;

    private JTextArea areaMensajes;

    private DefaultComboBoxModel comboDefaultclientes;
    private JComboBox comboClientes;

    private JButton botonEncerderServ;
    private JButton botonApagarSer;

    private JScrollPane barraDesAreaMen;

    private ImageIcon imagenServidor;

    public ServidorPrincipalArchivo(){
        inicializarComponentes();
        dimensionar();
        adicionar();
        visualizar();
        accionar();
    }

    public void inicializarComponentes(){
        this.labelIconoServidor = new JLabel("", JLabel.CENTER);

        this.tituloServidor = new JLabel("Servidor Pricipal");
        this.tituloServidor.setFont(new Font(letra, Font.BOLD, 20));

        this.tituloEjecucion = new JLabel("Servidor ejecutandose en:");
        this.tituloEjecucion.setFont(new Font(letra, Font.BOLD, 15));

        this.tituloNumPuerto = new JLabel("Puerto:");
        this.tituloNumPuerto.setFont(new Font(letra, Font.BOLD, 15));

        this.tituloClienConec = new JLabel("Clientes conectados:");
        this.tituloClienConec.setFont(new Font(letra, Font.BOLD, 15));

        this.tituloMensajes = new JLabel("Mensajes");
        this.tituloMensajes.setFont(new Font(letra, Font.BOLD, 15));

        this.campoTextEjecEn = new JTextField();
        this.campoTextEjecEn.setEditable(false);

        this.campoTextPuerto = new JTextField();
        this.campoTextPuerto.setEditable(false);

        this.botonEncerderServ = new JButton("Encender ▲");
        this.botonEncerderServ.setBackground(Color.decode("#DFEDDD"));
        this.botonEncerderServ.setFont(new Font(letra, Font.BOLD, 15));

        this.botonApagarSer = new JButton("Apagar ▼");
        this.botonApagarSer.setBackground(Color.decode("#EDDDDD"));
        this.botonApagarSer.setFont(new Font(letra, Font.BOLD, 15));
        this.botonApagarSer.setEnabled(false);

        this.comboClientes = new JComboBox<>();
        this.comboClientes.setEditable(false);

        this.areaMensajes = new JTextArea(20, 30);

        this.imagenServidor = new ImageIcon("iconos/iconoServidor.jpg");
        this.labelIconoServidor.setIcon(this.imagenServidor);

        this.barraDesAreaMen = new JScrollPane();
        this.barraDesAreaMen.setViewportView(this.areaMensajes);
    }

    public void dimensionar(){
        setLayout(null);

        this.labelIconoServidor.setBounds(160,20,64,64);

        this.tituloServidor.setBounds(116, 100, 170, 20);

        this.botonEncerderServ.setBounds(50, 150, 130, 30);
        this.botonApagarSer.setBounds(212, 150, 120, 30);

        this.tituloEjecucion.setBounds(35, 220, 192, 20);
        this.campoTextEjecEn.setBounds(35, 250,192, 25);

        this.tituloNumPuerto.setBounds(266, 220, 60, 20);
        this.campoTextPuerto.setBounds(266, 250,60, 25);

        this.tituloClienConec.setBounds(35, 290, 160, 20);
        this.comboClientes.setBounds(35, 320, 165, 25);

        this.tituloMensajes.setBounds(35,380,165,20);
        this.barraDesAreaMen.setBounds(35,410,315,180);
    }

    public void adicionar(){
        add(labelIconoServidor);

        add(tituloServidor);
        add(tituloEjecucion);
        add(tituloNumPuerto);
        add(tituloClienConec);
        add(tituloMensajes);

        add(campoTextEjecEn);
        add(campoTextPuerto);

        add(comboClientes);

        add(botonEncerderServ);
        add(botonApagarSer);

        add(barraDesAreaMen);

    }

    public void visualizar(){
        this.setTitle(titulo);
        this.setVisible(true);
        this.setSize(400,650);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.WHITE);
    }

    public void accionar(){
        this.botonEncerderServ.addActionListener(this);
        this.botonApagarSer.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.botonEncerderServ){
            int puerto = definirPuertoServ();
            encenderServidor(puerto);
        } else if (e.getSource() == this.botonApagarSer) {
            apagarServidor();
        }
    }

    public void encenderServidor(int puerto){
        byte[] buf = new byte[1000];

        new Thread(() -> {
            DatagramPacket dp = null;
            try {
                socketudp = new DatagramSocket(puerto);
                ejecucion = true;

                this.campoTextPuerto.setText(String.valueOf(puerto));

                configurarBotones(1);

                while (ejecucion) {

                    dp = new DatagramPacket(buf, buf.length);

                    socketudp.receive(dp); // aquí se bloquea esperando mensajes

                    String elmensaje = new String(dp.getData(), 0, dp.getLength());

                    if (elmensaje.equalsIgnoreCase("ping")) {

                        String respuesta = "pong";
                        byte[] datosRespuesta = respuesta.getBytes();

                        DatagramPacket paqueteRespuesta = new DatagramPacket(
                                datosRespuesta,
                                datosRespuesta.length,
                                dp.getAddress(),
                                dp.getPort()
                        );

                        socketudp.send(paqueteRespuesta);
                        continue;
                    }

                    if (elmensaje.startsWith("CONNECT:")) {

                        String[] partes = elmensaje.split(":");
                        int puertoEscucha = Integer.parseInt(partes[1]);

                        int nuevoId = generarIdUnico(idEnUso);
                        idEnUso.add(nuevoId);

                        ClienteUDP nuevoCliente = new ClienteUDP(nuevoId, dp.getAddress(), puertoEscucha);
                        clientesServ.add(nuevoCliente);

                        areaMensajes.append("Cliente " + nuevoId + " conectado desde " + dp.getAddress().getHostAddress() + ":" + puertoEscucha + "\n");

                        actualizarCombo();
                        enviarIdACliente(nuevoCliente);
                        enviarListaClientes();
                    }
                    else if(elmensaje.startsWith("DESCONECTAR:")) {

                        String[] partes = elmensaje.split(":");
                        int clienteId = Integer.parseInt(partes[1]);

                        areaMensajes.append("Cliente " + clienteId + " se ha desconectado.\n");

                        idEnUso.remove(idEnUso.indexOf(clienteId));

                        clientesServ.removeIf(clienteUDP -> clienteUDP.getId() == clienteId);

                        actualizarCombo();
                    }
                    else if (elmensaje.startsWith("FILE:")) {
                        procesarArchivo(dp, elmensaje);
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(ServidorPrincipalArchivo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
    }

    private void apagarServidor(){
        ejecucion = false;
        if (socketudp != null && !socketudp.isClosed()) {
            socketudp.close(); // Esto libera el puerto inmediatamente
        }
        configurarBotones(2);
        areaMensajes.append("Servidor UDP apagado.\n");
    }

    public void configurarBotones(int decision){

        switch (decision){
            case 1:
                this.botonEncerderServ.setEnabled(false);
                this.botonApagarSer.setEnabled(true);
                break;
            case 2:
                this.botonEncerderServ.setEnabled(true);
                this.botonApagarSer.setEnabled(false);

                this.campoTextEjecEn.setText("");
                this.campoTextPuerto.setText("");
                break;
            default:
        }
    }
    public int definirPuertoServ(){
        Random rand = new Random();
        int puertoVer;
        do {
            puertoVer = rand.nextInt(15100 - 15000 + 1) + 15000;
        } while (verificarExistenciaServidor(puertoVer));
        return puertoVer;
    }
    public boolean verificarExistenciaServidor(int puertoVer) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(500);

            String mensaje = "ping";
            byte[] datos = mensaje.getBytes();
            InetAddress direccion = InetAddress.getByName("localhost");

            DatagramPacket paquete = new DatagramPacket(datos, datos.length, direccion, puertoVer);
            socket.send(paquete);

            byte[] buffer = new byte[1024];
            DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);

            socket.receive(respuesta);

            String resp = new String(respuesta.getData(), 0, respuesta.getLength());

            socket.close();

            return resp.equalsIgnoreCase("pong");
        } catch (IOException e) {

            return false;
        }
    }

    private void enviarIdACliente(ClienteUDP cliente) {
        String mensaje = "ID:" + cliente.getId() + " ";

        byte[] datos = mensaje.getBytes();
        try {
            DatagramPacket paquete = new DatagramPacket(
                    datos,
                    datos.length,
                    cliente.getDireccion(),
                    cliente.getPuerto()
            );
            socketudp.send(paquete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void procesarArchivo(DatagramPacket dp, String encabezado) {
        try {
            // FILE:idCliente:Destino:Nombre:Tamaño
            String[] partes = encabezado.split(":");
            String destino = partes[1];
            String nombreArchivo = partes[2];
            long tamanio = Long.parseLong(partes[3]);
            int idCliente = Integer.parseInt(String.valueOf(partes[4]));


            if (destino.equalsIgnoreCase("Servidor")) {
                File carpeta = new File("Archivos/Servidor");
                if (!carpeta.exists()) carpeta.mkdirs();
                File archivoRecibido = new File(carpeta, nombreArchivo);

                areaMensajes.append("Cliente " + idCliente + " envio archivo a servidor \n");
                try (FileOutputStream fos = new FileOutputStream(archivoRecibido)) {
                    long total = 0;
                    byte[] buffer = new byte[1024];
                    while (total < tamanio) {
                        DatagramPacket bloque = new DatagramPacket(buffer, buffer.length);
                        socketudp.receive(bloque);
                        fos.write(bloque.getData(), 0, bloque.getLength());
                        total += bloque.getLength();
                    }
                }
                areaMensajes.append("Archivo guardado en Servidor: " + nombreArchivo + "\n");

            } else if (destino.startsWith("Cliente")) {
                int id = Integer.parseInt(destino.split(" ")[1]);
                reenviarACliente(encabezado, id, idCliente);
            }

        } catch (Exception e) {
            e.printStackTrace();
            areaMensajes.append("Error al procesar archivo\n");
        }
    }
    public void actualizarCombo() {
        SwingUtilities.invokeLater(() -> {
            try{
                comboClientes.removeAllItems();
                int i = 0;
                for (ClienteUDP cliente : clientesServ) {
                    comboClientes.addItem("Cliente " + idEnUso.get(i));
                    i++;
                }
            } catch (Exception ex){
                // error por temas de cantidad de datos
            }
        });
    }
    private int generarIdUnico(ArrayList<Integer> idsActuales) {
        int id = 1;
        while (idsActuales.contains(id)) {
            id++;
        }
        return id;
    }

    private void enviarListaClientes() {
        StringBuilder sb = new StringBuilder();
        sb.append("LIST:Servidor");

        for (ClienteUDP c : clientesServ) {
            sb.append(",Cliente ").append(c.getId());
        }

        String listaFinal = sb.toString();
        byte[] datos = listaFinal.getBytes();

        for (ClienteUDP c : clientesServ) {
            try {
                DatagramPacket paquete = new DatagramPacket(
                        datos,
                        datos.length,
                        c.getDireccion(),
                        c.getPuerto()
                );
                socketudp.send(paquete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void reenviarACliente(String encabezado, int id, int idEnvia) throws IOException {
        for (ClienteUDP c : clientesServ) {
            if (c.getId() == id) {
                DatagramPacket headerPacket = new DatagramPacket(encabezado.getBytes(), encabezado.getBytes().length, c.getDireccion(), c.getPuerto());
                socketudp.send(headerPacket);

                ArrayList<ClienteUDP> soloUno = new ArrayList<>();
                soloUno.add(c);
                reenviarContenidoAClientes(soloUno);

                areaMensajes.append("Cliente "+ idEnvia + " envio archivo a Cliente " + id + "\n");
                break;
            }
        }
    }

    private void reenviarContenidoAClientes(ArrayList<ClienteUDP> destinos) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            DatagramPacket bloque = new DatagramPacket(buffer, buffer.length);
            socketudp.receive(bloque);

            String msg = new String(bloque.getData(), 0, bloque.getLength());
            if ("EOF".equals(msg)) {
                for (ClienteUDP c : destinos) {
                    DatagramPacket fin = new DatagramPacket(msg.getBytes(), msg.length(), c.getDireccion(), c.getPuerto());
                    socketudp.send(fin);
                }
                break;
            }

            for (ClienteUDP c : destinos) {
                DatagramPacket nuevo = new DatagramPacket(bloque.getData(), bloque.getLength(), c.getDireccion(), c.getPuerto());
                socketudp.send(nuevo);
            }
        }
    }
    public  static void main(String args[]){
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServidorPrincipalArchivo().setVisible(true);
            }
        });
    }

}
