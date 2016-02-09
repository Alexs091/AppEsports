package com.dam.g_leo.appesportsv22;


import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Torneosfragment extends Fragment {
    SoundPool soundPool;
    TorneoAdapter adaptadorTorneos;
    List<Torneo> listaTorneos;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
    int carga;
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

        String[] args = new String[] {String.valueOf(((MainActivity)getActivity()).miUsuarioID)};
        /*Cursor c = ((MainActivity)getActivity()).sqLiteDatabase.rawQuery("SELECT t.nombre, t.fecha_fin_registro, t.fecha_comienzo, (select count(*) from inscripcion where id_torneo = i.id_torneo and id_torneo = t.id) as 'inscritos', t.numero_participantes \n" +
                "FROM jugador j, torneo t, inscripcion i\n" +
                "where t.id = i.id_torneo\n" +
                "and j.id = ?\n" +
                "and j.id not in (Select id_usuario from inscripcion where id_torneo = t.id)\n" +
                "UNION\n" +
                "SELECT t.nombre, t.fecha_fin_registro, t.fecha_comienzo, (select count(*) from inscripcion where id_torneo = i.id_torneo and id_torneo = t.id) as 'inscritos', t.numero_participantes \n" +
                "FROM jugador j, torneo t, inscripcion i\n" +
                "where t.id not in (select id_torneo from inscripcion)\n" +
                "and j.id = i.id_usuario", args);
        if(c.moveToFirst()){
            do{
                Torneo torneo1 = null;
                try {
                    torneo1 = new Torneo(c.getString(0),formatter.parse(c.getString(1)),
                            formatter.parse(c.getString(2)), c.getInt(3), c.getInt(4));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                listaTorneos.add(torneo1);
            }while(c.moveToNext());
        }*/

        //TextView user = (TextView) view.findViewById(R.id.NickUsuario);
        ListView lvstring = (ListView)view.findViewById(R.id.lvstring);

        adaptadorTorneos = new TorneoAdapter(getActivity(),listaTorneos, true);
        lvstring.setAdapter(adaptadorTorneos);
        lvstring.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(view.getContext());
                dialogo1.setTitle(listaTorneos.get(position).getNombre());
                dialogo1.setMessage("¿Quieres inscribirte en este torneo?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Fragment frg = getFragmentManager().findFragmentByTag("Torneosfragment");
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(frg);
                        ft.attach(frg);
                        ft.commit();
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


}
