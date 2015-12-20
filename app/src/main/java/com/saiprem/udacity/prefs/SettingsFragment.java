package com.saiprem.udacity.prefs;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.saiprem.udacity.R;

/**
 * Created by anupam on 19/12/2015.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    public void onDestroy (){
        super.onDestroy();
        getPreferenceScreen().getDialog().dismiss();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        getFragmentManager().invalidateOptionsMenu();
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
