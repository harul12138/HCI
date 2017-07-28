package com.example.hci2;

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SensorView extends Activity {
	
	//感应器
	public static int state = 0;
    public static SensorManager sensorManager;
    public static MySensorEventListener sensorEventListener;
    public static Sensor accelerometerSensor;
    public static Sensor gravitySensor;
    public static Sensor orientationSensor;
    private Toast mToast;
    private ImageView imageView;//清明上河图
    //变换矩阵
    private Matrix matrix = new Matrix();
    private float last_x = 0f;
    private float last_y = 0f;
    //特殊点的获取
	private PointF startPoint = new PointF();
	private Matrix currentMaritx = new Matrix();
	
	private int mode = 0;//用于标记模式
	private static final int NONE = 0;//拖动
	private static final int DRAG = 1;//拖动
	private static final int ZOOM = 2;//放大
	private float startDis = 0;
	private PointF midPoint;//中心点
	private static float SCALE = 10000;//记录当前的放大倍数
	 //保存上一次感应器 x y z 的坐标
    private float bx;
    private float by;
    private float bz;
    private long btime;//这一次的时间
    //sound
    private SoundPool soundPool;
    private HashMap musicId=new HashMap();//定义一个HashMap用于存放音频流的ID
    //场景定位
    private float[][] sceneInform=new float[4][4];
    private int play_num = 0;
    private float max_speed = 1.0f;
    private boolean var_speed_broad = false;
    private boolean left = false, right = false, flag = false;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor_layout);
		TextView tv = (TextView) this.findViewById(R.id.menu_text);  
		tv.setHeight(30);
        //把文字控件添加监听，点击弹出自定义窗口  
        tv.setOnClickListener(new OnClickListener() {             
            public void onClick(View v) {  
            	Intent intent = new Intent(); 
            	intent.setClass(SensorView.this, SelectWindow.class);
            	startActivityForResult(intent, 1); 
            	//soundPool.autoPause();
            }  
        }); 
      

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        
        /**
         * 感应器 
         */
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取感应器管理器
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorEventListener = new MySensorEventListener();
        //图片触摸感应
        imageView = (ImageView) findViewById(R.id.backimage);
        imageView.setOnTouchListener(new TounchListener());
        
        //声音
        //新建音频播放池
        soundPool= new SoundPool(2,AudioManager.STREAM_MUSIC,0);
    	musicId.put(1, soundPool.load(this, R.raw.water1, 1));
    	musicId.put(2, soundPool.load(this, R.raw.bird, 1));
    	musicId.put(3, soundPool.load(this, R.raw.noise1, 1));
    	musicId.put(4, soundPool.load(this, R.raw.wind1, 1));
    	//soundPool.setLoop(1, -1);
    	
    	  //流水声
        sceneInform[0][0]=-1400f;
        sceneInform[0][1]=-0f;
        sceneInform[0][2]=1f;//id
        sceneInform[0][3]=0f;//isPlaying 0 no; 1 yes
        //鸟声
        sceneInform[1][0]=-2050f;
        sceneInform[1][1]=50f;
        sceneInform[1][2]=2f;//id
        sceneInform[1][3]=0f;
        //嘈杂声
        sceneInform[2][0]=-225f;
        sceneInform[2][1]=-80f;
        sceneInform[2][2]=3f;
        sceneInform[2][3]=0f;
        //风声
        sceneInform[3][0]=-1600f;
        sceneInform[3][1]= -80f;
        sceneInform[3][2]=4f;
        sceneInform[3][3]=0f;
        
        //设置背景图片初始状态
        matrix.set(imageView.getImageMatrix());
       // matrix.postScale(2, 2, midPoint.x, midPoint.y);
        //matrix.postScale(2, 2);
		//matrix.setTranslate(-500, -50);
		imageView.setImageMatrix(matrix);
		matrix.set(imageView.getImageMatrix());
		
		imageView.setImageMatrix(matrix);
       // Intent intent = this.getIntent(); 
       // state = intent.getIntExtra("state", 1); 
        
	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	super.onActivityResult(requestCode, resultCode, data); 
	//当otherActivity中返回数据的时候，会响应此方法 
	//requestCode和resultCode必须与请求startActivityForResult()和返回setResult()的时候传入的值一致。 
	 //Toast.makeText(getApplicationContext(), resultCode+"",   
          //   Toast.LENGTH_SHORT).show();  
	switch(resultCode){
	case 1:
		sensorManager.registerListener(SensorView.sensorEventListener, SensorView.gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.unregisterListener(SensorView.sensorEventListener, SensorView.accelerometerSensor);
		sensorManager.unregisterListener(SensorView.sensorEventListener, SensorView.orientationSensor);
		var_speed_broad = false;
		break;
	case 2:
		sensorManager.registerListener(SensorView.sensorEventListener, SensorView.orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.unregisterListener(SensorView.sensorEventListener, SensorView.accelerometerSensor);
		sensorManager.unregisterListener(SensorView.sensorEventListener, SensorView.gravitySensor);
		play_num = 1;
		var_speed_broad = false;
		//float dis_x=sceneInform[0][0]-last_x;
		//float dis_y=sceneInform[0][1]-last_y;
		//showTip("last_x:"+last_x+"dis_x:"+dis_x);
		
		soundPool.autoPause();
		last_x=sceneInform[0][0];
		last_y=sceneInform[0][1];
		sceneInform[0][3]= 1f;
		
		matrix.set(imageView.getImageMatrix());
		matrix.setScale(3, 3);
		matrix.setTranslate(sceneInform[0][0], sceneInform[0][1]);
		imageView.setImageMatrix(matrix);
		soundPool.play(play_num, 0.2f, 0.2f, 0, -1, 1);
		break;
	case 3:
		sensorManager.registerListener(SensorView.sensorEventListener, SensorView.accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.unregisterListener(SensorView.sensorEventListener, SensorView.gravitySensor);
		sensorManager.unregisterListener(SensorView.sensorEventListener, SensorView.orientationSensor);
		play_num = 2;
		var_speed_broad = true;
		//float dis_x2=sceneInform[0][0]-last_x;
		//float dis_y2=sceneInform[0][1]-last_y;
		//showTip(""+last_x);
		
		soundPool.autoPause();
		last_x=sceneInform[1][0];
		last_y=sceneInform[1][1];
		sceneInform[1][3]= 1f;
		
		matrix.set(imageView.getImageMatrix());
		matrix.setScale(3, 3);
		matrix.setTranslate(sceneInform[1][0], sceneInform[1][1]);
		
		imageView.setImageMatrix(matrix);
		soundPool.play(play_num, 0.4f, 0.4f, 0, -1, 1);
		
		break;
	case 4:
		sensorManager.unregisterListener(SensorView.sensorEventListener, SensorView.accelerometerSensor);
		sensorManager.unregisterListener(SensorView.sensorEventListener, SensorView.gravitySensor);
		sensorManager.unregisterListener(SensorView.sensorEventListener, SensorView.orientationSensor);
		//soundPool.play(play_num, 0.2f, 0.2f, 0, -1, 1);
		var_speed_broad = false;
		soundPool.autoPause();
		break;
		default:
			sensorManager.unregisterListener(SensorView.sensorEventListener, SensorView.accelerometerSensor);
			sensorManager.unregisterListener(SensorView.sensorEventListener, SensorView.gravitySensor);
			sensorManager.unregisterListener(SensorView.sensorEventListener, SensorView.orientationSensor);
			//soundPool.play(play_num, 0.2f, 0.2f, 0, -1, 1);
			var_speed_broad = false;
			soundPool.autoPause();
			break;
	}
	}
	/*if(requestCode==1&&resultCode==1) 
	{ 
		
		//int three = data.getIntExtra("three", 0); 
	    //state = three;
	} else if(requestCode==1&&resultCode==2)
	{
		state = 2;
	}else if(requestCode==1&&resultCode==3){
		state = 3;
	}
	} */
	
	  //移动操作
		public void Up(){
				float dx=0f;
				float dy=-2f;
				last_x=last_x+dx;
				last_y=last_y+dy;
				matrix.set(imageView.getImageMatrix());
				matrix.postTranslate(dx, dy);
				imageView.setImageMatrix(matrix);
		}
		
		public void Down(){
				float dx=0f;
				float dy=2f;
				last_x=last_x+dx;
				last_y=last_y+dy;
				matrix.set(imageView.getImageMatrix());
				matrix.postTranslate(dx, dy);
				imageView.setImageMatrix(matrix);
		}
		
		public void Left(){
				float dx=-2f;
				float dy=0f;
				last_x=last_x+dx;
				last_y=last_y+dy;
				matrix.set(imageView.getImageMatrix());
				matrix.postTranslate(dx, dy);
				imageView.setImageMatrix(matrix);
		}
		
		public void Right(){
				float dx=2f;
				float dy=0f;
				last_x=last_x+dx;
				last_y=last_y+dy;
				matrix.set(imageView.getImageMatrix());
				matrix.postTranslate(dx, dy);
				imageView.setImageMatrix(matrix);
		}
		

private final class MySensorEventListener implements SensorEventListener{
	
	private float maxSpeed(float speedX, float speedY, float speedZ){
		float max=0;
		if(speedX>speedY){
			max=speedX;
		}
		else{
			max=speedY;
		}
		if(max>speedZ){
			return max;
		}
		else{
			max=speedZ;
			return max;
		}
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {//可以得到传感器实时测量出来的变化值
		if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
			/**
			 * 方向感应器
			 */
			sensorManager.registerListener(SensorView.sensorEventListener, SensorView.orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
			float x = event.values[SensorManager.DATA_X];      
			float y = event.values[SensorManager.DATA_Y];      
        	float z = event.values[SensorManager.DATA_Z];
        	//showTip("fdfsdfsdfsd,,,,,,f");
        	int id=playingMusicId();
			if(z>15 && z<30){
				if(id>0){
				right = true;
				left = false;
					//soundPool.setVolume(play_num, 1, 1/100);
					if(right && !flag)
					{
						soundPool.autoPause();
					soundPool.play(play_num, 1.0f, 0.1f, 0, 1, 1.0f);
					soundPool.setVolume(play_num, 0.7f, 0);
					flag = true;
					//showTip("fdfsdfsdfsdf"+play_num);
					}
					
				}
				else{
					showTip("There's no music!");
				}
			}else if(z<-15 && z>-30){
				float z2=Math.abs(z);
				if(id>0){
					//soundPool.setVolume(play_num, 1/100, 1);
					right = false;
					left = true;
					if(left && flag){
						soundPool.autoPause();
					soundPool.play(play_num, 0.1f, 1.0f, 0, 1, 1.0f);
					flag = false;
					//showTip("-------");
					}
					//showTip(""+id);
				}
				else{
					showTip("There's no music!");
				}
			}
			//else
				//soundPool.play(play_num, 0.3f, 0.3f, 0, -1, 1.0f);
				//soundPool.setVolume(play_num, 0.3f, 0.3f);

		}else if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			/**
			 * 加速度感应器
			 */
			sensorManager.registerListener(SensorView.sensorEventListener, SensorView.accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
			float x = event.values[SensorManager.DATA_X];      
			float y = event.values[SensorManager.DATA_Y];      
        	float z = event.values[SensorManager.DATA_Z]; 
        	
        	//X轴的速度
        	float speedX = (x - bx)*1000 / (System.currentTimeMillis() - btime);
        	//y轴的速度
        	float speedY = (y - by)*1000 / (System.currentTimeMillis() - btime);
        	//z轴的速度
        	float speedZ = (z - bz)*1000 / (System.currentTimeMillis() - btime);
        	
        	bx = x;
        	by = y;
        	bz = z;
        	btime = System.currentTimeMillis();
        	
        	int id=playingMusicId();
        	//soundPool.autoPause();
        	//soundPool.play(play_num, 0.3f, 0.3f, 0, -1, max_speed);
        	//showTip("msdfdf"+max_speed);
        	if(speedX<=10 && speedY<=10 && speedZ<=10){
        		if(id>0){
        			//max_speed = 1.0f;
					soundPool.setRate(play_num, 1.0f);
        			//soundPool.autoPause();
        			//soundPool.play(play_num, 0.3f, 0.3f, 0, -1, 1.0f);
					//showTip("摇晃速度过小，正常播放！");
				}
        		else{
        			showTip("There's no music!");
        		}
        	}
        	else{
        		if(speedX<120 && speedY<120 && speedZ<120){
        			float maxspeed=maxSpeed(speedX, speedY, speedZ);
        			if(id>0){
        				float rate=(maxspeed-10)/55f;
        				//showTip("speed为："+rate);
        				if(max_speed < rate)
        					max_speed = rate;
        				soundPool.play(play_num, 0.4f, 0.4f, 0, -1, rate);
						//soundPool.setRate(play_num, rate);
        				//soundPool.play(play_num, 0.3f, 0.3f, 0, -1, rate);
					}
	        		else{
	        			showTip("There's no music!");
	        		}
        		}
        		else{
        			showTip("您摇晃地太快.....");
        		}
        	}
		}else if(event.sensor.getType()==Sensor.TYPE_GRAVITY){
			/**
			 * 重力感应器
			 */
			SensorView.sensorManager.registerListener(SensorView.sensorEventListener, SensorView.gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
			float x = event.values[SensorManager.DATA_X];      
			float y = event.values[SensorManager.DATA_Y];      
        	float z = event.values[SensorManager.DATA_Z];
        	
        	//left
        	if(x>3 && (y<2&&y>-2)){
        		showTip("图片左移");
        		Left();
        	}
        	//right
        	if(x<-3 && (y>-2&&y<2)){
        		showTip("图片右移");
        		Right();
        	}
        	//down
        	if((x>-2&&x<2) && y>3){
        		showTip("图片下移");
        		Down();
        	}
        	//up
        	if((x>-2&&x<2) && y<-3){
        		showTip("图片上移");
        		Up();
        	}
		}
	
	}

	@Override
	public void onAccuracyChanged(android.hardware.Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}

private int playingMusicId() {
	int a=-1;
	for(int k=0; k<sceneInform.length; k++){
		if(sceneInform[k][3]==1f){
			a=k+1;
		}
	}
	return a;
}
@Override  
public boolean onKeyDown(int keyCode, KeyEvent event)  
{  
    if (keyCode == KeyEvent.KEYCODE_BACK )  
    {  
        // 创建退出对话框  
        AlertDialog isExit = new AlertDialog.Builder(this).create();  
        // 设置对话框标题  
        isExit.setTitle("系统提示");  
        // 设置对话框消息  
        isExit.setMessage("确定要退出吗");  
        // 添加选择按钮并注册监听  
        isExit.setButton("确定", listener);  
        isExit.setButton2("取消", listener);  
        // 显示对话框  
        isExit.show();  

    }  
      
    return false;  
      
}  
/**监听对话框里面的button点击事件*/  
DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()  
{  
    public void onClick(DialogInterface dialog, int which)  
    {  
        switch (which)  
        {  
        case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序  
            System.exit(0);  
            soundPool.release();
            break;  
        case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框  
            break;  
        default:  
            break;  
        }  
    }  
};    


private class TounchListener implements OnTouchListener{
	private boolean zoom_in=false;
	
	@SuppressLint("ShowToast")
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			currentMaritx.set(imageView.getImageMatrix());//记录ImageView当期的移动位置
			startPoint.set(event.getX(),event.getY());//开始点
			break;

		case MotionEvent.ACTION_MOVE://移动事件
			
			soundPool.play(play_num, 0.2f, 0.2f, 0, -1, 1);
			for(int i = 0 ; i < sceneInform.length; i++)
				sceneInform[i][3] = 0f;
			
			if (mode == DRAG) {//图片拖动事件
				float dx = event.getX() - startPoint.x;//x轴移动距离
				float dy = event.getY() - startPoint.y;
				matrix.set(currentMaritx);//在当前的位置基础上移动
				matrix.postTranslate(dx, dy);
				//showTip("last_x"+last_x+"last_y"+last_y+"play_num:"+play_num);
				if(last_x > -1600 && last_x < -1400)
				{
					play_num = 1;
					sceneInform[play_num-1][3] = 1f;
				}else if(last_x > -2250 && last_x < -2050 ){
					play_num = 2;
					sceneInform[play_num-1][3] = 1f;
				}else if(last_x > -250 && last_x <-110){
					play_num = 3;
					sceneInform[play_num-1][3] = 1f;
				}else if(last_x > -1000 && last_x <-800){
					play_num = 4;
					sceneInform[play_num-1][3] = 1f;
				}
				else {
					play_num = 0;
					/*for(int i = 0 ; i < sceneInform.length; i++)
						sceneInform[i][3] = 0f;*/
					soundPool.autoPause();
				}
			}
			else if(mode == ZOOM){//图片放大事件
				float endDis = distance(event);//结束距离
				if(endDis > 10f){
					float scale = endDis / startDis;//放大倍数
					matrix.set(currentMaritx);
					matrix.postScale(scale, scale, midPoint.x, midPoint.y);
					if(endDis > 150f && !zoom_in){
						zoom_in=true;
					}
				}
			}
			break;
			
		case MotionEvent.ACTION_UP:
			float x=event.getX() - startPoint.x;
			float y=event.getY() - startPoint.y;
			last_x=last_x+x;
			last_y=last_y+y;
			mode = NONE;
			break;
		//有手指离开屏幕，但屏幕还有触点(手指)
		case MotionEvent.ACTION_POINTER_UP:
			mode = DRAG;
			break;
		//当屏幕上已经有触点（手指）,再有一个手指压下屏幕
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			startDis = distance(event);
			
			if(startDis > 10f){//避免手指上有两个茧
				midPoint = mid(event);
				currentMaritx.set(imageView.getImageMatrix());//记录当前的缩放倍数
			}
			break;

		}
		
		imageView.setImageMatrix(matrix);
		return true;
	}
	
}
/**
 * 两点之间的距离
 */
private static float distance(MotionEvent event){
	//两根线的距离
	float dx = event.getX(1) - event.getX(0);
	float dy = event.getY(1) - event.getY(0);
	return FloatMath.sqrt(dx*dx + dy*dy);
}
/**
 * 计算两点之间中心点的距离
 */
private static PointF mid(MotionEvent event){
	float midx = event.getX(1) + event.getX(0);
	float midy = event.getY(1) - event.getY(0);
	
	return new PointF(midx/2, midy/2);
}
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
}
