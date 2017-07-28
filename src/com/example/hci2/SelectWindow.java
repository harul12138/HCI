package com.example.hci2;

//import com.iflytek.voicedemo.SensorViewActivity.MySensorEventListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SelectWindow extends Activity implements OnClickListener{
	  
    private Button btn_gravity, btn_direction, btn_acceleration;//感应器选择按钮  
    private LinearLayout layout;
    private Toast mToast;
  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.select_layout);  
        btn_gravity = (Button) this.findViewById(R.id.btn_gravity);  
        btn_direction = (Button) this.findViewById(R.id.btn_direction);  
        btn_acceleration = (Button) this.findViewById(R.id.btn_acceleration);  
          
        layout=(LinearLayout)findViewById(R.id.pop_layout);  
          
        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity  
        layout.setOnClickListener(new OnClickListener() {  
              
            public void onClick(View v) {  
                // TODO Auto-generated method stub  
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",   
                        Toast.LENGTH_SHORT).show();   
            }  
        });  
    

		//设置监听，进入"多点触摸"界面
		btn_gravity.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {  
	        	Intent i=new Intent(SelectWindow.this,SensorView.class); 
	        	//i.putExtra("three", 1); 
	        	setResult(1, i);//设置resultCode，onActivityResult()中能获取到 
	        	finish();
			}
		});
		//设置监听，进入"多点触摸"界面
		btn_direction.setOnClickListener(new OnClickListener() {			
					@Override
					public void onClick(View v) {  
			        	Intent i=new Intent(SelectWindow.this,SensorView.class); 
			        	//i.putExtra("three", 1); 
			        	setResult(2, i);//设置resultCode，onActivityResult()中能获取到 
			        	finish();
					}
				});
		btn_acceleration.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {  
	        	Intent i=new Intent(SelectWindow.this,SensorView.class); 
	        	//i.putExtra("three", 1); 
	        	setResult(3, i);//设置resultCode，onActivityResult()中能获取到 
	        	finish();
			}
		});
    }  
      
    //实现onTouchEvent触屏函数但点击屏幕时销毁本Activity  
    @Override  
    public boolean onTouchEvent(MotionEvent event){  
    	Intent i=new Intent(SelectWindow.this,SensorView.class); 
    	//i.putExtra("three", 1); 
    	setResult(4, i);//设置resultCode，onActivityResult()中能获取到 
    	finish();  
        return true;  
    }
  
   /* public void onClick(View v) {  
        switch (v.getId()) { 
        
        case R.id.btn_gravity: 
        	//SensorView.statew = 1;
        	//新声明一个Intent用于存放放回的数据 
        	 Toast.makeText(getApplicationContext(), "11111",   
                     Toast.LENGTH_SHORT).show();   
        	Intent i=new Intent(SelectWindow.this,SensorView.class); 
        	//i.putExtra("three", 1); 
        	setResult(1, i);//设置resultCode，onActivityResult()中能获取到 
        	finish();
        	Toast.makeText(getApplicationContext(), "00000",   
                     Toast.LENGTH_SHORT).show();   
            break;  
        case R.id.btn_direction:                 
            break;  
        case R.id.btn_acceleration:                 
            break;  
        default: 
        	Toast.makeText(getApplicationContext(), "11111",   
                    Toast.LENGTH_SHORT).show();   
            break;  
        }  
        finish();  
    } */
    //一些提示信息
	private void showTip(final String str)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mToast.setText(str);
				mToast.show();
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
      
}  