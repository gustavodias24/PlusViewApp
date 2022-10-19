package com.brasil.benicio.constelacao2.loginactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.brasil.benicio.constelacao2.TutorialActivity;
import com.brasil.benicio.constelacao2.databinding.ActivityMainBinding;
import com.brasil.benicio.constelacao2.databinding.RecuperarEmailDialogBinding;
import com.brasil.benicio.constelacao2.fragments.Principal;
import com.brasil.benicio.constelacao2.models.UserModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String email, senha;
    private FirebaseAuth auth;
    private ActionBar bar;
    private Dialog dialog_esq_pwd, dialog_confirm, dialog_numero;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private UserModel newUser;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private ProgressBar progress;

    private SharedPreferences sh;
    private SharedPreferences.Editor edt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sh = getApplicationContext().getSharedPreferences("assistiu", Context.MODE_PRIVATE);
        edt = sh.edit();
        if ( !sh.getBoolean("assistiu",false)){
            startActivity(new Intent(getApplicationContext(), TutorialActivity.class));
            // edt.putBoolean("assistiu", true).commit();
        }

        bar = getSupportActionBar();
        bar.setTitle("Login");
        progress = binding.progressBar;

        auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("pt-br");

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    @Override
    public void onVerificationCompleted(PhoneAuthCredential credential) {

        Toast.makeText(MainActivity.this, "Verificação ok "+ credential.toString(), Toast.LENGTH_SHORT).show();
        passarDeTela();
    }

    @Override
    public void onVerificationFailed(FirebaseException e) {
        // This callback is invoked in an invalid request for verification is made,
        // for instance if the the phone number format is not valid.


        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            // Invalid request
            Toast.makeText(MainActivity.this, "invalid request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (e instanceof FirebaseTooManyRequestsException) {
            Toast.makeText(MainActivity.this, "muitas tentativas erradas", Toast.LENGTH_SHORT).show();
        }

        // Show a message and update the UI
    }

    @Override
    public void onCodeSent(@NonNull String verificationId,
                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
        // The SMS verification code has been sent to the provided phone number, we
        // now need to ask the user to enter the code and then construct a credential
        // by combining the code with a verification ID.
        // Save verification ID and resending token so we can use them later
        Toast.makeText(MainActivity.this, "token "+token.toString() , Toast.LENGTH_SHORT).show();
    }
};

        if (auth.getCurrentUser() != null){
            Toast.makeText(getApplicationContext(), "Bem-vindo de volta!", Toast.LENGTH_LONG).show();
            passarDeTela();
        }

        binding.logincelular.setOnClickListener(view ->{
            startActivity(new Intent(getApplicationContext(), LoginCelularActivity.class));
            /*MensagemNumeroBinding bindingNumero = MensagemNumeroBinding.inflate(LayoutInflater.from(getApplicationContext()));
            AlertDialog.Builder builderDialog = new AlertDialog.Builder(MainActivity.this);
            builderDialog.setTitle("Aviso");
            builderDialog.setMessage("Insira o número do seu telefone corretamente!");
            bindingNumero.pronto.setOnClickListener(view1 ->{
                String phoneNumber = bindingNumero.editNumero.getText().toString();
                if (!phoneNumber.isEmpty() && phoneNumber != null){
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(auth)                                                                                       
                                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }else{
                    Toast.makeText(this, "Preencha com o seu número de celular.", Toast.LENGTH_SHORT).show();
                }
            });
            bindingNumero.cancelar.setOnClickListener(view2 ->{
                dialog_numero.dismiss();
            });
            builderDialog.setView(bindingNumero.getRoot());
            dialog_numero = builderDialog.create();
            dialog_numero.show();*/
        });
        binding.opcLogin.setOnClickListener(v ->{
            if (bar.getTitle().toString().equals("Login")){
                bar.setTitle("Cadastro");
            }else{
                bar.setTitle("Login");
            }
        });

        binding.pronto.setOnClickListener(view -> {
            email = binding.editEmail.getText().toString().trim();
            senha = binding.editSenha.getText().toString();

            if (!senha.isEmpty() && senha != null){
                if (!email.isEmpty() && email != null){
                    if(binding.opcLogin.isChecked()) {
                        progress.setVisibility(View.VISIBLE);
                        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(
                                task ->{
                                    if(task.isSuccessful()){
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Sucesso ao fazer cadastro!",
                                                Toast.LENGTH_SHORT).show();

                                        newUser = new UserModel();
                                        newUser.setCredencial(email);
                                        newUser.setQtMoedas(0);
                                        newUser.setVerAnuncio(true);
                                        Log.d("mds", "email = " + email + " codificado = " + codbase64.cod(email));
                                        ref.child("usuarios").child(codbase64.cod(email).trim()).setValue(newUser).addOnCompleteListener(task1 -> {
                                            if(task1.isSuccessful()){
                                                logar(view);
                                            }else{
                                                Log.d("mds", task1.getException().toString());
                                                Toast.makeText(MainActivity.this, "Erro ao cadastrar no banco!", Toast.LENGTH_SHORT).show();
                                                progress.setVisibility(View.GONE);
                                            }
                                        });
                                    }else{
                                        progress.setVisibility(View.GONE);
                                        // erros
                                        try {
                                            throw task.getException();
                                        }
                                        catch (FirebaseAuthWeakPasswordException e){
                                            Snackbar.make(view, "Senha muito fraca para ser usada no aplicativo.", Snackbar.LENGTH_LONG).show();
                                        }
                                        catch (FirebaseAuthInvalidCredentialsException em){
                                            Snackbar.make(view, "E-mail mal formatado", Snackbar.LENGTH_LONG).show();
                                        }
                                        catch (FirebaseAuthUserCollisionException co){
                                            Snackbar.make(view, "Essa conta já existe, tente fazer login!", Snackbar.LENGTH_LONG).show();
                                        }
                                        catch (FirebaseNetworkException net){
                                            Snackbar.make(view, "Sem internet", Snackbar.LENGTH_LONG).show();
                                        }
                                        catch (Exception m){
                                            Toast.makeText(getApplicationContext(), "bata print disso e envie para o desenvolvedor:\n"+m.getMessage()
                                            ,Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }
                        );
                    }else{
                        // login
                       logar(view);
                    }
                }else{
                    Snackbar.make(view, "Preencha o campo e-mail.", Snackbar.LENGTH_LONG).show();
                }
            }else{
                Snackbar.make(view, "Preencha o campo senha.", Snackbar.LENGTH_LONG).show();
            }
        });

        binding.esqueci.setOnClickListener(view ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            RecuperarEmailDialogBinding v = RecuperarEmailDialogBinding.inflate(LayoutInflater.from(getApplicationContext()));
            builder.setView(v.getRoot());
            dialog_esq_pwd = builder.create();

            v.confirmar.setOnClickListener(view1 ->{
                String emailRecuperar = v.editMudarSenha.getText().toString().trim();
                if (!emailRecuperar.isEmpty() && emailRecuperar != null){
                    auth.sendPasswordResetEmail(emailRecuperar).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            AlertDialog.Builder b2 = new AlertDialog.Builder(MainActivity.this);
                            b2.setMessage("Mandamos um E-mail para você poder trocar sua senha, por favor, verifique A CAIXA DE SPAM DO SEU E-MAIL e volte aqui para tentar fazer login novamente.");
                            b2.setTitle("Aviso!");
                            b2.setPositiveButton("OK", (dialogInterface, i) -> {
                                dialog_confirm.dismiss();
                                dialog_esq_pwd.dismiss();
                            });
                            b2.setCancelable(false);
                            dialog_confirm = b2.create();
                            dialog_confirm.show();
                            Toast.makeText(getApplicationContext(), "E-mail enviado para recuperação!", Toast.LENGTH_SHORT).show();
                        }else{
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthInvalidCredentialsException bf){
                                Toast.makeText(getApplicationContext(),"Esse e-mail está icorreto", Toast.LENGTH_LONG).show();
                            }
                            catch (FirebaseAuthInvalidUserException ne){
                                Toast.makeText(getApplicationContext(),"Esse e-mail não está cadastrado!", Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e){
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Insira o e-mail para ser recuperado!", Toast.LENGTH_SHORT).show();
                }

            });
            dialog_esq_pwd.show();
        });
    }

    public void passarDeTela(){
        // passando de tela
        finish();
        startActivity(new Intent(getApplicationContext(), Principal.class));
    }

    public void logar(View view){
        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(
                task ->{
                    if (task.isSuccessful()){
                        passarDeTela();
                        Toast.makeText(getApplicationContext(), "Bem-vindo de volta!", Toast.LENGTH_LONG).show();
                    }else{
                        //erros
                        try{
                            throw task.getException();
                        }catch (FirebaseAuthInvalidCredentialsException c){
                            Snackbar.make(view, "Voccê digitou o e-mail ou senha errado!", Snackbar.LENGTH_LONG).show();
                        }
                        catch(FirebaseAuthInvalidUserException e){
                            Snackbar.make(view, "Essa conta não existe, tente fazer cadastro!", Snackbar.LENGTH_LONG).show();
                        }
                        catch (FirebaseNetworkException net){
                            Snackbar.make(view, "Sem internet", Snackbar.LENGTH_LONG).show();
                        }
                        catch (Exception m){
                            Toast.makeText(getApplicationContext(), "bata print disso e envie para o desenvolvedor:\n"+m.getMessage()
                                    ,Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

}