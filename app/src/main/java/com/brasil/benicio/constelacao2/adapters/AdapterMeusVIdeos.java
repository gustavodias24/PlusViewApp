package com.brasil.benicio.constelacao2.adapters;

import android.content.Context;
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
import com.brasil.benicio.constelacao2.models.MeusVideosModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMeusVIdeos extends RecyclerView.Adapter<AdapterMeusVIdeos.MyViewHolder> {
    List<MeusVideosModel> l;
    Context c;
    DatabaseReference ref;
    String idUser;

    public AdapterMeusVIdeos(List<MeusVideosModel> l, Context c, DatabaseReference ref, String idUser) {
        this.l = l;
        this.c = c;
        this.ref = ref;
        this.idUser = idUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.videos_cadastrados, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MeusVideosModel model = l.get(position);

        holder.status.setText("Id do pedido: " + model.getId());
//        switch (model.getStatus()){
//            case 0:
//                status_msg = "Em processo...";
//                holder.status.setTextColor(Color.BLUE);
//                break;
//            case 2:
//                status_msg = "Assistido.";
//                holder.status.setTextColor(Color.GREEN);
//                break;
//            case 3:
//                holder.status.setTextColor(Color.BLUE);
//                status_msg = "Comcluído.";
//                break;
//            default:
//                status_msg = "";
//        }

        switch (model.getThumb()){
            case 0:
                Picasso.get().load("https://www.remessaonline.com.br/blog/wp-content/uploads/2022/04/redes-sociais-mais-usadas.jpg").into(holder.thumb);
                break;
            case 404:
                Picasso.get().load(R.drawable.tetse).into(holder.thumb);
                break;
            default:
                Picasso.get().load("https://www.remessaonline.com.br/blog/wp-content/uploads/2022/04/redes-sociais-mais-usadas.jpg").into(holder.thumb);
        }

        holder.moedasInvestidas.setText("Moedas investidas: "+ model.getMoedasInvestidas());
        holder.processo.setText("Tempo por moeda: "+ model.getTempo() +" segundos.");

        holder.btnDel.setOnClickListener( view ->{
            ref.child(idUser).child(model.getId()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(c, "Deletado com sucesso!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(c, "Erro ao ser deletado!"+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return l.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView moedasInvestidas, status,processo;
        public ImageView thumb;
        public Button btnDel;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            moedasInvestidas = itemView.findViewById(R.id.qtMoedasGasta);
            status = itemView.findViewById(R.id.status);
            processo = itemView.findViewById(R.id.qtVisitas);
            thumb = itemView.findViewById(R.id.thumbVideoCadastro);
            btnDel = itemView.findViewById(R.id.apagarRegistro);
        }
    }
}
