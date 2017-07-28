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
	
	//��Ӧ��
	public static int state = 0;
    public static SensorManager sensorManager;
    public static MySensorEventListener sensorEventListener;
    public static Sensor accelerometerSensor;
    public static Sensor gravitySensor;
    public static Sensor orientationSensor;
    private Toast mToast;
    private ImageView imageView;//�����Ϻ�ͼ
    //�任����
    private Matrix matrix = new Matrix();
    private float last_x = 0f;
    private float last_y = 0f;
    //�����Ļ�ȡ
	private PointF startPoint = new PointF();
	private Matrix currentMaritx = new Matrix();
	
	private int mode = 0;//���ڱ��ģʽ
	private static final int NONE = 0;//�϶�
	private static final int DRAG = 1;//�϶�
	private static final int ZOOM = 2;//�Ŵ�
	private float startDis = 0;
	private PointF midPoint;//���ĵ�
	private static float SCALE = 10000;//��¼��ǰ�ķŴ���
	 //������һ�θ�Ӧ�� x y z ������
    private float bx;
    private float by;
    private float bz;
    private long btime;//��һ�ε�ʱ��
    //sound
    private SoundPool soundPool;
    private HashMap musicId=new HashMap();//����һ��HashMap���ڴ����Ƶ����ID
    //������λ
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
        //�����ֿؼ���Ӽ�������������Զ��崰��  
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
         * ��Ӧ�� 
         */
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//��ȡ��Ӧ��������
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorEventListener = new MySensorEventListener();
        //ͼƬ������Ӧ
        imageView = (ImageView) findViewById(R.id.backimage);
        imageView.setOnTouchListener(new TounchListener());
        
        //����
        //�½���Ƶ���ų�
        soundPool= new SoundPool(2,AudioManager.STREAM_MUSIC,0);
    	musicId.put(1, soundPool.load(this, R.raw.water1, 1));
    	musicId.put(2, soundPool.load(this, R.raw.bird, 1));
    	musicId.put(3, soundPool.load(this, R.raw.noise1, 1));
    	musicId.put(4, soundPool.load(this, R.raw.wind1, 1));
    	//soundPool.setLoop(1, -1);
    	
    	  //��ˮ��
        sceneInform[0][0]=-1400f;
        sceneInform[0][1]=-0f;
        sceneInform[0][2]=1f;//id
        sceneInform[0][3]=0f;//isPlaying 0 no; 1 yes
        //����
        sceneInform[1][0]=-2050f;
        sceneInform[1][1]=50f;
        sceneInform[1][2]=2f;//id
        sceneInform[1][3]=0f;
        //������
        sceneInform[2][0]=-225f;
        sceneInform[2][1]=-80f;
        sceneInform[2][2]=3f;
        sceneInform[2][3]=0f;
        //����
        sceneInform[3][0]=-1600f;
        sceneInform[3][1]= -80f;
        sceneInform[3][2]=4f;
        sceneInform[3][3]=0f;
        
        //���ñ���ͼƬ��ʼ״̬
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
	//��otherActivity�з������ݵ�ʱ�򣬻���Ӧ�˷��� 
	//requestCode��resultCode����������startActivityForResult()�ͷ���setResult()��ʱ�����ֵһ�¡� 
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
	
	  //�ƶ�����
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
	public void onSensorChanged(SensorEvent event) {//���Եõ�������ʵʱ���������ı仯ֵ
		if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
			/**
			 * �����Ӧ��
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
			 * ���ٶȸ�Ӧ��
			 */
			sensorManager.registerListener(SensorView.sensorEventListener, SensorView.accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
			float x = event.values[SensorManager.DATA_X];      
			float y = event.values[SensorManager.DATA_Y];      
        	float z = event.values[SensorManager.DATA_Z]; 
        	
        	//X����ٶ�
        	float speedX = (x - bx)*1000 / (System.currentTimeMillis() - btime);
        	//y����ٶ�
        	float speedY = (y - by)*1000 / (System.currentTimeMillis() - btime);
        	//z����ٶ�
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
					//showTip("ҡ���ٶȹ�С���������ţ�");
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
        				//showTip("speedΪ��"+rate);
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
        			showTip("��ҡ�ε�̫��.....");
        		}
        	}
		}else if(event.sensor.getType()==Sensor.TYPE_GRAVITY){
			/**
			 * ������Ӧ��
			 */
			SensorView.sensorManager.registerListener(SensorView.sensorEventListener, SensorView.gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
			float x = event.values[SensorManager.DATA_X];      
			float y = event.values[SensorManager.DATA_Y];      
        	float z = event.values[SensorManager.DATA_Z];
        	
        	//left
        	if(x>3 && (y<2&&y>-2)){
        		showTip("ͼƬ����");
        		Left();
        	}
        	//right
        	if(x<-3 && (y>-2&&y<2)){
        		showTip("ͼƬ����");
        		Right();
        	}
        	//down
        	if((x>-2&&x<2) && y>3){
        		showTip("ͼƬ����");
        		Down();
        	}
        	//up
        	if((x>-2&&x<2) && y<-3){
        		showTip("ͼƬ����");
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
        // �����˳��Ի���  
        AlertDialog isExit = new AlertDialog.Builder(this).create();  
        // ���öԻ������  
        isExit.setTitle("ϵͳ��ʾ");  
        // ���öԻ�����Ϣ  
        isExit.setMessage("ȷ��Ҫ�˳���");  
        // ���ѡ��ť��ע�����  
        isExit.setButton("ȷ��", listener);  
        isExit.setButton2("ȡ��", listener);  
        // ��ʾ�Ի���  
        isExit.show();  

    }  
      
    return false;  
      
}  
/**�����Ի��������button����¼�*/  
DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()  
{  
    public void onClick(DialogInterface dialog, int which)  
    {  
        switch (which)  
        {  
        case AlertDialog.BUTTON_POSITIVE:// "ȷ��"��ť�˳�����  
            System.exit(0);  
            soundPool.release();
            break;  
        case AlertDialog.BUTTON_NEGATIVE:// "ȡ��"�ڶ�����ťȡ���Ի���  
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
			currentMaritx.set(imageView.getImageMatrix());//��¼ImageView���ڵ��ƶ�λ��
			startPoint.set(event.getX(),event.getY());//��ʼ��
			break;

		case MotionEvent.ACTION_MOVE://�ƶ��¼�
			
			soundPool.play(play_num, 0.2f, 0.2f, 0, -1, 1);
			for(int i = 0 ; i < sceneInform.length; i++)
				sceneInform[i][3] = 0f;
			
			if (mode == DRAG) {//ͼƬ�϶��¼�
				float dx = event.getX() - startPoint.x;//x���ƶ�����
				float dy = event.getY() - startPoint.y;
				matrix.set(currentMaritx);//�ڵ�ǰ��λ�û������ƶ�
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
			else if(mode == ZOOM){//ͼƬ�Ŵ��¼�
				float endDis = distance(event);//��������
				if(endDis > 10f){
					float scale = endDis / startDis;//�Ŵ���
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
		//����ָ�뿪��Ļ������Ļ���д���(��ָ)
		case MotionEvent.ACTION_POINTER_UP:
			mode = DRAG;
			break;
		//����Ļ���Ѿ��д��㣨��ָ��,����һ����ָѹ����Ļ
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			startDis = distance(event);
			
			if(startDis > 10f){//������ָ����������
				midPoint = mid(event);
				currentMaritx.set(imageView.getImageMatrix());//��¼��ǰ�����ű���
			}
			break;

		}
		
		imageView.setImageMatrix(matrix);
		return true;
	}
	
}
/**
 * ����֮��ľ���
 */
private static float distance(MotionEvent event){
	//�����ߵľ���
	float dx = event.getX(1) - event.getX(0);
	float dy = event.getY(1) - event.getY(0);
	return FloatMath.sqrt(dx*dx + dy*dy);
}
/**
 * ��������֮�����ĵ�ľ���
 */
private static PointF mid(MotionEvent event){
	float midx = event.getX(1) + event.getX(0);
	float midy = event.getY(1) - event.getY(0);
	
	return new PointF(midx/2, midy/2);
}
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
}
