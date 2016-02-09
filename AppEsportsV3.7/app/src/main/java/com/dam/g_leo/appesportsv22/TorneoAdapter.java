package com.dam.g_leo.appesportsv22;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by Adrián on 19/11/2015.
 */
public class TorneoAdapter<T> extends ArrayAdapter<T> {

    private boolean inscripcion;

    public boolean isInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(boolean inscripcion) {
        this.inscripcion = inscripcion;
    }

    public TorneoAdapter(Context context, List<T> objects, boolean inscripcion) {
        super(context, 0, objects);
        setInscripcion(inscripcion);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View listItemView = convertView;

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo con image_list_view.xml
            if(inscripcion){
                listItemView = inflater.inflate(
                        R.layout.torneoinscripcion,
                        parent,
                        false);
            }
            else{
                listItemView = inflater.inflate(
                        R.layout.torneolayout,
                        parent,
                        false);
            }
        }

        //Obteniendo instancias de los elementos
        TextView fecha_comienzo = (TextView) listItemView.findViewById(R.id.fecha_comienzo);
        TextView nombre = (TextView) listItemView.findViewById(R.id.nombre);
        TextView max_participantes = (TextView) listItemView.findViewById(R.id.max_participantes);

        //Obteniendo instancia de la Tarea en la posición actual
        Torneo item = (Torneo) getItem(position);

        if(inscripcion){
            TextView fecha_max = (TextView) listItemView.findViewById(R.id.fecha_max);
            fecha_max.setText(item.getFecha_Inscripcion().toString());
        }

        fecha_comienzo.setText(item.getFecha_Comienzo().toString());
        nombre.setText(item.getNombre());
        max_participantes.setText(String.valueOf(item.getParticipantes_Actuales()) +
                "/" + String.valueOf(item.getMaximo_Participantes()));

        //Devolver al ListView la fila creada
        return listItemView;

    }
}