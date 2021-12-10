package com.example.senensig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VisitanteActivity extends AppCompatActivity {

    Button btn_MiHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitante);

        btn_MiHistorial = findViewById(R.id.btn_MiHistorial);

        btn_MiHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VisitanteActivity.this, MiHistorial.class));
            }
        });
    }
}