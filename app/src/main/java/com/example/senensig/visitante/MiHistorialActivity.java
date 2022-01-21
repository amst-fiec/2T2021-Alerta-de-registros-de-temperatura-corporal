package com.example.senensig.visitante;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.senensig.R;
import com.example.senensig.objects.Visita;
import com.example.senensig.objects.Visitante;

import java.util.List;

public class MiHistorialActivity extends AppCompatActivity {

    private RecyclerView misVisitasRecycler;
    private List<Visita> misVisitasArray;

    private ImageView imageView_MeFromMyHistorial;
    private TextView textView_myNameMisHistorial, textView_myIDMyHistorial, textView_historialTitulo;
    private String previousActivity, activityTitulo;
    private Visitante visitor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_historial);


        imageView_MeFromMyHistorial = findViewById(R.id.imageView_MeFromMyHistorial);
        textView_myNameMisHistorial = findViewById(R.id.textView_myNameMisHistorial);
        textView_myIDMyHistorial = findViewById(R.id.textView_myIDMyHistorial);
        misVisitasRecycler = (RecyclerView) findViewById(R.id.RecyclerView_myVisits);
        textView_historialTitulo = findViewById(R.id.textView_historialTitulo);

        Bundle bundleData =getIntent().getExtras();
        previousActivity =  bundleData.getString("activity");
        if (previousActivity.equals("VisitanteActivity")) {
            activityTitulo = "Mi historial";
        }else if (previousActivity.equals("HistorialVisitantesActivity")){
            activityTitulo = "Visitante";
        }

        visitor = (Visitante) bundleData.getSerializable("intentGetDataVisitante");
        misVisitasArray =visitor.getVisitas();

        textView_historialTitulo.setText(activityTitulo);
        imageView_MeFromMyHistorial.setImageResource(R.drawable.app_user);
        textView_myNameMisHistorial.setText(visitor.getName());
        textView_myIDMyHistorial.setText("ID: "+visitor.getIdVisitor());
        // If get the product from the "previous" activity then ill store it in a Bundle var


        toImageAdapter();
        System.out.println("misVisitasArray: " + misVisitasArray);
        /*if (bundleVisitas!=null)
        {
            // change visibility to VISIBLE cause there is a products to purchase

            if (!bundleVisitas.isEmpty())
            {
                //misVisitasArray =  bundleVisitas.getParcelable("bundleList");
                toImageAdapter();
            }
            else
            {
                Toast.makeText(
                        this, "Error al mostrar el visitas", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(
                    this, "Error al cargar el visitas", Toast.LENGTH_LONG).show();
        }
         */

    }

    // <==========|| toImageAdapter method creates a CaptionImagesAdapter
    // object that will initialize all the cardViews in the RecyclerView ||==========> [BEGIN]
    public void toImageAdapter()
    {
        // create an CaptionImagesAdapter object and then i'll pass the arrays with the products
        // information that i want yo show in  the RecyclerView
        // The CaptionImagesAdapter only receive as arguments arrays with the elements to be
        // displayed.
        CaptionedImagesAdapterVisitas Adapter = new CaptionedImagesAdapterVisitas(
                misVisitasArray
        );

        misVisitasRecycler.setAdapter(Adapter);
        // LinearLayoutManager is used to show the cardView in a list way
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        misVisitasRecycler.setLayoutManager(layoutManager);

        // <==========|| Set Listeners ||==========>

        // active the interactive buttons in each product on the captionedPurchaseAdapter
        // * the listener will launch the EditActivity activity when the cardView within the
        //   recyclerView is touched
        // * "startActivity" implements the Listener onClick() method.
        //   It starts PizzaDetailActivity, passing it the product the user chose.

        Adapter.setListener(new CaptionedImagesAdapterVisitas.Listener()
        {
            @Override
            public void onClick(Visita visitasArray) {
                //System.out.println(": " + visitasArray);
            }
        });
        // <==========|| Set Listeners ||==========>

    }
    // <==========|| toImageAdapter method creates a CaptionImagesAdapter
    // object that will initialize all the cardViews in the RecyclerView ||==========> [END]
}