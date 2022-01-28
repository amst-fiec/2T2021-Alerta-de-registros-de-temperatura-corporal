package com.example.senensig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.senensig.admin.LogingGoogleActivityAdmin;
import com.example.senensig.visitante.LogingGoogleActivityVisitor;

public class UserDistinctionActivityLgn extends AppCompatActivity {

    ImageButton imageButton_admin_lgn, imageButton_visitante_lgn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_distinction_lgn);

        imageButton_visitante_lgn = findViewById(R.id.imageButton_visitante_lgn);
        imageButton_admin_lgn = findViewById(R.id.imageButton_admin_lgn);

        imageButton_visitante_lgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserDistinctionActivityLgn.this, LogingGoogleActivityVisitor.class));
            }
        });

        imageButton_admin_lgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserDistinctionActivityLgn.this, LogingGoogleActivityAdmin.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}