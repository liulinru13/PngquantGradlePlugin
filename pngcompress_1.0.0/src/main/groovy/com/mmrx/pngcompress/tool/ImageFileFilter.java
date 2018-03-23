package com.mmrx.pngcompress.tool;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

/**
 * 过滤指定目录下所有的图片文件，将图片文件全部存储于列表中返回
 * */
public class ImageFileFilter {
	private CustomFilter dirFilter;
	private ImageFileFilter(){
		dirFilter = new CustomFilter();
	}
	
	public static ImageFileFilter getInstance(){
		return FileFilterHolder.instance;
	}
	
	public List<String> getAllImageFileAbsPath(String rootDirectoryPath){
		List<String> paths = null;
		File rootDirectory = new File(rootDirectoryPath);
		LocalLog.log("getAllImageFileAbsPath", rootDirectoryPath);
		if(!rootDirectory.exists() || !rootDirectory.isDirectory()){
			LocalLog.log("getAllImageFileAbsPath", rootDirectoryPath + " is not exist");
			return paths;
		}
		paths = new LinkedList<String>();

		findImageFromDirectory(paths,rootDirectory);
		return paths;
	}
	

	private void findImageFromDirectory(List<String> imagePaths,File dir){
		if(imagePaths == null || !dir.exists() ||dir.isFile() 
				|| imagePaths == null){
			return;
		}

		File[] imageFiles = dir.listFiles(dirFilter.setFilterType(FilterType.PNG));
		for(File file: imageFiles){
			imagePaths.add(file.getAbsolutePath());
			LocalLog.log("findImageFromDirectory", file.getAbsolutePath());
		}

		File[] dirFiles = dir.listFiles(dirFilter.setFilterType(FilterType.DIR));
		for(File childDir:dirFiles){
			findImageFromDirectory(imagePaths,childDir);
		}
		
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
					return true;
				}
			}
			return false;
		}
		
		private boolean acceptDirectory(File file){
			if(file != null && file.exists()){
				if(file.isDirectory()){
					return true;
				}
			}
			return false;
		}
		
	}
}
