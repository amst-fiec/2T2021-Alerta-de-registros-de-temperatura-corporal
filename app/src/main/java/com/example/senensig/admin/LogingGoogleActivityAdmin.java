package com.example.senensig.admin;

import static com.firebase.ui.auth.ui.email.TroubleSigningInFragment.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.senensig.MainActivity;
import com.example.senensig.R;
import com.example.senensig.admin.AdminActivity;
import com.example.senensig.objects.MyDBHandler;
import com.example.senensig.objects.User;
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

import java.util.HashMap;


public class LogingGoogleActivityAdmin extends AppCompatActivity{
    private SignInButton signInButton;

    private Toast toastErrorSignIn;

    //
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loging_google_admin);

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
        updateUI(account);
        // |------------- GOOGLE SIGN IN ------------| //

        /*
        Intent intent = getIntent();
        String msg = intent.getStringExtra("msg");
        if(msg != null){
            if(msg.equals("cerrarSesion")){
                //cerrarSesion();
            }
        }
        */

        signInButton=(SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // seteando estado de internet
                if(!showInternetConnectionMessage()){
                    toastErrorSignIn=Toast.makeText(
                            getApplicationContext(),"No hay conección a internet", Toast.LENGTH_SHORT);
                    toastErrorSignIn.setMargin(80,80);
                    toastErrorSignIn.show();
                }else{
                    //iniciarSesion();
                    signIn();
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            System.out.println("Email: " + account.getEmail());
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    public void retrievingGUserInformation (){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
        }
    }

    // action to do if a user is already signed in
    private void updateUI(GoogleSignInAccount account){
        if (account == null){
            System.out.println("Not signed in: ");
        }
        else{
            System.out.println("Already signed in: "+account);
            System.out.println("Email: " + account.getEmail());

            setUserTypeOnDB();

            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
        }
    }

    private boolean showInternetConnectionMessage(){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        return nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
    }

    private void setUserTypeOnDB(){
        String activityType = "AdminActivity";
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 2);
        dbHandler.addHandler( new User(10, activityType));
    }

    private void setUserTypeOnDB2(){
        String activityType = "AdminActivity";
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        if (dbHandler.findHandler(10) != null) { // si ya existe un tipo de paciente update the value
            boolean result = dbHandler.updateHandler(10, activityType);
            if (result) {
                System.out.println("tipo de paciente actualizado a: "+activityType);
            } else
                System.out.println("No encontro tipo de paciente");
        } else { // si no existe, crealo
            dbHandler.addHandler( new User(10, activityType));
        }
    }
}