package ho.Jun.ho;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ErrorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error);
		TextView error_text_title = (TextView)findViewById(R.id.error_title);
		error_text_title.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		TextView error_text_detail1 = (TextView)findViewById(R.id.error_detail1);
		error_text_detail1.setTypeface(Typeface.createFromAsset(getAssets(), "BMHANNA_11yrs_ttf.ttf"));		
		TextView error_text_detail2 = (TextView)findViewById(R.id.error_detail2);
		error_text_detail2.setTypeface(Typeface.createFromAsset(getAssets(), "BMHANNA_11yrs_ttf.ttf"));		
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
					finish();
			}
		}, 2000);
		
	}
}
