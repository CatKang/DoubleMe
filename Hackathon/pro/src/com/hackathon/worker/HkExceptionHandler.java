package com.hackathon.worker;

import java.lang.Thread.UncaughtExceptionHandler;


import com.hackathon.common.util.Log;

public class HkExceptionHandler implements UncaughtExceptionHandler {

	private static Log log_file = new Log();
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		try {
			 System.out.println("123");
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log_file.saveLog(ex, thread.getName());
		 System.exit(0); 

		
	}

}
