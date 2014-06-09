package com.hackathon.activity;

import com.hackathon.main.R;
import com.hackathon.main.R.id;
import com.hackathon.main.R.layout;
import com.hackathon.worker.HkExceptionHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class HachathonFirstActivity extends Activity
{

	ImageButton doubleButton;
	ImageButton threeButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first);
		Thread.setDefaultUncaughtExceptionHandler(new HkExceptionHandler()); 
		doubleButton = (ImageButton)findViewById(R.id.buttondouble);
		doubleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(HachathonFirstActivity.this,HachathonMainActivity.class);
				startActivity(intent);
			}
		});
		threeButton = (ImageButton)findViewById(R.id.buttonthree);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			HachathonFirstActivity.this.finish();
			return true;
		} 
		return super.onKeyDown(keyCode, event);
	}
	
}