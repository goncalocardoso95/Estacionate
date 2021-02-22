package com.example.estacionate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

class Historico {

    private String guid;
    private String Local;
    private String Data;
    private Double latitude;
    private Double longitude;


    public Historico(){

    }

    public String getGuid() {
        return guid;
    }
    public void setGuid() {
        if (guid == null) {
            this.guid = UUID.randomUUID().toString();
        };
    }

    public String getLocal() {
        return Local;
    }
    public void setLocal(String local) {
        Local = local;
    }

    public String getData() {
        return Data;
    }
    public void setData(String data) {
        Data = data;
    }

    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString(){
        String lData;
        String lTime;
        try {
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
            DateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy");
            DateFormat df3 = new SimpleDateFormat("HH:mm");
            lData = df2.format(df1.parse(Data));
            lTime = df3.format(df1.parse(Data));
        }
        catch (ParseException e) {
            lData = "";
            lTime="";
        }

        Double Dlat = (double) Math.round(latitude*10000);
        Double Dlng = (double) Math.round(longitude*10000);
        return "Local: " + Local + "\nPosição (Lat:" + Dlat/10000
                + " e Lng:" + Dlng/10000
                + ")\nData:" + lData + " " + lTime;
    }
}
