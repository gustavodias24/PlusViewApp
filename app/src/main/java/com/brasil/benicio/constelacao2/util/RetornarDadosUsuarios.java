package com.brasil.benicio.constelacao2.util;

import android.content.Intent;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.brasil.benicio.constelacao2.loginactivity.MainActivity;
import com.brasil.benicio.constelacao2.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RetornarDadosUsuarios {

    public static String getId(){

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usuarios");
        FirebaseUser userLogadoAtual =  FirebaseAuth.getInstance().getCurrentUser();

        if(userLogadoAtual.getEmail() != null){
           return Base64.encodeToString(userLogadoAtual.getEmail().getBytes(), Base64.DEFAULT).replaceAll("(\\n | \\r)", "").trim();
        }else{
           return Base64.encodeToString(userLogadoAtual.getPhoneNumber().getBytes(), Base64.DEFAULT).replaceAll("(\\n | \\r)", "").trim();
        }
    }


}


