package com.brasil.benicio.constelacao2.adapters;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brasil.benicio.constelacao2.R;
import com.brasil.benicio.constelacao2.models.UserModel;
import com.brasil.benicio.constelacao2.models.VideoModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdapterVideoAssistir extends RecyclerView.Adapter<AdapterVideoAssistir.MyViewHolder> {
    List<VideoModel> list;
    Context c;
    private FirebaseAuth user = FirebaseAuth.getInstance();
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usuarios");
    private UserModel userRefLogado;
    private String id;

    public AdapterVideoAssistir(List<VideoModel> list, Context c) {
        this.list = list;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_template_assitir, parent, false);
        return new MyViewHolder(v);
    }

    public void assistir(String video){
        c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video)));
    }

    public void darRecompensa(VideoModel videoModel){
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

        UserModel userModel = userRefLogado;
        userModel.setQtMoedas(
                userModel.getQtMoedas() + videoModel.getQtdMoedas()
        );

        userRef.child(id.trim()).setValue(userModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                assistir(videoModel.getUrl());
            }else{
                Toast.makeText(c, "Erro de conexão!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void removerDoBanco(){
        /*
        *  lógica de remover do bancoo
        * */
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        VideoModel video = list.get(position);

        holder.moedasOferecer.setText("Esse vídeo oferece "+ video.getQtdMoedas() + " moedas!");

        holder.view.setOnClickListener(view ->{
            ClipboardManager clipboardManager = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("url", video.getUrl());
            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(c, "Link copiado!", Toast.LENGTH_LONG).show();
        });

        holder.like.setOnClickListener(view1 ->{
            holder.like.setBackgroundResource(R.drawable.coracao_24);
            Toast.makeText(c, "Deixei um gostei nesse vídeo!", Toast.LENGTH_LONG).show();
            assistir((video.getUrl()));
        });
        WebView wb = holder.viewAssistir;

//      wb.setOnTouchListener((v, event) -> true);
        wb.setWebViewClient(new WebViewClient());
        //wb.getSettings().setJavaScriptEnabled(true);

        wb.loadUrl(video.getUrl());

        holder.assitir.setOnClickListener(view2 ->{
            darRecompensa(video);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private Button like,view,assitir;
        private TextView moedasOferecer;
        private WebView viewAssistir;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            assitir = itemView.findViewById(R.id.btnassistir);
            like = itemView.findViewById(R.id.btnLike);
            view = itemView.findViewById(R.id.btnView);
            viewAssistir = itemView.findViewById(R.id.wvAssitir);
            moedasOferecer = itemView.findViewById(R.id.textOferece);
        }
    }
}
