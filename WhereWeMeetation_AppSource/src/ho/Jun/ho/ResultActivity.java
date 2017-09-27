package ho.Jun.ho;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import bean.ResultObject;
import bean.ReturnLineColor;
import bean.StationObject;
import map.SubwayMap;
import net.daum.android.map.openapi.search.Item;
import net.daum.android.map.openapi.search.OnFinishSearchListener;
import net.daum.android.map.openapi.search.Searcher;
import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPOIItem.CalloutBalloonButtonType;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPoint.GeoCoordinate;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapView.MapViewEventListener;
import net.daum.mf.map.api.MapView.POIItemEventListener;

public class ResultActivity extends Activity implements MapViewEventListener, POIItemEventListener {
	private static final String DAUMMAPAPIKEY = "c52998f48e8847ea40c560e6300a417f";
	ApplicationSubwayMap applicationSubwayMap;
	TextView result_text_title;
	TextView result_text_subtitle;
	TextView result_text_lookdetail;
	TextView result_text_midstation;
	TextView result_text_movable1;
	TextView result_text_movable2;
	TextView result_text_movable3;
	TextView result_text_movable4;	
	TextView result_alert_message;
	TextView result_text_average;
	ImageView result_alert;
	MapView mapView;//다음맵뷰
	private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();
	ResultObject ro;
	ArrayList<String> list;
	ArrayList<TextView> movableTextList;
	ArrayList<ImageView> img;
	
	double latitude;
	double longitude;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		applicationSubwayMap = (ApplicationSubwayMap) getApplicationContext();
		img = new ArrayList<>();
		movableTextList = new ArrayList<>();
		setFont();
		Intent intent = getIntent();
		ro = (ResultObject)intent.getSerializableExtra("resultObject");
		list = (ArrayList<String>)intent.getSerializableExtra("list");
		result_text_midstation.setText(ro.getMidStationName().trim());
		if(ro.getMidStationName().length() > 5){
			ScalableLayout sl = (ScalableLayout)findViewById(R.id.result_layout);
			sl.setScale_TextSize(result_text_midstation, 63f);
		}else if(ro.getMidStationName().length() > 3){
			ScalableLayout sl = (ScalableLayout)findViewById(R.id.result_layout);
			sl.setScale_TextSize(result_text_midstation, 70f);
		}
		
		
		//mapview
		latitude = applicationSubwayMap.getSubwayMap().get(ro.getMidStationName().trim()).getLatitude();
		longitude = applicationSubwayMap.getSubwayMap().get(ro.getMidStationName().trim()).getLongitude();
		
