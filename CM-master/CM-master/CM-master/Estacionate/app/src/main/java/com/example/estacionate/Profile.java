package com.example.estacionate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button metodo_pagamento = (Button) findViewById(R.id.metodo_pagamento);
        Button alt_pass = (Button) findViewById(R.id.alt_pass);
        Button alt_email = (Button) findViewById(R.id.alt_email);
        Button alt_user = (Button) findViewById(R.id.alt_user);

        metodo_pagamento.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(getBaseContext(), MetodoPagamento.class);
                startActivity(myIntent);
            }
        });

        alt_pass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(getBaseContext(), AlteraPass.class);
                startActivity(myIntent);
            }
        });

        alt_email.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(getBaseContext(), AlteraEmail.class);
                startActivity(myIntent);
            }
        });

        alt_user.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(getBaseContext(), AlteraUser.class);
                startActivity(myIntent);
            }
        });
    }
}
