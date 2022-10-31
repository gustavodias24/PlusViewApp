package com.brasil.benicio.constelacao2.fragments;

import android.annotation.SuppressLint;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Base64;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;

import android.widget.ProgressBar;
import android.widget.TextView;


import com.brasil.benicio.constelacao2.adapters.AdapterVideoAssistir;
import com.brasil.benicio.constelacao2.databinding.FragmentAssistirBinding;
import com.brasil.benicio.constelacao2.models.UserModel;
import com.brasil.benicio.constelacao2.models.VideoModel;

import com.brasil.benicio.constelacao2.util.RetornarDadosUsuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Assistir extends Fragment {

    private FirebaseAuth user = FirebaseAuth.getInstance();
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usuarios");
    private DatabaseReference videosRef = FirebaseDatabase.getInstance().getReference("videos");
    private List<VideoModel> listaVideo = new ArrayList<>();
    private UserModel userRefLogado;
    private String id;

    private boolean ganharRecomp = true;

    private FragmentAssistirBinding viewBinding;


    private int tempoAtual;
    private Thread t, tReal;

    private VideoModel videoProximo;
    private boolean carregada = false;
    private ProgressBar progress;


    private String ultimo, atual = "";
    private boolean assistido = false;
    private boolean masPulou = false;
    private boolean pri = true;

    private RecyclerView recyclerVideos;
    private AdapterVideoAssistir adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewBinding = FragmentAssistirBinding.inflate(getLayoutInflater());

        recyclerVideos = viewBinding.recyclerVideos;
        recyclerVideos.setHasFixedSize(true);
        recyclerVideos.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerVideos.addItemDecoration(new DividerItemDecoration(inflater.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new AdapterVideoAssistir(listaVideo, inflater.getContext());
        recyclerVideos.setAdapter(adapter);

        id = RetornarDadosUsuarios.getId();

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


        /*progress = viewBinding.progressAssistir;

        moedasGanhar = viewBinding.contMoedas;
        tempoAssistir = viewBinding.contTempo;

        wb = viewBinding.webVideos;
        wb.setOnTouchListener((v, event) -> true);
        wb.getSettings().setJavaScriptEnabled(true);


        FirebaseUser userLogadoAtual = user.getCurrentUser();

        if(userLogadoAtual.getPhoneNumber() == null){
            id = Base64.encodeToString(userLogadoAtual.getEmail().getBytes(), Base64.DEFAULT).replaceAll("(\\n | \\r)", "").trim();
        }else{
            id = Base64.encodeToString(userLogadoAtual.getPhoneNumber().getBytes(), Base64.DEFAULT).replaceAll("(\\n | \\r)", "").trim();
        }

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

        videosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaVideo.clear();
                for (DataSnapshot dado : snapshot.getChildren()) {
                    VideoModel nv = dado.getValue(VideoModel.class);
                    listaVideo.add(nv);
                }

                if (listaVideo.size() > 0 && posAtual < listaVideo.size()) {

                videoProximo = listaVideo.get(posAtual);

                wb.loadUrl(videoProximo.getUrl());

                carregada = false;
                sapoha(container.getContext());
                progress.setVisibility(View.VISIBLE);
                wb.setVisibility(View.GONE);
                moedasGanhar.setText(videoProximo.getQtdMoedas() + "");
                tempoAssistir.setText(videoProximo.getQtdTempo() + "");

                ultimo = videoProximo.getUrl();
                Log.d("assistido", "ultimo como primieoro video" + ultimo);


                viewBinding.assistirAgora.setOnClickListener(v0 -> {
                    if (masPulou) {
                        assistido = false;
                        masPulou = false;
                    } else {
                        if(pri){
                            pri = false;
                        }else{
                            if (atual.equals(ultimo)) {
                                Log.d("assistido", "ultimo se for equal" + ultimo);
                                Log.d("assistido", "atual se for equal" + atual);
                                assistido = true;
                            }else{
                                assistido = false;
                                atual = ultimo;

                                Log.d("assistido", "ultimo" + ultimo);
                                Log.d("assistido", "atual" + atual);
                            }
                        }
                    }

                    if (carregada) {
                        if (t.isAlive()) {
                            Toast.makeText(getContext(), "Você já esta assistindo!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!assistido) {
                                tempoAtual = videoProximo.getQtdTempo();

                                tReal = new Thread() {
                                    @Override
                                    public void run() {
                                        for (int i = tempoAtual; i > 0; i--) {

                                            tempoAtual--;
                                            Log.d("tempo", tempoAtual + "");
                                            try {
                                                getActivity().runOnUiThread(() -> {
                                                    tempoAssistir.setText(tempoAtual + "");
                                                    if (tempoAtual == 0) {
                                                        if (ganharRecomp) {
                                                            int valor = videoProximo.getQtdMoedas();
                                                            UserModel novoUser = userRefLogado;
                                                            novoUser.setQtMoedas(novoUser.getQtMoedas() + valor);
                                                            userRef.child(id.trim()).setValue(novoUser).addOnCompleteListener(task -> {
                                                                if (task.isSuccessful()) {
                                                                    videosRef.child(videoProximo.getVideoId().trim()).removeValue().addOnCompleteListener(task1 -> {
                                                                        if (task1.isComplete()) {
                                                                            Toast.makeText(getContext(), "Sua recopensa de " + valor + " foi registrada!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                } else {
                                                                    Toast.makeText(getContext(), "Erro ao inserir recompensa " + task.getException().toString(), Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            } catch (Exception e) {

                                            }

                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            if (tempoAtual == 0) {
                                                Thread.currentThread().interrupt();
                                            }
                                        }
                                        super.run();
                                    }
                                };

                                tReal.start();
                                t = new Thread() {
                                    @Override
                                    public void run() {
                                        while (tReal.isAlive()) {
                                            Log.d("isInterupted", "nouuuup");
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        super.run();
                                    }
                                };
                                t.start();
                                Toast.makeText(getContext(), "Iniciado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Você já recebeu a recompensa desse vídeo!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Espere o vídeo carregar!", Toast.LENGTH_SHORT).show();
                    }
                });

                viewBinding.pular.setOnClickListener(vivi -> {
                    if (t.isAlive()) {
                        Toast.makeText(getContext(), "Você não pode pular vídeos enquanto estiver assistindo um!", Toast.LENGTH_SHORT).show();
                    } else {
                        ganharRecomp = true;
                        if (posAtual < (listaVideo.size() - 1)) {
                            posAtual++;
                            videoProximo = listaVideo.get(posAtual);

                            wb.loadUrl(videoProximo.getUrl());
                            carregada = false;

                            sapoha(getContext());
                            progress.setVisibility(View.VISIBLE);
                            wb.setVisibility(View.GONE);
                            moedasGanhar.setText(videoProximo.getQtdMoedas() + "");
                            tempoAssistir.setText(videoProximo.getQtdTempo() + "");

                            atual = videoProximo.getUrl();
                            masPulou = true;
                            Log.d("assistido", "atual no pular" + atual);
                        } else {
                            // mexendo
                            Toast.makeText(getContext(), "Sem vídeos no momento, volte mais tarde!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                 }else{
                    Toast.makeText(getContext(), "Não existe vídeos para exibir", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        t = new Thread(){};*/

        return viewBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        videosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaVideo.clear();
                for (DataSnapshot dado : snapshot.getChildren()){
                    VideoModel videoAdd = dado.getValue(VideoModel.class);
                    listaVideo.add(videoAdd);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*@Override
    public void onStop() {
        Toast.makeText(getContext(), "Sistema detectou que você deixou de assitir o vídeo! Você não receberá a recompensa!", Toast.LENGTH_LONG).show();
        ganharRecomp = false;
        super.onStop();
    }
    private void sapoha(Context c){
        wb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                carregada = true;
                progress.setVisibility(View.GONE);
                wb.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if( URLUtil.isNetworkUrl(url) ) {
                    return false;
                }
                if (appInstalledOrNot(url, c)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity( intent );
                } else {
                    // do something if app is not installed
                }
                return true;
            }

        });
    }
    private boolean appInstalledOrNot(String uri, Context c) {
        PackageManager pm = c.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }*/
}