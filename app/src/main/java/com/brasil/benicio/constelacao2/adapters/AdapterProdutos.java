package com.brasil.benicio.constelacao2.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brasil.benicio.constelacao2.R;
import com.brasil.benicio.constelacao2.models.ProdutoModel;

import java.util.List;

public class AdapterProdutos extends RecyclerView.Adapter<AdapterProdutos.MyViewHolder> {
    List<ProdutoModel> l;
    Context c;

    public AdapterProdutos(List<ProdutoModel> l, Context c) {
        this.l = l;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.produto_comprar, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
         ProdutoModel produto =  l.get(position);

         holder.maisInfo.setOnClickListener(view -> {
             c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(produto.getInfo())));
             Toast.makeText(c, "Fale com o vendedor sobre isso!", Toast.LENGTH_LONG).show();
         });
         holder.title.setText(produto.getTitle());
         holder.descri.setText(produto.getDescri());
         holder.price.setText("R$: " + produto.getPrice());

         switch (produto.getImg()){
             case 0:
                 holder.imageProduto.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
                 break;
             case 1:
                 holder.imageProduto.setImageResource(R.drawable.ic_baseline_shopping_cart_24);
                 break;
             default:
                 holder.imageProduto.setImageResource(R.drawable.ic_baseline_monetization_on_24);

         }
    }

    @Override
    public int getItemCount() {
        return l.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView title, descri, price;
        ImageView imageProduto;
        Button maisInfo;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleProduto);
            descri = itemView.findViewById(R.id.descriProduto);
            price = itemView.findViewById(R.id.priceProduto);
            imageProduto = itemView.findViewById(R.id.imagemProduto);
            maisInfo = itemView.findViewById(R.id.maisInfo);
        }
    }
}
