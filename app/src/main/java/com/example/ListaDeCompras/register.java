package com.example.ListaDeCompras;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.database.Cursor;

public class register extends AppCompatActivity {

    EditText etNome, etEmail, etPassword;
    Button btnRegister;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etNome = findViewById(R.id.etNome);
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister);
        db = new DatabaseHelper(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = etNome.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();


                if (nome.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(register.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else {
                    boolean success = db.registerUser(nome, email, password);
                    if (success) {
                        Toast.makeText(register.this, "Usuário cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(register.this, login.class));
                        finish();
                    }
                    else {
                        Toast.makeText(register.this, "Erro ao cadastrar usuário", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


}