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
	  
    private Button btn_gravity, btn_direction, btn_acceleration;//��Ӧ��ѡ��ť  
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
          
        //���ѡ�񴰿ڷ�Χ�����������Ȼ�ȡ���㣬������ִ��onTouchEvent()��������������ط�ʱִ��onTouchEvent()��������Activity  
        layout.setOnClickListener(new OnClickListener() {  
              
            public void onClick(View v) {  
                // TODO Auto-generated method stub  
                Toast.makeText(getApplicationContext(), "��ʾ����������ⲿ�رմ��ڣ�",   
                        Toast.LENGTH_SHORT).show();   
            }  
        });  
    

		//���ü���������"��㴥��"����
		btn_gravity.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {  
	        	Intent i=new Intent(SelectWindow.this,SensorView.class); 
	        	//i.putExtra("three", 1); 
	        	setResult(1, i);//����resultCode��onActivityResult()���ܻ�ȡ�� 
	        	finish();
			}
		});
		//���ü���������"��㴥��"����
		btn_direction.setOnClickListener(new OnClickListener() {			
					@Override
					public void onClick(View v) {  
			        	Intent i=new Intent(SelectWindow.this,SensorView.class); 
			        	//i.putExtra("three", 1); 
			        	setResult(2, i);//����resultCode��onActivityResult()���ܻ�ȡ�� 
			        	finish();
					}
				});
		btn_acceleration.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {  
	        	Intent i=new Intent(SelectWindow.this,SensorView.class); 
	        	//i.putExtra("three", 1); 
	        	setResult(3, i);//����resultCode��onActivityResult()���ܻ�ȡ�� 
	        	finish();
			}
		});
    }  
      
    //ʵ��onTouchEvent���������������Ļʱ���ٱ�Activity  
    @Override  
    public boolean onTouchEvent(MotionEvent event){  
    	Intent i=new Intent(SelectWindow.this,SensorView.class); 
    	//i.putExtra("three", 1); 
    	setResult(4, i);//����resultCode��onActivityResult()���ܻ�ȡ�� 
    	finish();  
        return true;  
    }
  
   /* public void onClick(View v) {  
        switch (v.getId()) { 
        
        case R.id.btn_gravity: 
        	//SensorView.statew = 1;
        	//������һ��Intent���ڴ�ŷŻص����� 
        	 Toast.makeText(getApplicationContext(), "11111",   
                     Toast.LENGTH_SHORT).show();   
        	Intent i=new Intent(SelectWindow.this,SensorView.class); 
        	//i.putExtra("three", 1); 
        	setResult(1, i);//����resultCode��onActivityResult()���ܻ�ȡ�� 
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
    //һЩ��ʾ��Ϣ
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