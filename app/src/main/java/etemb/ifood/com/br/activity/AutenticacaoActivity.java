package etemb.ifood.com.br.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import etemb.ifood.com.br.R;
import etemb.ifood.com.br.helper.ConfiguracaoFirebase;
import etemb.ifood.com.br.helper.UsuarioFirebase;

public class AutenticacaoActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso, tipoUsuario;
    private LinearLayout linearTipoUsuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);
       //getSupportActionBar().hide();

        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getReferenciaAutenticacao();
        //autenticacao.signOut();

        //Verificar usuario logado
        verificaUsuarioLogado();

        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){//empresa
                    linearTipoUsuario.setVisibility(View.VISIBLE);
                }else { //usuario
                    linearTipoUsuario.setVisibility(View.GONE);
                }
            }
        });

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (!email.isEmpty()){
                    if (!senha.isEmpty()){

                        //Verificar o estado do switch
                        if (tipoAcesso.isChecked() ){//Cadastro
                            autenticacao.createUserWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Cadastro realizado com sucesso!",
                                                Toast.LENGTH_LONG).show();
                                        String tipoUsuario = getTipoUsuario();
                                        UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);
                                        abrirTelaPrincipal(tipoUsuario);
                                    }else{
                                        String erroExcecao = "";
                                        try {
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e){
                                            erroExcecao = "Digite um senha mais forte!";
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            erroExcecao = "Digite um E-mail v??lido!";
                                        }catch (FirebaseAuthUserCollisionException e ){
                                            erroExcecao = "Ja existe uma conta com o email informado";
                                        }catch (Exception e){
                                            erroExcecao = "ao cadastrar usu??rio: " + e.getMessage();
                                            e.printStackTrace();
                                        }

                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Erro "+ erroExcecao,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }else{//Login

                            autenticacao.signInWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Logado com sucesso",
                                                Toast.LENGTH_LONG).show();
                                        String tipoUsuario = task.getResult().getUser().getDisplayName();
                                        abrirTelaPrincipal(tipoUsuario);
                                    }else{
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Erro ao fazer login : "+ task.getException(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }) ;
                        }

                    }else {
                        Toast.makeText(AutenticacaoActivity.this,
                                "Preencha a Senha!",
                                Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(AutenticacaoActivity.this,
                            "Preencha o E-mail",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void verificaUsuarioLogado(){
        FirebaseUser  usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual !=null){
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    private String getTipoUsuario() {
        return tipoUsuario.isChecked() ? "E" : "U";
    }

    private void abrirTelaPrincipal(String tipoUsuario){
        if (tipoUsuario.equals("E")) {//Empresa
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
        }else {//Usuario
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

    private void inicializarComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchTipoUsuario);
        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
    }
}