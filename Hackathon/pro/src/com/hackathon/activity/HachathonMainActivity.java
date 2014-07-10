package com.hackathon.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hackathon.common.listener.TouchFocusListener;
import com.hackathon.common.util.FileUtil;
import com.hackathon.common.util.GeometryUtil;
import com.hackathon.common.util.Log;
import com.hackathon.entity.HkWindow;
import com.hackathon.entity.ImageSize;
import com.hackathon.main.R;
import com.hackathon.view.FocusBoxView;
import com.hackathon.worker.HkExceptionHandler;
import com.umeng.update.UmengUpdateAgent;

public class HachathonMainActivity extends Activity implements
		SurfaceHolder.Callback, OnTouchListener {
	private static String TAG = "HMainActivity";
	private SurfaceView surfaceView;
	private Camera camera;

	private FrameLayout frameLayout;
	private LinearLayout mainLayout;
	private FrameLayout left;
	private FrameLayout right;
	private ImageView leftImage;
	private ImageView rightImage;
	private ImageView floatImage;
	private ImageView xiangjiImage;
	private ImageView moveImage;
	private Button noButton;
	private Button takephotoButton;
	private TouchFocusListener touchFocusListener;
	private FocusBoxView focusBoxView;

	HkWindow curWindow;
	int flag = 0;
	Size pictureSize;
	Size previewSize;
	int windowSize_width;
	int windowSize_height;

	// for frame drag
	boolean isSetFrame = false, can_drag = true;
	int lastX, lastY;
	TextView text;
	// for frame drag end
	Log log_file = null;

	int exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UmengUpdateAgent.update(this);
		Thread.setDefaultUncaughtExceptionHandler(new HkExceptionHandler());
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		this.initial();
		mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
		takephotoButton = (Button) findViewById(R.id.takephotoButton);
		noButton = (Button) findViewById(R.id.noButton);
		floatImage = (ImageView) findViewById(R.id.imageFloat);
		rightImage = (ImageView) findViewById(R.id.imageRight);
		left = (FrameLayout) findViewById(R.id.left);
		right = (FrameLayout) findViewById(R.id.right);
		leftImage = (ImageView) findViewById(R.id.imageLeft);
		text = (TextView) findViewById(R.id.text);
		text.setVisibility(View.VISIBLE);
		xiangjiImage = (ImageView) findViewById(R.id.imageXiangji);
		moveImage = (ImageView) findViewById(R.id.imageMove);
		// picFrameImageL = (ImageView) findViewById(R.id.picFrameImageL);
		// picFrameImageR = (ImageView) findViewById(R.id.picFrameImageR);
		// picFrameImageL.setVisibility(View.VISIBLE);
		// picFrameImageR.setVisibility(View.GONE);
		// moveImage.setBackgroundResource(R.drawable.bai);
		surfaceView = (SurfaceView) this.findViewById(R.id.camera);
		focusBoxView = (FocusBoxView) this.findViewById(R.id.focusBoxView);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		windowSize_width = metric.widthPixels; // 屏幕宽度（像素）
		windowSize_height = metric.heightPixels; // 屏幕高度（像素）

		takephotoButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (!takephotoButton.isEnabled())
					return;
				takephotoButton.setEnabled(false);
				// TODO Auto-generated method stub
				if (flag == 0) {
					text.setVisibility(View.VISIBLE);
					camera.takePicture(shutterCallback, rawCallback,
							jpegCallback);
					can_drag = false;

					// camera.stopPreview();
					// camera.startPreview();
				} else if (flag == 1) {
					// leftImage.setVisibility(View.VISIBLE);
					// rightImage.setVisibility(View.INVISIBLE);
					// floatImage.setVisibility(View.VISIBLE);
					// picFrameImageL.setVisibility(View.GONE);
					// picFrameImageR.setVisibility(View.VISIBLE);

					// Camera.Parameters parameters = camera.getParameters();
					// if (parameters.isAutoExposureLockSupported()) {
					//
					// //parameters.setAutoExposureLock(false);
					// }else{
					// log_file.saveLog("do not support exposure lock!");
					// }
					camera.takePicture(shutterCallback, rawCallback,
							jpegCallback);
				}
				// } else if (flag == 1) {
				// noButton.setVisibility(View.INVISIBLE);
				// floatImage.setVisibility(View.VISIBLE);
				// rightImage.setVisibility(View.INVISIBLE);
				// xiangjiImage.setVisibility(View.GONE);
				// moveImage.setVisibility(View.GONE);
				// leftImage.setVisibility(View.VISIBLE);
				// // floatImage.setBackgroundColor(Color.TRANSPARENT);
				// camera.stopPreview();
				// camera.startPreview();
				// picFrameImageL.setVisibility(View.GONE);
				// picFrameImageR.setVisibility(View.VISIBLE);
				// Camera.Parameters parameters = camera.getParameters();
				// if(parameters.isAutoExposureLockSupported())
				// {
				// parameters.setAutoExposureLock(false);
				// }
				// } else if (flag == 2) {
				// camera.takePicture(shutterCallback, rawCallback,
				// jpegCallback);
				// }
				// flag++;

			}
		});
		surfaceView.setOnTouchListener(this);
		// surfaceView.seton
		SurfaceHolder holder = surfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Toast.makeText(this, "后退键", Toast.LENGTH_SHORT).show();
			switch (flag) {
			case 0:
				if (exitTime == 0) {
					Toast.makeText(getApplicationContext(), "再按一次退出程序", 30)
							.show();
					exitTime = 1;
					Timer mTimer = new Timer();
					mTimer.schedule(new TimerTask() {
						public void run() {
							Looper.prepare();
							if (exitTime == 1)
								exitTime = 0;
							Looper.loop();

						}
					}, 2000);
				} else {
					HachathonMainActivity.this.finish();
				}
				return true;
			case 1:
				setStatus("initial");
				// finish();
				break;
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initial() {
		try {
			
			FileUtil.init_file_env();
			log_file = new Log();
		} catch (Exception e) {
			log_file.saveLog(e, "initial");
			new AlertDialog.Builder(this)
			.setTitle("无法打开")
			.setMessage("请确定已获得存储设备访问权限")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// 点击“确认”后的操作
							HachathonMainActivity.this.finish();

						}
					}).show();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus)
			setStatus("initial");
		super.onWindowFocusChanged(hasFocus);
	}

	private void setStatus(String input) {
		if ("initial".equals(input)) {
			flag = 0;
			// takephotoButton.setEnabled(true);
			// windowSize_width = getWindowManager().getDefaultDisplay()
			// .getWidth();

			// windowSize_height = getWindowManager().getDefaultDisplay()
			// .getHeight();
			FileUtil.recordEnv("WindowSize: (" + windowSize_width + " , "
					+ windowSize_height + ")");
			log_file.saveLog("windwo width :" + windowSize_width + "height :"
					+ windowSize_height);
			// takephotoButton.setY((windowSize_height - 50) / 2 - 50);
			// 设置外边框
			GeometryUtil.uniformScale(previewSize, windowSize_width,
					windowSize_height);
			surfaceView.setLayoutParams(new FrameLayout.LayoutParams(
					previewSize.width, previewSize.height));
			mainLayout.setLayoutParams(new FrameLayout.LayoutParams(
					previewSize.width, previewSize.height));
			curWindow = new HkWindow(surfaceView, previewSize.width,
					previewSize.height);

			// 设置对焦框
			touchFocusListener = new TouchFocusListener(curWindow, camera,
					focusBoxView, log_file);
			touchFocusListener.refreshFocusBox(flag);

			// 设置左右图框
			left.setLayoutParams(new LinearLayout.LayoutParams(
					curWindow.viewWidth / 2, curWindow.viewHeight));
			right.setLayoutParams(new LinearLayout.LayoutParams(
					curWindow.viewWidth / 2, curWindow.viewHeight));

			// 设置照相按钮
			int siderbar_width = curWindow.siderBarWidth;
			takephotoButton.setLayoutParams(new FrameLayout.LayoutParams(
					siderbar_width, siderbar_width));
			takephotoButton.setX(previewSize.width - (int)(siderbar_width*1.3));
			takephotoButton.setY(previewSize.height / 2 - siderbar_width / 2);
			takephotoButton.setBackgroundResource(R.drawable.photobutton);

			// 设置显示图层
			leftImage.setVisibility(View.GONE);
			rightImage.setVisibility(View.VISIBLE);
			rightImage.setBackgroundResource(R.drawable.shadow_right);
			moveImage.setVisibility(View.VISIBLE);
			floatImage.setVisibility(View.INVISIBLE);
			// xiangjiImage.setVisibility(View.GONE);
			// picFrameImageL.setVisibility(View.VISIBLE);
			// picFrameImageR.setVisibility(View.GONE);
			can_drag = true;
			// camera.startPreview();

		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO 自动生成方法存根

		try {
			camera = Camera.open();

		} catch (Exception e) {
			// TODO 自动生成 catch 块
			// e.printStackTrace();
			log_file.saveLog(e, "surfaceCreated");
			new AlertDialog.Builder(this)
					.setTitle("无法打开")
					.setMessage("请确定已获得相机权限")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 点击“确认”后的操作
									HachathonMainActivity.this.finish();

								}
							}).show();
			return;
		}

		try {
			camera.setPreviewDisplay(holder);
			Camera.Parameters parameters = camera.getParameters();
			// parameters.setPictureFormat(PixelFormat.);
			// parameters.getSupportedPictureFormats();
			List<Camera.Size> s_previewSize = parameters
					.getSupportedPreviewSizes();
			List<Camera.Size> s_pictureSize = parameters
					.getSupportedPictureSizes();
			boolean vet = setPreveiewAndPictureSize(s_previewSize,
					s_pictureSize, windowSize_height);

			// record env.log
			FileUtil.recordSupportSize(s_previewSize, s_pictureSize);
			FileUtil.recordEnv("PreviewSize: (" + previewSize.width + " , "
					+ previewSize.height + ")");
			FileUtil.recordEnv("PictureSize: (" + pictureSize.width + " , "
					+ pictureSize.height + ")");

			parameters.setPreviewSize(previewSize.width, previewSize.height);

			try {
				parameters
						.setPictureSize(pictureSize.width, pictureSize.height);
				camera.setParameters(parameters);
				camera.setPreviewDisplay(holder);
				// camera.startPreview();

			} catch (Exception e) {
				log_file.saveLog(e, "surfaceCreated");
			}
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			// e.printStackTrace();
			log_file.saveLog(e, "surfaceCreated");
		}
		camera.startPreview();

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Toast.makeText(getApplicationContext(), "surfaceStop", 100).show();
		// TODO 自动生成方法存根
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}

	}

	private ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Toast.makeText(getApplicationContext(), "shutterCallback",
			// 100).show();
		}
	};

	private PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
			// TODO Handle RAW image data
			// Toast.makeText(getApplicationContext(), "rawcallback",
			// 100).show();
		}
	};

	private PictureCallback jpegCallback = new PictureCallback() {

		public void onPictureTaken(byte[] _data, Camera _camera) {
			// Toast.makeText(getApplicationContext(), "jpegCallback",
			// 100).show();
			// Toast.makeText(getApplicationContext(), "jepgCallback",
			// 100).show();

			log_file.saveLog("image data_length : " + _data.length);
			Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length);
			// Bitmap bm = BitmapFactory.decodeByteArray(_data, 0,
			// _data.length);
			if (flag == 0) {
				flag = 1;

				ImageSize sizes = curWindow.getImageSize("left",
						pictureSize.width, pictureSize.height);

				Bitmap targetbm_left = Bitmap.createBitmap(bm, sizes.x,
						sizes.y, sizes.width, sizes.height);
				sizes = curWindow.getImageSize("right", pictureSize.width,
						pictureSize.height);
				Bitmap targetbm_right = Bitmap.createBitmap(bm, sizes.x,
						sizes.y, sizes.width, sizes.height);

				FileUtil.memoryOneImage(targetbm_left, "left");
				FileUtil.memoryOneImage(targetbm_right, "float");
				FileUtil.memoryOneImage(bm, "whole");

				leftImage.setVisibility(View.VISIBLE);
				leftImage.setImageBitmap(targetbm_left);
				// xiangjiImage.setVisibility(View.VISIBLE);
				moveImage.setVisibility(View.GONE);
				// picFrameImageL.setVisibility(View.GONE);
				// picFrameImageR.setVisibility(View.VISIBLE);
				rightImage.setVisibility(View.INVISIBLE);
				floatImage.setVisibility(View.INVISIBLE);
				// rightImage.setBackgroundColor(Color.BLACK);
				// rightImage.getBackground().setAlpha(200);
				// rightImage.setVisibility(View.VISIBLE);
				noButton.setVisibility(View.INVISIBLE);
				floatImage.setVisibility(View.VISIBLE);
				floatImage.setImageBitmap(targetbm_right);
				floatImage.setAlpha(99);
				noButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						noButton.setEnabled(false);
						// TODO Auto-generated method stub
						leftImage.setVisibility(View.GONE);
						noButton.setVisibility(View.GONE);
						rightImage.setVisibility(View.VISIBLE);
						rightImage
								.setBackgroundResource(R.drawable.shadow_right);
						moveImage.setVisibility(View.VISIBLE);
						floatImage.setVisibility(View.INVISIBLE);
						xiangjiImage.setVisibility(View.GONE);
						// picFrameImageL.setVisibility(View.VISIBLE);
						// picFrameImageR.setVisibility(View.GONE);
						flag = 0;
						can_drag = true;
						camera.startPreview();
						noButton.setEnabled(true);
					}
				});
				Camera.Parameters parameters = camera.getParameters();
				if (parameters.isAutoExposureLockSupported()) {
					// Toast.makeText(getApplicationContext(),
					// "support expoure lock", 100).show();
					parameters.setAutoExposureLock(true);
					camera.setParameters(parameters);
				}
				touchFocusListener.refreshFocusBox(flag);
				camera.stopPreview();
				camera.startPreview();
				text.setVisibility(View.INVISIBLE);
				// targetbm_left.recycle();
				// targetbm_right.recycle();
				// bm.recycle();
				takephotoButton.setEnabled(true);
			} else {
				flag = 0;
				ImageSize sizes = curWindow.getImageSize("right",
						pictureSize.width, pictureSize.height);
				Bitmap targetbm_right = Bitmap.createBitmap(bm, sizes.x,
						sizes.y, sizes.width, sizes.height);
				String path = FileUtil.memoryOneImage(targetbm_right, "right");

				Intent finalImageIntent = new Intent(
						HachathonMainActivity.this,
						HachathonFinalImageActivity.class);
				finalImageIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				finalImageIntent.putExtra("HkWindow", curWindow);
				startActivity(finalImageIntent);
				if (camera != null) {
					camera.stopPreview();
					camera.release();
					camera = null;
				}
				HachathonMainActivity.this.finish();
				// targetbm_right.recycle();
				// bm.recycle();
			}

		}
	};

	public boolean onTouch(View v, MotionEvent event) {
		if (camera == null)
			return true;
		// if (mGestureDetector == null)
		// return true;
		// mGestureDetector.onTouchEvent(event);
		if (MotionEvent.ACTION_DOWN == event.getAction())
			touchFocusListener.onTouch(v, event);

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
			if (Math.abs(dx) > 20)
				touchFocusListener.refreshFocusBox(flag);

			lastX = cx;
			lastY = cy;
			// text.setText("curFrameX:" + curWindow.curFrameX + "   cx:" + cx
			// + "  cy:" + cy + "   dx:" + dx + "  paramLeft.width:"
			// + paramLeft.width + "   paramRight.width:"
			// + paramRight.width);
			break;

		case MotionEvent.ACTION_UP:
			isSetFrame = false;
			break;
		}
		return true;
	}

	/**
	 * set the Optimal previewSize and pictureSize 1，找跟屏幕比例最接近的preview size
	 * 2，找与选中的preview size 一致的 picture size 3，在符合条件的picture size
	 * 中找与target_pic_height 最接近的尺寸 3，设置previewSize pictureSize
	 * 
	 * @param s_previewSize
	 *            所有支持的preview size
	 * @param s_pictureSize
	 *            所有支持的picture size
	 * @param target_pic_height
	 *            目标图片比例
	 * @return 找到合适的返回true。 否则false
	 */
	private boolean setPreveiewAndPictureSize(List<Camera.Size> s_previewSize,
			List<Camera.Size> s_pictureSize, int target_pic_height) {
		double precision = 0.00001;
		double windowSize_ratio = (double) windowSize_width / windowSize_height;
		double tolerance_first = 0, tolerance_max = 0.5, tolerance_dt = 0.1;

		// 1.第一层， 循环可接受的preview 与屏幕的比例差距
		for (double tolerance_cur = tolerance_first; tolerance_cur <= tolerance_max; tolerance_cur += tolerance_dt) {
			// 2.第二层， 循环所有支持的previewsize
			for (Size preSize : s_previewSize) {
				double preRatio = (double) preSize.width
						/ (double) preSize.height;
				if (Math.abs(preRatio - windowSize_ratio) - tolerance_cur > precision)
					continue;
				log_file.saveLog("preSize :" + preRatio + " tolerance_cur : "
						+ tolerance_cur + "windowSize_ratio: "
						+ windowSize_ratio);
				// 找到最可接受的preview size， 用这个size 尝试去找同比例的picture size
				int min_diff = target_pic_height;
				Size cur_best_pic = null;
				// 3.第三层， 循环所有支持的picturesize
				for (Size picSize : s_pictureSize) {
					double picRatio = (double) picSize.width
							/ (double) picSize.height;

					if (Math.abs(picRatio - preRatio) <= precision) {
						// preview size 与 picture size 相等
						if (cur_best_pic == null
								|| Math.abs(picSize.height - target_pic_height) < min_diff) {
							min_diff = Math.abs(picSize.height
									- target_pic_height);
							cur_best_pic = picSize;
						}
					}
				}
				if (cur_best_pic != null) {
					previewSize = preSize;
					pictureSize = cur_best_pic;
					return true;
				}
			}
		}
		return false;
	}

}
