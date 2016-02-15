package com.dam.g_leo.appesportsv22;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PerfilFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PerfilFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    Spinner nacionalidad;
    TextView nombreUsuario;
    TextView battleTag;
    TextView email;
    TextView telefono;
    ImageView miImageButton;
    ObtenerWebService hiloconexion;
    Jugador miJugador;
    boolean esAmigo;
    static Integer[] mThumbIds = {
            R.drawable.profileicon10,
            R.drawable.profileicon13,
            R.drawable.profileicon23,
            R.drawable.profileicon24old,
            R.drawable.profileicon29old,
            R.drawable.avataricon
     };

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //Inicializar controles
        nombreUsuario = (TextView)view.findViewById(R.id.nombreUsuario);
        battleTag = (TextView)view.findViewById(R.id.battleTag);
        email = (TextView)view.findViewById(R.id.email);
        telefono = (TextView)view.findViewById(R.id.telefono);
        miImageButton = (ImageView)view.findViewById(R.id.imageButton);

        //TODO rellenar los campos con la select del jugador actual
        nombreUsuario.setText("");
        battleTag.setText("");
        email.setText("");
        telefono.setText("");

        hiloconexion = new ObtenerWebService();
        hiloconexion.execute(((MainActivity)getActivity()).otroJugadorNick);

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

    public class ObtenerWebService extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            URL url = null;
            String devuelve = "";
            String cadenaConexion = "http://mucedal.hol.es/appesports/obtener_jugador_por_nick.php";
            cadenaConexion += "?nick=" + params[0];


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
                    devuelve = resultJSON;
                    if (resultJSON.equals("1") || resultJSON == "1") {
                        JSONObject jugadorJson = respuestaJSON.getJSONObject("jugador");
                        devuelve = "ok";

                        //Saco algún campo de más por si acaso
                        String nombre = jugadorJson.getString("nick");
                        String correo = jugadorJson.getString("correo");
                        String idOnline = jugadorJson.getString("idonline");
                        String telefono = jugadorJson.getString("telefono");
                        int avatar = jugadorJson.getInt("avatar");
                        double latitud = jugadorJson.getDouble("latitud");
                        double longitud = jugadorJson.getDouble("longitud");

                        miJugador = new Jugador();
                        miJugador.setNick(nombre);
                        miJugador.setCorreo(correo);
                        miJugador.setIDOnline(idOnline);
                        miJugador.setTelefono(telefono);
                        miJugador.setAvatar(avatar);
                        miJugador.setLatitud(latitud);
                        miJugador.setLongitud(longitud);

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
            super.onPostExecute(s);

            if (s == "ok") {
                nombreUsuario.setText(miJugador.getNick());
                battleTag.setText(miJugador.getIDOnline());
                email.setText(miJugador.getCorreo());
                telefono.setText(miJugador.getTelefono());
                LatLng latLng = new LatLng(miJugador.getLatitud(), miJugador.getLongitud());
                miImageButton.setImageResource(mThumbIds[miJugador.getAvatar()]);
            } else {
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Amigar y desamigar
    public class ObtenerWebService2 extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            //String cadena = params[0];
            String cadenaConexion;
            if (esAmigo) {
                cadenaConexion = "http://mucedal.hol.es/appesports/amigar.php";
            } else {
                cadenaConexion = "http://mucedal.hol.es/appesports/desamigar.php";
            }

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
                jsonParam.put("id_amigo", miJugador.getID_Jugador());

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
//                Toast.makeText(getApplicationContext(), "Registrado con éxito", Toast.LENGTH_LONG).show();
//                finish();
            }
            else{
//                Toast.makeText(getApplicationContext(), "Se produjo un error", Toast.LENGTH_SHORT).show();
            }
            //super.onPostExecute(aVoid);
        }
    }

    //Comprobar si es amigo
    public class ObtenerWebService3 extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadenaConexion = "http://mucedal.hol.es/appesports/comprobaramistad.php";
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
                jsonParam.put("id_amigo", miJugador.getID_Jugador());

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
                        //son amigos
                        devuelve = "ok";
                    } else if (resultJSON.equals("2") || resultJSON == "2") {
                        //no son amigos
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
                esAmigo = true;
            }
            else{
//                Toast.makeText(getApplicationContext(), "Se produjo un error", Toast.LENGTH_SHORT).show();
            }
            //super.onPostExecute(aVoid);
        }
    }



}
