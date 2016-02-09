package com.dam.g_leo.appesportsv22;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by g_leo on 19/11/2015.
 */
public class BDAppesports extends SQLiteOpenHelper {

    String sqlCreateTorneos = "CREATE TABLE TORNEO(\n" +
            "   id integer PRIMARY KEY AUTOINCREMENT not null,\n" +
            "   nombre varchar(60) NOT NULL UNIQUE,\n" +
            "   fecha_fin_registro varchar(60),\n" +
            "   fecha_comienzo varchar(60),\n" +
            "   numero_participantes integer\n" +
            ")";
    String sqlCreateUsuarios = "CREATE TABLE JUGADOR(\n" +
            "   id integer PRIMARY KEY AUTOINCREMENT not null,\n" +
            "   nick varchar(60) NOT NULL UNIQUE,\n" +
            "   password varchar(60) not null,\n" +
            "   correo varchar(100) not null unique,\n" +
            "   idonline varchar(60) not null unique\n" +
            ")";
    String sqlCreateAmigos = "CREATE TABLE AMIGO(\n" +
            "   id_usuario integer,\n" +
            "   id_amigo integer,\n" +
            "    CONSTRAINT pk_amigos PRIMARY KEY (id_usuario, id_amigo),\n" +
            "    CONSTRAINT fk_id_usuario_usuarios FOREIGN KEY (id_usuario) REFERENCES JUGADOR,\n" +
            "    CONSTRAINT fk_id_amigo_torneos FOREIGN KEY (id_amigo) REFERENCES JUGADOR\n" +
            ")";
    String sqlCreateInscripciones = "CREATE TABLE INSCRIPCION(\n" +
            "   id_usuario integer,\n" +
            "   id_torneo integer,\n" +
            "    CONSTRAINT pk_inscripciones PRIMARY KEY (id_usuario, id_torneo),\n" +
            "    CONSTRAINT fk_id_usuario_usuarios FOREIGN KEY (id_usuario) REFERENCES JUGADOR,\n" +
            "    CONSTRAINT fk_id_torneo_torneos FOREIGN KEY (id_torneo) REFERENCES TORNEO\n" +
            ")";

    String insertPrueba = "insert into jugador (nick, password, correo, idonline) values ('user', 'user', 'user@user', 'user69')";

    String insertPrueba2 = "insert into torneo(nombre, fecha_fin_registro, fecha_comienzo, numero_participantes) values ('torneo1', '2016/01/31', '2016/02/28', 8)";
    String insertPrueba3 = "insert into torneo(nombre, fecha_fin_registro, fecha_comienzo, numero_participantes) values ('torneo2', '2017/01/31', '2017/02/28', 16)";
    String insertPrueba4 = "insert into torneo(nombre, fecha_fin_registro, fecha_comienzo, numero_participantes) values ('torneo3', '2018/01/31', '2018/02/28', 32)";

    String insertPrueba5 = "insert into jugador (nick, password, correo, idonline) values ('user2', 'user2', 'user2@user', 'aaaaa69')";
    String insertPrueba6 = "insert into jugador (nick, password, correo, idonline) values ('user3', 'user3', 'user3@user', 'bbbbb69')";
    String insertPrueba7 = "insert into jugador (nick, password, correo, idonline) values ('user4', 'user4', 'user4@user', 'ccccc69')";

    String insertPrueba8 = "INSERT INTO inscripcion values (1,1)";
    String insertPrueba9 = "INSERT INTO inscripcion values (1,2)";
    String insertPrueba10 = "INSERT INTO inscripcion values (2,3)";

    String insertPrueba11 = "INSERT INTO amigo values (1,2)";
    String insertPrueba12 = "INSERT INTO amigo values (1,3)";
    String insertPrueba13 = "INSERT INTO amigo values (2,3)";

    public BDAppesports(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(sqlCreateUsuarios);
        sqLiteDatabase.execSQL(sqlCreateTorneos);
        sqLiteDatabase.execSQL(sqlCreateAmigos);
        sqLiteDatabase.execSQL(sqlCreateInscripciones);
        sqLiteDatabase.execSQL(insertPrueba);
        sqLiteDatabase.execSQL(insertPrueba2);
        sqLiteDatabase.execSQL(insertPrueba3);
        sqLiteDatabase.execSQL(insertPrueba4);
        sqLiteDatabase.execSQL(insertPrueba5);
        sqLiteDatabase.execSQL(insertPrueba6);
        sqLiteDatabase.execSQL(insertPrueba7);
        sqLiteDatabase.execSQL(insertPrueba8);
        sqLiteDatabase.execSQL(insertPrueba9);
        sqLiteDatabase.execSQL(insertPrueba10);
        sqLiteDatabase.execSQL(insertPrueba11);
        sqLiteDatabase.execSQL(insertPrueba12);
        sqLiteDatabase.execSQL(insertPrueba13);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}