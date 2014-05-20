package com.hackathon.activity;

import com.hackathon.common.util.FileUtil;
import com.hackathon.main.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HachathonLastActivity extends Activity {
	LinearLayout hideLayout;
    ImageButton continueImage;
    TextView continueText;
    ImageButton shareButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.last);
		
		hideLayout = (LinearLayout)findViewById(R.id.hideLayout);
	    continueImage = (ImageButton)findViewById(R.id.continueImage);
	    continueText = (TextView)findViewById(R.id.continueText);
	    shareButton = (ImageButton)findViewById(R.id.shareButton);
		
	    
	  
	    
	    
	    continueText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startActivity(new Intent(HachathonLastActivity.this,HachathonFirstActivity.class));
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

	}
}
