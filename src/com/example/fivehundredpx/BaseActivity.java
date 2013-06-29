package com.example.fivehundredpx;

import java.util.WeakHashMap;

import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public class BaseActivity extends Activity{

	protected static final String BASE_URL = "https://api.500px.com/v1/";
	
	protected static final String REQUEST_URL = BASE_URL+"oauth/request_token";
	protected static final String ACCESS_URL = BASE_URL+"oauth/access_token";
	protected static final String AUTHORIZE_URL = BASE_URL+"oauth/authorize";
	
	protected static final String CONSUMER_KEY = "Enter your consumer key";
	protected static final String CONSUMER_SECRET= "Enter your consumer secret";
	
	protected static final String OAUTH_CALLBACK_URL = "http://localhost";
	
	protected static ProgressDialog mDialog;
	protected static boolean isLoggedIn = false;
	Menu menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	protected void onResume()
	{
		super.onResume();
		createMenu();
	}
	
	private void createMenu()
	{
		if(menu != null)
		{
			if(isLoggedIn)
	        {
	        	menu.findItem(R.menu_id.action_login).setVisible(false);
	        	menu.findItem(R.menu_id.action_logout).setVisible(true);
	        }
	        else
	        {
	        	menu.findItem(R.menu_id.action_logout).setVisible(false);
	        	menu.findItem(R.menu_id.action_login).setVisible(true);
	        }
		}
	}
	protected void logout()
	{
		isLoggedIn = false;
		createMenu();
	}
	@Override
    public void onBackPressed() 
    {
        this.finish();
        overridePendingTransition  (R.animator.right_slide_in2, R.animator.right_slide_out2);
        return;
    }
	
	protected void changeActivity(Class<?> cls, WeakHashMap<String,String> data)
	{
		this.changeActivity(cls, data, false);
	}
	
	protected void changeActivity(Class<?> cls, WeakHashMap<String,String> data, boolean reverse)
	{
		Intent i = new Intent(getApplicationContext(), cls);
		if(data != null)
		{
			for(String key:data.keySet())
			{
				i.putExtra(key, data.get(key));
			}
		}
    	startActivity(i);
    	if(reverse)
    		overridePendingTransition  (R.animator.right_slide_in2, R.animator.right_slide_out2);
    	else
    		overridePendingTransition  (R.animator.right_slide_in, R.animator.right_slide_out);
	}
	
	protected void request(final String url, final int id, RequestParams params)
	{
		request(url, id,true, params);
	}
	
	protected void request(final String url,final int id)
	{
		request(url, id, false, null);
	}
	
	protected void request(final String url,final int id, boolean isPost, RequestParams params)
	{
		AsyncHttpClient client = new AsyncHttpClient();
		mDialog = new ProgressDialog(BaseActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        
		AsyncHttpResponseHandler rpHandler = new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) 
		    {
		    	mDialog.dismiss();
		    	response(response,id);
		    }
		    
		    @Override
		    public void onFailure(Throwable arg0, String arg1)
		    {
		    	mDialog.dismiss();
		    	requestFailed(arg1,id);
		    }
		};
		if(isPost)
			client.post(url, params, rpHandler);
		else
			client.get(url, rpHandler);
	}
	
	protected void response(Object response, int id)
	{
		// Override action
	}
	
	protected void requestFailed(String msg, int id)
	{
		// Override action
	}
	
	protected void ShowMessage(String title, String msg, Activity activity)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
 
			alertDialogBuilder.setTitle(title);
 
			alertDialogBuilder.setMessage(msg);
			alertDialogBuilder
				.setCancelable(false)
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	protected String get(JSONObject js, String name, String defaultVal)
	{
		try
		{
			return js.get(name).toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return defaultVal;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		createMenu();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		onBackPressed();
	    		return true;
	        case R.menu_id.action_login:
	            login();
	            return true;
	        case R.menu_id.action_logout:
	            logout();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	protected void login()
	{
		this.changeActivity(LoginActivity.class, null);
	}
}
