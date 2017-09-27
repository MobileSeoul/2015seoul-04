package setting;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.preference.DialogPreference;
import android.text.style.TtsSpan.VerbatimBuilder;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import ho.Jun.ho.ApplicationSubwayMap;
import android.widget.TextView;

/**
 * A {@link android.preference.Preference} that displays a number picker as a dialog.
 */
public class NumberPickerPreference extends DialogPreference {
	private ApplicationSubwayMap applicationSubwayMap;
	
    // allowed range
    public int MAX_VALUE = 50;
//    public static final int MIN_VALUE = 10;
    // enable or disable the 'circular behavior'
    public static final boolean WRAP_SELECTOR_WHEEL = true; 
    private Context context;
    private TextView detail;
    private TextView detailNumber;
//    private NumberPicker picker;
    private int value;
    private SeekBar seekbar;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected View onCreateDialogView() {
        
    	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(0, 10, 0, 10);
        
		 //MAX_VALUE
        applicationSubwayMap = (ApplicationSubwayMap)context.getApplicationContext();
        if(!applicationSubwayMap.isUseNetwork())
        	MAX_VALUE = 30;
        else
        	MAX_VALUE = 50;
        
        String detailText = "폰에서 직접 검색하는 경우\n설정에 따라 성능이 크게 달라질 수 있습니다.";
        detail = new TextView(getContext());
        detail.setText(detailText);
        detail.setGravity(Gravity.CENTER);
        detail.setLayoutParams(layoutParams);
        
        detailNumber = new TextView(getContext());
        detailNumber.setGravity(Gravity.RIGHT);
        detailNumber.setLayoutParams(layoutParams);
        
        
        seekbar = new SeekBar(getContext());
        LinearLayout.LayoutParams seeklayout =  new LinearLayout.LayoutParams(400, 60);
        seeklayout.bottomMargin = 10;
        seekbar.setMax(MAX_VALUE);
        
        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				 //MAX_VALUE
		        applicationSubwayMap = (ApplicationSubwayMap)context.getApplicationContext();
		        if(!applicationSubwayMap.isUseNetwork())
		        	MAX_VALUE = 30;
		        else
		        	MAX_VALUE = 50;
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				detailNumber.setText((progress+10)+"");
				applicationSubwayMap.setMaxSize(progress+10);
			}
		});
        
        LinearLayout dialogView = new LinearLayout(getContext());
        dialogView.setGravity(Gravity.CENTER);
        dialogView.setOrientation(LinearLayout.VERTICAL);
        dialogView.addView(detail);
        dialogView.addView(detailNumber);
        dialogView.addView(seekbar);
        detailNumber.setText((seekbar.getProgress()+10)+"");
        
        
        return dialogView;
    }

    
    
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        seekbar.setProgress(getValue());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int newValue = seekbar.getProgress();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    public int getValue() {
        return this.value;
    }
}