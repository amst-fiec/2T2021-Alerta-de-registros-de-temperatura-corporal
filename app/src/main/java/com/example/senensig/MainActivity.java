package com.example.senensig;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.senensig.admin.AdminActivity;
import com.example.senensig.objects.MyDBHandler;
import com.example.senensig.objects.User;
import com.example.senensig.visitante.VisitanteActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button btn_loginMenu, btn_signupMenu;

    private Toast toastErrorSignIn;

    private int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // |------------- GOOGLE SIGN IN ------------| //
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(showInternetConnectionMessage()){
            updateUI(account);
        }
        else{
            System.out.println("No hay conexión a internet");
            toastErrorSignIn=Toast.makeText(getApplicationContext(),"No hay conección a internet",Toast.LENGTH_LONG);
            toastErrorSignIn.show();
        }
        // |------------- GOOGLE SIGN IN ------------| //

        btn_loginMenu = findViewById(R.id.btn_loginMenu);
        //btn_signupMenu = findViewById(R.id.btn_signupMenu);
        btn_loginMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // seteando estado de internet
                if(!showInternetConnectionMessage()){
                    System.out.println("No hay conexión a internet");
                    toastErrorSignIn=Toast.makeText(
                            getApplicationContext(),"No hay conección a internet",Toast.LENGTH_LONG);
                    toastErrorSignIn.show();
                }else{
                    System.out.println("Si hay conexión a internet");
                    startActivity(new Intent(MainActivity.this, UserDistinctionActivityLgn.class));
                }
            }
        });

        // btn_signupMenu.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, UserDistinctionActivitySgnp.class)));
    }

    private boolean showInternetConnectionMessage(){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        return nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
    }

    // action to do if a user is already signed in
    private void updateUI(GoogleSignInAccount account){
        if (account == null){
            System.out.println("Not signed in: ");
        }
        else{
            System.out.println("Already signed in: "+account);
            System.out.println("Email: " + account.getEmail());

            // aqui leer la base de datos
            if ( getUserType().equals("AdminActivity") ){
                Intent intent = new Intent(this, AdminActivity.class);
                startActivity(intent);
            }
            else if ( getUserType().equals("VisitanteActivity") ){
                Intent intent = new Intent(this, VisitanteActivity.class);
                startActivity(intent);
            }
            else{
                System.out.println("sesion iniciada pero no direccionado el menu correspondiente a la sesion iniciada");
            }
        }
    }

    private String getUserType(){
        String userType = "";
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 2);
        User user = dbHandler.findHandler(10);
        if (user != null) {
            userType = user.getUserName();
        } else {
            userType = "";
        }
        return userType;
    }

    /*
    private void cerrarSesion() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> updateUI(null));
    }
     */

    /*
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            HashMap<String, String> info_user = new HashMap<String, String>();
            info_user.put("user_name", user.getDisplayName());
            info_user.put("user_email", user.getEmail());
            info_user.put("user_photo", String.valueOf(user.getPhotoUrl()));
            info_user.put("user_id", user.getUid());
            finish();
            System.out.println("updateUI");
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra("info_user", info_user);
            startActivity(intent);
        } else {
            System.out.println("===========================  sin registrarse");
        }
    }
     */
}