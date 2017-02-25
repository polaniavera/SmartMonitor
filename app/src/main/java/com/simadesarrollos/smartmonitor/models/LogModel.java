package com.simadesarrollos.smartmonitor.models;

import android.app.Application;

/**
 * Modelo de la tabla 'SM_LOG' en la base de datos
 */
public class LogModel extends Application {

    private static final String TAG = LogModel.class.getSimpleName();
    /*
        Atributos
         */
    private String id_log;
    private String id_cliente;
    private String id_item;
    private String latitud;
    private String longitud;
    private String rpm;
    private String velocidad;
    private String nivel1;
    private String nivel2;
    private String fecha;
    private String hora;

    public LogModel(String id_log,
                    String id_cliente,
                    String id_item,
                    String latitud,
                    String longitud,
                    String rpm,
                    String velocidad,
                    String nivel1,
                    String nivel2,
                    String fecha,
                    String hora) {
        this.id_log = id_log;
        this.id_cliente = id_cliente;
        this.id_item = id_item;
        this.latitud = latitud;
        this.longitud = longitud;
        this.rpm = rpm;
        this.velocidad = velocidad;
        this.nivel1 = nivel1;
        this.nivel2 = nivel2;
        this.fecha = fecha;
        this.hora = hora;
    }

    public String getIdLog() {
        return id_log;
    }

    public void setIdLog(String _id_log) {
        id_log = _id_log;
    }

    public String getIdCliente() {
        return id_cliente;
    }

    public void setIdCliente(String _id_cliente) {
        id_cliente = _id_cliente;
    }

    public String getIdItem() {
        return id_item;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getRpm() {
        return rpm;
    }

    public String getVelocidad() {
        return velocidad;
    }

    public String getNivel1() {
        return nivel1;
    }

    public void setNivel1(String _nivel1) {
        nivel1 = _nivel1;
    }

    public String getNivel2() {
        return nivel2;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String _fecha) {
        fecha = _fecha;
    }

    public String getHora() {
        return hora;
    }

    /**
     * Compara los atributos de dos metas
     *
     * @param log LogModel externa
     * @return true si son iguales, false si hay cambios
     */
    public boolean compararCon(LogModel log) {
        return this.id_log.compareTo(log.id_log) == 0 &&
                this.id_cliente.compareTo(log.id_cliente) == 0 &&
                this.id_item.compareTo(log.id_item) == 0 &&
                this.latitud.compareTo(log.latitud) == 0 &&
                this.longitud.compareTo(log.longitud) == 0 &&
                this.rpm.compareTo(log.rpm) == 0 &&
                this.velocidad.compareTo(log.velocidad) == 0 &&
                this.nivel1.compareTo(log.nivel1) == 0 &&
                this.nivel2.compareTo(log.nivel2) == 0 &&
                this.fecha.compareTo(log.fecha) == 0 &&
                this.hora.compareTo(log.hora) == 0;
    }

    public void remove(){
        this.remove();
    }
}