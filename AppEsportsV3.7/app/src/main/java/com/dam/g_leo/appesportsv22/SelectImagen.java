package com.dam.g_leo.appesportsv22;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

public class SelectImagen extends AppCompatActivity {

    public static int posicionImagen = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_imagen);

        final GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter((ListAdapter) new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Registro.botonavatar.setImageResource(ImageAdapter.mThumbIds[position]);
                posicionImagen = position;
                finish();
            }
        });
    }

}
