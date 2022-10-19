package com.brasil.benicio.constelacao2.models;

public class VideoModel {
    public VideoModel() {
    }

    String url, videoId;
    int tipo, qtdMoedas, qtdTempo;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getQtdMoedas() {
        return qtdMoedas;
    }

    public void setQtdMoedas(int qtdMoedas) {
        this.qtdMoedas = qtdMoedas;
    }

    public int getQtdTempo() {
        return qtdTempo;
    }

    public void setQtdTempo(int qtdTempo) {
        this.qtdTempo = qtdTempo;
    }
}
