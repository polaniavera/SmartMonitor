package com.simadesarrollos.smartmonitor.models;

import android.app.Application;

/**
 * Created by C POLANIA on 28/09/2016.
 */

public class GlobalData extends Application {

    private String user;
    private int codigo;
    private int diabetico;
    private int talla;
    private int peso;
    private float imc;

    public String getUser() {
        return user;
    }

    public void setUser(String _user) {
        user = _user;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int _codigo) {
        codigo = _codigo;
    }

    public int getDiabetico() {
        return diabetico;
    }

    public void setDiabetico(int _diabetico) {
        diabetico = _diabetico;
    }

    public int getTalla() {
        return talla;
    }

    public void setTalla(int _talla) {
        talla = _talla;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int _peso) {
        peso = _peso;
    }

    public float getImc() {
        return imc;
    }

    public void setImc(float _imc) {
        imc = _imc;
    }

}