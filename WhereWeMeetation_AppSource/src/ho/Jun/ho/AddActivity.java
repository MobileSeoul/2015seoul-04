package ho.Jun.ho;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;

import org.xml.sax.InputSource;

import adapter.AddStationListAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bean.Checker;
import bean.ResultObject;
import dialog.AddStationDialog;
import dialog.AddStationErrorDialog;
import dialog.AddStationProcessingDialog;

public class AddActivity extends Activity {
	private ApplicationSubwayMap applicationSubwayMap;
	private AddStationDialog addStationDialog;
	private AddStationErrorDialog addStationErrorDialog;
	private	AddStationProcessingDialog addStationProcessingDialog;
	private View.OnClickListener okClickListener;
	private ArrayList<String> stationNames = new ArrayList<String>();
	private AddStationListAdapter listadapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		applicationSubwayMap = (ApplicationSubwayMap) getApplicationContext();
		TextView add_text_title = (TextView) findViewById(R.id.add_text_title);
		add_text_title.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));
		TextView add_text_subtitle = (TextView) findViewById(R.id.add_text_subtitle);
		add_text_subtitle.setTypeface(Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf"));

		ListView listview = (ListView) findViewById(R.id.add_listview);
		listadapter = new AddStationListAdapter(getApplicationContext(), R.layout.add_listview_row, stationNames);
		listview.setAdapter(listadapter);

		okClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AutoCompleteTextView at = (AutoCompleteTextView) addStationDialog.findViewById(R.id.add_dialog_input);
				String str = at.getText().toString();
				if (applicationSubwayMap.getSubwayMap().get(str) != null && !stationNames.contains(str)) {
					stationNames.add(str);
					at.setTextColor(Color.BLACK);
					at.setText("");
					listadapter.notifyDataSetChanged();
					addStationDialog.dismiss();
				} else {
					at.setTextColor(Color.RED);
				}

			}
		};

		addStationDialog = new AddStationDialog(this, "역 추가하기", okClickListener);
		addStationErrorDialog = new AddStationErrorDialog(this);
		addStationProcessingDialog = new AddStationProcessingDialog(this);

		
		
		
		
		
		Button add_button = (Button) findViewById(R.id.add_add_button);
		add_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addStationDialog.show();
			}
		});

		Button result_start = (Button) findViewById(R.id.add_start_button);
		result_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (stationNames.size() > 1) {//역 입력 2개 이상이면
					addStationProcessingDialog.show();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							ResultObject ro =  getSearchResult(stationNames);
							//왜 반투명이 풀리는지 모르겠다.
							findViewById(R.id.add_scalable_layout).setVisibility(View.INVISIBLE);
							if(ro != null && ro.getDetails() != null && ro.getMidStationName() != null && !ro.getMidStationName().equals("")){
								Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
								intent.putExtra("resultObject", ro);
								intent.putExtra("list", stationNames);
								startActivityForResult(intent, 1);
								stationNames.clear();
								listadapter.notifyDataSetChanged();
								ro = null;
								addStationProcessingDialog.dismiss();
							}else{
								Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
								startActivity(intent);
								addStationProcessingDialog.dismiss();
								finish();
							}
						}
					}, 500);
				} else {
					addStationErrorDialog.show();
				}
			}
		});
		
		
		//시작하면 어차피 역 입력해야되니까
		addStationDialog.show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		findViewById(R.id.add_scalable_layout).setVisibility(View.VISIBLE);
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1){
			if(resultCode == -1)
				finish();
		}
		
	}
	
	
	
	private ResultObject getSearchResult(final ArrayList<String> list) {
		applicationSubwayMap.getSubwayMap().resourceClear();
		
		if (applicationSubwayMap.isUseNetwork()) {//서버에서 찾는경우
			final ResultObject ro = new ResultObject();
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						String listStation = "http://partjun.cafe24.com/WhereWeMeetation/getmidstation.do?size="
								+ applicationSubwayMap.getMaxSize() + "&list=";
						String s = "";
						
						for (int i = 0; i < list.size(); i++) {
							s += list.get(i);
							if (i + 1 != list.size())
								s += ":";
						}
						listStation += URLEncoder.encode(s, "UTF-8");
						
						URL url = new URL(listStation);
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder db = dbf.newDocumentBuilder();
						org.w3c.dom.Document doc = db.parse(new InputSource(url.openStream()));
						doc.getDocumentElement().normalize();
						
						NodeList nodeList = doc.getElementsByTagName("result");

						for (int i = 0; i < nodeList.getLength(); i++) {

							Node node = nodeList.item(i); // result - Element node
							Element fstElmnt = (Element) node;
							NodeList mStation = fstElmnt.getElementsByTagName("midstation");
							Element nameElement = (Element) mStation.item(0);
							mStation = nameElement.getChildNodes();
							ro.setMidStationName(mStation.item(0).getNodeValue());

							NodeList costElmnt = fstElmnt.getElementsByTagName("maxcost");
							String costStr = ((costElmnt.item(0).getChildNodes().item(0).getNodeValue())).toString();
							if(costStr != null)
								ro.setMaxCost(Integer.parseInt(costStr.trim()));
							NodeList candidateElmnt = fstElmnt.getElementsByTagName("detail"); // 중간역
							for (int k = 0; k < candidateElmnt.getLength(); k++) {
								ArrayList<String> arr = new ArrayList<>();
								String details[] = candidateElmnt.item(k).getChildNodes().item(0).getNodeValue().split(",");
								
								
								for (int j = 0; j < details.length; j++) {
									arr.add(details[j]);
								}
								Checker c = new Checker(arr);
								String costStr2 = ((Attr) candidateElmnt.item(k).getAttributes().getNamedItem("cost")).getValue().trim();
								if(costStr2 != null)
									c.setCost(Integer.parseInt(costStr2));
								ro.getDetails().add(c);
								
								
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
			}

			if(ro != null && ro.getDetails() != null)
				return ro;
		}else{//폰에서 찾는경우
			
			applicationSubwayMap.getSubwayMap().MAXCHECKABLE = applicationSubwayMap.getMaxSize();
			ResultObject ro = applicationSubwayMap.getSubwayMap().searchMidStation(list);	
			
			if(ro != null)
				return ro;
		}
		
		return null;
	}
}
