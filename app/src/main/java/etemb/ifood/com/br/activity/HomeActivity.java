package etemb.ifood.com.br.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import etemb.ifood.com.br.R;
import etemb.ifood.com.br.helper.ConfiguracaoFirebase;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair :
                autenticacao = ConfiguracaoFirebase.getReferenciaAutenticacao();
                autenticacao.signOut();
                startActivity(new Intent(this, AutenticacaoActivity.class ));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}