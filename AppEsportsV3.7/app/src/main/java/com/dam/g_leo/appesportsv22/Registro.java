package com.dam.g_leo.appesportsv22;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Registro extends AppCompatActivity implements View.OnClickListener {
    EditText nombreUsuario;
    EditText battleTag;
    EditText email;
    EditText contraseña;
    EditText confirmarContraseña;
    EditText Telefono;
    Button botonRegistro;
    ObtenerWebService hiloconexion;
    public static ImageButton botonavatar;
    Integer[] imageIDs = {
            R.drawable.profileicon10,
            R.drawable.profileicon13,
            R.drawable.profileicon23,
            R.drawable.profileicon24old,
            R.drawable.profileicon29old
    };
    private LocationManager locationManager;
    private Location location;
    private LocationListener locationListener;
    private String localizacion;
    TextView textoLocalizacion;
    AlertDialog alert = null;

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(alert != null)
        {
            alert.dismiss ();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            } else {
                locationManager.removeUpdates(locationListener);
            }
        } else {
            locationManager.removeUpdates(locationListener);
        }


    }
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nombreUsuario = (EditText)findViewById(R.id.nombreUsuario);
        battleTag = (EditText)findViewById(R.id.battleTag);
        email = (EditText)findViewById(R.id.email);
        contraseña = (EditText)findViewById(R.id.contraseña);
        confirmarContraseña = (EditText)findViewById(R.id.confirmarContraseña);
        Telefono = (EditText)findViewById(R.id.telefono);
        botonRegistro = (Button)findViewById(R.id.botonRegistrar);
        botonRegistro.setOnClickListener(this);
        botonavatar = (ImageButton)findViewById(R.id.avatarUsuario);
        botonavatar.setImageResource(R.drawable.avataricon);
        botonavatar.setOnClickListener(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        /****Mejora****/
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertNoGps();
        }
        /********/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            } else {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                localizacion = String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude());
                //textoLocalizacion.setText(String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.botonRegistrar:
                String mensaje = "";
                boolean registrado = false;
                switch(isPasswordRight()){
                    case 0:
                        mensaje += "La contraseña es un campo requerido\n";
                        break;
                    case 1:
                        mensaje += "Las contraseñas deben ser iguales\n";
                        break;
                    case 2:
                        registrado = true;
                        break;
                }

                switch(isNickRight()){
                    case 0:
                        mensaje += "El nick es un campo requerido\n";
                        registrado = false;
                        break;
                    case 1:
                        mensaje += "El nick está en uso, elige otro nick\n";
                        registrado = false;
                        break;
                    case 2:
                        if(!registrado){
                            registrado = false;
                        }
                        break;
                }
                if(isBattleTagRight()){
                    if(!registrado) {
                        registrado = true;
                    }
                }
                else {
                    registrado = false;
                    mensaje += "El battletag es un campo requerido";
                }

                if(registrado){
                    Toast.makeText(Registro.this, "Has sido registrado correctamente", Toast.LENGTH_SHORT).show();
                    if(SelectImagen.posicionImagen == -1){
                        SelectImagen.posicionImagen = 5;
                    }
                    hiloconexion = new ObtenerWebService();
                    hiloconexion.execute(nombreUsuario.getText().toString(), contraseña.getText().toString(), email.getText().toString()
                            , battleTag.getText().toString(), Telefono.getText().toString(), String.valueOf(SelectImagen.posicionImagen),
                            String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                    SelectImagen.posicionImagen = -1;
                    this.finish();
                }
                else{
                    new AlertDialog.Builder(this)
                            .setTitle("Registro incorrecto")
                            .setMessage(mensaje)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                break;
            case R.id.avatarUsuario:
                startActivity(new Intent(Registro.this, SelectImagen.class));
                break;
        }

    }

    public int isPasswordRight(){
        if(!contraseña.getText().toString().equals("")) {
            if (contraseña.getText().toString().equals(confirmarContraseña.getText().toString())) {
                return 2;
            }
            else
                return 1;
        }
        else
            return 0;
    }

    public boolean isBattleTagRight(){
        if(battleTag.getText().toString().equals("")) {
            return false;
        }
        else
            return true;
    }

    public int isNickRight(){
        if(!nombreUsuario.getText().toString().equals("")) {
            if (!nombreUsuario.getText().toString().equals("user")) {
                //TODO recoger con una select si el usuario existe, en vez de comparar con user
                return 2;
            }
            else
                return 1;
        }
        else
            return 0;
    }

    public class ObtenerWebService extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            //String cadena = params[0];
            URL url = null;
            try {
                url = new URL("http://mucedal.hol.es/appesports/insertar_jugador.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String devuelve = "";

            try {
                HttpURLConnection urlConn;

                DataOutputStream printout;
                DataInputStream input;
                urlConn = (HttpURLConnection)url.openConnection();
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
                jsonParam.put("correo", params[2]);
                jsonParam.put("idonline", params[3]);
                jsonParam.put("telefono", params[4]);
                jsonParam.put("avatar", Integer.parseInt(params[5]));
                jsonParam.put("latitud", Double.parseDouble(params[6]));
                jsonParam.put("longitud", Double.parseDouble(params[7]));

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
                    //Accedemos al vector de resultados
                    String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON

                    if (resultJSON.equals("1") || resultJSON == "1") {
                        devuelve = "ok";
                    } else if (resultJSON.equals("2") || resultJSON == "2") {
                        devuelve = "error";
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

            if (s == "ok") {
                Toast.makeText(getApplicationContext(), "Registrado con éxito", Toast.LENGTH_LONG).show();
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "Se produjo un error", Toast.LENGTH_SHORT).show();
            }
            //super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
