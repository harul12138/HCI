package com.example.hci2;

import java.io.InputStream;

//import com.iflytek.voicedemo.MyImageView;
//import com.iflytek.voicedemo.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;

public class ImageActivity extends Activity {
	static int window_width;
	static int window_height;
	
	@SuppressLint("InlinedApi") @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//full window
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
		
		DisplayMetrics dm=new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);//get the pixels of the window
		window_width=dm.widthPixels;
		window_height=dm.heightPixels;
		
		BitmapFactory.Options option=new BitmapFactory.Options();
	   // option.inJustDecodeBounds = true; 
		option.inPreferredConfig = Bitmap.Config.RGB_565;
		option.inPurgeable = true;
		option.inInputShareable = true;
		option.inSampleSize=2;
		InputStream inputstream = getResources().openRawResource(R.drawable.background);
		Bitmap bitmap = BitmapFactory.decodeStream(inputstream,null,option);//the size of picture changed with the windows

		MyImageView myImageView = new MyImageView(this, bitmap);
	    setContentView(myImageView);
		
		
		
	}
	

}
