package com.example.hci2;

import android.app.Application;

import com.iflytek.cloud.SpeechUtility;

public class SpeechApp extends Application{
  
	@Override
	public void onCreate() {
		// Ӧ�ó�����ڴ�����,�����ֻ��ڴ��С��ɱ����̨����,���SpeechUtility����Ϊnull
		// �����������Ӧ��appid
		SpeechUtility.createUtility(SpeechApp.this, "appid="+getString(R.string.app_id));
		super.onCreate();
	}
}
