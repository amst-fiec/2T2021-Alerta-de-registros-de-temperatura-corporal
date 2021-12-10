package com.example.senensig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btn_loginMenu, btn_signupMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_loginMenu = findViewById(R.id.btn_loginMenu);
        btn_signupMenu = findViewById(R.id.btn_signupMenu);

        btn_loginMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserDistinctionActivityLgn.class));
            }
        });

        btn_signupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserDistinctionActivitySgnp.class));
            }
        });
    }
}