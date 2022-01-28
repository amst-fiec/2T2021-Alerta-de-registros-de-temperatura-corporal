package com.example.senensig.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.senensig.MainActivity;
import com.example.senensig.R;
import com.example.senensig.objects.MyDBHandler;
import com.example.senensig.objects.Visita;
import com.example.senensig.objects.Visitante;
import com.example.senensig.visitante.MiHistorialActivity;
import com.example.senensig.visitante.VisitanteActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {

    private Button btn_accederVHistorial, btnSignOutAdmin;
    private GoogleApiClient mGoogleApiClient;
    private boolean nextActivity;
    private List<Double> temperaturasArray = new ArrayList<>();
    private BarChart barChartTempPercent;
    private TextView textViewPersonasConFiebre,textViewPorcentajeConFiebre,
            textViewPorcentajeSinFiebre, textViewPersonasSinFiebre;
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // |------------- GOOGLE SIGN IN ------------| //
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // |------------- GOOGLE SIGN IN ------------| //
        /*
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()) //Use app context to prevent leaks using activity
                //.enableAutoManage(this // FragmentActivity // , connectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
         */

        btn_accederVHistorial = findViewById(R.id.btn_accederVHistorial);
        btnSignOutAdmin = findViewById(R.id.btnSignOutAdmin);
        barChartTempPercent = findViewById(R.id.barChartTempPercent);
        textViewPersonasConFiebre = findViewById(R.id.textViewPersonasConFiebre);
        textViewPorcentajeConFiebre = findViewById(R.id.textViewPorcentajeConFiebre);
        textViewPorcentajeSinFiebre = findViewById(R.id.textViewPorcentajeSinFiebre);
        textViewPersonasSinFiebre = findViewById(R.id.textViewPersonasSinFiebre);


        btnSignOutAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //signOut();
                deleteTableEntry();
            }
        });
        btn_accederVHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextActivity = true;
                getVisitorsFromFirebase();
            }
        });

        getVisitorsFromFirebase();
    }

    private void deleteTableEntry(){
        MyDBHandler dbHandler = new MyDBHandler(this, null,
                null, 2);
        boolean result = dbHandler.deleteHandler(10);
        if (result) {
            System.out.println("table delete it");
            signOut();
        } else
            System.out.println("table to be deleted not found");
    }

    public void signOut(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

        startActivity(new Intent(AdminActivity.this, MainActivity.class));
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            HashMap<String, String> info_user = new HashMap<String, String>();
            info_user.put("user_name", user.getDisplayName());
            info_user.put("user_email", user.getEmail());
            info_user.put("user_photo", String.valueOf(user.getPhotoUrl()));
            info_user.put("user_id", user.getUid());
            finish();
            System.out.println("user_name: " + user.getDisplayName());
            Intent intent = new Intent(this, VisitanteActivity.class);
            intent.putExtra("info_user", info_user);
            startActivity(intent);
        } else {
            System.out.println("===========================  sin registrarse");
        }
    }

    private void getVisitorsFromFirebase(){
        temperaturasArray = new ArrayList<>();
        List<Visitante> visitantesArray = new ArrayList<>();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference listIdRef = rootRef.child("visitors"); // VisitantesDatabase - visitanteID - visitas(array) - (obtener visitas)
        //ValueEventListener valueEventListener = new ValueEventListener() {
        listIdRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // values that will renewed
                Visitante visitante; // =  new Visitante();
                //Visita visit; // = new Visita();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    double lastTemp = 36;
                    // creo arreglo visitas por cada visitante
                    List<Visita> visitasArray = new ArrayList<>();
                    // creo visitante nuevo
                    visitante =  new Visitante();
                    for (DataSnapshot dataPost :  child.child("visitas").getChildren()) {
                        visitasArray.add(dataPost.getValue(Visita.class));
                        lastTemp = Objects.requireNonNull(dataPost.getValue(Visita.class)).getTemperaturaC();
                    }
                    temperaturasArray.add(lastTemp);
                    // inicializo visitante
                    visitante.setName(Objects.requireNonNull(child.child("name").getValue()).toString());
                    visitante.setIdVisitor(Objects.requireNonNull(child.child("idVisitor").getValue()).toString());
                    visitante.setVisitas(visitasArray);
                    // añado a arreglo visitantes
                    visitantesArray.add(visitante);
                }
                // set statistical graph
                setStatistic(temperaturasArray);
                System.out.println("arreglo con todos los visitatnes: " + visitantesArray);
                if (nextActivity){
                    // after retrieve the user visits data from firebase, then launch the next activity. In this way you can ensure no null data is send over activities transition
                    Intent intentVisitsList = new Intent(AdminActivity.this, HistorialVisitantesActivity.class);
                    intentVisitsList.putExtra("intentGetVisitorsList", (Serializable) visitantesArray);
                    startActivity(intentVisitsList);
                    System.out.println("hacia la siguiente activity: " + visitantesArray);
                    nextActivity = false;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", databaseError.getMessage());
            }
        });
        //listIdRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void setStatistic(List<Double> datosTemperaturas){
        System.out.println("datosTemperaturas: " + datosTemperaturas);
        double contadorBajo = 0,contadorAlto = 0,  porcentajeBajo,  porcentajeAlto;
        for (double temp : datosTemperaturas){
            if (temp<38)contadorBajo+=1;
            else contadorAlto+=1;
        }
        porcentajeBajo = (contadorBajo*100) / datosTemperaturas.size();
        porcentajeAlto = (contadorAlto*100) / datosTemperaturas.size();
        String[] arrayHabilidadesHero = new String[2];
        arrayHabilidadesHero[0] = String.valueOf(porcentajeBajo);//no
        arrayHabilidadesHero[1] = String.valueOf(porcentajeAlto);//si


        textViewPorcentajeSinFiebre.setText(decimalFormat.format(porcentajeBajo));;
        textViewPersonasSinFiebre.setText(decimalFormat.format(contadorBajo));;
        textViewPorcentajeConFiebre.setText(decimalFormat.format(porcentajeAlto));;
        textViewPersonasConFiebre.setText(decimalFormat.format(contadorAlto));

        renderChartData(arrayHabilidadesHero);
    }

    public void renderChartData(String[] arrayTemperaturePercent){
        // init data/values
        ArrayList<Double> valueList = new ArrayList<Double>();
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        // add data
        valueList.add(Double.valueOf(arrayTemperaturePercent[0])); // no
        valueList.add(Double.valueOf(arrayTemperaturePercent[1])); // si

        // fit data into a bar
        for ( int i = 0 ; i< valueList.size(); i++){
            BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue());
            entries.add(barEntry);
        }

        // seteo de columnas de las barras en el grafico
        BarDataSet barDataSet = new BarDataSet(entries, arrayTemperaturePercent[0]);
        BarData data = new BarData(barDataSet);
        barChartTempPercent.setData(data);
        barChartTempPercent.invalidate();

        // seteo de legend / hero nombre
        Legend legendHero = barChartTempPercent.getLegend();
        legendHero.setTextColor(Color.RED);

        // seteo de hailidades de heroe
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Sin fiebre");
        xAxisLabel.add("Con fiebre");

        barChartTempPercent.getXAxis().setValueFormatter(new ValueFormatter(xAxisLabel));
        XAxis xAxis = barChartTempPercent.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(0);
        xAxis.setTextSize(18f);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
    }

}