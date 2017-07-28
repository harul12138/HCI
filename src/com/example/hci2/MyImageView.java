package com.example.hci2;

import android.annotation.SuppressLint;
import android.graphics.*;
import android.os.Handler;
import android.os.SystemClock;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Chronometer;
import android.widget.Toast;


public class MyImageView  extends SurfaceView implements SurfaceHolder.Callback{
	private ImageActivity imageActivity;
	private Paint paint;
	private Bitmap bitmap;
	private Matrix matrix;
//	private Canvas canvas;
	private static final int double_clicked_time_space=350;
	private static final int long_clicked_time=500;
	private float screenWidth,screenHeight;
	private float picWidth,picHeight,ppicWidth,ppicHeight;
	private float left,top,pleft,ptop;
	//private float 
	private float scale=1;
	private boolean isMove=false;
	private long nexteventTime=0;
	PointF start=new PointF();
	PointF first=new PointF();
	PointF second=new PointF();
	PointF mid=new PointF();
	Handler mHandler = new Handler();
	private enum MODE {
		NONE, DRAG, ZOOM

	};//限制参数的有限状态，为三种
	MODE mode;
	Chronometer Time;
    private  ClickPressedThread clickPressedThread;
    private LongPressedThread  longPressedThread;
    
    //private Click
	public MyImageView(ImageActivity activity,Bitmap bitmap){
		super(activity);
		this.imageActivity = activity;
		this.getHolder().addCallback(this); // 设置生命周期回调接口的实现者
		this.bitmap = bitmap;

		paint = new Paint();
		paint.setAntiAlias(true);
		matrix = new Matrix();
		
		screenWidth=ImageActivity.window_width;
		screenHeight=ImageActivity.window_height;
		
		picWidth=ppicWidth=this.bitmap.getWidth();
		picHeight=ppicHeight=this.bitmap.getHeight();
		pleft= (screenWidth - picWidth) / 2;
		ptop = (screenHeight-picHeight)/2;
		left=pleft;
		top=ptop;
		//Toast.makeText(imageActivity, "ddX: " + left+ "ddY: " + top, 200).show();
		matrix.postScale(scale, scale, mid.x, mid.y);
		matrix.postTranslate(left, top);
	
       
	}
	
	 @Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		paint.setColor(Color.BLACK);
		
