package ho.Jun.ho;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TextView start_text_where = (TextView)findViewById(R.id.start_text_where);
		start_text_where.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		TextView start_text_meet = (TextView)findViewById(R.id.start_text_meet);
		start_text_meet.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		TextView start_text_subtitle = (TextView)findViewById(R.id.start_text_subtitle);
		start_text_subtitle.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		TextView start_text_detail = (TextView)findViewById(R.id.start_text_detail);
		start_text_detail.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		
		//설정
		Button start_config = (Button)findViewById(R.id.start_config);
		start_config.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), SettingActivity.class);
				startActivity(i);
			}
		});
		
		//시작하기
		Button add_start = (Button)findViewById(R.id.start_start_button);
		add_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), AddActivity.class);
				startActivity(i);
			}
		});
		
		
		
		
		
		//스플래시
		Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
		startActivityForResult(intent, 0);
		
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 0){
			
			if(resultCode == 1000){
				//로딩실패
				Toast.makeText(getApplicationContext(), "지하철 노선 파일 로딩에 실패했습니다.", 1000).show();
				this.finish();
			}
			
			
		}
		
		
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
