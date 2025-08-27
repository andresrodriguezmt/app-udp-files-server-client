package org.andres.dto;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiDatagrama {
    public static DatagramPacket crearDataG(String ip, int puerto, byte[] data) {
        try {
            InetAddress direccion = InetAddress.getByName(ip);
            return new DatagramPacket(
                    data,
                    data.length,
                    direccion,
                    puerto
            );
        } catch (UnknownHostException ex) {
            Logger.getLogger(MiDatagrama.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
