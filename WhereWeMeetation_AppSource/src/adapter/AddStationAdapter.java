package adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;
import bean.StationObject;
import ho.Jun.ho.ApplicationSubwayMap;
import ho.Jun.ho.R;

public class AddStationAdapter extends ArrayAdapter<String>implements Filterable {
	Context context;
	int textViewResourceId;
	ArrayList<String> list;
	View view;
	ApplicationSubwayMap applicationSubwayMap;
	private ArrayList<String> suggestions;
	private ArrayList<String> listAll;
	
	public AddStationAdapter(Context context, int textViewResourceId, ArrayList<String> list) {
		super(context, textViewResourceId, list);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.list = list;
		this.listAll = (ArrayList<String>) list.clone();
		this.applicationSubwayMap = (ApplicationSubwayMap) context.getApplicationContext();
		this.suggestions = new ArrayList<String>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.add_autocompletetextview, null);
			TextView text = (TextView) v.findViewById(R.id.add_autocomplete_text);
			text.setTypeface(Typeface.createFromAsset(context.getAssets(), "BMHANNA_11yrs_ttf.ttf"));
		}
		
		String stationName = list.get(position);
		if (stationName != null) {
			TextView text = (TextView) v.findViewById(R.id.add_autocomplete_text);
			ArrayList<ImageView> img = new ArrayList<>();
			img.add((ImageView) v.findViewById(R.id.add_autocomplete_image1));
			img.add((ImageView) v.findViewById(R.id.add_autocomplete_image2));
			img.add((ImageView) v.findViewById(R.id.add_autocomplete_image3));
			img.add((ImageView) v.findViewById(R.id.add_autocomplete_image4));
			img.add((ImageView) v.findViewById(R.id.add_autocomplete_image5));

			text.setText(stationName);
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
				 
			}
		}
		return v;
	}

	@Override
	public android.widget.Filter getFilter() {
		return new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				if (constraint != null) {
					suggestions.clear();
					for (String str : listAll) {
						if (str.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
							suggestions.add(str);
						}
					}
					FilterResults filterResults = new FilterResults();
					filterResults.values = suggestions;
					filterResults.count = suggestions.size();
					return filterResults;

				} else {
					return new FilterResults();
				}
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				ArrayList<String> filteredList = (ArrayList<String>) results.values;
				if (results != null && results.count > 0) {
					clear();
					for (String str : filteredList)
						add(str.trim());
					
					notifyDataSetChanged();
				}
			}

			@Override
			public CharSequence convertResultToString(Object resultValue) {
				return (String) resultValue;
			}

		};
	}

}