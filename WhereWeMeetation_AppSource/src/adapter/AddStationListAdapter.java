package adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import bean.StationObject;
import ho.Jun.ho.ApplicationSubwayMap;
import ho.Jun.ho.R;

public class AddStationListAdapter extends ArrayAdapter<String>{
	private ArrayList<String> list;
	private Context context;
	ApplicationSubwayMap applicationSubwayMap;
	ArrayList<ImageView> img;
	
	public AddStationListAdapter(Context context, int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.list = (ArrayList<String>) objects;
		this.applicationSubwayMap = (ApplicationSubwayMap) context.getApplicationContext();
		
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.add_listview_row, null);
			TextView text = (TextView) v.findViewById(R.id.add_list_text);
			text.setTypeface(Typeface.createFromAsset(context.getAssets(), "BMJUA_ttf.ttf"));
		}
		String stationName = list.get(position);
		if (stationName != null) {
			TextView text = (TextView) v.findViewById(R.id.add_list_text);
			text.setText(stationName);
			
			
			//imageset
			img = new ArrayList<>();
			img.add((ImageView) v.findViewById(R.id.add_list_image1));
			img.add((ImageView) v.findViewById(R.id.add_list_image2));
			img.add((ImageView) v.findViewById(R.id.add_list_image3));
			img.add((ImageView) v.findViewById(R.id.add_list_image4));
			img.add((ImageView) v.findViewById(R.id.add_list_image5));

			
			ImageButton ib = (ImageButton)v.findViewById(R.id.add_list_delete);
			ib.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					for(int i=0;i<img.size();i++){
						 img.get(i).setImageDrawable(null);
					}
					list.remove(position);
					notifyDataSetChanged();
				}
			});
			

			
			StationObject so = applicationSubwayMap.getSubwayMap().get(stationName);
			for (int i = 0; i < so.getLineData().size(); i++) {
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
		}
		return v;
	}

}
