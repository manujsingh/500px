package com.example.fivehundredpx;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;

public class GalleryActivity extends BaseActivity implements OnItemSelectedListener, OnScrollListener{

	private String PHOTO_API = "photos?sort=rating&image_size[]=3&image_size[]=4";
	private Spinner spinner_category;
	private GridView grid_images;
	private int PAGE_NUM = 1, MAX_IMAGE = 60;
	private BaseAdapter adapter;
	private List<String> photos = new ArrayList<String>();;
	private int previousCat = 0;
	public static WeakHashMap<String, Bitmap> imageCache = new WeakHashMap<String, Bitmap>();
	private boolean inRequest = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		spinner_category = (Spinner)findViewById(R.main_id.spinner_category);
		grid_images = (GridView)findViewById(R.main_id.grid_images);
		
		spinner_category.setOnItemSelectedListener(this);
		grid_images.setOnScrollListener(this);
		initGallery();
	}
	
	private void initGallery()
	{
		PAGE_NUM = 1;
		
		photos = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, photos)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ImageView img = new ImageView(GalleryActivity.this);
				JSONObject picture = null;
				String image = null;
				try {
					picture = new JSONObject(photos.get(position));
					JSONArray images = picture.getJSONArray("image_url");
					image = images.getString(0);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				final String imageUrl = image;
				img.setTag(imageUrl);
				img.setPadding(5, 5, 5, 5);
				img.setImageResource(R.drawable.img);
				img.setScaleType(ImageView.ScaleType.FIT_XY);
				
				final String jsonString = picture.toString();

				img.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						WeakHashMap<String,String> data = new WeakHashMap<String,String>();
						data.put("photo", jsonString);
						changeActivity(PhotoActivity.class, data);
					}});
				if(imageCache.containsKey(imageUrl)) 
					img.setImageBitmap(imageCache.get(imageUrl));
				else
					new ImageDownloadTask().execute(img);
				
				return img;
			}
		};
		grid_images.setAdapter(adapter);
		getPhotos();
	}
	
	private void updatePhotoAdapter(JSONArray photosArray)
	{
		try 
		{
			for(int i = 0 ; i < photosArray.length(); i++)
			{
				JSONObject photo = photosArray.getJSONObject(i);
				photos.add(photo.toString());
			}
			adapter.notifyDataSetChanged();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void response(Object response, int id)
	{
		try {
			JSONObject photosObj = new JSONObject(response.toString());
			JSONArray photoArray = photosObj.getJSONArray("photos");
			PAGE_NUM++;
			updatePhotoAdapter(photoArray);
			inRequest = false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void requestFailed(String msg, int id)
	{
		// Override action
	}
	private void getPhotos()
	{
		if(inRequest) return;
		inRequest = true;
		String category = spinner_category.getSelectedItem().toString();
		String url = BASE_URL + PHOTO_API 
				+ "&consumer_key=" + CONSUMER_KEY 
				+ "&page=" + PAGE_NUM 
				+ "&only=" + category 
				+ "&rpp=" + MAX_IMAGE;
		request(url, 1);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if(previousCat != arg2)
		{
			previousCat = arg2;
			initGallery();
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if(photos != null && photos.size() > 0 && firstVisibleItem + visibleItemCount >= totalItemCount-3 && photos.size() < 600)
			getPhotos();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
	}
}
