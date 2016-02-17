package com.dam.g_leo.appesportsv22;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    SoundPool soundPool;
    int avatar;
    TorneoAdapter adaptadorTorneos;
    List<Torneo> listaTorneos;
    ObtenerWebService1 hiloconexion1;
    ObtenerWebService2 hiloDestornear;
    int carga;
    static Integer[] mThumbIds = {
            R.drawable.profileicon10,
            R.drawable.profileicon13,
            R.drawable.profileicon23,
            R.drawable.profileicon24old,
            R.drawable.profileicon29old,
            R.drawable.avataricon
    };
    ListView lvstring;
    ImageView imagenUsuario;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    //boolean haySonido;
    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        listaTorneos = new ArrayList<Torneo>();

        TextView user = (TextView) view.findViewById(R.id.NickUsuario);
        user.setText(((MainActivity)getActivity()).miUsuarioNick);
        imagenUsuario = (ImageView)view.findViewById(R.id.ImagenUsuario);

        lvstring = (ListView)view.findViewById(R.id.lvstring);
        hiloconexion1 = new ObtenerWebService1();
        hiloconexion1.execute(String.valueOf(((MainActivity) getActivity()).miUsuarioID));

        adaptadorTorneos = new TorneoAdapter(getActivity(),listaTorneos, false);
        lvstring.setAdapter(adaptadorTorneos);

        lvstring.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(view.getContext());
                dialogo1.setTitle(listaTorneos.get(position).getNombre());
                dialogo1.setMessage("¿Quieres darte de baja de este torneo?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        hiloDestornear = new ObtenerWebService2();
                        hiloDestornear.execute(String.valueOf(listaTorneos.get(position).getIDTorneo()));
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if(((MainActivity)getActivity()).haySonido) {
            soundPool.play(carga, 1, 1, 0, 0, 1);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    //este hilo rellena los torneos
    public class ObtenerWebService1 extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            URL url = null;
            String devuelve = "";
            String cadenaConexion = "";
            cadenaConexion = "http://mucedal.hol.es/appesports/mistorneos.php";
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
                            int participantes = torneosJSON.getJSONObject(i).getInt("inscritos");
                            int maxParticipantes = torneosJSON.getJSONObject(i).getInt("numero_participantes");

                            Torneo torneo  = new Torneo(idTorneo, nombre, null, fechaComienzo, participantes, maxParticipantes);
                            listaTorneos.add(torneo);
                        }
                        avatar = torneosJSON.getJSONObject(0).getInt("avatar");
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
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return devuelve;
        }

        @Override
        protected void onPostExecute(String s) {

            if (s == "ok") {
                Toast.makeText((MainActivity) getActivity(), "código avatar: " + avatar, Toast.LENGTH_SHORT).show();
                imagenUsuario.setImageResource(mThumbIds[avatar]);
                adaptadorTorneos = new TorneoAdapter(getActivity(), listaTorneos, false);
                lvstring.setAdapter(adaptadorTorneos);
                Toast.makeText(getActivity(), "estoy entrando en onPostExecute", Toast.LENGTH_LONG).show();
                lvstring.refreshDrawableState(); //esto probablemente no haga falta, lo puse por un error mío
                //pero no lo quito porque no me apetece probar si funciona sin esto
            } else {
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

    //destornear
    public class ObtenerWebService2 extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            //String cadena = params[0];
            String cadenaConexion;
            cadenaConexion = "http://mucedal.hol.es/appesports/destornear.php";

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
                Fragment frg = getFragmentManager().findFragmentByTag("MainFragment");
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
                Toast.makeText(getActivity(), "Desinscripción con éxito", Toast.LENGTH_SHORT).show();
            }
            else{
//                Toast.makeText(getApplicationContext(), "Se produjo un error", Toast.LENGTH_SHORT).show();
            }
            //super.onPostExecute(aVoid);
        }
    }

}
