package com.example.estacionate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.estacionate.ui.main.SectionsPagerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HistoricoItemTabbed extends AppCompatActivity {


    private FirebaseDatabase database;
    private DatabaseReference myRef;
    boolean deleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_item_tabbed);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        if (Clipboard.getFirebaseDatabase() == null) {
            Clipboard.setFirebaseDatabase(FirebaseDatabase.getInstance());
            Clipboard.getFirebaseDatabase().setPersistenceEnabled(true);
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.historicmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.lixo:
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("Historico");
                String user = Clipboard.getLoggedUserGuid();
                myRef.child(user).removeValue();
                Intent myIntent = new Intent(getBaseContext(), HistoricoItemTabbed.class);
                startActivity(myIntent);


        }
        return super.onOptionsItemSelected(item);
    }

}