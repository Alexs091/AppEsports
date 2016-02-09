package com.dam.g_leo.appesportsv22;

import android.os.Bundle;

/**
 * Created by g_leo on 09/11/2015.
 */
public class Preferencias extends android.preference.PreferenceActivity {
    @Override
    protected  void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.preferencias);
    }
}