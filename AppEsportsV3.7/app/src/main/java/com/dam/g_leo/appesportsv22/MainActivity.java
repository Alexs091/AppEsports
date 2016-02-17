package com.dam.g_leo.appesportsv22;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener, NavigationView.OnNavigationItemSelectedListener,
        PerfilFragment.OnFragmentInteractionListener,
        MainFragment.OnFragmentInteractionListener {

    SharedPreferences preferences;
    boolean haySonido, hayVibracion, hayNotificacion;
    String tema, idioma;
    SoundPool soundPool;
    int carga;
    SensorManager senSensorManager;
    Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    SQLiteDatabase sqLiteDatabase;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
    public int miUsuarioID;
    String miUsuarioNick, otroJugadorNick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Abrimos la base de datos "AppEsports" en modo escritura
        BDAppesports bdAlumnos = new BDAppesports(this, "AppEsports", null, 1);
        sqLiteDatabase = bdAlumnos.getWritableDatabase();
        /////////////////////////////////////////

        //Recogemos datos de usuario del login
        Bundle weeklyHumbleBundle = this.getIntent().getExtras();
        miUsuarioID =  weeklyHumbleBundle.getInt("id");
        miUsuarioNick = weeklyHumbleBundle.getString("user");
        Toast.makeText(this, miUsuarioID + " - " + miUsuarioNick, Toast.LENGTH_SHORT).show();

        //Navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ////////////////////////////////////////

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        //Inicializar controles
        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        carga = soundPool.load(this, R.raw.notification1, 1);
        abrirFragmentMain();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Recoger preferencias de usuario
        recogerPreferencias();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, Preferencias.class));
            return true;
        } else if (id == R.id.action_logout) {
            //Borro datos de login -> Guardo fichero en blanco
            escribirMemoriaInterna(Login.ficheroLogin, "");
            //Cierro la activity
            finish();
            //Muestro Login
            Intent intent = new Intent(this, Login.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_principal) {
            abrirFragmentMain();
        } else if (id == R.id.nav_perfil) {
            abrirFragmentPerfil();
        } else if (id == R.id.nav_torneos) {
            abrirFragmentTorneos();
        } else if (id == R.id.nav_amigos) {
            abrirFragmentAmigos();
        } else if (id == R.id.nav_busqueda) {
                abrirFragmentBuscar();
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void abrirFragmentPerfil() {
        otroJugadorNick = miUsuarioNick;
        //Paso 1: Obtener la instancia del administrador de fragmentos
        FragmentManager fragmentManager = getFragmentManager();

        //Paso 2: Crear una nueva transacción
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //Paso 3: Crear un nuevo fragmento y añadirlo
        PerfilFragment fragment = new PerfilFragment();
        fragmentTransaction.replace(R.id.layoutPrincipal, fragment);

        //Paso 4: Confirmar el cambio
        fragmentTransaction.commit();
    }

    public void abrirFragmentMain() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainFragment fragment = new MainFragment();
        fragmentTransaction.replace(R.id.layoutPrincipal, fragment, "MainFragment");
        fragmentTransaction.commit();
    }

    public void abrirFragmentTorneos() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Torneosfragment fragment = new Torneosfragment();
        fragmentTransaction.replace(R.id.layoutPrincipal, fragment, "Torneosfragment");
        fragmentTransaction.commit();
    }

    public void abrirFragmentAmigos() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AmigosFragment fragment = new AmigosFragment();
        fragmentTransaction.replace(R.id.layoutPrincipal, fragment);
        fragmentTransaction.commit();
    }

    public void abrirFragmentBuscar() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BuscarFragment fragment = new BuscarFragment();
        fragmentTransaction.replace(R.id.layoutPrincipal, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void recogerPreferencias() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean("sonido", true)) {
            haySonido = true;
        } else {
            haySonido = false;
        }
        if (preferences.getBoolean("vibracion", true)) {
            hayVibracion = true;
        } else {
            hayVibracion = false;
        }
        if (preferences.getBoolean("notificacion", true)) {
            hayNotificacion = true;
        } else {
            hayNotificacion = false;
        }
        idioma = preferences.getString("idioma", "No se ha seleccionado un idioma");
        tema = preferences.getString("tema", "No se ha seleccionado un tema");
    }

    public void onClick(View view) {
//        if(haySonido) {
//            soundPool.play(carga, 1, 1, 0, 0, 1);
//        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if(haySonido) {
            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                long curTime = System.currentTimeMillis();

                if ((curTime - lastUpdate) > 100) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;

                    float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                    if (speed > SHAKE_THRESHOLD) {
                        soundPool.play(carga, 1, 1, 0, 0, 1);
                    }

                    last_x = x;
                    last_y = y;
                    last_z = z;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
}

//TODO: hacer algo con las preferencias que cojo
