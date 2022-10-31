package com.brasil.benicio.constelacao2.util;

import android.util.Base64;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RetornarDadosUsuarios {

    public final static String getId(){

        FirebaseUser userLogadoAtual =  FirebaseAuth.getInstance().getCurrentUser();
        String id = "";
        if(userLogadoAtual.getEmail() != null)
        {
           id = Base64.encodeToString(userLogadoAtual.getEmail().getBytes(), Base64.DEFAULT).replaceAll("(\\n | \\r)", "").trim();
        }else
        {
           id =  Base64.encodeToString(userLogadoAtual.getPhoneNumber().getBytes(), Base64.DEFAULT).replaceAll("(\\n | \\r)", "").trim();
        }
        if( id.isEmpty() )
        {
            id =  Base64.encodeToString(userLogadoAtual.getPhoneNumber().getBytes(), Base64.DEFAULT).replaceAll("(\\n | \\r)", "").trim();
        }
        Log.d("IdTmj", "Seu id é: "+ id);
        return id;

    }


}


