package com.farcore.videoplayer;

import java.io.File;
import java.util.List;

import com.farcore.videoplayer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Bitmap iconFolder;
	private Bitmap iconAudio;
	private Bitmap iconVideo;
	private Bitmap iconImage;
	private Bitmap iconFile;
	private List<String> items;
	private List<String> paths;

	public MyAdapter(Context context,List<String> it,List<String> pa) {
		mInflater = LayoutInflater.from(context);
		items = it;
		paths = pa;
		iconFolder = BitmapFactory.decodeResource(context.getResources(),R.drawable.folder);
		iconAudio = BitmapFactory.decodeResource(context.getResources(),R.drawable.music);
		iconVideo = BitmapFactory.decodeResource(context.getResources(),R.drawable.movie);
		iconImage = BitmapFactory.decodeResource(context.getResources(),R.drawable.photo);
		iconFile = BitmapFactory.decodeResource(context.getResources(),R.drawable.doc);
	}
  
	
	public int getCount() {
		return items.size();
	}

	
	public Object getItem(int position) {
		return items.get(position);
	}
  
	
	public long getItemId(int position) {
		return position;
	}
  
	
	public View getView(int position,View convertView,ViewGroup parent) {
		ViewHolder holder;
    
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.file_row, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
      
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		File f=new File(paths.get(position).toString());
		
        File txtf = new File(items.get(position).toString());
        
		holder.text.setText(txtf.getName());
		if(f.isDirectory()) {
			holder.icon.setImageBitmap(iconFolder);
		}
		else {
				holder.icon.setImageBitmap(iconVideo);
		}
		return convertView;
	}
  
	private class ViewHolder {
		TextView text;
		ImageView icon;
	}
}
