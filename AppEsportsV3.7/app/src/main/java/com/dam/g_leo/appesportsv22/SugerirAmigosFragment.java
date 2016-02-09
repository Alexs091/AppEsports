package com.dam.g_leo.appesportsv22;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SugerirAmigosFragment extends Fragment implements View.OnClickListener {

    AmigoAdapter adaptadorAmigos;
    List<Jugador> listaWebService;
    List<String> listaContentProvider;
    List<Jugador> listaDefinitiva;
    ListView lvstring;
    Button bttn_amigos;
    ObtenerWebService hiloconexion;

    public SugerirAmigosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sugerir_amigos, container, false);
        listaWebService = new ArrayList<Jugador>();
        listaContentProvider = new ArrayList<>();
        listaDefinitiva = new ArrayList<Jugador>();
        lvstring = (ListView) view.findViewById(R.id.lvstring2);
        //recojo los números de teléfono
        obtenerContactos(); //TODO
        //recojo los números de la bbdd y comparo
        hiloconexion = new ObtenerWebService();
        hiloconexion.execute(String.valueOf(((MainActivity) getActivity()).miUsuarioID));

        bttn_amigos = (Button)view.findViewById(R.id.bttn_misamigos);
        bttn_amigos.setOnClickListener(this);
        adaptadorAmigos = new AmigoAdapter(getActivity(), listaWebService);
        lvstring.setAdapter(adaptadorAmigos);
        return view;
    }

    private void obtenerContactos() {
        String[] projection = new String[]{
//                ContactsContract.Data._ID,
//                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER/*,*/
//                ContactsContract.CommonDataKinds.Phone.TYPE
        };

        String selectionClause = ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";

        String sortOrder = ContactsContract.Data.DISPLAY_NAME + " ASC";


        Cursor contactsCursor = getActivity().getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selectionClause,
                null,
                sortOrder);

        //textView.setText("");

        while (contactsCursor.moveToNext()) {
//            textView.append("Identificador: " + contactsCursor.getString(0)
//                            + "Nombre: " + contactsCursor.getString(1)
//                            + "Número: " + contactsCursor.getString(2)
//                            + "Tipo: " + contactsCursor.getString(3)
//                            + "\n"
//            );
            listaContentProvider.add(contactsCursor.getString(0).replace(" ", ""));
        }

        contactsCursor.close();
    }

    public void compararTelefonos(){
        for (Jugador j : listaWebService) {
            if (listaContentProvider.contains(j.getTelefono())) {
                //si el teléfono del jugador no está en la lista de
                //los de mis contactos lo elimino de la lista
                listaDefinitiva.add(j);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bttn_misamigos: abrirFragmentAmigos();
                break;
        }
    }

    public void abrirFragmentAmigos() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AmigosFragment fragment = new AmigosFragment();
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

            cadenaConexion = "http://mucedal.hol.es/appesports/obtenertodostelefonos.php";
//            cadenaConexion += "?id=" + params[0];

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
                        JSONArray telefonosJSON = respuestaJSON.getJSONArray("jugadores");
                        devuelve = "ok";
                        for (int i = 0; i < telefonosJSON.length(); i++) {
                            int id_jugador = telefonosJSON.getJSONObject(i).getInt("id_jugador");
                            String nick = telefonosJSON.getJSONObject(i).getString("nick");
                            String idonline = telefonosJSON.getJSONObject(i).getString("idonline");
                            String telefono = telefonosJSON.getJSONObject(i).getString("telefono");
                            Jugador jugador = new Jugador(id_jugador, nick, idonline, telefono);
                            listaWebService.add(jugador);
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
                compararTelefonos();
                adaptadorAmigos = new AmigoAdapter(getActivity(), listaDefinitiva);
                lvstring.setAdapter(adaptadorAmigos);
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

}
