package com.brasil.benicio.constelacao2.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.brasil.benicio.constelacao2.R;
import com.brasil.benicio.constelacao2.TutorialActivity;
import com.brasil.benicio.constelacao2.databinding.ActivityPrincipalBinding;
import com.brasil.benicio.constelacao2.loginactivity.MainActivity;
import com.brasil.benicio.constelacao2.models.UserModel;
import com.brasil.benicio.constelacao2.util.RetornarDadosUsuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Principal extends AppCompatActivity {
    private Outros outros_fragment;
    private Promover promover_fragment;
    private Assistir fragment_assistir;

    private ActivityPrincipalBinding binding;
    private Toolbar bar;

    private FirebaseAuth user = FirebaseAuth.getInstance();
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usuarios");
    private UserModel userRefLogado;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.laranja_escuro));
        }

        id = RetornarDadosUsuarios.getId();

        Log.d("id atual", id+" id email ne");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dado : snapshot.getChildren()){
                   if( dado.getKey().equals(id.trim()) ){
                       userRefLogado = dado.getValue(UserModel.class);
                   }
                }
                if (userRefLogado != null){
                    binding.qtdMoedas.setText(userRefLogado.getQtMoedas() + "");
                }else{
                    user.signOut();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    Toast.makeText(getApplicationContext(), "Faça login novamente", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
            });


        bar = binding.myToolbar;
        bar.setTitle("");
        setSupportActionBar(bar);

        outros_fragment = new Outros();
        promover_fragment = new Promover();
        fragment_assistir = new Assistir();

        replaceFragment(promover_fragment);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.exibir:
                    item.setChecked(true);
                    replaceFragment(fragment_assistir);
                    break;
                case R.id.outros:
                    item.setChecked(true);
                    replaceFragment(outros_fragment);
                    break;
                case R.id.promover:
                    item.setChecked(true);
                    replaceFragment(promover_fragment);
                    break;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_padrao, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sair){
            user.signOut();
            finish();
        }
        else if( item.getItemId() == R.id.tutorial){
            startActivity(new Intent(getApplicationContext(), TutorialActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment (Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }
}

