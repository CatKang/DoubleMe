package com.hackathon.common.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.hackathon.activity.HachathonMainActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	private static String TAG = "FileUtil";
	private static String root_dir = Environment.getExternalStorageDirectory()
			+ "/cache_heying";
	private static String final_dir = Environment.getExternalStorageDirectory()
			+ "/HeyingCamera";
	private static String schedul_path = "schedul.txt";
	private static String evn_log_path = "/env.txt";

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
		else if ("final_direct".equals(type))
			path += "/camera_final_direct.jpg";
		else if ("final_left_tmp".equals(type))
			path += "/camera_final_left_tmp.jpg";
		else if ("final_right_tmp".equals(type))
			path += "/camera_final_right_tmp.jpg";
		else if ("final".equals(type))
			return final_dir + "/" + System.currentTimeMillis() + "final.jpg";
		else if ("final_record_left".equals(type))
			path += "/" + System.currentTimeMillis() + "final_left.jpg";
		else if ("final_record_right".equals(type))
			path += "/" + System.currentTimeMillis() + "final_right.jpg";
		else
			return null;
		return path;
	}

	public static void init_file_env() {
		Log.i(TAG, "initEnv");
		File dir = new File(root_dir);
		if (!dir.exists())
			dir.mkdir();
		File fdir = new File(final_dir);
		if (!fdir.exists())
			fdir.mkdir();
		File env_log = new File(root_dir + evn_log_path);
		if (env_log.exists())
			env_log.delete();

		clearCache();
	}

	public static Bitmap loadBitmapFromFile(String type) {
		String path = getFilePathByType(type);
		if (path == null)
			return null;
		Bitmap load_bitmap = BitmapFactory.decodeFile(path);
		return load_bitmap;
	}

	public static String memoryOneImage(Bitmap bitmap, String type) {
		String path = null;
		path = getFilePathByType(type);
		File myCaptureFile = new File(path);
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			return path;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return path;
		} catch (IOException e) {
			e.printStackTrace();
			return path;
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
				String fineName = oldFile[i].getName();
				String fileType = fineName.substring(
						fineName.lastIndexOf(".") + 1, fineName.length());
				if ("jpg".equals(fileType))
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

	public static void readSchedule() {
		// TODO read from file

	}

	public static void recordSupportSize(List<Camera.Size> pre_size,
			List<Camera.Size> pic_size) {

		recordEnv("***************preview size*************");
		for (Size one_size : pre_size) {
			recordEnv("PreviewSupportSize: (" + one_size.width + " , "
					+ one_size.height + ") radio : "
					+ ((double) one_size.width / (double) one_size.height));
		}
		recordEnv("***************picture size*************");
		for (Size one_size : pic_size) {
			recordEnv("PictureSupportSize: (" + one_size.width + " , "
					+ one_size.height + ") radio : "
					+ ((double) one_size.width / (double) one_size.height));
		}
	}

	public static void recordEnv(String content) {
		try {
			File saveFile = new File(root_dir, evn_log_path);
			FileOutputStream outStream = new FileOutputStream(saveFile, true);
			outStream.write((content + "\r\n").getBytes());
			outStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}

	// public static void memoryImages() {
	// Bitmap target_whole = loadBitmapFromFile("whole");
	// Bitmap target_cur = Bitmap.createBitmap(target_whole, 0,0, curFrameX -
	// viewX + dx, imageSizeY);
	// memory(file_path, target_cur);
	// }
	// Bitmap target_left = BitmapFactory.decodeFile(strCaptureFilePathLeft);
	// memory(root_dir + "/ImageLeft_" + System.currentTimeMillis() +
	// ".jpg",target_left);
	// Bitmap target_right = BitmapFactory.decodeFile(strCaptureFilePathRight);
	// memory(root_dir + "/ImageRight_" + System.currentTimeMillis() +
	// ".jpg",target_right);
	// //memory(root_dir + "/ImageLeft_" + System.currentTimeMillis() + ".jpg",
	// // target_whole);
	// for (int dx = 100; dx <= 100; dx += 100) {
	// String file_path = root_dir + "/ImagePart_" + dx
	// + System.currentTimeMillis() + ".jpg";
	// }
	//
	// Bitmap target_right = loadBitmapFromFile("final_right");
	// memory(root_dir + "/ImageRight_" + System.currentTimeMillis() + ".jpg",
	// target_right);
	//
	// }
	//
	//
	//
	// }
}
