package com.hackathon.common.util;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class Log {
	private static String root_dir = Environment.getExternalStorageDirectory()
			+ "/high_camera";
	private static String log_path = "/log.txt";
	private File file = null;

	public Log(){
		this.file = new File(root_dir + log_path);
		
	}
	
	public void saveLog(String fileName) {
		try {

			PrintWriter writer = null;
			FileWriter fileWrite = new FileWriter(file, true);
			writer = new PrintWriter(fileWrite);
			writer.append(System.getProperty("line.separator")
					+ new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss")
							.format(new Date()) + "__" + fileName);
			writer.flush();
			writer.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public void saveLog(Throwable e, String fileName) {
		try {
			PrintWriter writer = null;
			FileWriter fileWrite = new FileWriter(file, true);
			writer = new PrintWriter(fileWrite);
			writer.append(System.getProperty("line.separator")
					+ new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss")
							.format(new Date()) + "__" + fileName);
			writer.append(System.getProperty("line.separator"));
			writer.append("      *************************" + e.toString()
					+ "*************************");
			writer.append(System.getProperty("line.separator"));
			e.printStackTrace(writer);
			writer.flush();
			writer.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}