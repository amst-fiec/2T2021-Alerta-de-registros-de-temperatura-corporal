package com.example.senensig.visitante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.senensig.MainActivity;
import com.example.senensig.R;
import com.example.senensig.admin.AdminActivity;
import com.example.senensig.admin.HistorialVisitantesActivity;
import com.example.senensig.objects.MyDBHandler;
import com.example.senensig.objects.User;
import com.example.senensig.objects.Visita;
import com.example.senensig.objects.Visitante;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Map;
import java.util.Objects;

public class VisitanteActivity extends AppCompatActivity {

    private Button btn_MiHistorial, btnSignOutVisitor, buttonCreateFalseVisit;
    private GoogleApiClient mGoogleApiClient;

    private DatabaseReference db_reference;

    private TextView textViewBienvenidaVisitor, textViewTempVisitante, textViewHoraLastVisit,
            textViewFechaLastVisit, textViewPlaceVisiter, textViewEstadoVisitante, alertaVisitor;
    private ImageView imageViewPeligroFiebreVisitante ;

    private Visitante visitor;
    private HashMap<String, String> info_user;

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private GoogleSignInClient mGoogleSignInClient;

    // local SQL database
    private MyDBHandler dbHandler = new MyDBHandler(this, null,null, 2);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitante);

        // |------------- GOOGLE SIGN IN ------------| //
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // |------------- GOOGLE SIGN IN ------------| //

        visitor = new Visitante();
        visitor.setIdVisitor("0706344240");
        visitor.setName("Ricardo Tapia");

        Intent intent = getIntent();
        info_user = (HashMap<String, String>) intent.getSerializableExtra("info_user");
        // TODO: System.out.println("========= user: " + info_user.get("user_id") + "\n"+ "========== user id: " + info_user.get("user_name"));
        if (info_user!= null ){
            System.out.println("info_user: " + info_user + "info_user.toString(): " + info_user.toString());
            visitor.setIdVisitor(info_user.get("user_id"));
            visitor.setName(info_user.get("user_name"));
        }
        else {
            retrievingGUserInformation ();
        }
        updateName(visitor.getName());

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()) //Use app context to prevent leaks using activity
                //.enableAutoManage(this /* FragmentActivity */, connectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        textViewBienvenidaVisitor = findViewById(R.id.textViewBienvenidaVisitor);
        textViewTempVisitante= findViewById(R.id.textViewTempVisitante);
        textViewHoraLastVisit= findViewById(R.id.textViewHoraLastVisit);
        textViewFechaLastVisit = findViewById(R.id.textViewFechaLastVisit);
        textViewPlaceVisiter= findViewById(R.id.textViewPlaceVisiter);
        textViewEstadoVisitante= findViewById(R.id.textViewEstadoVisitante);
        alertaVisitor= findViewById(R.id.alertaVisitor);
        imageViewPeligroFiebreVisitante= findViewById(R.id.imageViewPeligroFiebreVisitante);
        btn_MiHistorial = findViewById(R.id.btn_MiHistorial);
        btnSignOutVisitor = findViewById(R.id.btnSignOutVisitor);
        buttonCreateFalseVisit = findViewById(R.id.buttonCreateFalseVisit);

        String bienvenida = "Bienvenido "+visitor.getName();
        textViewBienvenidaVisitor.setText( bienvenida );

        btnSignOutVisitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logout
                //signOut();
                //startActivity(new Intent(VisitanteActivity.this, MainActivity.class));
                deleteTableEntry();
            }
        });
        btn_MiHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVisitorsFromFirebase();
                //startActivity(new Intent(VisitanteActivity.this, MiHistorialActivity.class));
            }
        });
        buttonCreateFalseVisit.setOnClickListener(buttonView ->{
            createFalseVisit();
        });
        //initDB();
        getUserData();
    }

    public void retrievingGUserInformation (){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            visitor.setIdVisitor(dbHandler.findHandler(20).getUserName());
            visitor.setName(acct.getDisplayName());
        }
    }

    private void getVisitorsFromFirebase(){
        List<Visita> visitasArray = new ArrayList<>();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference listIdRef = rootRef.child("visitors").child(visitor.getIdVisitor()).child("visitas"); // VisitantesDatabase - visitanteID - visitas(array) - (obtener visitas)
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot visita: dataSnapshot.getChildren()) {
                    visitasArray.add(visita.getValue(Visita.class));
                }
                // after retrieve the user visits data from firebase, then launch the next activity. In this way you can ensure no null data is send over activities transition
                visitor.setVisitas(visitasArray);
                Bundle bundle=new Bundle();
                bundle.putSerializable("intentGetDataVisitante",(Serializable) visitor);
                bundle.putString("activity","VisitanteActivity");

                Intent intentVisitsList = new Intent(VisitanteActivity.this, MiHistorialActivity.class);
                intentVisitsList.putExtras(bundle);
                startActivity(intentVisitsList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", databaseError.getMessage());
            }
        };
        listIdRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void getUserData(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference listIdRef = rootRef.child("visitors").child(visitor.getIdVisitor()).child("visitas"); // VisitantesDatabase - visitanteID - visitas(array) - (obtener visitas)
        listIdRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Visita visit = new Visita();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    visit =  child.getValue(Visita.class);
                }
                textViewPlaceVisiter.setText(visit != null ? visit.getLugar() : "Sin datos");
                textViewTempVisitante.setText( String.valueOf(visit != null ? visit.getTemperaturaC() + "°C" : "Sin datos") );
                textViewFechaLastVisit.setText(getFecha(visit != null ? visit.getFecha() : "Sin datos")[0]);
                textViewHoraLastVisit.setText(getFecha(visit != null ? visit.getFecha() : "Sin datos")[1]);

                // Integer.parseInt( String.valueOf(dataSnapshot.child("temperaturaC").getValue())) < 38
                if ( (visit != null ? visit.getTemperaturaC() : -1 ) != -1) {
                    if ( visit.getTemperaturaC() < 38 ){
                        alertaVisitor.setTextColor(Color.parseColor("#67BA6B"));
                        imageViewPeligroFiebreVisitante.setImageResource(R.drawable.thumb_ups);
                        textViewEstadoVisitante.setText("Bien");
                        alertaVisitor.setText("Esta bien");

                    }else{
                        alertaVisitor.setTextColor(Color.parseColor("#BE1E1E"));
                        imageViewPeligroFiebreVisitante.setImageResource(R.drawable.alert__copy_);
                        textViewEstadoVisitante.setText("enfermo");
                        alertaVisitor.setText("Esta enfermo");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void updateName(String name){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference listIdRef = rootRef.child("visitors").child(visitor.getIdVisitor()).child("name"); // VisitantesDatabase - visitanteID - visitas(array) - (obtener visitas)
        listIdRef.setValue(name);
    }

    //TODO: year:month:day:hour:min:sec
    private void createFalseVisit(){ // TODO: solo para probar

        double minTemp = 36.5;
        double maxTemp = 39.2;
        double randomTemp = Math.random()*(maxTemp-minTemp+1)+minTemp;
        String randomTempStr = decimalFormat.format(randomTemp);

        Visita visitaFalsa = new Visita("2021:01:17:17:15:15", "133456", "Mall del Oreo", Double.valueOf(randomTempStr));

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference listIdRef = rootRef.child("visitors").child(visitor.getIdVisitor()).child("visitas"); // VisitantesDatabase - visitanteID - visitas(array) - (obtener visitas)

        // añadir nueva visita
        listIdRef.push();
        String keyVisitArray = rootRef.child("visitors").child(visitor.getIdVisitor()).child("visitas").push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put(keyVisitArray,visitaFalsa );
        listIdRef.updateChildren(map);
    }

    public String[] getFecha (String strFecha){
        String[] arrayFecha =  strFecha.split(":");
        String mes;
        if (arrayFecha.length == 6){
            switch (Integer.parseInt(arrayFecha[1])){
                case 1:
                    mes = "Enero";
                    break;
                case 2:
                    mes = "Febrero";
                    break;
                case 3:
                    mes = "Marzo";
                    break;
                case 4:
                    mes = "Abril";
                    break;
                case 5:
                    mes = "Mayo";
                    break;
                case 6:
                    mes = "Junio";
                    break;
                case 7:
                    mes = "Julio";
                    break;
                case 8:
                    mes = "Agosto";
                    break;
                case 9:
                    mes = "Septiembre";
                    break;
                case 10:
                    mes = "Octubre";
                    break;
                case 11:
                    mes = "Noviembre";
                    break;
                case 12:
                    mes = "Diciembre";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + Integer.parseInt(arrayFecha[1]));
            }
            return new String[]{arrayFecha[2] + " " +mes + " , " + arrayFecha[0], arrayFecha[3] + ":" + arrayFecha[4] + ":" + arrayFecha[5]};
        }
        return new String[]{"Sin datos", "Sin datos"};
    }


    public void signOut(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        startActivity(new Intent(this, MainActivity.class));
    }

    private void deleteTableEntry(){
        dbHandler.deleteHandler(20); // delete user ID
        boolean result = dbHandler.deleteHandler(10); // delete user type identifier
        if (result) {
            System.out.println("table delete it");
            signOut();
        } else
            System.out.println("table to be deleted not found");
    }
}