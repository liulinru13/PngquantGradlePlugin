package com.mmrx.pngcompress.tool;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 过滤指定目录下所有的图片文件，将图片文件全部存储于列表中返回
 * */
public class ImageFileFilter {
	private CustomFilter dirFilter;
	private SimpleDateFormat dateFormat;
	private long lastModifiedTime = -1L;
	
	private String fileNameRegexStr;//文件名的正则匹配
	private String filePathRegexStr;//文件路径的正则匹配

	private ImageFileFilter(){
		dirFilter = new CustomFilter();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	public static ImageFileFilter getInstance(){
		return FileFilterHolder.instance;
	}
	
	//获取参数路径下所有符合要求的png图片的绝对路径列表
	public List<String> getAllImageFileAbsPath(long lastModifiedTime,String rootDirectoryPath){

		this.lastModifiedTime = lastModifiedTime;
		List<String> paths = null;
		File rootDirectory = new File(rootDirectoryPath);
		LocalLog.log("getAllImageFileAbsPath", rootDirectoryPath);
		if(!rootDirectory.exists() || !rootDirectory.isDirectory()){
			LocalLog.log("getAllImageFileAbsPath", rootDirectoryPath + " is not exist");
			return paths;
		}
		paths = new ArrayList<>();

		findImageFromDirectory(paths,rootDirectory);
		return paths;
	}
	
	public void setFileNameRegexStr(String str){
		fileNameRegexStr = str;
	}
	
	public void setFilePathRegexStr(String str){
		filePathRegexStr = str;
	}

	private void findImageFromDirectory(List<String> imagePaths,File dir){
		if(imagePaths == null || !dir.exists() ||dir.isFile() 
				|| imagePaths == null){
			return;
		}

		File[] imageFiles = dir.listFiles(dirFilter.setFilterType(FilterType.PNG));
		for(File file: imageFiles){
			long modifiedTime = findFileLastModifyTime(file);
			if(hasModified(modifiedTime)){
				imagePaths.add(file.getAbsolutePath());
				LocalLog.log("findImageFromDirectory", file.getAbsolutePath());
			}
		}

		File[] dirFiles = dir.listFiles(dirFilter.setFilterType(FilterType.DIR));
		for(File childDir:dirFiles){
			findImageFromDirectory(imagePaths,childDir);
		}
		
	}
	
	/**
	 * 和上次的处理时间作对比，修改时间在处理时间之后的
	 * 表示图片有改动，需要重新压缩一下
	 * @param modifiedTime
	 * @return
	 */
	private boolean hasModified(long modifiedTime){
		if(modifiedTime > lastModifiedTime)
			return true;
		return false;
	}
	
	/**
	 * 获取文件最后修改时间
	 * @param file
	 */
	private long findFileLastModifyTime(File file){
		Calendar cal = Calendar.getInstance();
		long modifedTime = file.lastModified();
		LocalLog.log("findFileLastModifyTime", "file " 
		+ file.getName() 
		+ " modified time is " 
		+ dateFormat.format(modifedTime));
		return modifedTime;
	}
	
	public static final class FileFilterHolder{
		private static final ImageFileFilter instance = new ImageFileFilter();
	}
	
	enum FilterType{
		PNG(1),
		DIR(2);
		
		private int content = 0;
		private FilterType(int con){
			this.content = con;
		}
	}
	

	/**
	 *文件过滤类
	 */
	private class CustomFilter implements FileFilter{
		private final String SUFFIX_PNG = ".png";
		private final String SUFFIX_9PNG = ".9.png";
		
		private FilterType type = FilterType.PNG;
		
		public CustomFilter setFilterType(FilterType type){
			this.type = type;
			return this;
		}
		
		@Override
		public boolean accept(File file) {
			// TODO Auto-generated method stub
			switch(type){
				case PNG:
					return acceptPng(file);
				case DIR:
					return acceptDirectory(file);
			}
			return false;
		}
		
		private boolean acceptPng(File file){
			if(file != null && file.exists()){
				String fileName = file.getName();
				if(fileName != null 
						&& !fileName.endsWith(SUFFIX_9PNG) 
						&& fileName.endsWith(SUFFIX_PNG)){
					
					if(fileNameRegexStr != null){
						return !regexMatch(fileNameRegexStr,fileName);
					}
					
					return true;
				}
			}
			return false;
		}
		
		private boolean acceptDirectory(File file){
			if(file != null && file.exists()){
				if(file.isDirectory()){
					if(filePathRegexStr != null){
						return !regexMatch(filePathRegexStr,file.getPath());
					}
					return true;
				}
			}
			return false;
		}
		
		/**
		 * 是否匹配正则表达式
		 * @param regex
		 * @param content
		 * @return
		 */
		private boolean regexMatch(final String regex,final String content){
			//正则表达式匹配
			try{
				 boolean match = Pattern.compile(regex).matcher(content).find();
				return match;
				}catch(Exception e){
					e.printStackTrace();
					//遇到异常，不进行过滤这个文件
					return true;
				}
		}
		
		
	}
}