		mapView = new MapView(this);
		mapView.setDaumMapApiKey(DAUMMAPAPIKEY);
		RelativeLayout mapViewContainer = (RelativeLayout) findViewById(R.id.result_map_view);
		mapViewContainer.addView(mapView);
		mapView.setMapViewEventListener(this);
		mapView.setPOIItemEventListener(this);
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        
        
        
		//좌표맞추기
		mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 2, true);

		//역위치로돌아가기
		Button nowLocButton = (Button)findViewById(R.id.result_nowloc_button);
		nowLocButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mapView.removeAllPOIItems(); // 기존 검색 결과 삭제
				mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 2, true);
			}
		});
		
		//카페
		Button searchCafeButton = (Button)findViewById(R.id.result_search_cafe_button);
		searchCafeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchMapView("카페");
			}
		});
		
		//맛집
		Button searchRestoButton = (Button)findViewById(R.id.result_search_resto_button);
		searchRestoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchMapView("맛집");
			}
		});
		
		//영화관
		Button searchTheaterButton = (Button)findViewById(R.id.result_search_theater_button);
		searchTheaterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchMapView("영화관");
			}
		});
		
		//크게보기
		Button viewBigButton = (Button)findViewById(R.id.result_big_button);
		viewBigButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("http://local.daum.net/map/look?p="
									+latitude + "," + longitude);
				Intent it = new Intent(Intent.ACTION_VIEW,uri);
				startActivity(it);
			}
		});
		
		
		
		
		
		
		String sub = "";
		for(int i=0; i<list.size(); i++){
			if((sub+list.get(i)).length() > 15){
				sub += "...";
				break;
			}
			sub += list.get(i);
			if(i+1 < list.size())
				sub += ", ";
		}
		sub += "의 중간역 검색 결과";
		result_text_subtitle.setText(sub);
		
		//주변역정보등록
		SubwayMap n = applicationSubwayMap.getSubwayMap();
		StationObject so = n.get(ro.getMidStationName().trim());
		for(int i=0; i<so.getMovableStations().size() && i < movableTextList.size(); i++){
			String s = so.getMovableStations().get(i).getStationList().get(0);
			movableTextList.get(i).setTextColor(Color.parseColor
					(ReturnLineColor.get(n.get(s).getLineData().get(0))));
			movableTextList.get(i).setText(s);
			ScalableLayout sl = (ScalableLayout)findViewById(R.id.result_layout);
			if(s.length() > 6){
				sl.setScale_TextSize(movableTextList.get(i), 20f);
			}else if(s.length() > 4){
				sl.setScale_TextSize(movableTextList.get(i), 27f);		
			}
		}
		
		if(so.getLineData().size() == 1 )
			img.add((ImageView)findViewById(R.id.result_linenum3));
		else{
			img.add((ImageView)findViewById(R.id.result_linenum2));
			img.add((ImageView)findViewById(R.id.result_linenum4));
			img.add((ImageView)findViewById(R.id.result_linenum3));
			img.add((ImageView)findViewById(R.id.result_linenum1));
			img.add((ImageView)findViewById(R.id.result_linenum5));
		}
		
		//역번호
		for (int i = 0; i < so.getLineData().size() && i < 5; i++) {
			 if(so.getLineData().get(i).equals("1"))
				 img.get(i).setImageResource(R.drawable.linenum1);
			 else if(so.getLineData().get(i).equals("2"))
				 img.get(i).setImageResource(R.drawable.linenum2);
			 else if(so.getLineData().get(i).equals("3"))
				 img.get(i).setImageResource(R.drawable.linenum3);
			 else if(so.getLineData().get(i).equals("4"))
				 img.get(i).setImageResource(R.drawable.linenum4);
			 else if(so.getLineData().get(i).equals("5"))
				 img.get(i).setImageResource(R.drawable.linenum5);
			 else if(so.getLineData().get(i).equals("6"))
				 img.get(i).setImageResource(R.drawable.linenum6);
			 else if(so.getLineData().get(i).equals("7"))
				 img.get(i).setImageResource(R.drawable.linenum7);
			 else if(so.getLineData().get(i).equals("8"))
				 img.get(i).setImageResource(R.drawable.linenum8);
			 else if(so.getLineData().get(i).equals("9"))
				 img.get(i).setImageResource(R.drawable.linenum9);
			 else if(so.getLineData().get(i).equals("K"))
				 img.get(i).setImageResource(R.drawable.linenumk);
			 else if(so.getLineData().get(i).equals("I"))
				 img.get(i).setImageResource(R.drawable.linenumi);
			 else if(so.getLineData().get(i).equals("Y"))
				 img.get(i).setImageResource(R.drawable.linenumy);
			 else if(so.getLineData().get(i).equals("A"))
				 img.get(i).setImageResource(R.drawable.linenuma);
			 else if(so.getLineData().get(i).equals("B"))
				 img.get(i).setImageResource(R.drawable.linenumb);
			 else if(so.getLineData().get(i).equals("S"))
				 img.get(i).setImageResource(R.drawable.linenums);
			 else if(so.getLineData().get(i).equals("E"))
				 img.get(i).setImageResource(R.drawable.linenume);
			 else if(so.getLineData().get(i).equals("U"))
				 img.get(i).setImageResource(R.drawable.linenumu);
			 else
				 img.get(i).setImageDrawable(null);
			 
		}
		
		
		
		//평균소요시간
		int cost =0;
		for(int i=0; i<ro.getDetails().size(); i++)
			cost += ro.getDetails().get(i).getCost();
		if(ro.getDetails().size() == 1)
			cost = cost /2;
		else
			cost = cost / ro.getDetails().size();
		int costHour = cost/60;
		int costMin = cost%60;
		String cText = "평균 소요 시간 : ";
		if(costHour > 0)
			cText += costHour + "시간 ";
		cText += costMin + "분";
		result_text_average.setText(cText);
		
		
		//경고문
		if(cost < 5){
			result_alert.setVisibility(View.VISIBLE);
			result_alert_message.setVisibility(View.VISIBLE);
		}
		
		
		
		//상세정보보기버튼
		result_text_lookdetail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
				intent.putExtra("resultObject", ro);
				startActivity(intent);
			}
		});
		
		
		Button result_finish_button = (Button)findViewById(R.id.result_finish_button);
		result_finish_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			setResult(-1);
			finish();	
			}
		});
		
	}
	
	private void searchMapView(String query){
		GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
		double latitude = geoCoordinate.latitude; // 위도
		double longitude = geoCoordinate.longitude; // 경도
		int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
		int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
		
		Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
		searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, radius, page, DAUMMAPAPIKEY, new OnFinishSearchListener() {
			@Override
			public void onSuccess(List<Item> itemList) {
				mapView.removeAllPOIItems(); // 기존 검색 결과 삭제
				mapViewShowResult(itemList); // 검색 결과 보여줌 
			}

			@Override
			public void onFail() {
				Toast.makeText(getApplicationContext(), "검색에 실패했습니다.", 1000).show();
			}
		});
	}
	
	private void mapViewShowResult(List<Item> itemList) {
		MapPointBounds mapPointBounds = new MapPointBounds();
		
		for (int i = 0; i < itemList.size(); i++) {
			Item item = itemList.get(i);

			MapPOIItem poiItem = new MapPOIItem();
			poiItem.setItemName(item.title);
			poiItem.setTag(i);
			MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
			poiItem.setMapPoint(mapPoint);
			mapPointBounds.add(mapPoint);
			poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
			poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
			poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
			poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
			poiItem.setCustomImageAutoscale(false);
			poiItem.setCustomImageAnchor(0.5f, 1.0f);
			
			mapView.addPOIItem(poiItem);
			mTagItemMap.put(poiItem.getTag(), item);
		}
	}
	
	
	
	
	private void setFont(){
		result_text_title = (TextView)findViewById(R.id.result_text_title);
		result_text_title.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		result_text_subtitle = (TextView)findViewById(R.id.result_text_subtitle);
		result_text_subtitle.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		result_text_lookdetail = (TextView)findViewById(R.id.result_text_lookdetail);
		result_text_lookdetail.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		result_text_midstation = (TextView)findViewById(R.id.result_text_midstation);
		result_text_midstation.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		result_text_movable1 = (TextView)findViewById(R.id.result_text_movable1);
		result_text_movable1.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		result_text_movable2 = (TextView)findViewById(R.id.result_text_movable2);
		result_text_movable2.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		result_text_movable3 = (TextView)findViewById(R.id.result_text_movable3);
		result_text_movable3.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		result_text_movable4 = (TextView)findViewById(R.id.result_text_movable4);
		result_text_movable4.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		result_text_average = (TextView)findViewById(R.id.result_text_average);
		result_text_average.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		
		
		/*
		 * 가까운 경고메시지
		result_alert_message = (TextView)findViewById(R.id.result_alert_message);
		result_alert_message.setTypeface(Typeface.createFromAsset(getAssets(), "BMHANNA_11yrs_ttf.ttf"));
		result_alert = (ImageView)findViewById(R.id.result_alert);
		*/
		
		
		movableTextList.add(result_text_movable1);
		movableTextList.add(result_text_movable2);
		movableTextList.add(result_text_movable3);
		movableTextList.add(result_text_movable4);

	}

	@Override
	public void onMapViewCenterPointMoved(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapViewDoubleTapped(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapViewDragEnded(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapViewDragStarted(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapViewInitialized(MapView arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapViewLongPressed(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapViewMoveFinished(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapViewSingleTapped(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapViewZoomLevelChanged(MapView arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
		Item item = mTagItemMap.get(mapPOIItem.getTag());
		Uri uri = Uri.parse("http://search.daum.net/search?nil_suggest=btn&w=tot&DA=SBC&q=" + 
		ro.getMidStationName().trim() + " " + item.title);
		Intent it = new Intent(Intent.ACTION_VIEW,uri);
		startActivity(it);
		
		
	}

	@Override
	public void onDraggablePOIItemMoved(MapView arg0, MapPOIItem arg1, MapPoint arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPOIItemSelected(MapView arg0, MapPOIItem arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	 class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
	    	
	    	private final View mCalloutBalloon;
	    	
	    	public CustomCalloutBalloonAdapter() {
	    		mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
			}

			@Override
			public View getCalloutBalloon(MapPOIItem poiItem) {
				if (poiItem == null) return null;
				Item item = mTagItemMap.get(poiItem.getTag());
				if (item == null) return null;
				ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge);
				TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
				textViewTitle.setText(item.title);
				TextView textViewDesc = (TextView) mCalloutBalloon.findViewById(R.id.desc);
				textViewDesc.setText(item.address);
				imageViewBadge.setImageDrawable(createDrawableFromUrl(item.imageUrl));
				return mCalloutBalloon;
			}

			@Override
			public View getPressedCalloutBalloon(MapPOIItem poiItem) {
				return null;
			}
	    	
	    }
	    
	
	 private Drawable createDrawableFromUrl(String url) {
			try {
				InputStream is = (InputStream) this.fetch(url);
				Drawable d = Drawable.createFromStream(is, "src");
				return d;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		private Object fetch(String address) throws MalformedURLException,IOException {
			URL url = new URL(address);
			Object content = url.getContent();
			return content;
		}
}
