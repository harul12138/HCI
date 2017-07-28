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
	
	private int mode = 0;//���ڱ��ģʽ
	private static final int NONE = 0;//�϶�
	private static final int DRAG = 1;//�϶�
	private static final int ZOOM = 2;//�Ŵ�
	private float startDis = 0;
	private PointF midPoint;//���ĵ�
	
	private SpeechRecognizer mIat;// ������д����
	private RecognizerDialog iatDialog;// ������дUI
	
	
	MediaPlayer mMediaPlayer;  //��������
	private SoundPool soundPool;
	private HashMap musicId=new HashMap();//����һ��HashMap���ڴ����Ƶ����ID
    
    //������һ��ͼƬ��λ������
    private float last_x=0f;
    private float last_y=0f;
	
    private int i=-1;     //�������붨λ����
    private int play_num = 0;   //����������С����
    
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        this.setContentView(R.layout.voice);
	        this.findViewById(R.id.speakClick).setOnClickListener(this);
	       

	        //�����ͷ
	        sceneInform[0][0]=-808f;
	        sceneInform[0][1]=0f;
	        sceneInform[0][2]=1f;//id
	        sceneInform[0][3]=0f;//isPlaying 0 no; 1 yes
	        //�꾩��Ұ
	        sceneInform[1][0]=-2100f;
	        sceneInform[1][1]=0f;
	        sceneInform[1][2]=2f;
	        sceneInform[1][3]=0f;
	        //�����ֵ�
	        sceneInform[2][0]=-200f;
	        sceneInform[2][1]=0f;
	        sceneInform[2][2]=3f;
	        sceneInform[2][3]=0f;
	        //Ϸ��
	        sceneInform[3][0]=-2760f;
	        sceneInform[3][1]=0f;
	        sceneInform[3][2]=4f;
	        sceneInform[3][3]=0f;
	        
			
	        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
	        
	        //�½���Ƶ���ų�
	        soundPool= new SoundPool(4,AudioManager.STREAM_SYSTEM,0);
	    	musicId.put(1, soundPool.load(this, R.raw.water1, 1));
	    	musicId.put(2, soundPool.load(this, R.raw.bird, 1));
	    	musicId.put(3, soundPool.load(this, R.raw.noise1, 1));
	    	musicId.put(4, soundPool.load(this, R.raw.opera, 1));
	    	
	    	
	        mMediaPlayer = MediaPlayer.create(this, R.raw.flute);
	 		mMediaPlayer.start();
	 			
	        /**
	         * �������ֶ�������
	         */
            mIat = SpeechRecognizer.createRecognizer(this, mInitListener);// ��ʼ��ʶ�����
	        iatDialog = new RecognizerDialog(this, mInitListener);// ��ʼ����дDialog,���ֻʹ����UI��д����,���贴��SpeechRecognizer
	     	
	        imageView = (ImageView) findViewById(R.id.imageview);
	        imageView.setOnTouchListener(new TounchListener());
	    //ͼƬ��ʼλ��    
	        matrix.set(imageView.getImageMatrix());
 			//matrix.postTranslate(-100, -100);
 			//matrix.setScale(3,3);
 			imageView.setImageMatrix(matrix);
	    }
		
	    public void onClick(View view) {				
			switch (view.getId()) {
			// ��ʼʶ��
			case R.id.speakClick:
				voice_text=null;
				setParam();// ���ò���
				iatDialog.setListener(recognizerDialogListener);// ��ʾ��д�Ի���
				iatDialog.show();
				//showTip(getString(R.string.text_begin));
				break;
			default:
				break;
			}
		}
	    
	    /**
		 * ��ʼ����������
		 */
		private InitListener mInitListener = new InitListener() {
			@Override
			public void onInit(int code) {
				Log.d(TAG, "SpeechRecognizer init() code = " + code);
				if (code != ErrorCode.SUCCESS) {
	        		showTip("��ʼ��ʧ��,�����룺"+code);
	        	}
			}
		};
	    
		/**
		 * ��дUI������
		 */
		private RecognizerDialogListener recognizerDialogListener=new RecognizerDialogListener(){
			
			public void onResult(RecognizerResult results, boolean isLast) {
				String text = JsonParser.parseIatResult(results.getResultString());
				voice_text=voice_text+text;
				if(voice_text!=null){
					String sub_voice_text=voice_text.substring(4);
					if(sub_voice_text!=null
							&&(sub_voice_text.equals("��")
							||sub_voice_text.equals("��")
							
							||sub_voice_text.equals("��")
							||sub_voice_text.equals("��")
							||sub_voice_text.equals("��")
							
							
							||sub_voice_text.equals("��")
							||sub_voice_text.equals("��")
							||sub_voice_text.equals("��")
							
							||sub_voice_text.equals("��")
							||sub_voice_text.equals("��")
							||sub_voice_text.equals("��")
							||sub_voice_text.equals("��")
							
							||sub_voice_text.equals("��")
							||sub_voice_text.equals("��")
							||sub_voice_text.equals("�Ŵ�")
							||sub_voice_text.equals("��")
							
							||sub_voice_text.equals("С")
							||sub_voice_text.equals("��С")
							
							||sub_voice_text.equals("�����ͷ")
							||sub_voice_text.equals("�����ֵ�")
							||sub_voice_text.equals("�꾩��Ұ")
							||sub_voice_text.equals("Ϸ��"))){
						showTip("��������Ϊ��"+sub_voice_text);
						int checkNum=checkVoiceString(sub_voice_text);
						voiceEvent(checkNum);
						i=checkNum;
					}
					else{
						showTip("����ȷ˵������");
					}
				}
			}

			/**
			 * ʶ��ص�����.
			 */
			public void onError(SpeechError error) {
				showTip(error.getPlainDescription(true));
			}
		};
		
		/**
		 * ��������
		 */
		@SuppressLint("SdCardPath")
		public void setParam(){
			// ��������
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// ������������
			mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
			// ��������ǰ�˵�
			mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
			// ����������˵�
			mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
			// ���ñ�����
			mIat.setParameter(SpeechConstant.ASR_PTT, "0");
			// ������Ƶ����·��
			mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, "/sdcard/iflytek/wavaudio.pcm");
		}
		
		/**
		 * �������ʶ������ַ���
		 * @param eventLabel
		 * @return
		 */
		public int checkVoiceString(String eventLabel){
			if(eventLabel.equals("��")||eventLabel.equals("��")){
				return 1;
			}
			if(eventLabel.equals("��")||eventLabel.equals("��")||eventLabel.equals("��")||eventLabel.equals("ϼ")){
				return 2;
			}
			if(eventLabel.equals("��")){
				return 3;
			}
			if(eventLabel.equals("��")||eventLabel.equals("��")){
				return 4;
			}
			if(eventLabel.equals("�Ŵ�")||eventLabel.equals("��")||eventLabel.equals("��")||eventLabel.equals("�ŵ�")||eventLabel.equals("��")||eventLabel.equals("�ŵ�")){
				return 5;
			}
			if(eventLabel.equals("��С")||eventLabel.equals("С")){
				return 6;
			}
			if(eventLabel.equals("�����ͷ")){
				return 7;
			}
			if(eventLabel.equals("�꾩��Ұ")){
				return 8;
			}
			if(eventLabel.equals("�����ֵ�")){
				return 9;
			}
			if(eventLabel.equals("Ϸ��")){
				return 10;
			}
			return 0;
		}
		
		/**
		 * ���ݲ���ȷ����Ҫִ�еĲ���
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
			currentMaritx.set(imageView.getImageMatrix());//��¼ImageView���ڵ��ƶ�λ��
			startPoint.set(event.getX(),event.getY());//��ʼ��
			break;

		case MotionEvent.ACTION_MOVE://�ƶ��¼�
			
			//soundPool.play(play_num, 0.2f, 0.2f, 0, -1, 1);
			
			if (mode == DRAG) {//ͼƬ�϶��¼�
				float dx = event.getX() - startPoint.x;//x���ƶ�����
				float dy = event.getY() - startPoint.y;
				matrix.set(currentMaritx);//�ڵ�ǰ��λ�û������ƶ�
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
			else if(mode == ZOOM){//ͼƬ�Ŵ��¼�
				float endDis = distance(event);//��������
				if(endDis > 10f){
					float scale = endDis / startDis;//�Ŵ���
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
}