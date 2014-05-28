package com.hackathon.activity;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hackathon.common.util.FileUtil;
import com.hackathon.common.util.GeometryUtil;
import com.hackathon.entity.HkWindow;
import com.hackathon.entity.ImageSize;
import com.hackathon.main.R;


public class HachathonMainActivity extends Activity implements
		SurfaceHolder.Callback, OnTouchListener {
	private static String TAG = "HMainActivity";
	private SurfaceView surfaceView;

	private Camera camera;

	private FrameLayout frameLayout;
	private FrameLayout left;
	private FrameLayout right;
	private ImageView leftImage;
	private ImageView rightImage;
	private ImageView floatImage;
	private ImageView xiangjiImage;
	private ImageView moveImage;
	private Button noButton;

	
	
	HkWindow curWindow;
	int flag = 0;
	Camera.Size pictureSize;
	int screenWidth, screenHeight;
	boolean isSetFrame = false, can_drag = true;
	int lastX, lastY;
	boolean first_draw = true;
	TextView text;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		FileUtil.init_file_env();
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();

		frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
		Button takephotoButton = (Button) findViewById(R.id.takephotoButton);
		noButton = (Button) findViewById(R.id.noButton);

		takephotoButton.setX(screenWidth - 180);
		takephotoButton.setY((screenHeight - 50) / 2 - 50);

		takephotoButton.setOnClickListener(new OnClickListener() {
	
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (flag == 0) {
					camera.takePicture(shutterCallback, rawCallback,
							jpegCallback);
					can_drag = false;
				} else if (flag == 1) {
					noButton.setVisibility(View.INVISIBLE);
					floatImage.setVisibility(View.VISIBLE);
					rightImage.setVisibility(View.INVISIBLE);
					xiangjiImage.setVisibility(View.GONE);
					moveImage.setVisibility(View.GONE);
					leftImage.setVisibility(View.VISIBLE);
					//floatImage.setBackgroundColor(Color.TRANSPARENT);
					camera.startPreview();
				} else if (flag == 2) {
					camera.takePicture(shutterCallback, rawCallback,
							jpegCallback);
				}
				flag++;
			}
		});

		surfaceView = (SurfaceView) this.findViewById(R.id.camera);
		surfaceView.setOnTouchListener(this);
		SurfaceHolder holder = surfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this);

		ViewTreeObserver vto = surfaceView.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				// initCamera();
				if (first_draw) {
					curWindow = new HkWindow(surfaceView);
					LinearLayout.LayoutParams paramLeft = (LinearLayout.LayoutParams) left
							.getLayoutParams();
					paramLeft.width = curWindow.viewWidth / 2;
					left.setLayoutParams(paramLeft);

					right = (FrameLayout) findViewById(R.id.right);
					LinearLayout.LayoutParams paramRight = (LinearLayout.LayoutParams) right
							.getLayoutParams();
					paramRight.width = curWindow.viewWidth / 2;
					right.setLayoutParams(paramRight);
					first_draw = false;
				}
				return true;
			}
		});

		floatImage = (ImageView) findViewById(R.id.imageFloat);
		rightImage = (ImageView) findViewById(R.id.imageRight);

		left = (FrameLayout) findViewById(R.id.left);
		leftImage = (ImageView) findViewById(R.id.imageLeft);

		text = (TextView) findViewById(R.id.text);
		
		xiangjiImage = (ImageView)findViewById(R.id.imageXiangji);
		moveImage = (ImageView)findViewById(R.id.imageMove);
	}


	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO 自动生成方法存根

		
	}


	public void surfaceCreated(SurfaceHolder holder) {
		// TODO 自动生成方法存根
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(holder);
			Camera.Parameters parameters = camera.getParameters();
			parameters.setPictureFormat(PixelFormat.RGB_565);

			List<Camera.Size> s_previewSize = parameters
					.getSupportedPreviewSizes();
			Camera.Size previewSize = GeometryUtil.getOptimalSize(
					s_previewSize, screenWidth, screenHeight);
			List<Camera.Size> s_pictureSize = parameters
					.getSupportedPictureSizes();
			pictureSize = GeometryUtil.getOptimalSize(
			
					s_pictureSize, screenWidth, screenHeight);
			
			//record env.log
			FileUtil.recordEnv("PreviewSize: ("+ previewSize.width + " , " + previewSize.height +")");
			FileUtil.recordEnv("PictureSize: ("+ pictureSize.width + " , " + pictureSize.height +")");
			FileUtil.recordEnv("WindowSize: ("+ screenWidth + " , " + screenHeight +")");
			parameters.setPreviewSize(previewSize.width, previewSize.height);
			try {
				parameters.setPictureSize(pictureSize.width, pictureSize.height);
				parameters.getSupportedPictureSizes();
				camera.setParameters(parameters);
				camera.setPreviewDisplay(holder);
				// camera.startPreview();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		camera.startPreview();

	}


	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO 自动生成方法存根
		camera.release();
	}

	private ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {

		}
	};

	private PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
			// TODO Handle RAW image data
		}
	};

	private PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
			// Toast.makeText(getApplicationContext(), "jepgCallback",
			// 100).show();

			Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length);
			if (flag == 1) {
				ImageSize sizes = curWindow.getImageSize("left", pictureSize.width,
						pictureSize.height);

				Bitmap targetbm_left = Bitmap.createBitmap(bm, sizes.x,
						sizes.y, sizes.width, sizes.height);
				sizes = curWindow.getImageSize("right", pictureSize.width, pictureSize.height);
				Bitmap targetbm_right = Bitmap.createBitmap(bm, sizes.x,
						sizes.y, sizes.width, sizes.height);

				FileUtil.memoryOneImage(targetbm_left, "left");
				FileUtil.memoryOneImage(targetbm_right, "float");
				FileUtil.memoryOneImage(bm, "whole");

				leftImage.setVisibility(View.VISIBLE);
				leftImage.setImageBitmap(targetbm_left);
//				xiangjiImage.setVisibility(View.VISIBLE);
				moveImage.setVisibility(View.GONE);
				rightImage.setBackgroundColor(Color.BLACK);
				rightImage.getBackground().setAlpha(200);
				rightImage.setVisibility(View.VISIBLE);
				noButton.setVisibility(View.VISIBLE);
				floatImage.setVisibility(View.INVISIBLE);
				floatImage.setImageBitmap(targetbm_right);
				floatImage.setAlpha(99);
				noButton.setOnClickListener(new OnClickListener() {


					public void onClick(View v) {
						// TODO Auto-generated method stub
						leftImage.setVisibility(View.GONE);
						noButton.setVisibility(View.GONE);
						rightImage.setVisibility(View.VISIBLE);
						rightImage.setBackgroundResource(R.drawable.shadow_right);
						moveImage.setVisibility(View.VISIBLE);
						floatImage.setVisibility(View.INVISIBLE);
						xiangjiImage.setVisibility(View.GONE);
						flag = 0;
						can_drag = true;
						camera.startPreview();
					}
				});
			} else {
				ImageSize sizes = curWindow.getImageSize("right", pictureSize.width,
						pictureSize.height);
				Bitmap targetbm_right = Bitmap.createBitmap(bm, sizes.x,
						sizes.y, sizes.width, sizes.height);
				FileUtil.memoryOneImage(targetbm_right, "right");
				Intent finalImageIntent = new Intent(HachathonMainActivity.this, HachathonFinalImageActivity.class);
				finalImageIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				finalImageIntent.putExtra("HkWindow", curWindow);
				startActivity(finalImageIntent);
				camera.release();
			}

		}
	};

	public boolean onTouch(View v, MotionEvent event) {
		if (!can_drag)
			return false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			if (curWindow.onFrame(lastX, lastY))
				isSetFrame = true;
			else
				isSetFrame = false;
			break;

		case MotionEvent.ACTION_MOVE:
			if (!isSetFrame)
				return true;
			int cx = (int) event.getRawX();
			int cy = (int) event.getRawY();
			int dx = curWindow.boundInWindow(cx - lastX);

			LinearLayout.LayoutParams paramLeft = (LinearLayout.LayoutParams) left
					.getLayoutParams();
			paramLeft.width += dx;
			paramLeft.height = curWindow.viewHeight;
			left.setLayoutParams(paramLeft);
			LinearLayout.LayoutParams paramRight = (LinearLayout.LayoutParams) right
					.getLayoutParams();
			paramRight.width -= dx;
			paramRight.height = curWindow.viewHeight;
			right.setLayoutParams(paramRight);
			curWindow.curFrameX += dx;
			lastX = cx;
			lastY = cy;
			text.setText("curFrameX:" + curWindow.curFrameX + "   cx:" + cx
					+ "  cy:" + cy + "   dx:" + dx + "  paramLeft.width:"
					+ paramLeft.width + "   paramRight.width:"
					+ paramRight.width);
			break;

		case MotionEvent.ACTION_UP:
			isSetFrame = false;
			break;
		}
		return true;
	}
}
