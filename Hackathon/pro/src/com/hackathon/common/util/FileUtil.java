package com.hackathon.common.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	private static String TAG = "FileUtil";
	private static String root_dir = Environment.getExternalStorageDirectory()
			+ "/high_camera";
	private static String schedul_path = "schedul.txt";

	public static String getFilePathByType(String type) {
		String path = root_dir;
		if ("right".equals(type))
			path += "/camera_snap_right.jpg";
		else if ("left".equals(type))
			path += "/camera_snap_left.jpg";
		else if ("whole".equals(type))
			path += "/camera_snap_whole.jpg";
		else if ("float".equals(type))
			path += "/camera_snap_float.jpg";
		else if ("final_left".equals(type))
			path += "/camera_final_left.jpg";
		else if ("final_right".equals(type))
			path += "/camera_final_right.jpg";
		else if ("final_tmp".equals(type))
			path += "/camera_final_left_Proc.jpg";
		else if ("final_left_tmp".equals(type))
			path += "/camera_final_left_tmp.jpg";
		else if ("final_right_tmp".equals(type))
			path += "/camera_final_right_tmp.jpg";
		else if ("final".equals(type))
			path += "/final_"+ System.currentTimeMillis()+".jpg";
		else
			return null;
		return path;
	}

	public static void init_file_env() {
		Log.i(TAG, "initEnv");
		File dir = new File(root_dir);
		if (!dir.exists())
			dir.mkdir();
	}

	public static Bitmap loadBitmapFromFile(String type) {
		String path = getFilePathByType(type);
		if (path == null)
			return null;
		Bitmap load_bitmap = BitmapFactory.decodeFile(path);
		return load_bitmap;
	}

	public static void memoryOneImage(Bitmap bitmap, String type) {
		String path = getFilePathByType(type);
		File myCaptureFile = new File(path);
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean checkSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static void clearCache() {
		String dir = root_dir;
		File delfolder = new File(dir);
		File oldFile[] = delfolder.listFiles();
		try {
			for (int i = 0; i < oldFile.length; i++) {
				oldFile[i].delete();
			}
		} catch (Exception e) {
			System.out.println("清空文件夹操作出错!");
			e.printStackTrace();
		}
	}

	private static void memory(String path, Bitmap bitmap) {

		File myCaptureFile = new File(path);
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void readSchedule()
	{
		//TODO read from file
		
		
	}
	
//	public static void memoryImages() {
//		Bitmap target_whole = loadBitmapFromFile("whole");
//		Bitmap target_cur = Bitmap.createBitmap(target_whole, 0,0, curFrameX - viewX + dx, imageSizeY);
//				 memory(file_path, target_cur);
//				 }
//				 Bitmap target_left = BitmapFactory.decodeFile(strCaptureFilePathLeft);
//				 memory(root_dir + "/ImageLeft_" + System.currentTimeMillis() +
//				 ".jpg",target_left);
//				 Bitmap target_right = BitmapFactory.decodeFile(strCaptureFilePathRight);
//				 memory(root_dir + "/ImageRight_" + System.currentTimeMillis() +
//				 ".jpg",target_right);
//		//memory(root_dir + "/ImageLeft_" + System.currentTimeMillis() + ".jpg",
//		//		target_whole);
//		for (int dx = 100; dx <= 100; dx += 100) {
//			String file_path = root_dir + "/ImagePart_" + dx
//					+ System.currentTimeMillis() + ".jpg";
//		}
//		
//		Bitmap target_right = loadBitmapFromFile("final_right");
//		memory(root_dir + "/ImageRight_" + System.currentTimeMillis() + ".jpg",
//				target_right);
//		
//	}
//	 
//	
//	
//	 }
}
