package com.example.fivehundredpx;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml.Encoding;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoginActivity extends BaseActivity{

	WebView webview;
	String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		webview = (WebView)findViewById(R.oauth_id.web_oauth);
	}
	
	public void onResume()
    {
		super.onResume();

	    OAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
	    OAuthProvider provider = new CommonsHttpOAuthProvider(REQUEST_URL, ACCESS_URL, AUTHORIZE_URL);
	   
	    new OAuthRequestTokenTask(consumer,provider).execute();
    }
	
	public class OAuthRequestTokenTask extends AsyncTask<Void, Void, Void> {
		 
        private OAuthProvider provider;
        private OAuthConsumer consumer;

        public OAuthRequestTokenTask(OAuthConsumer consumer, OAuthProvider provider) {
            this.consumer = consumer;
            this.provider = provider;
        }

        @SuppressLint("SetJavaScriptEnabled")
		@Override
        protected Void doInBackground(Void... params) {
            try {
            	String msg="<h3><i>Requesting OAuth Token...</i></h3>";
            	webview.loadDataWithBaseURL("", msg, "text/html", Encoding.UTF_8.toString(),""); 
                
                final String url = provider.retrieveRequestToken(consumer, OAUTH_CALLBACK_URL);
                webview.setWebViewClient(new WebViewClient(){
    				@Override
    				public void onPageStarted(WebView view, String url, Bitmap favicon)
    				{
    					Uri furl = Uri.parse(url);
    				    if (furl.getHost().equalsIgnoreCase(Uri.parse(OAUTH_CALLBACK_URL).getHost()))
    				    {
    				    	new OAuthAccessTokenTask(consumer, provider).execute(furl);
    				    	webview.setVisibility(View.GONE);
    				    }
    				    else
    				    {
    				        super.onPageStarted(view, url, favicon);
    				    }           
    				}
    			});
    			webview.getSettings().setJavaScriptEnabled(true);
    			webview.loadUrl(url);
    			mDialog.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
	}
	
	public class OAuthAccessTokenTask extends AsyncTask<Uri, Void, Void> {
		 
        private OAuthProvider provider;
        private OAuthConsumer consumer;

        public OAuthAccessTokenTask(OAuthConsumer consumer, OAuthProvider provider) {
            this.consumer = consumer;
            this.provider = provider;
        }

        @Override
        protected Void doInBackground(Uri...params) {
        	final Uri uri = params[0];

        	final String oauth_verifier = uri.getQueryParameter("oauth_verifier");

        	try {
        		provider.retrieveAccessToken(consumer, oauth_verifier);

        		String token = consumer.getToken();
        		String secret = consumer.getTokenSecret();

        		consumer.setTokenWithSecret(token, secret);
        		
        		isLoggedIn = true;
        		LoginActivity.this.onBackPressed();

        	} catch (Exception e) {
        		e.printStackTrace();
        	}

        	return null;
        }
	}
}
