package com.dam.g_leo.appesportsv22;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity implements View.OnClickListener {

    Button btn_regsitrar;
    Button btn_login;
    SQLiteDatabase sqLiteDatabase;
    EditText etUser, etPass;
    ObtenerWebService hiloconexion;
    String respuestaLogin = "pene";
    CheckBox checkBox;
    int idJugador = 0;
    public static final String ficheroLogin = "logindata.txt";
    String user = "";
    String pass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btn_regsitrar = (Button) findViewById(R.id.btn_registro);
        btn_login = (Button) findViewById(R.id.btn_login);
        etUser = (EditText) findViewById(R.id.et_user);
        etPass = (EditText) findViewById(R.id.et_pass);

        checkBox = (CheckBox) findViewById(R.id.checkBox);
        leerMemoriaInterna(ficheroLogin);
        btn_regsitrar.setOnClickListener(this);
        btn_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                user = etUser.getText().toString();
                pass = etPass.getText().toString();
                comprobarLogin(user, pass);
                break;
            case R.id.btn_registro:
                startActivity(new Intent(Login.this, Registro.class));
                break;
        }
    }

    public void comprobarLogin(String nick, String pass) {
        hiloconexion = new ObtenerWebService();
        hiloconexion.execute(nick, pass);
    }

    public String leerMemoriaInterna(String nombreFichero) {
        String retorno = "";
        try {
            BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput(nombreFichero)));
            retorno += fin.readLine();
            fin.close();
            /*String*/
            user = retorno.substring(0, retorno.indexOf(";"));
            Toast.makeText(this, user, Toast.LENGTH_SHORT).show();
            /*String*/
            pass = retorno.substring(retorno.indexOf(";") + 1);
            Toast.makeText(this, pass, Toast.LENGTH_SHORT).show();
            comprobarLogin(user, pass);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return retorno;
        }
    }

    public void escribirMemoriaInterna(String nombreFichero, String texto) {
        try {
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(nombreFichero, Context.MODE_PRIVATE));
            out.write(texto);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ObtenerWebService extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            //String cadena = params[0];
            URL url = null;
            try {
                url = new URL("http://mucedal.hol.es/appesports/comprobarLogin.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String devuelve = "";

            try {
                HttpURLConnection urlConn;

                DataOutputStream printout;
                DataInputStream input;
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);
                urlConn.setUseCaches(false);
                urlConn.setRequestProperty("Content-Type", "application/json");
                urlConn.setRequestProperty("Accept", "application/json");
                urlConn.connect();

                //Creo el Objeto JSON
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("nick", params[0]);
                jsonParam.put("password", params[1]);

                // Envio los parámetros post.
                OutputStream os = urlConn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParam.toString());
                writer.flush();
                writer.close();

                int respuesta = urlConn.getResponseCode();

                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                        //response+=line;
                    }

                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                    JSONObject jsonJugador = respuestaJSON.getJSONObject("jugador");
                    //Accedemos al vector de resultados

                    String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON

                    devuelve = resultJSON;
                    if (resultJSON.equals("1") || resultJSON == "1") {
                        devuelve = "Login correcto";
                        //JSONArray jsonArray = respuestaJSON.getJSONArray("jugador");
                        idJugador = jsonJugador.getInt("id_jugador");
                    } else if (resultJSON.equals("2") || resultJSON == "2") {
                        devuelve = "Login incorrecto";
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return devuelve;
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onPostExecute(String s) {
            respuestaLogin = s;

            if (respuestaLogin == "Login correcto") {
                Toast.makeText(getApplicationContext(), "Autentificación correcta", Toast.LENGTH_LONG).show();

                //Abro la activity principal
                Intent intent = new Intent(Login.this, MainActivity.class);
                Bundle humbleBundle = new Bundle();
                humbleBundle.putString("user", user);
                humbleBundle.putInt("id", idJugador);
                intent.putExtras(humbleBundle);
                startActivity(intent);

                //Escribo los credenciales de inicio de sesión
                if (checkBox.isChecked()) {
                    //Casilla marcada -> Guardo los datos introducidos
                    escribirMemoriaInterna(ficheroLogin, user + ";" + pass);
                } else {
                    //Casilla desmarcada -> Guardo fichero en blanco
                    escribirMemoriaInterna(ficheroLogin, "");
                }

                //Cierro la activity de login
                finish();
            } else {
                //TODO: quitar esta linea (de momento me sirve para saber cuándo ejecuta los sweb)
                Toast.makeText(getApplicationContext(), respuestaLogin /*+ localizacion*/, Toast.LENGTH_SHORT).show();
            }
            //super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            //resultado.setText("");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
