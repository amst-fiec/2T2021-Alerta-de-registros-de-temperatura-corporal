package com.example.senensig.visitante;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.senensig.R;
import com.example.senensig.admin.AdminActivity;
import com.example.senensig.admin.HistorialVisitantesActivity;
import com.example.senensig.objects.Visita;
import com.example.senensig.objects.Visitante;
import com.example.senensig.visitante.VisitanteActivity;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class LogingGoogleActivityVisitor extends AppCompatActivity{
    private SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private EditText editTextTextPersonName;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private Toast toastErrorSignIn;

    private String idUserStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loging_google_visitor);

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        editTextTextPersonName =  findViewById(R.id.editTextTextPersonName);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent = getIntent();
        String msg = intent.getStringExtra("msg");
            if(msg != null){
                if(msg.equals("cerrarSesion")){
                cerrarSesion();
            }
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idUserStr = editTextTextPersonName.getText().toString();
                if (!idUserStr.equals("")){
                    System.out.println("hola desde btn");
                    //iniciarSesion(view);
                    getVisitorsFromFirebase(idUserStr, view);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Ingrese ID",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void cerrarSesion() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
        task -> updateUI(null));
    }

    public void iniciarSesion(View view) {
        System.out.println("=========================== iniciarSesion");
        resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
    }

    public ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                System.out.println("=========================== onActivityResult OK");
                Intent intent = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        System.out.println("===========================  onActivityResult");
                        firebaseAuthWithGoogle(account);
                    }
                    } catch (ApiException e) {

                    Log.w("TAG", "Fallo el inicio de sesión con google.", e);
                    String msn = "Fallo el inicio de sesión con google";
                    toastErrorSignIn=Toast.makeText(
                            getApplicationContext(),
                            msn,
                            Toast.LENGTH_SHORT);
                    toastErrorSignIn.setMargin(50,50);
                    toastErrorSignIn.show();

                }
                }
            System.out.println("=========================== onActivityResult NOT OK");
        }
    });

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),
                null);
        mAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                System.out.println("===========================  firebaseAuthWithGoogle");
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
                } else {
                System.out.println("error");
                updateUI(null);
                }
            });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            HashMap<String, String> info_user = new HashMap<String, String>();
            info_user.put("user_name", user.getDisplayName());
            info_user.put("user_email", user.getEmail());
            info_user.put("user_photo", String.valueOf(user.getPhotoUrl()));
            info_user.put("FirebaseUser_id", user.getUid());
            info_user.put("user_id", idUserStr);
            info_user.put("user_type", "visitor");
            finish();
            System.out.println("updateUI");
            Intent intent = new Intent(this, VisitanteActivity.class);
            intent.putExtra("info_user", info_user);
            startActivity(intent);
        } else {
            System.out.println("===========================  sin registrarse");
        }
    }

    private void getVisitorsFromFirebase(String idIngresado, View view){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference listIdRef = rootRef.child("visitors"); // VisitantesDatabase - visitanteID - visitas(array) - (obtener visitas)
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean ingresar = false; // =  new Visitante();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    System.out.println("idViitor: "+ child.getKey() +" kay: " + child.getKey());
                    if (Objects.equals(child.getKey(), idIngresado)){
                        ingresar = true;
                        System.out.println("idViitor: "+ Objects.requireNonNull(child.child("idVisitor").getValue()));
                        iniciarSesion(view);
                    }
                }
                if (!ingresar){
                    Toast.makeText(getApplicationContext(),"Usuario sin datos",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", databaseError.getMessage());
            }
        };
        listIdRef.addListenerForSingleValueEvent(valueEventListener);
    }
}