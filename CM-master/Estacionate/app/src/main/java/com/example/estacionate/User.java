package com.example.estacionate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

class User {

    private String guid;
    private String Nome;
    private String Data;
    private Double Lugares;
    private Double cLatitude;
    private Double cLongitude;

    public User(){

    }

    public String getGuid() {
        return guid;
    }
    public void setGuid() { this.guid = Clipboard.getLoggedUserGuid(); }
    public void setGuid(String guid) { this.guid = guid; }

    public String getNome() { return Nome; }
    public void setNome(String nome) { Nome = nome; }

    public String getData() { return Data; }
    public void setData(String data) { Data = data; }

    public Double getLugares() { return Lugares; }
    public void setLugares(Double lugares) { Lugares = lugares; }

    public Double getcLatitude() { return cLatitude; }
    public void setcLatitude(Double cLatitude) { this.cLatitude = cLatitude; }

    public Double getcLongitude() { return cLongitude; }
    public void setcLongitude(Double cLongitude) { this.cLongitude = cLongitude; }

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

        return "Guid: " + guid + "\nData:" + lData + " " + lTime + "\nLugares: " + Lugares;

    }



}
