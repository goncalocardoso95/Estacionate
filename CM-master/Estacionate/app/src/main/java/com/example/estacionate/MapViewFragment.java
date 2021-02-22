package com.example.estacionate;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MapViewFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        com.example.estacionate.TaskLoadedCallback
{

    MapView mMapView;
    public GoogleMap googleMap;
    private double latitude = 0.0;
    private double longitude = 0.0;

    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SearchView sv;
    private int count = 0;
    private Map<String, Marker> mMarkers = new ConcurrentHashMap<String, Marker>();
    private List<Marker> marcadores = new ArrayList<>();
    private Polyline currentPolyline;
    Marker currentPositionMarker;
    Polyline polyLine;

    Circle circle = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_fragment, container, false);

        RadioGroup semaforo = (RadioGroup) rootView.findViewById(R.id.group_semaforo);
        semaforo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                Double lugares = 0D;
                switch(checkedId)
                {
                    case R.id.green:
                        lugares = 1D;
                        break;
                    case R.id.yellow:
                        lugares = 0.5D;
                        break;
                    case R.id.red:
                        lugares = 0D;
                        break;
                }
                User u = new User();
                u.setGuid();
                if ( Clipboard.getAnonimousLoggedUser() ) {
                    u.setNome("Anónimo");
                } else {
                    u.setNome(Clipboard.getLoggedUserName());
                }
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
                u.setData(sdf.format(new Date()));
                u.setLugares(lugares);
                u.setcLatitude(currentPositionMarker.getPosition().latitude);
                u.setcLongitude(currentPositionMarker.getPosition().longitude);
                if (Clipboard.getFirebaseDatabase() == null) {
                    Clipboard.setFirebaseDatabase(FirebaseDatabase.getInstance());
                    Clipboard.getFirebaseDatabase().setPersistenceEnabled(true);
                }
                DatabaseReference myRef = Clipboard.getFirebaseDatabase().getReference();
                myRef.child("Users").child(Clipboard.getLoggedUserGuid()).setValue(u);
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());


        final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                latitude =  location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();




        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if(location!= null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }

                        LatLng currentPosition = new LatLng(latitude,longitude);
                        currentPositionMarker = googleMap.addMarker(new MarkerOptions().position(currentPosition).title("Localização atual").snippet("Este é o seu ponto de partida!"));

                        // For zooming automatically to the location of the marker
                        if ( ! Clipboard.isComeFromHistoric() ) {
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentPosition).zoom(16).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }

                        marcadores.add(currentPositionMarker);

                        if ( Clipboard.isComeFromHistoric() ) {
                            Clipboard.setComeFromHistoric(false);
                            sv.onActionViewExpanded();
                            sv.setQuery(Clipboard.getLocal(),true);
                        }



                    }
                });


                sv = (SearchView) getView().findViewById(R.id.search_view);
                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        String location = sv.getQuery().toString();
                        List<Address> addressList = null;
                        boolean foundResult = false;
                        if(location != null || !location.equals("")  ){
                            Geocoder geocoder = new Geocoder(getContext());
                            try {
                                if (count >= 1 ){
                                    removeAllMarkers(marcadores);
                                    if (currentPolyline != null){
                                        currentPolyline.remove();
                                    }
                                    if (circle != null) {
                                        circle.remove();
                                    }
                                }
                                addressList= geocoder.getFromLocationName(location,1);
                                if (addressList.size() == 0){

                                    Toast.makeText(getContext(),"Sem resultados, refine a pesquisa.",Toast.LENGTH_LONG).show();
                                    foundResult = false;
                                } else {
                                    foundResult = true;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (foundResult) {
                                Address address = addressList.get(0);
                                if (addressList.size() == 1){
                                    count++;
                                }

                                double lat = 0;
                                double longi = 0;

                                if(address!= null){
                                    lat = address.getLatitude();
                                    longi = address.getLongitude();
                                }

                                LatLng latLng = new LatLng(lat,longi);
                                Marker m = googleMap. addMarker(new MarkerOptions()
                                        .draggable(true)
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                        .title(address.getAddressLine(0))
                                        .snippet("Clique para marcar o lugar"));

                                for (int z=0;z<marcadores.size();z++){
                                    if (m.getPosition().longitude != marcadores.get(z).getPosition().longitude &&
                                            m.getPosition().latitude != marcadores.get(z).getPosition().latitude)
                                        marcadores.add(m);

                                }

                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));


                                if (currentPolyline != null){
                                    currentPolyline.remove();
                                }
                                if (circle != null) {
                                    circle.remove();
                                }
                                procuraLugares(m);
                            }

                            googleMap.setOnInfoWindowClickListener( new GoogleMap.OnInfoWindowClickListener() {

                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    if (marcadores.size() > 1) {
                                        if (marker.equals(marcadores.get(1))) {  // corresponde ao marcador de destino
                                            if (!marker.getTitle().equals("Local sem lugares")){
                                                Historico h = new Historico();
                                                h.setGuid();
                                                h.setLocal(marker.getTitle());
                                                h.setLatitude(marker.getPosition().latitude);
                                                h.setLongitude(marker.getPosition().longitude);
                                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
                                                h.setData(sdf.format(new Date()));
                                                if (Clipboard.getFirebaseDatabase() == null) {
                                                    Clipboard.setFirebaseDatabase(FirebaseDatabase.getInstance());
                                                    Clipboard.getFirebaseDatabase().setPersistenceEnabled(true);
                                                }
                                                DatabaseReference myRef = Clipboard.getFirebaseDatabase().getReference();
                                                myRef.child("Historico").child(Clipboard.getLoggedUserGuid()).child(h.getGuid()).setValue(h);
                                            }

                                        }
                                    }
                                }
                            });

                            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                @Override
                                public void onMarkerDragStart(Marker marker) {

                                }

                                @Override
                                public void onMarkerDrag(Marker marker) {

                                }

                                @Override
                                public void onMarkerDragEnd(Marker marker) {

                                    String tituloLocal = null;
                                    Geocoder geocoder = new Geocoder(getContext());
                                    String zipcode = null;
                                    try {
                                        List<Address> addList = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                                        if (addList.size() == 1) {
                                            tituloLocal = addList.get(0).getAddressLine(0);
                                            zipcode = addList.get(0).getPostalCode();
                                        }
                                    } catch (Exception e ) {

                                    }
                                    if (tituloLocal != null) {
                                        if (zipcode == null) {
                                            marker.setTitle("Local sem lugares");
                                            marker.hideInfoWindow();
                                            if (currentPolyline != null) {
                                                currentPolyline.remove();
                                            }
                                            if (circle != null) {
                                                circle.remove();
                                            }
                                        } else {
                                            marker.setTitle(tituloLocal);
                                            marker.showInfoWindow();
                                            procuraLugares(marker);
                                        }
                                    }


                                }
                            });

                        }
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }

                });

            }

        });

        return rootView;
    }


    private void showPeople() {
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

        u.setGuid("person1");
        u.setNome("Afonso Menezes");
        lDate.setTime(System.currentTimeMillis() - (10*60*1000)); // data atual - 10min
        u.setData(sdf.format(lDate));
        u.setLugares(0.5D);    // poucos lugares
        u.setcLatitude(38.756828);
        u.setcLongitude(-9.154676);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person2");
        u.setNome("Barbara Conceicao");
        lDate.setTime(System.currentTimeMillis() - (20*60*1000)); // data atual - 20min
        u.setData(sdf.format(lDate));
        u.setLugares(0.5D);  // poucos lugares
        u.setcLatitude(38.756666);
        u.setcLongitude(-9.154565);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person3");
        u.setNome("Carlota Joaquina");
        lDate.setTime(System.currentTimeMillis() - (30*60*1000)); // data atual - 30min
        u.setData(sdf.format(lDate));
        u.setLugares(0.5D);  // Poucos lugares
        u.setcLatitude(38.756472);
        u.setcLongitude(-9.154422);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person4");
        u.setNome("Daniel Semedo");
        lDate.setTime(System.currentTimeMillis() - (10*60*1000)); // data atual - 10min
        u.setData(sdf.format(lDate));
        u.setLugares(0D);  // Sem lugares
        u.setcLatitude(38.756207);
        u.setcLongitude(-9.155286);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person5");
        u.setNome("Elvira Antunes");
        lDate.setTime(System.currentTimeMillis() - (40*60*1000)); // data atual - 40min
        u.setData(sdf.format(lDate));
        u.setLugares(1D);  // Muitos lugares
        u.setcLatitude(38.756015);
        u.setcLongitude(-9.155131);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person6");
        u.setNome("Filipe Dâmaso");
        lDate.setTime(System.currentTimeMillis() - (10*60*1000)); // data atual - 40min
        u.setData(sdf.format(lDate));
        u.setLugares(1D);  // Muitos lugares
        u.setcLatitude(38.755859);
        u.setcLongitude(-9.155150);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person7");
        u.setNome("Gonçalo Martins");
        lDate.setTime(System.currentTimeMillis() - (20*60*1000)); // data atual - 40min
        u.setData(sdf.format(lDate));
        u.setLugares(1D);  // Muitos lugares
        u.setcLatitude(38.755882);
        u.setcLongitude(-9.155056);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person8");
        u.setNome("Hélio Santos");
        lDate.setTime(System.currentTimeMillis() - (40*60*1000)); // data atual - 40min
        u.setData(sdf.format(lDate));
        u.setLugares(0.5D);  // Poucos lugares
        u.setcLatitude(38.755934);
        u.setcLongitude(-9.154876);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person8");
        u.setNome("Irene Silva");
        lDate.setTime(System.currentTimeMillis() - (40*60*1000)); // data atual - 40min
        u.setData(sdf.format(lDate));
        u.setLugares(0.5D);  // Poucos lugares
        u.setcLatitude(38.755761);
        u.setcLongitude(-9.155458);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person9");
        u.setNome("Januário Ocante");
        lDate.setTime(System.currentTimeMillis() - (40*60*1000)); // data atual - 40min
        u.setData(sdf.format(lDate));
        u.setLugares(0.5D);  // Poucos lugares
        u.setcLatitude(38.755663);
        u.setcLongitude(-9.155737);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person10");
        u.setNome("Kevin Durant");
        lDate.setTime(System.currentTimeMillis() - (40*60*1000)); // data atual - 40min
        u.setData(sdf.format(lDate));
        u.setLugares(0.5D);  // Poucos lugares
        u.setcLatitude(38.755889);
        u.setcLongitude(-9.155032);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person11");
        u.setNome("Leonardo Miguel");
        lDate.setTime(System.currentTimeMillis() - (50*60*1000)); // data atual - 40min
        u.setData(sdf.format(lDate));
        u.setLugares(0D);  // Sem lugares
        u.setcLatitude(38.755821);
        u.setcLongitude(-9.155239);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person12");
        u.setNome("Manuel Marques");
        lDate.setTime(System.currentTimeMillis() - (10*60*1000)); // data atual - 10min
        u.setData(sdf.format(lDate));
        u.setLugares(1D);  // Muitos lugares
        u.setcLatitude(38.755716);
        u.setcLongitude(-9.155574);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person13");
        u.setNome("Nuno Carvalho");
        lDate.setTime(System.currentTimeMillis() - (50*60*1000)); // data atual - 50min
        u.setData(sdf.format(lDate));
        u.setLugares(0D);  // Sem lugares
        u.setcLatitude(38.757246);
        u.setcLongitude(-9.154987);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person14");
        u.setNome("Otilia Marques");
        lDate.setTime(System.currentTimeMillis() - (50*60*1000)); // data atual - 50min
        u.setData(sdf.format(lDate));
        u.setLugares(0D);  // Sem lugares
        u.setcLatitude(38.757156);
        u.setcLongitude(-9.154915);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person15");
        u.setNome("Pedro Lourenço");
        lDate.setTime(System.currentTimeMillis() - (20*60*1000)); // data atual - 20min
        u.setData(sdf.format(lDate));
        u.setLugares(0.5D);  // Poucos lugares
        u.setcLatitude(38.757094);
        u.setcLongitude(-9.154872);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person16");
        u.setNome("Quartis Ambrósio");
        lDate.setTime(System.currentTimeMillis() - (10*60*1000)); // data atual - 10min
        u.setData(sdf.format(lDate));
        u.setLugares(0.5D);  // Poucos lugares
        u.setcLatitude(38.756989);
        u.setcLongitude(-9.154802);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person17");
        u.setNome("Rita Marques");
        lDate.setTime(System.currentTimeMillis() - (10*60*1000)); // data atual - 10min
        u.setData(sdf.format(lDate));
        u.setLugares(0.5D);  // Poucos lugares
        u.setcLatitude(38.756039);
        u.setcLongitude(-9.154102);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person18");
        u.setNome("Saul Gonzalez");
        lDate.setTime(System.currentTimeMillis() - (30*60*1000)); // data atual - 30min
        u.setData(sdf.format(lDate));
        u.setLugares(0.5D);  // Poucos lugares
        u.setcLatitude(38.755868);
        u.setcLongitude(-9.153981);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person19");
        u.setNome("Tânia Gaivoto");
        lDate.setTime(System.currentTimeMillis() - (50*60*1000)); // data atual - 50min
        u.setData(sdf.format(lDate));
        u.setLugares(0D);  // Sem lugares
        u.setcLatitude(38.755596);
        u.setcLongitude(-9.153794);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person20");
        u.setNome("Urbano Silva");
        lDate.setTime(System.currentTimeMillis() - (20*60*1000)); // data atual - 20min
        u.setData(sdf.format(lDate));
        u.setLugares(0.5D);  // Poucos lugares
        u.setcLatitude(38.756029);
        u.setcLongitude(-9.156267);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person21");
        u.setNome("Vasco Martinho");
        lDate.setTime(System.currentTimeMillis() - (20*60*1000)); // data atual - 20min
        u.setData(sdf.format(lDate));
        u.setLugares(1D);  // Muitos lugares
        u.setcLatitude(38.756784);
        u.setcLongitude(-9.156698);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person22");
        u.setNome("Xana Almeida");
        lDate.setTime(System.currentTimeMillis() - (10*60*1000)); // data atual - 20min
        u.setData(sdf.format(lDate));
        u.setLugares(1D);  // Muitos lugares
        u.setcLatitude(38.756393);
        u.setcLongitude(-9.156444);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person23");
        u.setNome("Wilson Torres");
        lDate.setTime(System.currentTimeMillis() - (30*60*1000)); // data atual - 20min
        u.setData(sdf.format(lDate));
        u.setLugares(1D);  // Muitos lugares
        u.setcLatitude(38.756918);
        u.setcLongitude(-9.156157);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person24");
        u.setNome("Yuri José");
        lDate.setTime(System.currentTimeMillis() - (40*60*1000)); // data atual - 20min
        u.setData(sdf.format(lDate));
        u.setLugares(0D);  // Sem lugares
        u.setcLatitude(38.757338);
        u.setcLongitude(-9.156433);
        myRef.child("Users").child(u.getGuid()).setValue(u);

        u.setGuid("person25");
        u.setNome("Zé António");
        lDate.setTime(System.currentTimeMillis() - (20*60*1000)); // data atual - 20min
        u.setData(sdf.format(lDate));
        u.setLugares(0D);  // Sem lugares
        u.setcLatitude(38.757504);
        u.setcLongitude(-9.156071);
        myRef.child("Users").child(u.getGuid()).setValue(u);
    }


    public void procuraLugares(Marker marker) {
        showPeople();

        Clipboard.setCircleCenter(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

        if (Clipboard.getFirebaseDatabase() == null) {
            Clipboard.setFirebaseDatabase(FirebaseDatabase.getInstance());
            Clipboard.getFirebaseDatabase().setPersistenceEnabled(true);
        }
        DatabaseReference DBRef = Clipboard.getFirebaseDatabase().getReference();

        DBRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long recentSearch = 180l;
                Date now = new Date();
                Clipboard.setCircleColor(0x20000000 + Color.GREEN);
                for (DataSnapshot objDS : dataSnapshot.getChildren()) {
                    User u = objDS.getValue(User.class);
                    if (u.getGuid() != null && ! u.getGuid().equals(Clipboard.getLoggedUserGuid())) {
                        if (mMarkers.containsKey(u.getGuid())) {
                            Marker m = mMarkers.get(u.getGuid());
                            float cor = 0f;
                            float transparency = 0f;
                            if (u.getLugares() == 1d) {
                                cor = BitmapDescriptorFactory.HUE_GREEN;
                            } else if (u.getLugares() == 0.5d) {
                                cor = BitmapDescriptorFactory.HUE_YELLOW;
                            } else if (u.getLugares() == 0d) {
                                cor = BitmapDescriptorFactory.HUE_RED;
                            }
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
                            try {
                                Date past = sdf.parse(u.getData());
                                long mn = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                                if (mn < recentSearch) {
                                    float[] distance =new float[2];
                                    Location.distanceBetween(Clipboard.getCircleCenter().latitude,
                                            Clipboard.getCircleCenter().longitude,
                                            u.getcLatitude(), u.getcLongitude(),
                                            distance);
                                    if (distance[0]<Clipboard.getRaioPesquisa()) {
                                        recentSearch = mn;
                                        if (cor == BitmapDescriptorFactory.HUE_RED) {
                                            Clipboard.setCircleColor(0x20000000 + Color.RED);
                                        } else if (cor == BitmapDescriptorFactory.HUE_YELLOW) {
                                            Clipboard.setCircleColor(0x20000000 + Color.YELLOW);
                                        } else {
                                            Clipboard.setCircleColor(0x20000000 + Color.GREEN);
                                        }
                                    }
                                }
                                if (mn < 20) { // 20 min
                                    transparency = 0.9f;
                                } else if (mn < 40) { //40 min
                                    transparency = 0.5f;
                                } else if (mn < 60) { // 60 min
                                    transparency = 0.15f;
                                } else  { // > 1 hora
                                    transparency = 0.0f;
                                }
                            } catch (ParseException e) {
                                transparency = 0f;
                            }

                            m.setPosition(new LatLng(u.getcLatitude(), u.getcLongitude()));
                            m.setIcon(BitmapDescriptorFactory.defaultMarker(cor));
                            m.setAlpha(transparency);

                        } else {
                            float cor = 0f;
                            float transparency = 0f;
                            MarkerOptions m = new MarkerOptions();
                            m.position(new LatLng(u.getcLatitude(), u.getcLongitude()));
                            if (u.getLugares() > 0.5d && u.getLugares() <= 1d) {
                                cor = BitmapDescriptorFactory.HUE_GREEN;
                            } else if (u.getLugares() <= 0.5d && u.getLugares() > 0d) {
                                cor = BitmapDescriptorFactory.HUE_YELLOW;
                            } else if (u.getLugares() == 0d) {
                                cor = BitmapDescriptorFactory.HUE_RED;
                            }
                            m.icon(BitmapDescriptorFactory.defaultMarker(cor));
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
                            try {
                                Date past = sdf.parse(u.getData());
                                long mn = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                                if (mn < recentSearch) {
                                    float[] distance =new float[2];
                                    Location.distanceBetween(Clipboard.getCircleCenter().latitude,
                                            Clipboard.getCircleCenter().longitude,
                                            u.getcLatitude(), u.getcLongitude(),
                                            distance);
                                    if (distance[0]<Clipboard.getRaioPesquisa()) {
                                        recentSearch = mn;
                                        if (cor == BitmapDescriptorFactory.HUE_RED) {
                                            Clipboard.setCircleColor(0x20000000 + Color.RED);
                                        } else if (cor == BitmapDescriptorFactory.HUE_YELLOW) {
                                            Clipboard.setCircleColor(0x20000000 + Color.YELLOW);
                                        } else {
                                            Clipboard.setCircleColor(0x20000000 + Color.GREEN);
                                        }
                                    }
                                }
                                if (mn < 20) { // 20 min
                                    transparency = 0.9f;
                                } else if (mn < 40) { //40 min
                                    transparency = 0.5f;
                                } else if (mn < 60) { // 60 min
                                    transparency = 0.15f;
                                } else  { // > 1 hora
                                    transparency = 0.0f;
                                }
                            } catch (ParseException e) {
                                transparency = 0f;
                            }
                            m.alpha(transparency);
                            m.title(u.getNome());
                            mMarkers.put(u.getGuid(), googleMap.addMarker(m));
                        }

                    }
                }

                new FetchURL(getContext()).execute(getUrl(currentPositionMarker.getPosition(), Clipboard.getCircleCenter(),"driving"), "driving");
                if (circle != null) {
                    circle.remove();
                }
                CircleOptions myCircle = new CircleOptions();
                myCircle.center(Clipboard.getCircleCenter());
                myCircle.radius(Clipboard.getRaioPesquisa());
                myCircle.strokeColor(Color.BLACK);
                myCircle.strokeWidth(2);
                myCircle.fillColor(Clipboard.getCircleColor());
                circle = googleMap.addCircle(myCircle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(getContext(),"Procurando lugares ...",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private void removeAllMarkers(@NonNull List<Marker>listMarkers){

       for (int i = 1;i< marcadores.size();i++){
            Marker m = listMarkers.get(i);
            m.remove();
            listMarkers.remove(m);
       }

    }

   private String getUrl(LatLng origin, LatLng dest, String directionMode) {

       double var1 =0;
       double var3 =0;
       double var2 =0;
       double var4 =0;

       if(origin!= null){
           var1 = origin.latitude;
           var3 = origin.longitude;
       }

       if(dest!= null){
           var2 = dest.latitude;
           var4 = dest.longitude;
       }

        // Origin of route
        String str_origin = "origin=" + var1 + "," + var3;
        // Destination of route
        String str_dest = "destination=" + var2 + "," + var4;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();

        currentPolyline = googleMap.addPolyline((PolylineOptions) values[0]);
    }

}
