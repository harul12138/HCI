package com.example.hci2;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.speech.util.JsonParser;
import com.example.hci2.R;
//import com.example.map.CustomTabActivity2.TounchListener;



public class CustomTabActivity2 extends Activity implements OnClickListener{
	
	private static String TAG = "CustomTabActivity2";
	private Toast mToast;
	private String voice_text=null;
	private float[][] sceneInform=new float[4][4];
	
	private ImageView imageView;
	
	private PointF startPoint = new PointF();
	private Matrix matrix = new Matrix();
	private Matrix currentMaritx = new Matrix();
	
	private int mode = 0;//用于标记模式
	private static final int NONE = 0;//拖动
	private static final int DRAG = 1;//拖动
	private static final int ZOOM = 2;//放大
	private float startDis = 0;
	private PointF midPoint;//中心点
	
	private SpeechRecognizer mIat;// 语音听写对象
	private RecognizerDialog iatDialog;// 语音听写UI
	
	
	MediaPlayer mMediaPlayer;  //背景音乐
	private SoundPool soundPool;
	private HashMap musicId=new HashMap();//定义一个HashMap用于存放音频流的ID
    
    //保存上一次图片的位置坐标
    private float last_x=0f;
    private float last_y=0f;
	
    private int i=-1;     //语音输入定位参数
    private int play_num = 0;   //控制音量大小参数
    
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        this.setContentView(R.layout.voice);
	        this.findViewById(R.id.speakClick).setOnClickListener(this);
	       

	        //汴河码头
	        sceneInform[0][0]=-808f;
	        sceneInform[0][1]=0f;
	        sceneInform[0][2]=1f;//id
	        sceneInform[0][3]=0f;//isPlaying 0 no; 1 yes
	        //汴京郊野
	        sceneInform[1][0]=-2100f;
	        sceneInform[1][1]=0f;
	        sceneInform[1][2]=2f;
	        sceneInform[1][3]=0f;
	        //市区街道
	        sceneInform[2][0]=-200f;
	        sceneInform[2][1]=0f;
	        sceneInform[2][2]=3f;
	        sceneInform[2][3]=0f;
	        //戏曲
	        sceneInform[3][0]=-2760f;
	        sceneInform[3][1]=0f;
	        sceneInform[3][2]=4f;
	        sceneInform[3][3]=0f;
	        
			
	        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
	        
	        //新建音频播放池
	        soundPool= new SoundPool(4,AudioManager.STREAM_SYSTEM,0);
	    	musicId.put(1, soundPool.load(this, R.raw.water1, 1));
	    	musicId.put(2, soundPool.load(this, R.raw.bird, 1));
	    	musicId.put(3, soundPool.load(this, R.raw.noise1, 1));
	    	musicId.put(4, soundPool.load(this, R.raw.opera, 1));
	    	
	    	
	        mMediaPlayer = MediaPlayer.create(this, R.raw.flute);
	 		mMediaPlayer.start();
	 			
	        /**
	         * 语音部分对象声明
	         */
            mIat = SpeechRecognizer.createRecognizer(this, mInitListener);// 初始化识别对象
	        iatDialog = new RecognizerDialog(this, mInitListener);// 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
	     	
