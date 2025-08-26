package org.andres.servidor.gui;

import org.andres.dto.MiDatagrama;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorPrincipalArchivo extends JFrame implements ActionListener {

    private final String titulo = "Servidor";
    private final String letra = "Arial";
    private final String error = "Error";
    private boolean estadoServidor = false;
    private DatagramSocket socketudp;
    private volatile boolean ejecucion = false;

    private final ArrayList<Socket> clientesServ = new ArrayList<>();
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
        areaMensajes.append(STR."Servidor UDP iniciado en el puerto\{puerto}\n");
        byte[] buf = new byte[1000];

            new Thread(() -> {
            DatagramPacket dp = null;
            try {
                socketudp = new DatagramSocket(puerto);
                ejecucion = true;

                configurarBotones(1);

                while (ejecucion) {

                    areaMensajes.append("Escuchando ...\n");

                    dp = new DatagramPacket(buf, buf.length);

                    socketudp.receive(dp); // aquí se bloquea esperando mensajes

                    String elmensaje = new String(dp.getData(), 0, dp.getLength()); // mejor con getLength()
                    areaMensajes.append("El mensaje recibido es " + elmensaje + "\n");

                    DatagramPacket mensajeServ = MiDatagrama.crearDataG(
                            dp.getAddress().getHostAddress(),
                            dp.getPort(),
                            "Mensaje recibido en el servidor"
                    );
                    socketudp.send(mensajeServ);
                }

            } catch (SocketException ex) {
                Logger.getLogger(PrincipalSrv.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PrincipalSrv.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
    }

    private void apagarServidor(){
        ejecucion = false;
        if (socketudp != null && !socketudp.isClosed()) {
            socketudp.close(); // Esto libera el puerto inmediatamente
        }
        this.botonEncerderServ.setEnabled(true);
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
            puertoVer = rand.nextInt(16000 - 15000 + 1) + 15000;
        } while (verificarExistenciaServidor(puertoVer));
        return puertoVer;
    }
    public boolean verificarExistenciaServidor(int puertoVer){
        try (ServerSocket serverSocket = new ServerSocket(puertoVer)) {
            // Si llegamos aquí, el puerto está libre
            return false;
        } catch (IOException e) {
            // Si falla, el puerto ya está en uso
            return true;
        }
    }

    public void actualizarCombo() {
        SwingUtilities.invokeLater(() -> {
            try{
                comboClientes.removeAllItems();
                int i = 0;
                for (Socket cliente : clientesServ) {
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

    public void actualizarAClientesLista(){
        StringBuilder listaClientes = new StringBuilder("LIST:Servidor,Todos,");
        String clientes = "Cliente ";

        for (Integer idClientes : idEnUso){
            listaClientes.append(clientes).append(idClientes).append(",");
        }

        if(!idEnUso.isEmpty()){
            listaClientes.deleteCharAt(listaClientes.length() - 1);
        }

        String lista = listaClientes.toString();

        //for(ClientHandlerFinal cliente : listaClientesManejados){
        //    cliente.enviarMensajeCliente(lista);
        //}

    }

    //public void enviarArchivoTodos(String nombreArchivo, long tamanio, byte[] contenido, int idClienteRemitente) {

    //    String mensaje = "El cliente " + idClienteRemitente + " envió el archivo: " + nombreArchivo;

    //    for (ClientHandlerFinal cliente : listaClientesManejados) {
    //        cliente.enviarArchivoCliente(nombreArchivo, tamanio, contenido, mensaje);
    //    }
    //}


    //public void enviarArchivoDedicado(String nombreArchivo, long tamanio, byte[] contenido, int idClienteEnviar, int idClienteRemitente) {
    //    String notificacion = "El cliente " + idClienteRemitente + " te envió el archivo: " + nombreArchivo;

    //    for (ClientHandlerFinal cliente : listaClientesManejados) {
    //        if (cliente.getClientId() == idClienteEnviar) {
    //            cliente.enviarArchivoCliente(nombreArchivo, tamanio, contenido, notificacion);
    //        }
    //    }
    //}

    //public void removerCliente(ClientHandlerFinal cliente) {
    //    listaClientesManejados.remove(cliente);
    //}

    public  static void main(String args[]){
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServidorPrincipalArchivo().setVisible(true);
            }
        });
    }

}
