package com.hackathon.common.util;

import java.util.ArrayList;
import java.util.List;

import com.hackathon.entity.FinalImageWindow;
import com.hackathon.entity.ImageSize;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.Camera.Size;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;

public class GeometryUtil {
	private static String TAG = "GeometryUtil";

	public static float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	public static void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

//	public static Size getOptimalSize(List<Size> sizes, int veiw_width,
//			int view_height) {
//		double targetRatio = (double) veiw_width / view_height;
//		double tolerance_first = 0, tolerance_max = 0.5, tolerance_dt = 0.1;
//		if (sizes == null)
//			return null;
//
//		Size optimalSize = null;
//		double minDiff = Double.MAX_VALUE;
//
//		int targetHeight = view_height;
//		// find an size match both ratio and size among a given tolerance range
//		boolean record = false;
//		for (double tolerance_cur = tolerance_first; tolerance_cur <= tolerance_max; tolerance_cur += tolerance_dt) {
//			for (Size size : sizes) {
//				
//				double ratio = (double) size.width / (double)size.height;
//				if (record == false)
//					FileUtil.recordEnv("support size : ("+ size.width + " , " + size.height +")  radio :" + ratio );
//				if (Math.abs(ratio - targetRatio) > tolerance_cur)
//					continue;
//				if (Math.abs(size.height - targetHeight) < minDiff) {
//					optimalSize = size;
//					minDiff = Math.abs(size.height - targetHeight);
//				}
//			}
//			FileUtil.recordEnv("");
//			record = true;
//		}
//
//		// failed to find that kind of size, ignore the radio
//		if (optimalSize == null) {
//			Log.i(TAG, "Ignore radio when try to find optimal size");
//			minDiff = Double.MAX_VALUE;
//			for (Size size : sizes) {
//				if (Math.abs(size.height - targetHeight) < minDiff) {
//					optimalSize = size;
//					minDiff = Math.abs(size.height - targetHeight);
//				}
//			}
//		}
//		return optimalSize;
//	}

	/**
	 * 等比例所犯preSize 使其大小限制在window_width 和  window_height 之中
	 * @param preSize
	 * @param window_width
	 * @param window_height
	 */
	public static void uniformScale(Size preSize, int window_width, int window_height)
	{
		int[] pre = {preSize.width, preSize.height};
		GeometryUtil.uniformScale(pre, window_width, window_height);
		preSize.width = pre[0];
		preSize.height = pre[1];
	}
	/**
	 * 等比例所犯preSize 使其大小限制在window_width 和  window_height 之中
	 * @param preSize
	 * @param window_width
	 * @param window_height
	 */
	public static void uniformScale(int[] preSize, int window_width, int window_height)
	{
		float previewRadio = (float)preSize[0] / (float)preSize[1];
		float windowRadio = (float)window_width / (float)window_height;
		if (previewRadio > windowRadio)
		{
			preSize[0] = window_width;
			preSize[1] = (int) (preSize[0] / previewRadio);
		}
		else
		{
			preSize[1] = window_height;
			preSize[0] = (int)(preSize[1] * previewRadio);
		}
	}
	
}
