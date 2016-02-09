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
 * Created by Alex on 19/11/2015.
 */
public class AmigoAdapter<T> extends ArrayAdapter<T> {

    public AmigoAdapter(Context context, List<T> objects) {
        super(context, 0, objects);
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
            listItemView = inflater.inflate(
                    R.layout.amigolayout,
                    parent,
                    false);

        }

        //Obteniendo instancias de los elementos
        TextView nombre = (TextView) listItemView.findViewById(R.id.nombre);
        TextView idonline = (TextView) listItemView.findViewById(R.id.idonline);

        //Obteniendo instancia de la Tarea en la posición actual
        Jugador item = (Jugador) getItem(position);

        nombre.setText(item.getNick());
        idonline.setText(item.getIDOnline());

        //Devolver al ListView la fila creada
        return listItemView;

    }
}