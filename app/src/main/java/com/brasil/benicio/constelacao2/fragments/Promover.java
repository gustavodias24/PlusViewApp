package com.brasil.benicio.constelacao2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.brasil.benicio.constelacao2.R;
import com.brasil.benicio.constelacao2.adapters.AdapterMeusVIdeos;
import com.brasil.benicio.constelacao2.databinding.DialogPromoverVideoBinding;
import com.brasil.benicio.constelacao2.databinding.FragmentPromoverBinding;
import com.brasil.benicio.constelacao2.models.MeusVideosModel;
import com.brasil.benicio.constelacao2.models.UserModel;
import com.brasil.benicio.constelacao2.models.VideoModel;
import com.brasil.benicio.constelacao2.util.RetornarDadosUsuarios;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Promover extends Fragment {
    private FragmentPromoverBinding binding;
    private Dialog dialog_promover;
    private RecyclerView r;
    private AdapterMeusVIdeos adapter;
    private List<MeusVideosModel> list = new ArrayList<>();

    private FirebaseAuth user = FirebaseAuth.getInstance();
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usuarios");
    private UserModel userRefLogado;
    private String id;

    private boolean permitir = false;
    private  int moedasgastar;
    private int tempoGastar;
    private int tipo;

    private ProgressBar progress;
    private DatabaseReference videosRef = FirebaseDatabase.getInstance().getReference("videos");
    private DatabaseReference videosStateRef = FirebaseDatabase.getInstance().getReference("videoState");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPromoverBinding.inflate(getLayoutInflater());

        progress = binding.progressPromover;

        id = RetornarDadosUsuarios.getId();

        binding.apagarTodosRegistros.setOnClickListener(vi234 ->{
            AlertDialog.Builder b = new AlertDialog.Builder(container.getContext());
            b.setTitle("Lembrando");
            b.setMessage("Isso só vai apagar dos seus registros, se tiver vídeos que ainda podem ser assistidos eles vão ser assistidos normalmente!");
            b.setCancelable(false);
            b.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    videosStateRef.child(id).setValue(null).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(container.getContext(), "Vídeos removidos com sucesso!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(container.getContext(), "Erro ao remover vídeos! "+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            b.setNegativeButton("Cancelar", null);
            Dialog d = b.create();
            d.show();

        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dado : snapshot.getChildren()){
                    if( dado.getKey().equals(id.trim()) ){
                        userRefLogado = dado.getValue(UserModel.class);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new AdapterMeusVIdeos(list, container.getContext(), videosStateRef, id);

        videosStateRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dado : snapshot.getChildren()){
                    MeusVideosModel mVideosM = dado.getValue(MeusVideosModel.class);
                    list.add(mVideosM);
                }
                if (list.isEmpty()){
                    binding.textSemVideo.setVisibility(View.VISIBLE);
                    binding.recyclerMeusVideos.setVisibility(View.GONE);
                }else{
                    binding.textSemVideo.setVisibility(View.GONE);
                    binding.recyclerMeusVideos.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        r = binding.recyclerMeusVideos;
        r.setLayoutManager(new LinearLayoutManager(container.getContext()));
        r.setHasFixedSize(true);
        r.addItemDecoration(new DividerItemDecoration(container.getContext(), DividerItemDecoration.VERTICAL));
        r.setAdapter(adapter);

        binding.btPromover.setOnClickListener(view ->{
            if (progress.getVisibility() == View.GONE){
                if (userRefLogado != null){
                    AlertDialog.Builder b = new AlertDialog.Builder(container.getContext());
                    DialogPromoverVideoBinding b_promover = DialogPromoverVideoBinding.inflate(LayoutInflater.from(container.getContext()));
                    b.setTitle("Painel promover vídeo");
                    b.setMessage("Escolha um vídeo e coloque a quantidade de moedas que deseja investir.");

                    b_promover.msgMoeda.setText("Você tem "+ userRefLogado.getQtMoedas() +" moedas para gastar!");
                    b_promover.prontoVideo.setOnClickListener(v ->{
                        progress.setVisibility(View.VISIBLE);
                        String urlVideo = b_promover.editUrlVideo.getText().toString();
                        try{
                            moedasgastar = Integer.parseInt(b_promover.editMoedas.getText().toString());
                            tempoGastar = Integer.parseInt(b_promover.editTempo.getText().toString());
                            permitir = true;
                        }catch (Exception e){
                            progress.setVisibility(View.GONE);
                            Snackbar.make(v, "Erro ao selecionar variáveis inteiras!", Snackbar.LENGTH_LONG).show();
                        }
                        if (permitir){


                            if(!urlVideo.isEmpty() && urlVideo != null && Patterns.WEB_URL.matcher(urlVideo).matches()){
                                if (moedasgastar <= userRefLogado.getQtMoedas()){
                                    dialog_promover.dismiss();
                                    UserModel userAtt = userRefLogado;
                                    userAtt.setQtMoedas(userRefLogado.getQtMoedas() - moedasgastar);
                                    userRef.child(id.trim()).setValue(userAtt).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()){

                                            MeusVideosModel mvideosM = new MeusVideosModel();
                                            mvideosM.setStatus(0);
                                            mvideosM.setMoedasInvestidas(moedasgastar);
                                            mvideosM.setTempo(tempoGastar);
                                            mvideosM.setIdUser(id);

                                            String idMV = videosRef.push().getKey();
                                            mvideosM.setId(idMV);
                                            Log.d("teste", idMV);
                                            videosStateRef.child(id.trim()).child(idMV).setValue(mvideosM);

                                            progress.setVisibility(View.GONE);
                                            VideoModel novoVideo = new VideoModel();
                                            novoVideo.setQtdMoedas(moedasgastar);
                                            novoVideo.setQtdTempo(tempoGastar);
                                            novoVideo.setUrl(urlVideo);

                                            if (urlVideo.contains("yout")){
                                                tipo = 0;
                                            }else{
                                                tipo = 1;
                                            }

                                            novoVideo.setTipo(tipo);

                                            String id = videosRef.push().getKey();
                                            novoVideo.setVideoId(id);
                                            videosRef.child(id).setValue(novoVideo);
                                        }
                                    });
                                }else{
                                    progress.setVisibility(View.GONE);
                                    Log.d("moedinha", "gaster: "+moedasgastar+ " tem: "+userRefLogado.getQtMoedas());
                                    Snackbar.make(v, "Saldo insuficiente!", Snackbar.LENGTH_LONG).show();
                                }
                            }else{
                                progress.setVisibility(View.GONE);
                                Snackbar.make(v, "Url de vídeo inválida!", Snackbar.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Log.d("promovertag", "nao permitiu");
                        }
                    });
                    b.setView(b_promover.getRoot());
                    dialog_promover = b.create();
                    dialog_promover.show();
                }
            }
        });
        return binding.getRoot();
    }
}