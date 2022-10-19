package com.brasil.benicio.constelacao2.models;

public class MeusVideosModel {

    int status, moedasInvestidas, tempo, thumb;
    String id, idUser;

    public String getId() {
        return id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public int getThumb() {
        return thumb;
    }

    public void setThumb(int thumb) {
        this.thumb = thumb;
    }

    public int getMoedasInvestidas() {
        return moedasInvestidas;
    }

    public void setMoedasInvestidas(int moedasInvestidas) {
        this.moedasInvestidas = moedasInvestidas;
    }

}
