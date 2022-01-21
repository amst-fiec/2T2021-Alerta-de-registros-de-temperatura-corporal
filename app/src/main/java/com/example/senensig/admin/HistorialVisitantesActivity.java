package com.example.senensig.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.senensig.R;
import com.example.senensig.objects.Visita;
import com.example.senensig.objects.Visitante;
import com.example.senensig.visitante.CaptionedImagesAdapterVisitas;
import com.example.senensig.visitante.MiHistorialActivity;

import java.io.Serializable;
import java.util.List;

public class HistorialVisitantesActivity extends AppCompatActivity {

    private Button btnAnterior, btnSiguiente;
    private RecyclerView  visitors_recyclerView;
    private List<Visitante> visitantesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_visitantes);

        visitantesArray = (List<Visitante>) getIntent().getSerializableExtra("intentGetVisitorsList");

        //btnAnterior = findViewById(R.id.btnAnterior);
        //btnSiguiente = findViewById(R.id.btnSiguiente);
        visitors_recyclerView = findViewById(R.id.visitors_recyclerView);

        toImageAdapter();
    }

    // <==========|| toImageAdapter method creates a CaptionImagesAdapter
    // object that will initialize all the cardViews in the RecyclerView ||==========> [BEGIN]
    public void toImageAdapter()
    {
        // create an CaptionImagesAdapter object and then i'll pass the arrays with the products
        // information that i want yo show in  the RecyclerView
        // The CaptionImagesAdapter only receive as arguments arrays with the elements to be
        // displayed.
        CaptionedImageAdapterVisitantes Adapter = new CaptionedImageAdapterVisitantes(
                visitantesArray
        );

        visitors_recyclerView.setAdapter(Adapter);
        // LinearLayoutManager is used to show the cardView in a list way
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        visitors_recyclerView.setLayoutManager(layoutManager);

        // <==========|| Set Listeners ||==========>

        // active the interactive buttons in each product on the captionedPurchaseAdapter
        // * the listener will launch the EditActivity activity when the cardView within the
        //   recyclerView is touched
        // * "startActivity" implements the Listener onClick() method.
        //   It starts PizzaDetailActivity, passing it the product the user chose.

        Adapter.setListener(new CaptionedImageAdapterVisitantes.Listener()
        {
            @Override
            public void onClick(Visitante visitasnte) {
                Bundle bundle=new Bundle();
                bundle.putSerializable("intentGetDataVisitante",(Serializable) visitasnte);
                bundle.putString("activity","HistorialVisitantesActivity");

                Intent intentVisitsList = new Intent(HistorialVisitantesActivity.this, MiHistorialActivity.class);
                intentVisitsList.putExtras(bundle);
                startActivity(intentVisitsList);
            }
        });
        // <==========|| Set Listeners ||==========>
    }
    // <==========|| toImageAdapter method creates a CaptionImagesAdapter
    // object that will initialize all the cardViews in the RecyclerView ||==========> [END]
}