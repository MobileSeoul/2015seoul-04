package dialog;

import java.util.ArrayList;

import adapter.AddStationAdapter;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ho.Jun.ho.ApplicationSubwayMap;
import ho.Jun.ho.R;

public class AddStationDialog extends Dialog {
	Context context;
	String title;
	View.OnClickListener onClickListener;
	ApplicationSubwayMap applicationSubwayMap;
	AutoCompleteTextView autotext;
	AddStationAdapter AddStationAdapter;
	RelativeLayout rLayout;
	
	public AddStationDialog(Context context , String title , View.OnClickListener onClickListener) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.title = title;
        this.onClickListener = onClickListener;
        applicationSubwayMap = (ApplicationSubwayMap)context.getApplicationContext();
    }

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_add_station);
        autotext = (AutoCompleteTextView)findViewById(R.id.add_dialog_input);
        autotext.setTypeface(Typeface.createFromAsset(context.getAssets(), "BMHANNA_11yrs_ttf.ttf"));
		setFont();
		setAutoTextView(R.id.add_dialog_input);
		AddStationAdapter.notifyDataSetChanged();
		
		//클릭하면 다시 원래대로
		autotext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				autotext.setTextColor(Color.BLACK);
			}
		});
		
		
		
		
		
		//확인버튼
		Button dialog_ok_button = (Button)findViewById(R.id.add_dialog_ok_button);
		dialog_ok_button.setOnClickListener(onClickListener);
		
		//취소버튼
		Button dialog_cancle_button = (Button)findViewById(R.id.add_dialog_cancle_button);
		dialog_cancle_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				autotext.setText("");
				dismiss();
			}
		});
		
		//자동으로 포커스줌
		autotext.requestFocus();
	}

	private void setFont(){
		TextView add_dialog_text_title = (TextView)findViewById(R.id.add_dialog_title);
		add_dialog_text_title.setTypeface(Typeface.createFromAsset(context.getAssets(), "BMJUA_ttf.ttf"));
	}
	
	private void setAutoTextView(int autotextId){
			AddStationAdapter = new AddStationAdapter(getContext(), autotextId, (ArrayList<String>) applicationSubwayMap.getStationNames().clone());
			autotext.setAdapter(AddStationAdapter);
	}
}
