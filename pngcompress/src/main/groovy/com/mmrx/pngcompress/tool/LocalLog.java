package com.mmrx.pngcompress.tool;

public class LocalLog {
	public static boolean IS_ON = true; 
	
	public static void log(String tag, String absolutePath) {
		// TODO Auto-generated method stub
		if(IS_ON){
			System.out.println("LOG: " + tag + " msg: " + absolutePath);
		}
	}
}
