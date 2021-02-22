package com.example.estacionate;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.FirebaseDatabase;

public final class Clipboard {

    private static boolean comeFromHistoric;
    private static String loggedUserGuid;
    private static String loggedUserName;
    private static Boolean anonimousLoggedUser;
    private static Double latitude;
    private static Double longitude;
    private static String Local;
    private static Double raioPesquisa;
    private static LatLng circleCenter;
    private static int circleColor;
    private static FirebaseDatabase firebaseDB;
    public static Context c;

    public Clipboard(){

    }

    public static String getLoggedUserGuid() {
        return loggedUserGuid;
    }
    public static void setLoggedUserGuid(String guidValue) { loggedUserGuid = guidValue; }

    public static String getLoggedUserName() { return loggedUserName; }
    public static void setLoggedUserName(String loggedUserName) { Clipboard.loggedUserName = loggedUserName; }

    public static Boolean getAnonimousLoggedUser() { return anonimousLoggedUser; }
    public static void setAnonimousLoggedUser(Boolean anonimousLoggedUser) { Clipboard.anonimousLoggedUser = anonimousLoggedUser; }

    public static Double getLatitude() { return latitude; }
    public static void setLatitude(Double lat) { latitude = lat; }

    public static Double getLongitude() { return longitude; }
    public static void setLongitude(Double lon) { longitude = lon; }

    public static String getLocal() {
        return Local;
    }
    public static void setLocal(String l) { Local = l; }

    public static Double getRaioPesquisa() { return raioPesquisa; }
    public static void setRaioPesquisa(Double raioPesquisa) { Clipboard.raioPesquisa = raioPesquisa; }

    public static LatLng getCircleCenter() { return circleCenter; }
    public static void setCircleCenter(LatLng circleCenter) { Clipboard.circleCenter = circleCenter; }

    public static int getCircleColor() { return circleColor; }
    public static void setCircleColor(int circleColor) { Clipboard.circleColor = circleColor; }

    public static FirebaseDatabase getFirebaseDatabase() { return firebaseDB; }
    public static void setFirebaseDatabase(FirebaseDatabase db) { firebaseDB = db; }

    public static boolean isComeFromHistoric() { return comeFromHistoric; }
    public static void setComeFromHistoric(boolean comeFromHistoric) { Clipboard.comeFromHistoric = comeFromHistoric; }
}
