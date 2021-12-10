package com.example.senensig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class HistorialVisitantes extends AppCompatActivity {

    ImageButton  imageButton_visitorListed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_visitantes);

        imageButton_visitorListed = findViewById(R.id.imageButton_visitorListed);

        imageButton_visitorListed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HistorialVisitantes.this, DatoVisitante.class));
            }
        });
    }
}