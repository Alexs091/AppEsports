package com.dam.g_leo.appesportsv22;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BuscarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class BuscarFragment extends Fragment implements View.OnClickListener {
    AmigoAdapter adaptadorAmigos;
    List<Jugador> listaAmigos;
    ObtenerWebService hiloconexion;
    ListView lvstring;
    Button bttn_buscar;
    EditText et_buscar;

    public BuscarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buscar, container, false);
        listaAmigos = new ArrayList<Jugador>();
        String[] args = new String[] {String.valueOf(((MainActivity)getActivity()).miUsuarioID)};
        bttn_buscar = (Button)view.findViewById(R.id.bttn_buscar);
        bttn_buscar.setOnClickListener(this);
        et_buscar = (EditText)view.findViewById(R.id.et_buscar);
        lvstring = (ListView)view.findViewById(R.id.lvstring);

        return view;
    }

    @Override
    public void onClick(View view) {
        listaAmigos.clear();
        String textoABuscar = et_buscar.getText().toString();
        hiloconexion = new ObtenerWebService();
        hiloconexion.execute(textoABuscar);
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
            String cadenaConexion = "";

            cadenaConexion = "http://mucedal.hol.es/appesports/obtener_jugador_por_nick.php";
            cadenaConexion += "?nick=" + params[0];

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
                        JSONObject jugadorJSON = respuestaJSON.getJSONObject("jugador");
                        devuelve = "ok";

                        String nick = jugadorJSON.getString("nick");
//                            devuelve += nick + ";;;";
                        String idonline = jugadorJSON.getString("idonline");
//                            devuelve += idonline + ";;;";
                        Jugador jugador = new Jugador(nick, idonline);
                        listaAmigos.add(jugador);

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
