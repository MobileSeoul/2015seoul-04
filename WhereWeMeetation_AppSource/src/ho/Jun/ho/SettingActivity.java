package ho.Jun.ho;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingActivity extends PreferenceActivity {
	private ApplicationSubwayMap applicationSubwayMap;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationSubwayMap = (ApplicationSubwayMap) getApplicationContext();
        addPreferencesFromResource(R.xml.activity_setting);
        
        final Preference network = (Preference)findPreference("useNetwork");
        network.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				applicationSubwayMap.setUseNetwork(Boolean.valueOf(newValue.toString()));

				if(applicationSubwayMap.getMaxSize() < 10)
					applicationSubwayMap.setMaxSize(10);
				
				if(!applicationSubwayMap.isUseNetwork()
						&& applicationSubwayMap.getMaxSize() > 40)
					applicationSubwayMap.setMaxSize(40);
				
				return true;
			}
		});
        
    }
}
