package dialog;

import adapter.AddStationAdapter;
import android.R.color;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import ho.Jun.ho.ApplicationSubwayMap;
import ho.Jun.ho.R;

public class AddStationErrorDialog extends Dialog {
	Context context;
	
	public AddStationErrorDialog(Context context) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
    }

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_add_error);
		setFont();
		
		
		//확인버튼
		Button dialog_ok_button = (Button)findViewById(R.id.add_dialog_error_ok_button);
		dialog_ok_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		
	}

	private void setFont(){
		TextView add_dialog_text_title = (TextView)findViewById(R.id.add_dialog_error_title);
		add_dialog_text_title.setTypeface(Typeface.createFromAsset(context.getAssets(), "BMJUA_ttf.ttf"));
		TextView add_dialog_text_detail = (TextView)findViewById(R.id.add_dialog_error_detail);
		add_dialog_text_detail.setTypeface(Typeface.createFromAsset(context.getAssets(), "BMHANNA_11yrs_ttf.ttf"));
		
		
	}
	
}
