package com.brasil.benicio.constelacao2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.brasil.benicio.constelacao2.loginactivity.MainActivity;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class TutorialActivity extends IntroActivity {
    private SharedPreferences sh;
    private SharedPreferences.Editor edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_tutorial);
        sh = getApplicationContext().getSharedPreferences("assistiu", Context.MODE_PRIVATE);
        edt = sh.edit();
        addSlide(
                new SimpleSlide.Builder().title("Tutorial de cadastro!").
                        image(R.drawable.cadastro).
                        background(R.color.purple_500).
                        title("Fazer cadastro").
                        description("Basta inserir o seu E-mail e senha deixando a opção de cadastro marcada como mostra a seta!")
                        .build()

        );

        addSlide(
                new SimpleSlide.Builder().title("Tutorial de login!").
                        image(R.drawable.login).
                        background(R.color.purple_500).
                        title("Fazer login").
                        description("Basta inserir o seu E-mail e senha (de forma correta) já cadastrado no aplicativo anteriormente!")
                        .build()
        );

        addSlide(
                new SimpleSlide.Builder().title("Passo 1 - Tutorial de login pelo celular!").
                        image(R.drawable.cel1).
                        background(R.color.purple_500).
                        description("Clique no texto 'logar pelo celular' aonde a seta aponta!")
                        .build()
        );

        addSlide(
                new SimpleSlide.Builder().title("Passo 2 - Tutorial de login pelo celular!").
                        image(R.drawable.cel2).
                        background(R.color.purple_500).
                        description("Insira o seu número de telefone completo e corretamente para receber o código de verificação!")
                        .build()
        );

        addSlide(
                new SimpleSlide.Builder().title("Passo 3 - Tutorial de login pelo celular!").
                        image(R.drawable.cel3).
                        background(R.color.purple_500).
                        description("Vá para suas mensagens e copie o código de verificação!")
                        .build()
        );

        addSlide(
                new SimpleSlide.Builder().title("Passo 4 - Tutorial de login pelo celular!").
                        image(R.drawable.cel4).
                        background(R.color.purple_500).
                        description("Volte para a aplicação e coloque o código corretamente para efetuar o login!")
                        .build()
        );

        addSlide(
                new SimpleSlide.Builder().title("Passo 1 - Tutorial de como cadastrar o seu vídeo!").
                        image(R.drawable.cad1).
                        background(R.color.purple_500).
                        description("Clique em 'promover' e depois 'promover um vídeo'!")
                        .build()
        );

        addSlide(
                new SimpleSlide.Builder().title("Passo 2 - Tutorial de como cadastrar o seu vídeo!").
                        image(R.drawable.cad2).
                        background(R.color.purple_500).
                        description("Insira todos os dados corretamente de acordo como é pedido e finalize clicando em 'pronto'!")
                        .build()
        );

        addSlide(
                new SimpleSlide.Builder().title("Passo 3 - Tutorial de como cadastrar o seu vídeo!").
                        image(R.drawable.cad3).
                        background(R.color.purple_500).
                        description("Seu vídeo será exibido assim caso tenha sido cadastrado!")
                        .build()
        );

        addSlide(
                new FragmentSlide.Builder().
                        background(R.color.purple_500).
                        canGoForward(false).
                        fragment(R.layout.fragment_last).
                build()
        );
        setButtonBackVisible(false);
    }
    public void fechar(View v){
        edt.putBoolean("assistiu", true);
        edt.commit();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}