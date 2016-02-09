package com.dam.g_leo.appesportsv22;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adrián on 19/11/2015.
 */
public class Jugador implements Parcelable{

    protected Jugador(Parcel in) {
        ID_Jugador = in.readInt();
        Nick = in.readString();
        Password = in.readString();
        IDOnline = in.readString();
        Correo = in.readString();
        Latitud = in.readDouble();
        Longitud = in.readDouble();
    }

    public static final Creator<Jugador> CREATOR = new Creator<Jugador>() {
        @Override
        public Jugador createFromParcel(Parcel in) {
            return new Jugador(in);
        }

        @Override
        public Jugador[] newArray(int size) {
            return new Jugador[size];
        }
    };

    public int getID_Jugador() {
        return ID_Jugador;
    }

    public void setID_Jugador(int ID_Jugador) {
        this.ID_Jugador = ID_Jugador;
    }

    public String getNick() {
        return Nick;
    }

    public void setNick(String nick) {
        Nick = nick;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getIDOnline() {
        return IDOnline;
    }

    public void setIDOnline(String IDOnline) {
        this.IDOnline = IDOnline;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        this.Telefono = telefono;
    }

    private int ID_Jugador;
    private String Nick;
    private String Password;
    private String IDOnline;
    private String Correo;
    private double Latitud;
    private double Longitud;
    private String Telefono;

    public Jugador(int ID_Jugador, String IDOnline, double latitud, double longitud, String nick) {
        this.ID_Jugador = ID_Jugador;
        this.IDOnline = IDOnline;
        this.Latitud = latitud;
        this.Longitud = longitud;
        Nick = nick;
    }

    //TODO: creo que habría que usar este en vez del otro
    public Jugador(int id_jugador, String nick, String IDOnline, double latitud, double longitud) {
        this.ID_Jugador = id_jugador;
        Nick = nick;
        Password = "";
        this.IDOnline = IDOnline;
        Correo = "";
        this.Latitud = latitud;
        this.Longitud = longitud;
    }

    //Constructor chustero de antes de tener los campos teléfono y posición
    public Jugador(String nick, String IDOnline) {
        this.ID_Jugador = 0;
        Nick = nick;
        Password = "";
        this.IDOnline = IDOnline;
        Correo = "";
    }

    //Constructor para los content providers
    public Jugador(int id_jugador, String nick, String IDOnline, String telefono) {
        this(id_jugador, nick, IDOnline, 0, 0);
        this.Telefono = telefono;
    }

    public double getLatitud() {
        return Latitud;
    }

    public void setLatitud(double latitud) {
        this.Latitud = latitud;
    }

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        this.Longitud = longitud;
    }

    public Jugador() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID_Jugador);
        dest.writeString(Nick);
        dest.writeString(Password);
        dest.writeString(IDOnline);
        dest.writeString(Correo);
        dest.writeDouble(Latitud);
        dest.writeDouble(Longitud);
    }
}
