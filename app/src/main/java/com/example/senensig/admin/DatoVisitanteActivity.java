package com.example.senensig.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.senensig.R;
import com.example.senensig.objects.Visita;
import com.example.senensig.objects.Visitante;
import com.example.senensig.visitante.CaptionedImagesAdapterVisitas;

import java.util.List;

public class DatoVisitanteActivity extends AppCompatActivity {

    private Visitante visitante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dato_visitante);

        visitante = (Visitante) getIntent().getSerializableExtra("intentVisitorsSelector");

    }
}