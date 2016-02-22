package com.dam.g_leo.appesportsv22;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Torneosfragment extends Fragment {
    SoundPool soundPool;
    TorneoAdapter adaptadorTorneos;
    List<Torneo> listaTorneos;
    int carga;
    ListView lvstring;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    ObtenerWebService hiloconexion;
    ObtenerWebService2 hiloTornear;
    //boolean haySonido;

    public Torneosfragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_torneos, container, false);
        listaTorneos = new ArrayList<Torneo>();

        //TextView user = (TextView) view.findViewById(R.id.NickUsuario);
        lvstring = (ListView)view.findViewById(R.id.lvstring);

        hiloconexion = new ObtenerWebService();
        hiloconexion.execute(String.valueOf(((MainActivity) getActivity()).miUsuarioID));

        adaptadorTorneos = new TorneoAdapter(getActivity(),listaTorneos, true);
        lvstring.setAdapter(adaptadorTorneos);

        lvstring.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(view.getContext());
                dialogo1.setTitle(listaTorneos.get(position).getNombre());
                dialogo1.setMessage("¿Quieres inscribirte en este torneo?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        //TODO tornear aquí
                        hiloTornear = new ObtenerWebService2();
                        hiloTornear.execute(String.valueOf(listaTorneos.get(position).getIDTorneo()));
                    }
                });
                dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.cancel();
                    }
                });
                dialogo1.show();
            }
        });

        //Inicializar controles
        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        carga = soundPool.load(getActivity(), R.raw.notification1, 1);

        return view;
    }

    //Para cargar lista de torneos
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
            cadenaConexion = "http://mucedal.hol.es/appesports/misnotorneos.php";
            cadenaConexion += "?id_jugador=" + params[0];


            try {
                url = new URL(cadenaConexion);
                HttpURLConnection urlConn;
                urlConn = (HttpURLConnection) url.openConnection(); //Abrir la conexión
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
                        JSONArray torneosJSON = respuestaJSON.getJSONArray("torneos");
                        devuelve = "ok";
                        for (int i = 0; i < torneosJSON.length(); i++) {

                            int idTorneo = torneosJSON.getJSONObject(i).getInt("id_torneo");
                            String nombre = torneosJSON.getJSONObject(i).getString("nombre");
                            Date fechaComienzo = formatter.parse(torneosJSON.getJSONObject(i).getString("fecha_comienzo"));
                            Date fechaFinRegistro = formatter.parse(torneosJSON.getJSONObject(i).getString("fecha_fin_registro"));
                            int participantes = torneosJSON.getJSONObject(i).getInt("inscritos");
                            int maxParticipantes = torneosJSON.getJSONObject(i).getInt("numero_participantes");

                            Torneo torneo = new Torneo(idTorneo, nombre, fechaFinRegistro, fechaComienzo, participantes, maxParticipantes);
                            listaTorneos.add(torneo);
                        }
                    } else if (resultJSON.equals("2") || resultJSON == "2") {
                        devuelve = "error";
                    }
                }

            } catch (JSONException | ParseException | IOException e) {
                e.printStackTrace();
            }
            return devuelve;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == "ok") {
                //Toast.makeText((MainActivity) getActivity(), "código avatar: " + avatar, Toast.LENGTH_SHORT).show();
                adaptadorTorneos = new TorneoAdapter(getActivity(), listaTorneos, true);
                lvstring.setAdapter(adaptadorTorneos);
                lvstring.refreshDrawableState();
            } else {
//                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            }
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

    //tornear
    public class ObtenerWebService2 extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadenaConexion;
            cadenaConexion = "http://mucedal.hol.es/appesports/tornear.php";

            URL url = null;
            try {
                url = new URL(cadenaConexion);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String devuelve = "";

            try {
                HttpURLConnection urlConn;

                urlConn = (HttpURLConnection)url.openConnection();
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);
                urlConn.setUseCaches(false);
                urlConn.setRequestProperty("Content-Type", "application/json");
                urlConn.setRequestProperty("Accept", "application/json");
                urlConn.connect();

                //Creo el Objeto JSON
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("id_jugador", ((MainActivity)getActivity()).miUsuarioID );
                jsonParam.put("id_torneo", params[0]);

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
        protected void onPostExecute(String s) {

            if (s == "ok") {
                Fragment frg = getFragmentManager().findFragmentByTag("Torneosfragment");
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
                Toast.makeText(getActivity(), "Inscrito con éxito", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getActivity(), "Error: no se pudo inscribir", Toast.LENGTH_SHORT).show();
            }
            //super.onPostExecute(aVoid);
        }
    }
}