		canvas.drawRect(0, 0,screenWidth,screenHeight,paint);
		canvas.drawBitmap(bitmap, matrix, paint);
	}
	
	public boolean onTouchEvent(MotionEvent e)
	{
		switch(e.getAction() & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:
			onTouchDown(e);
			break;
		case MotionEvent.ACTION_UP:
			onTouchUp(e);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			onTouchPointDown(e);
			break;
		case MotionEvent.ACTION_MOVE:
			onTouchMove(e);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			onTouchPointUp(e);
			break;
		}
		return true;
	}
	
	private int count;
	private float eventX,eventY;
	@SuppressLint("ShowToast")
	public void onTouchDown(MotionEvent event){
		start.x=event.getX();
		start.y=event.getY();
		first.set(event.getX(),event.getY());
		second.set(event.getX(),event.getY());
		count++;	
		mode=MODE.DRAG;
		if( event.getEventTime()-nexteventTime<double_clicked_time_space){
				scale=(float) (1/1.06);
			for(int i=0;i<5;i++){
				if(picWidth*scale>ppicWidth/3)
				   {picWidth=picWidth*scale;
					picHeight=picHeight*scale;
					matrix.postScale(scale, scale,event.getX(),event.getY());}
					repaint();
					}
				
			
			if (clickPressedThread != null) {
				
				mHandler.removeCallbacks(clickPressedThread);}
		}
			else{
				 {  
					longPressedThread = new LongPressedThread();
					mHandler.postDelayed(longPressedThread, long_clicked_time);}
					
					
			
			
			}
		nexteventTime=event.getEventTime();
	}
	
	public void onTouchUp(final MotionEvent e){
		first.x=second.x;
		first.y=second.y;
		if(!isMove){
			if(e.getEventTime()-nexteventTime<long_clicked_time ){
				mHandler.removeCallbacks(longPressedThread);
				if(count<2){
					eventX=e.getX();
					eventY=e.getY();
					
					clickPressedThread = new ClickPressedThread();
					mHandler.postDelayed(clickPressedThread, double_clicked_time_space);}
				
				
				else{
					count=0;
				
				
				}
			}
		}
		mode = MODE.NONE;
		isMove= false;
	}
	
	private float beforeLenght,afterLenght;
	public void onTouchPointDown(MotionEvent event){
		if (event.getPointerCount() == 2) {
			second.set(event.getX(1),event.getY(1));
			mHandler.removeCallbacks(longPressedThread);
			mode = MODE.ZOOM;
			isMove=false;
			beforeLenght = getDistance(event);// 获取两点的距离
		}
		if(event.getPointerCount()==1){
			
		}
		
		
	}
	@SuppressLint("FloatMath")
	public void onTouchMove(MotionEvent event){
		if(mode==MODE.NONE){
			
		}
		if(mode==MODE.DRAG){
			float x=event.getX()-start.x;
			float y=event.getY()-start.y;
			
			if(FloatMath.sqrt(x * x +y * y) >= 10)
				{  
				//Toast.makeText(imageActivity, "Xdray: " +left+ "Ydray: " +top, 20).show();
					mHandler.removeCallbacks(longPressedThread);
					mHandler.removeCallbacks(clickPressedThread);
					    second.set(event.getX(),event.getY());
						float disx=second.x-start.x;
						float disy=second.y-start.y;
						left=disx;
						top=disy;
						matrix.postTranslate(left, top);
						repaint();
						start.x=second.x;
						start.y=second.y;}
					    isMove=false;
					    
					  
					
			
			}
			else {
				
				float fx = event.getX(0);
				float fy = event.getY(0);
				float fdis = FloatMath.sqrt((fx - first.x) * (fx - first.x) + (fy - first.y) * (fy - first.y));
                isMove=true;
				
				count = 0;
				mHandler.removeCallbacks(longPressedThread);

				if (fdis <= 10 ) { 
					// 第一个手指不动
				
				//	Toast.makeText(imageActivity, "left: " +left+ "top: " +top, 2).show();

					mHandler.removeCallbacks(longPressedThread);
					mHandler.removeCallbacks(clickPressedThread);
					float sx = event.getX(1);
					float sy = event.getY(1);
					float sdis = FloatMath.sqrt((sx - second.x) * (sx - second.x) + (sy - second.y)
							* (sy - second.y));
					
					float Y = Math.abs(sy - second.y);
					double angle = Math.asin(Y / sdis);
                            
                           
					if (angle <= Math.PI / 6) { 

						matrix.postTranslate(sx - second.x, 0);//水平移动
					
					} else if (angle >= Math.PI / 3) {
						matrix.postTranslate(0, sy - second.y);
					
					}
					repaint();
					second.set(event.getX(1), event.getY(1));//竖直移动
					mode=MODE.NONE;
				   // isMove=false;
					}
	
				else 
				{
					//Toast.makeText(imageActivity, "zoom: " +left+ "zoom: " +top, 20).show();

					    mHandler.removeCallbacks(longPressedThread);
					    mHandler.removeCallbacks(clickPressedThread);
					    afterLenght = getDistance(event);// 获取两点的距离
					    getMidpoint(event);
						float gapLenght = afterLenght - beforeLenght;
						scale=1+(gapLenght / 500);// 变化的长度
						if (Math.abs(gapLenght) > 5f  ){
							if(picWidth*scale>ppicWidth/3 && picWidth*scale<5*ppicWidth)
								{
							    picWidth=picWidth*scale;
								picHeight=picHeight*scale;
							//	Toast.makeText(imageActivity, "mid.x: " +mid.x+ "mid.y: " +mid.y, 2000).show();
								matrix.postScale(scale, scale,mid.x,mid.y);
								repaint();}
							beforeLenght = afterLenght;
							
						
					}
				}
			}
			
		}
			
		
	
	
	public void onTouchPointUp(MotionEvent e){
		mode=MODE.DRAG;
		second.set(0,0);
	if (e.getActionIndex() == 0) {
			start.set(e.getX(1), e.getY(1));
			first.set(e.getX(1), e.getY(1));
		} else {
			start.set(e.getX(0), e.getY(0));
			first.set(e.getX(0), e.getY(0));
		}
	second.set(0,0);		
	}
	public float getDistance(MotionEvent event){
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
		
	}
	public void getMidpoint(MotionEvent event){
		float x=event.getX(0)+event.getY(1);
		float y=event.getY(0)+event.getY(1);
		mid.set(x/2,y/2);
	}
	@SuppressLint("WrongCall") 
	public void repaint() {
		SurfaceHolder holder = this.getHolder();
		Canvas canvas = holder.lockCanvas();
		try {
			synchronized (holder) {
				onDraw(canvas);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//Toast.makeText(imageActivity, "errors", Toast.LENGTH_SHORT);
		} finally {
			if (canvas != null) {
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	
   @Override
	public void surfaceChanged(SurfaceHolder s, int arg1, int arg2, int arg3) {
		repaint();
	}

	@SuppressLint("ShowToast") @Override
	public void surfaceCreated(SurfaceHolder arg0) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		
	}

class ClickPressedThread implements Runnable {

	@SuppressLint("ShowToast") @Override
	public void run() {
		// TODO Auto-generated method stub
			scale=(float) 1.06;
		
		     for(int i=0;i<5;i++){
		    	 if(picWidth*scale<ppicWidth*5){
			    	picWidth=picWidth*scale;
			    	picHeight=picHeight*scale;
					matrix.postScale(scale, scale,eventX,eventY);
				//	Toast.makeText(imageActivity, "clicked jjjjjjjjjjj", 2000).show();
					repaint();}
		    	 }
				    
			count=0;
			
	}
	
}
@SuppressLint("ShowToast")
class LongPressedThread implements Runnable{

	@Override
	public void run() {
	  
		//if(mode==MODE.NONE)	
		{
		    scale=1;
			left=pleft;
			top=ptop;
			picWidth=ppicWidth;
			picHeight=ppicHeight;
			matrix.reset();
			matrix.postScale(scale, scale);
			matrix.postTranslate(left, top);
			//Toast.makeText(imageActivity, "long clicked jjjjjjjjjjj", 2000).show();
			repaint();
			count=0;}
	}
	
}


}