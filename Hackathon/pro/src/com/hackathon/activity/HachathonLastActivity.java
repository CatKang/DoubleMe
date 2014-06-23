package com.hackathon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hackathon.main.R;
import com.hackathon.worker.HkExceptionHandler;

public class HachathonLastActivity extends Activity {
	LinearLayout hideLayout;
    ImageButton continueImage;
    TextView continueText;
    ImageButton shareButton;
    ImageView homeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new HkExceptionHandler()); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.last);
		
		hideLayout = (LinearLayout)findViewById(R.id.hideLayout);
	    continueImage = (ImageButton)findViewById(R.id.continueImage);
	    continueText = (TextView)findViewById(R.id.continueText);
	    shareButton = (ImageButton)findViewById(R.id.shareButton);
		
	    
	    continueImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HachathonLastActivity.this.finish();
				startActivity(new Intent(HachathonLastActivity.this,HachathonMainActivity.class));
				// TODO Auto-generated method stub
//				mBottomPhotoFrameLayout.setVisibility(View.VISIBLE);
//				buttonBottomSave.setVisibility(View.INVISIBLE);
//				buttonBottomCancel.setVisibility(View.INVISIBLE);
//				resetCamera();
//				initCamera();
//				can_drag = true;
//				buttonYes.setVisibility(View.GONE);
//				buttonNo.setVisibility(View.GONE);
//				myMidLayout.setVisibility(View.VISIBLE);
			}
	    	
	    });
	    
	    
	    continueText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HachathonLastActivity.this.finish();
				startActivity(new Intent(HachathonLastActivity.this,HachathonMainActivity.class));
				// TODO Auto-generated method stub
//				mBottomPhotoFrameLayout.setVisibility(View.VISIBLE);
//				buttonBottomSave.setVisibility(View.INVISIBLE);
//				buttonBottomCancel.setVisibility(View.INVISIBLE);
//				resetCamera();
//				initCamera();
//				can_drag = true;
//				buttonYes.setVisibility(View.GONE);
//				buttonNo.setVisibility(View.GONE);
//				myMidLayout.setVisibility(View.VISIBLE);
			}
	    	
	    });
	    
	    homeButton = (ImageView)findViewById(R.id.homeButton);
	    homeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HachathonLastActivity.this.finish();
				startActivity(new Intent(HachathonLastActivity.this,HachathonFirstActivity.class));
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {

					HachathonLastActivity.this.finish();
					startActivity(new Intent(HachathonLastActivity.this,HachathonFirstActivity.class));
					return true;
				}else if (keyCode == KeyEvent.KEYCODE_HOME)
				{
					moveTaskToBack(true);  
		            return true;  
				}
				return super.onKeyDown(keyCode, event);
	}
	
	
}
