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

public class AddStationProcessingDialog extends Dialog {
	Context context;
	
	public AddStationProcessingDialog(Context context) {
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
        setContentView(R.layout.dialog_processing);
		setFont();
		this.setCancelable(false);
	}

	private void setFont(){
		TextView add_dialog_processing_title = (TextView)findViewById(R.id.add_dialog_processing_title);
		add_dialog_processing_title.setTypeface(Typeface.createFromAsset(context.getAssets(), "BMJUA_ttf.ttf"));
	}
	
}
