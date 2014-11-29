package com.coursera.dailyselfie;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelfieViewAdapter extends BaseAdapter {
	
	private ArrayList<String> list = new ArrayList<String>();
	private static LayoutInflater inflater = null;
	private Context mContext;

	public SelfieViewAdapter(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		
		View newView = convertView;
		ViewHolder holder;
		
		if (null == convertView) {
			holder = new ViewHolder();
			newView = inflater.inflate(R.layout.selfie_list_item, null);
			holder.icon = (ImageView) newView.findViewById(R.id.icon);
			holder.date = (TextView) newView.findViewById(R.id.date);
			holder.filename = (TextView) newView.findViewById(R.id.filename);
			newView.setTag(holder);
		} else {
			holder = (ViewHolder) newView.getTag();
		}
		
		//holder.icon.setImageBitmap();
		String filename = list.get(position);
		holder.date.setText("Date: " + formatDateFromFile(filename));
		holder.filename.setText("File: " + filename);
		
		return newView;
	}
	
	private String formatDateFromFile(String filename)
	{
		int startIndex = filename.indexOf('_')+1;
		int endIndex = filename.indexOf('_', startIndex);
		String date = filename.substring(startIndex, endIndex);
		String year = date.substring(0, 4);
		String month = date.substring(4, 6);
		String day = date.substring(6, 8);
		return year + "-" + month + "-" + day;
	}

	static class ViewHolder {
	
		ImageView icon;
		TextView date;
		TextView filename;
		
	}
	
	public void add(String file) {
		list.add(file);
		notifyDataSetChanged();
	}
	
	public ArrayList<String> getList() {
		return list;
	}

	public void removeAllViews(){
		list.clear();
		this.notifyDataSetChanged();
	}
}
