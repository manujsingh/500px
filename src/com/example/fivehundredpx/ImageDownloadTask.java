package com.example.fivehundredpx;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageDownloadTask extends AsyncTask<ImageView, Void, Bitmap> {

    ImageView imgView = null;

    @Override
    protected Bitmap doInBackground(ImageView... imageViews) {
        this.imgView = imageViews[0];
        return downloadImage((String)imgView.getTag());
    }

    @Override
    protected void onPostExecute(Bitmap bmp) {
    	GalleryActivity.imageCache.put(imgView.getTag().toString(), bmp);
    	imgView.setImageBitmap(bmp);
    }

    private Bitmap downloadImage(String url) {

        Bitmap bmp = null;
        try{
            URL aUrl = new URL(url);
            HttpURLConnection aConnection = (HttpURLConnection)aUrl.openConnection();
            InputStream is = aConnection.getInputStream();
            bmp = BitmapFactory.decodeStream(is);
            if (null != bmp)
                return bmp;

            }catch(Exception e){}
        return bmp;
    }
}
