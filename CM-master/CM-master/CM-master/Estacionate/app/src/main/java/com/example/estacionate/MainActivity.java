package com.example.estacionate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sign_up = (Button)findViewById(R.id.sign_up);
        Button log_in = (Button)findViewById(R.id.log_in);

        sign_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(getBaseContext(),   SignUp.class);
                startActivity(myIntent);
            }
        });

        log_in.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(getBaseContext(),   LogIn.class);
                startActivity(myIntent);
            }
        });
    }

}