	        imageView = (ImageView) findViewById(R.id.imageview);
	        imageView.setOnTouchListener(new TounchListener());
	    //图片初始位置    
	        matrix.set(imageView.getImageMatrix());
 			//matrix.postTranslate(-100, -100);
 			//matrix.setScale(3,3);
 			imageView.setImageMatrix(matrix);
	    }
		
	    public void onClick(View view) {				
			switch (view.getId()) {
			// 开始识别
			case R.id.speakClick:
				voice_text=null;
				setParam();// 设置参数
				iatDialog.setListener(recognizerDialogListener);// 显示听写对话框
				iatDialog.show();
				//showTip(getString(R.string.text_begin));
				break;
			default:
				break;
			}
		}
	    
	    /**
		 * 初始化监听器。
		 */
		private InitListener mInitListener = new InitListener() {
			@Override
			public void onInit(int code) {
				Log.d(TAG, "SpeechRecognizer init() code = " + code);
				if (code != ErrorCode.SUCCESS) {
	        		showTip("初始化失败,错误码："+code);
	        	}
			}
		};
	    
		/**
		 * 听写UI监听器
		 */
		private RecognizerDialogListener recognizerDialogListener=new RecognizerDialogListener(){
			
			public void onResult(RecognizerResult results, boolean isLast) {
				String text = JsonParser.parseIatResult(results.getResultString());
				voice_text=voice_text+text;
				if(voice_text!=null){
					String sub_voice_text=voice_text.substring(4);
					if(sub_voice_text!=null
							&&(sub_voice_text.equals("上")
							||sub_voice_text.equals("尚")
							
							||sub_voice_text.equals("下")
							||sub_voice_text.equals("吓")
							||sub_voice_text.equals("夏")
							
							
							||sub_voice_text.equals("左")
							||sub_voice_text.equals("做")
							||sub_voice_text.equals("祚")
							
							||sub_voice_text.equals("右")
							||sub_voice_text.equals("幼")
							||sub_voice_text.equals("有")
							||sub_voice_text.equals("由")
							
							||sub_voice_text.equals("大")
							||sub_voice_text.equals("打")
							||sub_voice_text.equals("放大")
							||sub_voice_text.equals("到")
							
							||sub_voice_text.equals("小")
							||sub_voice_text.equals("缩小")
							
							||sub_voice_text.equals("汴河码头")
							||sub_voice_text.equals("市区街道")
							||sub_voice_text.equals("汴京郊野")
							||sub_voice_text.equals("戏曲"))){
						showTip("您的命令为："+sub_voice_text);
						int checkNum=checkVoiceString(sub_voice_text);
						voiceEvent(checkNum);
						i=checkNum;
					}
					else{
						showTip("请正确说出命令");
					}
				}
			}

			/**
			 * 识别回调错误.
			 */
			public void onError(SpeechError error) {
				showTip(error.getPlainDescription(true));
			}
		};
		
		/**
		 * 参数设置
		 */
		@SuppressLint("SdCardPath")
		public void setParam(){
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
			// 设置语音前端点
			mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
			// 设置语音后端点
			mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
			// 设置标点符号
			mIat.setParameter(SpeechConstant.ASR_PTT, "0");
			// 设置音频保存路径
			mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, "/sdcard/iflytek/wavaudio.pcm");
		}
		
		/**
		 * 检测区分识别出的字符串
		 * @param eventLabel
		 * @return
		 */
		public int checkVoiceString(String eventLabel){
			if(eventLabel.equals("尚")||eventLabel.equals("上")){
				return 1;
			}
			if(eventLabel.equals("下")||eventLabel.equals("吓")||eventLabel.equals("夏")||eventLabel.equals("霞")){
				return 2;
			}
			if(eventLabel.equals("左")){
				return 3;
			}
			if(eventLabel.equals("右")||eventLabel.equals("又")){
				return 4;
			}
			if(eventLabel.equals("放大")||eventLabel.equals("大")||eventLabel.equals("打")||eventLabel.equals("放到")||eventLabel.equals("到")||eventLabel.equals("放荡")){
				return 5;
			}
			if(eventLabel.equals("缩小")||eventLabel.equals("小")){
				return 6;
			}
			if(eventLabel.equals("汴河码头")){
				return 7;
			}
			if(eventLabel.equals("汴京郊野")){
				return 8;
			}
			if(eventLabel.equals("市区街道")){
				return 9;
			}
			if(eventLabel.equals("戏曲")){
				return 10;
			}
			return 0;
		}
		
		/**
		 * 根据参数确定将要执行的操作
		 * @param checkNum
		 */
		public void voiceEvent(int checkNum){
			switch(checkNum){
			case 1:
				picUp();
				break;
			case 2:
				picDown();
				break;
			case 3:
				picLeft();
				break;
			case 4:
				picRight();
				break;
			case 5:
				voiceBig();
				break;
			case 6:
				voiceSmall();
				break;
			default:
				voiceLocScene(checkNum);
				break;
			}
		}
		
		public void picUp(){
			for(int k=0; k<5; k++){
				float dx=0f;
				float dy=-10f;
				last_x=last_x+dx;
				last_y=last_y+dy;
				matrix.set(imageView.getImageMatrix());
				matrix.postTranslate(dx, dy);
				imageView.setImageMatrix(matrix);
			}
		}
		
		public void picDown(){
			for(int k=0; k<5; k++){
				float dx=0f;
				float dy=10f;
				last_x=last_x+dx;
				last_y=last_y+dy;
				matrix.set(imageView.getImageMatrix());
				matrix.postTranslate(dx, dy);
				imageView.setImageMatrix(matrix);
			}
		}
		
		public void picLeft(){
			for(int k=0; k<5; k++){
				float dx=-10f;
				float dy=0f;
				last_x=last_x+dx;
				last_y=last_y+dy;
				matrix.set(imageView.getImageMatrix());
				matrix.postTranslate(dx, dy);
				imageView.setImageMatrix(matrix);
			}
		}
		
		public void picRight(){
			for(int k=0; k<5; k++){
				float dx=10f;
				float dy=0f;
				last_x=last_x+dx;
				last_y=last_y+dy;
				matrix.set(imageView.getImageMatrix());
				matrix.postTranslate(dx, dy);
				imageView.setImageMatrix(matrix);
			}
		}
		
		public void voiceBig(){
			soundPool.autoPause();
			soundPool.play(play_num+1, 1.0f, 1.0f, 1, -1, 1);
		}
		
		public void voiceSmall(){
			//showTip("xiao"+play_num);
			soundPool.autoPause();
			soundPool.play(play_num+1, 0.1f, 0.1f, 1, -1, 1);
		}
		
		public void voiceLocScene(int id){
			
			int index=id-7;
			soundPool.autoPause();
			
			float dis_x=sceneInform[index][0]-last_x;
			float dis_y=sceneInform[index][1]-last_y;
			
			last_x=sceneInform[index][0];
			last_y=sceneInform[index][1];
			
			matrix.set(imageView.getImageMatrix());
			matrix.setTranslate(sceneInform[index][0], sceneInform[index][1]);
			
			imageView.setImageMatrix(matrix);
		
			if(sceneInform[index][3]!=1f){
				//showTip("indexindex:"+index);
				soundPool.play((int)sceneInform[index][2], 0.2f, 0.2f, 1, -1, 1);
				play_num = index;
				//sceneInform[index][3]=1f;
			}
			
		}
		
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
			
			//soundPool.play(play_num, 0.2f, 0.2f, 0, -1, 1);
			
			if (mode == DRAG) {//图片拖动事件
				float dx = event.getX() - startPoint.x;//x轴移动距离
				float dy = event.getY() - startPoint.y;
				matrix.set(currentMaritx);//在当前的位置基础上移动
				matrix.postTranslate(dx, dy);
				//showTip("last_x:"+last_x+"play_num"+play_num);
				if(last_x > -2930 && last_x < -2700)
				{
					//play_num = 4;
					//sceneInform[play_num-1][3] = 1f;
				}else if(last_x > -2250 && last_x < -2050 ){
					//play_num = 2;
					//sceneInform[play_num-1][3] = 1f;
				}else if(last_x > -300 && last_x <-100){
					//play_num = 3;
					//sceneInform[play_num-1][3] = 1f;
				}else if(last_x > -1000 && last_x <-800){
					//play_num = 1;
					//sceneInform[play_num-1][3] = 1f;
				}
				else {
					play_num = 0;
					soundPool.autoPause();
				}
			}
			else if(mode == ZOOM){//图片放大事件
				float endDis = distance(event);//结束距离
				if(endDis > 10f){
					float scale = endDis / startDis;//放大倍数
					matrix.set(currentMaritx);
					matrix.postScale(scale, scale, midPoint.x, midPoint.y);
					if(endDis > 100f && !zoom_in){
						zoom_in=true;
						
				mMediaPlayer.stop();
				if(i != -1)
				soundPool.play((int)sceneInform[i-7][2], 0.2f, 0.2f, 1, -1, 1);
				
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
}