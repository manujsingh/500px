package com.example.fivehundredpx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class PhotoActivity extends BaseActivity{

	private ImageView photo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		
		photo = (ImageView)findViewById(R.photo_id.image);
		Intent intent = getIntent();

		try {
			JSONObject photoJSON = new JSONObject(intent.getStringExtra("photo"));
			JSONArray images = photoJSON.getJSONArray("image_url");
			photo.setTag(images.getString(1));
			setTitle(photoJSON.getString("name"));
			if(GalleryActivity.imageCache.containsKey(images.getString(0))) 
				photo.setImageBitmap(GalleryActivity.imageCache.get(images.getString(0)));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		new ImageDownloadTask().execute(photo);
	}
}
