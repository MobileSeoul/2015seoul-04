package ho.Jun.ho;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;
import map.SubwayMap;

public class SplashActivity extends Activity {
	private ApplicationSubwayMap applicationSubwayMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		applicationSubwayMap = (ApplicationSubwayMap) getApplicationContext();
		
		TextView start_text_where = (TextView)findViewById(R.id.splash_text_where);
		start_text_where.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		TextView start_text_meet = (TextView)findViewById(R.id.splash_text_meet);
		start_text_meet.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(getSubwayMap(applicationSubwayMap)){
					//appclass에 로딩완료 설정도 로드해야함
					SharedPreferences prefs = 
							PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					applicationSubwayMap.setMaxSize(prefs.getInt("maxSize", 60)+10);
					applicationSubwayMap.setUseNetwork(prefs.getBoolean("useNetwork", true));
					
					
					if(applicationSubwayMap.getMaxSize() < 10)
						applicationSubwayMap.setMaxSize(10);
					
					if(!applicationSubwayMap.isUseNetwork()
							&& applicationSubwayMap.getMaxSize() > 40)
						applicationSubwayMap.setMaxSize(40);
					
					
					
					//get key array
					ArrayList<String> stationNames =  new ArrayList<String>();
					Iterator ite = applicationSubwayMap.getSubwayMap().keySet().iterator();
					while(ite.hasNext())
						stationNames.add((String) ite.next());
					applicationSubwayMap.setStationNames(stationNames);
					finish();
				}else{
					//로딩실패
					setResult(500);
					finish();
				}
				
			}
		}, 500);
		
		
	}

	
	
	
	private boolean getSubwayMap(ApplicationSubwayMap applicationSubwayMap){
		SubwayMap n = null;
        try {
        	Resources res = getResources();
        	InputStream fis = res.openRawResource(R.raw.stationmap);
        	ObjectInputStream ois = new ObjectInputStream(fis);
            n = (SubwayMap) ois.readObject();
			ois.close();

			if(n != null)
				applicationSubwayMap.setSubwayMap(n);
			else
				return false;
			
			return true;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
	}
	
	
	
	
}
