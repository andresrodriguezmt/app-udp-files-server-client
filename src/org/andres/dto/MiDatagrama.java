package org.andres.dto;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiDatagrama {
    public static DatagramPacket crearDataG(String ip, int puerto, String mensaje) {
        try {
            // 1. Resolver la IP de destino
            InetAddress direccion = InetAddress.getByName(ip);

            // 2. Convertir el mensaje a bytes (UTF-8 por defecto)
            byte[] mensajeB = mensaje.getBytes();

            // 3. Crear el paquete con el tamaño en bytes, NO con mensaje.length()
            return new DatagramPacket(
                    mensajeB,
                    mensajeB.length,
                    direccion,
                    puerto
            );

        } catch (UnknownHostException ex) {
            Logger.getLogger(MiDatagrama.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
