package com.brasil.benicio.constelacao2.loginactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brasil.benicio.constelacao2.databinding.ActivityLoginCelularBinding;
import com.brasil.benicio.constelacao2.fragments.Principal;
import com.brasil.benicio.constelacao2.models.UserModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginCelularActivity extends AppCompatActivity {

    private ActivityLoginCelularBinding viewBinding;
    private boolean veryNu = true;
    private TextView msg;
    private EditText edtPhone;
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios");
    private UserModel newUser;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressBar progress;
    private boolean jaExiste = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityLoginCelularBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        msg = viewBinding.msg;
        edtPhone = viewBinding.editPhone;
        auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("pt-br");
        progress = viewBinding.progress;

        viewBinding.send.setOnClickListener(view -> {
            if (progress.getVisibility() == View.GONE){
                if (veryNu){
                    String numeroVerificacao = edtPhone.getText().toString().replace("-", "");
                    if (!numeroVerificacao.isEmpty() && numeroVerificacao != null){
                        progress.setVisibility(View.VISIBLE);

                        String numeroFormatado = "+55"+ numeroVerificacao.split(" ")[1];

                        Log.d("NUMERO VERI", numeroVerificacao);
                        Log.d("NUMERO VERI", numeroFormatado);

                        startPhoneNumberVerification(numeroVerificacao);
                    }else{
                        Snackbar.make(view, "Insira o seu número", Snackbar.LENGTH_LONG).show();
                    }
                }else{
                    String codVerificacao = viewBinding.edtCodeVerification.getText().toString();
                    if (!codVerificacao.isEmpty() && codVerificacao != null){
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codVerificacao);
                        signInWithPhoneAuthCredential(credential);
                    }else{
                        Snackbar.make(view, "Insira o código de verificação", Snackbar.LENGTH_LONG).show();
                    }

                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // ja ta logado
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progress.setVisibility(View.GONE);
                viewBinding.debugError.setVisibility(View.VISIBLE);
                viewBinding.debugError.setText("Ocorreu alguma erro na verificação: "+ e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                progress.setVisibility(View.GONE);
                veryNu = false;
                msg.setText("Insira o código de verificação agora!");

                edtPhone.setVisibility(View.GONE);
                viewBinding.edtCodeVerification.setVisibility(View.VISIBLE);

                //precisa fazer a validação
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        progress.setVisibility(View.VISIBLE);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        Toast.makeText(LoginCelularActivity.this, "Bem-vindo "+ user.getPhoneNumber(), Toast.LENGTH_SHORT).show();

                        String id = codbase64.cod(user.getPhoneNumber()).trim();

                        // verificar se já tem cadastro
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot data:snapshot.getChildren()){
                                    if ( data.getKey().equals(id) ){
                                        jaExiste = true;
                                    }
                                }
                                if (jaExiste){
                                    Toast.makeText(LoginCelularActivity.this, "Bem-vindo de volta "+ user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Principal.class));
                                    progress.setVisibility(View.GONE);
                                    finish();
                                }else{
                                    newUser = new UserModel();
                                    newUser.setCredencial(user.getPhoneNumber());
                                    newUser.setQtMoedas(0);
                                    newUser.setVerAnuncio(true);

                                    ref.child(id).setValue(
                                            newUser
                                    ).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()){
                                            Toast.makeText(LoginCelularActivity.this, "Bem-vindo "+ user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), Principal.class));
                                            progress.setVisibility(View.GONE);
                                            finish();
                                        }else{
                                            viewBinding.debugError.setVisibility(View.VISIBLE);
                                            viewBinding.debugError.setText("Erro no banco: "+ task1.getException().toString());
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        Toast.makeText(LoginCelularActivity.this, "Erro "+ task.getException().toString() , Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.GONE);
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        }
                    }
                });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
}
