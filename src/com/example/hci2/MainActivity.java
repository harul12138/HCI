package com.example.hci2;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	//定义Button按钮对象
	private Button btn1,btn2,btn3,btn4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	
	}

	/**
	 * 初始化组件
	 */
	private void initView(){
		//实例化按钮对象
		btn1 = (Button)findViewById(R.id.button1);
		btn2 = (Button)findViewById(R.id.button2);
		btn3 = (Button)findViewById(R.id.button3);
		btn4 = (Button)findViewById(R.id.button4);
		btn1.setHeight(50);
		btn2.setHeight(50);
		btn3.setHeight(50);
		btn4.setHeight(50);

		//设置监听，进入"多点触摸"界面
		btn1.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,ImageActivity.class));
			}
		});
		
		//设置监听，进入"语音输入"界面
		btn2.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,CustomTabActivity2.class));
			}
		});
		
		//设置监听，进入"语音输出"界面
		btn4.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				android.os.Process.killProcess(android.os.Process.myPid());    //获取PID 
				System.exit(0);
			}
		});
		
		//设置监听，进入"感应器"界面
		btn3.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SensorView.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
				startActivity(intent);
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
  
}
