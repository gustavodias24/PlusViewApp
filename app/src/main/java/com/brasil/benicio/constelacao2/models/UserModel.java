package com.brasil.benicio.constelacao2.models;

public class UserModel {
    String credencial;
    boolean verAnuncio;
    int qtMoedas;

    public UserModel() {

    }

    public String getCredencial() {
        return credencial;
    }

    public void setCredencial(String credencial) {
        this.credencial = credencial;
    }


    public boolean isVerAnuncio() {
        return verAnuncio;
    }

    public void setVerAnuncio(boolean verAnuncio) {
        this.verAnuncio = verAnuncio;
    }

    public int getQtMoedas() {
        return qtMoedas;
    }

    public void setQtMoedas(int qtMoedas) {
        this.qtMoedas = qtMoedas;
    }
}
