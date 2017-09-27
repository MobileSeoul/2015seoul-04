package ho.Jun.ho;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import bean.Checker;
import bean.ResultObject;
import bean.ReturnLineColor;
import bean.StationObject;

public class DetailActivity extends Activity {
	ArrayList<Checker> checkers;
	private ApplicationSubwayMap applicationSubwayMap;
	LinearLayout layout;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		checkers = new ArrayList<>();
		TextView detail_text_title = (TextView)findViewById(R.id.detail_text_title);
		detail_text_title.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		applicationSubwayMap = (ApplicationSubwayMap)getApplicationContext();
		
		Intent intent = getIntent();
		ResultObject ro = (ResultObject) intent.getSerializableExtra("resultObject");
		if(ro.getDetails().size() == 1){//하나인경우 체커 잘라서 두개로 넣어야함
			int i;
			ArrayList<String> arr1 = new ArrayList<>();
			ArrayList<String> arr2 = new ArrayList<>();
			for(i=0;i<=ro.getDetails().get(0).getLastStationList().size()/2; i++)
				arr1.add(ro.getDetails().get(0).getLastStationList().get(i));
			Checker c1 = new Checker(arr1);
			c1.setCost(ro.getDetails().get(0).getCost()/2);
			checkers.add(c1);
			for(i=ro.getDetails().get(0).getLastStationList().size()-1;i>=ro.getDetails().get(0).getLastStationList().size()/2;i--)
				arr2.add(ro.getDetails().get(0).getLastStationList().get(i));
			Checker c2 = new Checker(arr2);
			c2.setCost(ro.getDetails().get(0).getCost()/2);
			checkers.add(c2);
		}else
			checkers = ro.getDetails();
		
		
		//listview(LinearLayout)
		layout = (LinearLayout) findViewById(R.id.detail_listview);
		LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layout.setGravity(Gravity.CENTER);
		
		
		//ImgPm
		final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
				getResources().getDisplayMetrics());
		final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
				getResources().getDisplayMetrics());
		LinearLayout.LayoutParams imgPm = new LinearLayout.LayoutParams(width, height);
		imgPm.setMargins(3, 0, 3, 0);

		//dividerPm
		final int divHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,3,
				getResources().getDisplayMetrics());
		LinearLayout.LayoutParams dividerPm = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, divHeight);
		dividerPm.setMargins(0, 10, 0, 10);
		
		//layoutPm
		LinearLayout.LayoutParams layoutPm = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		layoutPm.setMargins(0, 5, 0, 5);

		
		
		for(int l =0; l < checkers.size(); l++){
			Checker c = checkers.get(l);
		if (c.getLastStationList().size() > 1) {
			//어디서부터 어디까지..
			LinearLayout newTitleLayout = new LinearLayout(getApplicationContext());
			newTitleLayout.setLayoutParams(layoutPm);
			newTitleLayout.setGravity(Gravity.CENTER);
			TextView stationTitleTextView = new TextView(getApplicationContext());
			String title = c.getLastStationList().get(0).trim() + " 부터 "
					+ c.getLastStationList().get(c.getLastStationList().size() - 1).trim() + "까지";
			title += "\n예상 소요시간 : ";
			int costHour = c.getCost() / 60;
			int costMin = c.getCost() % 60;
			if (costHour > 0)
				title += costHour + "시간 ";
			title += costMin + "분";

			stationTitleTextView.setText(title);
			stationTitleTextView.setLayoutParams(pm);
			stationTitleTextView.setPadding(0, 0, 0, 5);
			stationTitleTextView.setTextColor(Color.BLACK);
			stationTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);// dpsize
			stationTitleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
			newTitleLayout.addView(stationTitleTextView);
			layout.addView(newTitleLayout);

			//무슨 역 거쳐가는지
			for (int i = 0; i < c.getLastStationList().size(); i++) {
				String stationName = c.getLastStationList().get(i).trim();
				LinearLayout newLayout = new LinearLayout(getApplicationContext());
				newLayout.setLayoutParams(layoutPm);
				newLayout.setGravity(Gravity.CENTER);
				TextView stationTextView = new TextView(getApplicationContext());
				stationTextView.setText(stationName);
				stationTextView.setTextColor(Color.BLACK);
				stationTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);// dpsize
				stationTextView.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
				stationTextView.setLayoutParams(pm);
				stationTextView.setPadding(0, 3, 5, 3);
				//마지막이거나 처음(검색시작역이거나 중간역)이면 역색깔넣음
				if(i==0 || i == c.getLastStationList().size()-1){
					stationTextView.setTextColor(Color.parseColor(ReturnLineColor.get(
							applicationSubwayMap.getSubwayMap().get(stationName).getLineData().get(0))));
				}
				newLayout.addView(stationTextView);

				//맵에서 확인해서 호선아이콘도 넣음
				StationObject so = applicationSubwayMap.getSubwayMap().get(stationName);
				for (int j = 0; j < so.getLineData().size(); j++) {
					ImageView iv = new ImageView(getApplicationContext());
					iv.setLayoutParams(imgPm);
					if (so.getLineData().get(j).equals("1"))
						iv.setImageResource(R.drawable.linenum1);
					else if (so.getLineData().get(j).equals("2"))
						iv.setImageResource(R.drawable.linenum2);
					else if (so.getLineData().get(j).equals("3"))
						iv.setImageResource(R.drawable.linenum3);
					else if (so.getLineData().get(j).equals("4"))
						iv.setImageResource(R.drawable.linenum4);
					else if (so.getLineData().get(j).equals("5"))
						iv.setImageResource(R.drawable.linenum5);
					else if (so.getLineData().get(j).equals("6"))
						iv.setImageResource(R.drawable.linenum6);
					else if (so.getLineData().get(j).equals("7"))
						iv.setImageResource(R.drawable.linenum7);
					else if (so.getLineData().get(j).equals("8"))
						iv.setImageResource(R.drawable.linenum8);
					else if (so.getLineData().get(j).equals("9"))
						iv.setImageResource(R.drawable.linenum9);
					else if (so.getLineData().get(j).equals("K"))
						iv.setImageResource(R.drawable.linenumk);
					else if (so.getLineData().get(j).equals("I"))
						iv.setImageResource(R.drawable.linenumi);
					else if (so.getLineData().get(j).equals("Y"))
						iv.setImageResource(R.drawable.linenumy);
					else if (so.getLineData().get(j).equals("A"))
						iv.setImageResource(R.drawable.linenuma);
					else if (so.getLineData().get(j).equals("B"))
						iv.setImageResource(R.drawable.linenumb);
					else if (so.getLineData().get(j).equals("S"))
						iv.setImageResource(R.drawable.linenums);
					else if (so.getLineData().get(j).equals("E"))
						iv.setImageResource(R.drawable.linenume);
					else if (so.getLineData().get(j).equals("U"))
						iv.setImageResource(R.drawable.linenumu);
					newLayout.addView(iv);
				}
				layout.addView(newLayout);
				//이 역이 마지막이 아니면 화살표도 넣음
				if(i+1 < c.getLastStationList().size()){
					ImageView iv = new ImageView(getApplicationContext());
					iv.setLayoutParams(pm);
					iv.setImageResource(R.drawable.detail_arrow);
					layout.addView(iv);	
				}
				
			}
			
		}
		//마지막이 아니면 구분선
		if(l+1 < checkers.size()){
			ImageView iv = new ImageView(getApplicationContext());
			iv.setLayoutParams(dividerPm);
			iv.setImageResource(R.drawable.listview_divider);
			layout.addView(iv);
		}
	}
		
		
		
		
		
		
		Button backButton = (Button)findViewById(R.id.detail_back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		layout.removeAllViews();
	}
	
	
}
