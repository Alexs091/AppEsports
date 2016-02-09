package com.dam.g_leo.appesportsv22;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AmigosFragment extends Fragment implements View.OnClickListener{
    SoundPool soundPool;
    AmigoAdapter adaptadorAmigos;
    ArrayList<Jugador> listaAmigos;
    int carga;
    //boolean haySonido;
    ObtenerWebService hiloconexion;
    protected final String TODOS = "1";
    protected final String BUSQUEDA = "2";
    ListView lvstring;
    Button botonmapa, botonsugerir;

    public AmigosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_amigos, container, false);
        listaAmigos = new ArrayList<Jugador>();
        /*String[] args = new String[] {String.valueOf(((MainActivity)getActivity()).miUsuarioID)};
        Cursor c = ((MainActivity)getActivity()).sqLiteDatabase.rawQuery("select j2.nick as 'mis amigos', j2.idonline\n" +
                "from jugador j, jugador j2, amigo a\n" +
                "where j.id = a.id_usuario\n" +
                "and j2.id = a.id_amigo\n" +
                "and j.id = ?", args);
        if(c.moveToFirst()){
            do{
                Jugador jugador = new Jugador(c.getString(0),c.getString(1));
                listaAmigos.add(jugador);
            }while(c.moveToNext());
        }*/

        lvstring = (ListView)view.findViewById(R.id.lvstring);
        botonmapa = (Button) view.findViewById(R.id.botonmapa);
        botonmapa.setOnClickListener(this);
        botonsugerir = (Button) view.findViewById(R.id.bttn_sugerir);
        botonsugerir.setOnClickListener(this);
        hiloconexion = new ObtenerWebService();
        hiloconexion.execute(TODOS, String.valueOf(((MainActivity)getActivity()).miUsuarioID));

        adaptadorAmigos = new AmigoAdapter(getActivity(),listaAmigos);
        lvstring.setAdapter(adaptadorAmigos);

        //Inicializar controles
        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        carga = soundPool.load(getActivity(), R.raw.notification1, 1);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.botonmapa:
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                Bundle humbleBundle = new Bundle();
                humbleBundle.putParcelableArrayList("amigos", listaAmigos);
                intent.putExtras(humbleBundle);
                startActivity(intent);
                break;
            case R.id.bttn_sugerir: abrirSugerirContactosFragment();
                break;
        }
    }

    private void abrirSugerirContactosFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SugerirAmigosFragment fragment = new SugerirAmigosFragment();
        fragmentTransaction.replace(R.id.layoutPrincipal, fragment);
        fragmentTransaction.commit();
    }

    public class ObtenerWebService extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            URL url = null;
            String devuelve = "";
            String cadenaConexion = "";
            if(params[0] == TODOS) {
                cadenaConexion = "http://mucedal.hol.es/appesports/misamigos.php";
                cadenaConexion += "?id=" + params[1];
            }
            else if (params[0] == BUSQUEDA) {
                cadenaConexion = "http://mucedal.hol.es/appesports/misamigos.php"; //TODO: apuntar al php correcto
                cadenaConexion += "?id=" + params[1];
                cadenaConexion += "&nick=" + params[2];
            }


            try {
                url = new URL(cadenaConexion);
                HttpURLConnection urlConn;
                urlConn = (HttpURLConnection)url.openConnection(); //Abrir la conexión
                urlConn.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                int respuesta = urlConn.getResponseCode();
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }

                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                    //Accedemos al vector de resultados
                    String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON

                    if (resultJSON.equals("1") || resultJSON == "1") {
                        JSONArray jugadoresJSON = respuestaJSON.getJSONArray("jugadores");
                        devuelve = "ok";
                        for(int i=0;i<jugadoresJSON.length();i++){
                            String nick = jugadoresJSON.getJSONObject(i).getString("nick");
//                            devuelve += nick + ";;;";
                            String idonline = jugadoresJSON.getJSONObject(i).getString("idonline");
//                            devuelve += idonline + ";;;";
                            int id_jugador = jugadoresJSON.getJSONObject(i).getInt("id_jugador");
                            double latitud = jugadoresJSON.getJSONObject(i).getDouble("latitud");
                            double longitud = jugadoresJSON.getJSONObject(i).getDouble("longitud");

                            Jugador jugador = new Jugador(id_jugador, idonline, latitud, longitud, nick);
                            listaAmigos.add(jugador);
                        }
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
        protected void onPostExecute(String s) {

            if (s == "ok") {
//                ListView lvstring = (ListView)view.findViewById(R.id.lvstring);
                adaptadorAmigos = new AmigoAdapter(getActivity(),listaAmigos);
                lvstring.setAdapter(adaptadorAmigos);
                lvstring.refreshDrawableState(); //esto probablemente no haga falta, lo puse por un error mío
                //pero no lo quito porque no me apetece probar si funciona sin esto
            }
            else{
                Toast.makeText((MainActivity) getActivity(), s, Toast.LENGTH_SHORT).show();
            }
            //super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
