package com.dam.g_leo.appesportsv22;

import java.util.Date;

/**
 * Created by Adrián on 19/11/2015.
 */
public class Torneo {
    private int IDTorneo;
    private Date Fecha_Inscripcion;
    private Date Fecha_Comienzo;
    private String Nombre;
    private int Maximo_Participantes;
    private int Participantes_Actuales;

    public Torneo(int idTorneo, String nombre, Date fecha_Inscripcion, Date fecha_Comienzo, int participantes_Actuales, int maximo_Participantes) {
        Fecha_Inscripcion = fecha_Inscripcion;
        Fecha_Comienzo = fecha_Comienzo;
        Nombre = nombre;
        Participantes_Actuales = participantes_Actuales;
        Maximo_Participantes = maximo_Participantes;
        this.IDTorneo = idTorneo;
    }

    public int getParticipantes_Actuales() {
        return Participantes_Actuales;
    }

    public void setParticipantes_Actuales(int participantes_Actuales) {
        Participantes_Actuales = participantes_Actuales;
    }

    public int getIDTorneo() {
        return IDTorneo;
    }

    public void setIDTorneo(int IDTorneo) {
        this.IDTorneo = IDTorneo;
    }

    public Date getFecha_Inscripcion() {
        return Fecha_Inscripcion;
    }

    public void setFecha_Inscripcion(Date fecha_Inscripcion) {
        Fecha_Inscripcion = fecha_Inscripcion;
    }

    public Date getFecha_Comienzo() {
        return Fecha_Comienzo;
    }

    public void setFecha_Comienzo(Date fecha_Comienzo) {
        Fecha_Comienzo = fecha_Comienzo;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public int getMaximo_Participantes() {
        return Maximo_Participantes;
    }

    public void setMaximo_Participantes(int maximo_Participantes) {
        Maximo_Participantes = maximo_Participantes;
    }

}
