package com.example.ListaDeCompras;

import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class tela_inicial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_inicial);

        // Temporizador para exibir a SplashScreen por 3 segundos
        new Handler().postDelayed(() -> {
            // Redireciona para a MainActivity após o temporizador
            Intent intent = new Intent(tela_inicial.this, login.class);
            startActivity(intent);
            finish(); // Finaliza a SplashActivity
        }, 3000); // Tempo em milissegundos
    }
}
