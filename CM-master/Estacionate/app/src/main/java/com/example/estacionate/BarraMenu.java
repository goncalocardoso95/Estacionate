package com.example.estacionate;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class BarraMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        com.example.estacionate.TaskLoadedCallback,
        GoogleApiClient.OnConnectionFailedListener{

    private Polyline currentPolyline;
    MapViewFragment mapa;

    private String mUsername;
    public static final String ANONYMOUS = "anonymous";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;

    private void createNeighbors() {
        //Metodo apenas existe para poder demonstrar as potencialidades da app
        //Posteriormente não será implementado
        if (Clipboard.getFirebaseDatabase() == null) {
            Clipboard.setFirebaseDatabase(FirebaseDatabase.getInstance());
            Clipboard.getFirebaseDatabase().setPersistenceEnabled(true);
        }
        DatabaseReference myRef = Clipboard.getFirebaseDatabase().getReference();
        User u = new User();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        Date lDate = new Date();

        u.setGuid("Z1YSskStjHTpRtrnYMs198mIT0o2");
        u.setNome("Anónimo");
        lDate.setTime(System.currentTimeMillis() - (10*60*1000)); // data atual - 10 min
        u.setData(sdf.format(lDate));
        u.setLugares(0D);    // nenhum lugares
        u.setcLatitude(38.734535);
        u.setcLongitude(-9.144789);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("Z2YSskStjHTpRtrnYMs198mIT0o2");
        u.setNome("João Parado");
        lDate.setTime(System.currentTimeMillis() - (20*60*1000)); // data atual - 20 min
        u.setData(sdf.format(lDate));
        u.setLugares(1D);  // muitos lugares
        u.setcLatitude(38.733941);
        u.setcLongitude(-9.146367);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("Z3YSskStjHTpRtrnYMs198mIT0o2");
        u.setNome("Maria Sortuda");
        lDate.setTime(System.currentTimeMillis() - (30*60*1000)); // data atual - 30 min
        u.setData(sdf.format(lDate));
        u.setLugares(0.5D);  // Poucos lugares
        u.setcLatitude(38.733096);
        u.setcLongitude(-9.148974);
        myRef.child("Users").child(u.getGuid()).setValue(u);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barra_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        MenuItem item = (MenuItem) navigationView.getMenu().findItem(R.id.profile);
        Switch switchAnonymous = item.getActionView().findViewById(R.id.profile);
        switchAnonymous.setChecked(false);

        switchAnonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (Clipboard.getLoggedUserGuid().equals("MoYSskStjHTpRtrnYMs198mIT0o2")) {
                    createNeighbors();
                }
                if (isChecked) {
                    DatabaseReference myRef = Clipboard.getFirebaseDatabase().getReference();
                    Clipboard.setAnonimousLoggedUser(Boolean.TRUE);
                    myRef.child("Users").child(Clipboard.getLoggedUserGuid()).child("nome").setValue("Anónimo");
                } else {
                    DatabaseReference myRef = Clipboard.getFirebaseDatabase().getReference();
                    Clipboard.setAnonimousLoggedUser(Boolean.FALSE);
                    myRef.child("Users").child(Clipboard.getLoggedUserGuid()).child("nome").setValue(Clipboard.getLoggedUserName());
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        mUsername = ANONYMOUS;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            startActivity(new Intent(this, LogIn.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            Clipboard.setLoggedUserGuid(mFirebaseUser.getUid());
            Clipboard.setLoggedUserName(mFirebaseUser.getDisplayName());
            Clipboard.setRaioPesquisa(100d);
            Clipboard.setAnonimousLoggedUser(Boolean.FALSE);
        }
        


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener*/ )
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mGoogleApiClient.connect();


        View mHeaderView = navigationView.getHeaderView(0);
        TextView companyNameTxt = (TextView) mHeaderView.findViewById(R.id.userTitle);
        companyNameTxt.setText(mUsername);
        TextView email = (TextView) mHeaderView.findViewById(R.id.emailTitle);
        email.setText(mFirebaseUser.getEmail());


        mapa = new MapViewFragment();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.barraLayout,mapa);
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.historic) {
            Intent myIntent = new Intent(getBaseContext(),   HistoricoItemTabbed.class);
            startActivity(myIntent);
        } else if (id == R.id.profile) {
            return  true;
        } else if (id == R.id.logOut) {
            mFirebaseAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mUsername = ANONYMOUS;
            Intent myIntent = new Intent(getBaseContext(),   LogIn.class);
            startActivity(myIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        mapa.onTaskDone(values);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
