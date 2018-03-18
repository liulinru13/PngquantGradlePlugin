package com.mmrx.pngcompress.tool;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 图片压缩工具类
 * */
public class PngquantTool implements Runnable{

	private final String WIN_OS = "Windows";
	private final String MAC_OS = "Mac";
	private final String CMD_WIN_STR = "pngquanti_win -f --ext _temp.png ";
	private final String TEMP_SUFFIX;
	private final String ORIGIN_TEMP_SUFFIX = "_temp.origin";
	private final String SUFFIX = ".png";
	private final String CMD_OS_WIN32 = "cmd /c ";
	private final String PNG_TOOL_NAME_WIN = "pngquanti_win.exe";
	private final String PNG_TOOL_NAME_MAC = "pngquanti_mac";
	private final int SUFFIX_LENGTH = 4;

	private final String osName = System.getProperty("os.name");
	private final String toolPath;
	private final List<String> fileList;
	private ICompressFinishCallBack compressFinishCallBack;
	private PngquantTool() {
		// TODO Auto-generated constructor stub
		toolPath = null;
		TEMP_SUFFIX = "_temp_" + System.currentTimeMillis()/10000 + SUFFIX;
		fileList = null;
	}

	public PngquantTool(String toolPath,List<String> fileList,
						ICompressFinishCallBack compressFinishCallBack) {
		// TODO Auto-generated constructor stub
		this.toolPath = toolPath;
		TEMP_SUFFIX = "_temp_" + System.currentTimeMillis()/10000 + SUFFIX;
		this.fileList = fileList;
		this.compressFinishCallBack = compressFinishCallBack;
	}

	@Override
	public void run() {
		boolean succ = pngCompress();
		LocalLog.log("thread " + Thread.currentThread().getId(),"pngCompress finish");
		if(compressFinishCallBack != null){
			compressFinishCallBack.onResult(succ);
		}
	}

	public boolean pngCompress() {
		if (toolPath == null || fileList == null) {
			LocalLog.log("PngquantTool pngCompress error,toolPath = ",
							toolPath + " fileList = null is " + (fileList == null));
			return false;
		}
		LocalLog.log("PngquantTool", "os " + osName);
		File pngTool = null;
		// windows
		if (osName != null && osName.contains(WIN_OS)) {
			pngTool = new File(toolPath,PNG_TOOL_NAME_WIN);
			if(pngTool.exists()) {
				cmd(CMD_OS_WIN32, CMD_WIN_STR, fileList);
				return true;
			}
		}
		else if (osName != null && osName.contains(MAC_OS)) {
			pngTool = new File(toolPath,PNG_TOOL_NAME_MAC);
			if(pngTool.exists()) {
				cmd(null, null, fileList);
				return true;
			}
		}
		if(pngTool != null) {
			LocalLog.log("PngquantTool error", pngTool.getPath() + " is not exist");
		}
		return false;
	}

	private void cmd(final String osType,final String cmdType,List<String> fileList) {
		for (String path : fileList) {
			boolean succ = _cmd(osType,cmdType,path);
			if (succ) {
				fileDeleteAndReName(path);
			}
		}
	}


	private void fileDeleteAndReName(String originPath) {
		File originFile = new File(originPath);
		if (!originFile.exists() || !originFile.isFile()) {
			return;
		}
		String originName = originFile.getName();
		LocalLog.log("PngquantTool", "fileDeleteAndReName originName "
				+ originName);
		String originDir = originFile.getParent();
		LocalLog.log("PngquantTool", "fileDeleteAndReName originDir "
				+ originDir);
		String tempFileName = null;
		File tempFile = null;
		//寻找压缩后的图片临时文件
		if (originName != null && originName.endsWith(SUFFIX)) {
			tempFileName = modifyFileName(originName,TEMP_SUFFIX);
			tempFile = new File(originDir, tempFileName);
		}
		if (tempFile == null || !tempFile.exists() || !tempFile.isFile()) {
			LocalLog.log("PngquantTool",
					"fileDeleteAndReName TempFile is not exist" + tempFile.getPath());
			return;
		}
		LocalLog.log("PngquantTool",
				"fileDeleteAndReName TempFile " + tempFile.getPath());
		

		String originFileTempName = modifyFileName(originName,ORIGIN_TEMP_SUFFIX);
		boolean originSucc = originFile.renameTo(new File(originDir,originFileTempName));

		if(originSucc){

			originFile = new File(originDir,originFileTempName);
			boolean tempSucc = tempFile.renameTo(new File(originDir,originName));

			if(tempSucc && originFile.exists()){
				originFile.delete();
				LocalLog.log("PngquantTool",
						"fileDeleteAndReName compress successful " + originPath);
				return;
			}
		}

		LocalLog.log("PngquantTool",
				"fileDeleteAndReName rename  " + originPath);
		tempFile.delete();
	}
	

	private String modifyFileName(String originName,final String suffix){
		StringBuilder sb = new StringBuilder();
		String tempFileName = sb
				.append(originName.substring(0, originName.length()
						- SUFFIX_LENGTH)).append(suffix).toString();
		return tempFileName;
	}

	private boolean _cmd(final String cmdByOs,final String cmdType,String imagePath) {
		try {
			Process pr = null;
			if(osName.contains(WIN_OS)){
//				Runtime rt = Runtime.getRuntime();
//				String cmd = cmdByOs + toolPath + cmdType + imagePath;
//				pr = rt.exec(cmd);
				ProcessBuilder pb = new ProcessBuilder(toolPath + "pngquanti_win", "--ext", TEMP_SUFFIX, imagePath);
				pr = pb.start();
			}else if(osName.contains(MAC_OS)) {
				ProcessBuilder pb = new ProcessBuilder(toolPath + PNG_TOOL_NAME_MAC, "--ext", TEMP_SUFFIX, imagePath);
				pr = pb.start();
			}
			if(pr != null) {
				BufferedReader input = new BufferedReader(new InputStreamReader(
						pr.getInputStream(), "GBK"));

				String line = null;
				while ((line = input.readLine()) != null) {
					System.out.println(line);
				}
				int exitVal = pr.waitFor();
				LocalLog.log("thread",Thread.currentThread().getId()+"");
				LocalLog.log("png compress ", imagePath
						+ " Exited with error code " + exitVal);
				return exitVal == 0;
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return false;
	}

	public interface ICompressFinishCallBack{
		void onResult(boolean succ);
	}

}