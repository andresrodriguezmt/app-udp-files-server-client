package org.andres.conexion;

import java.net.InetAddress;

public class ClienteUDP {
    private int id;
    private InetAddress direccion;
    private int puerto;

    public ClienteUDP(int id, InetAddress direccion, int puerto) {
        this.id = id;
        this.direccion = direccion;
        this.puerto = puerto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public InetAddress getDireccion() {
        return direccion;
    }

    public void setDireccion(InetAddress direccion) {
        this.direccion = direccion;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }
}
