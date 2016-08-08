package com.ithub.mda.elearn;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Sagar Pahwa on 25-06-2016.
 */
public class ELearnSettings extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.elearn_settings);
    }
}
