package com.brasil.benicio.constelacao2.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.brasil.benicio.constelacao2.R;
import com.brasil.benicio.constelacao2.adapters.AdapterProdutos;
import com.brasil.benicio.constelacao2.databinding.FragmentOutrosBinding;
import com.brasil.benicio.constelacao2.models.ProdutoModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Outros extends Fragment {

    private FragmentOutrosBinding binding;
    private RecyclerView r;
    private AdapterProdutos adapter;
    private List<ProdutoModel> lista = new ArrayList<>();
    private DatabaseReference produtosRef = FirebaseDatabase.getInstance().getReference("produtos");
    private ProgressBar progress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOutrosBinding.inflate(getLayoutInflater());
        progress = binding.progressProduto;
        adapter = new AdapterProdutos(lista, container.getContext());
        // Inflate the layout for this fragment

        produtosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                lista.clear();
                for (DataSnapshot dado : snapshot.getChildren()){
                    ProdutoModel produto = dado.getValue(ProdutoModel.class);
                    lista.add(produto);
                }
                adapter.notifyDataSetChanged();
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        /*
        * teste
        * */
        /*
        ProdutoModel p = new ProdutoModel();
        p.setTitle("Moedas teste");
        p.setPrice(1.92f);
        p.setImg(0);
        p.setInfo("wwww.whatsap.com");
        p.setDescri("Produto x");
        lista.add(p);

        ProdutoModel p2 = new ProdutoModel();
        p2.setTitle("Outro exemplo");
        p2.setPrice(21.92f);
        p2.setImg(1);
        p2.setInfo("wwww.whatsap.com");
        p2.setDescri("Produto x outro exemplo de produto com outra descricao");
        lista.add(p2);

        ProdutoModel p3 = new ProdutoModel();
        p3.setTitle("exemplo3");
        p3.setPrice(99.92f);
        p3.setImg(3);
        p3.setInfo("wwww.whatsap.com");
        p3.setDescri("outro exemplo de icone de produto bla bla bla");
        lista.add(p3);*/

        r = binding.recycleProdutos;
        r.setLayoutManager(new LinearLayoutManager(container.getContext()));
        r.setHasFixedSize(true);
        r.addItemDecoration(new DividerItemDecoration(container.getContext(), DividerItemDecoration.VERTICAL));

        r.setAdapter(adapter);
        return binding.getRoot();
    }
}