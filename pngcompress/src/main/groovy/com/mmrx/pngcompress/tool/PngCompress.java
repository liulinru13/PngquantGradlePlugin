package com.mmrx.pngcompress.tool;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PngCompress implements PngquantTool.ICompressFinishCallBack {

    private final String RECORD_FILE_NAME = "pngcompress_modify_record.record";

    private final String toolDirPath;
    private final String[] pngResDirs;
    private final String fileNameRegexStr;//文件名的正则匹配
	private final String filePathRegexStr;//文件路径的正则匹配
	
    private boolean useModifiedTimeFilter = false;//是否使用文件修改时间来过滤文件
    private Byte[] fileLock = new Byte[0];

    /**
     * 
     * @param toolDirPath 工具所在位置的路径
     * @param pngResDirs 要处理的图片父文件夹路径数组
     */
    public PngCompress(String toolDirPath,String[] pngResDirs){
        this.toolDirPath = toolDirPath;
        this.pngResDirs = pngResDirs;
        fileNameRegexStr = null;
        filePathRegexStr = null;
    }
    
    /**
     * 
     * @param toolDirPath 工具所在位置的路径
     * @param pngResDirs 要处理的图片父文件夹路径数组
     * @param filePathRegexStr 要过滤的文件路径正则表达式
     * @param fileNameRegexStr 要过滤的文件名称正则表达式
     */
    public PngCompress(String toolDirPath,String[] pngResDirs,String filePathRegexStr,String fileNameRegexStr){
        this.toolDirPath = toolDirPath;
        this.pngResDirs = pngResDirs;
        this. fileNameRegexStr = fileNameRegexStr;
        this.filePathRegexStr = filePathRegexStr;
    }

    public void doCompress(){

        List<String> pathList = new ArrayList<>();
        long modifiedTime = getLastModifiedTime(toolDirPath);
        if(!useModifiedTimeFilter){
            modifiedTime = -1L;
        }
        
        ImageFileFilter.getInstance().setFileNameRegexStr(this.fileNameRegexStr);
        ImageFileFilter.getInstance().setFilePathRegexStr(this.filePathRegexStr);
        
        for(String path : pngResDirs) {
            List<String> paths = ImageFileFilter.getInstance()
                    .getAllImageFileAbsPath(modifiedTime, path);
            pathList.addAll(paths);
        }

        //初始化线程池
        int cpuNums = Runtime.getRuntime().availableProcessors();
        ExecutorService es = Executors.newFixedThreadPool(cpuNums > 8? 8:cpuNums);

        int singleNum = pathList.size()/cpuNums;
        int startIndex = 0;

        long startTime = System.currentTimeMillis();

        while(startIndex+singleNum <= pathList.size() && pathList.size() > 0){
            List<String> temp = pathList.subList(startIndex,startIndex+singleNum);
            startIndex += singleNum;
            PngquantTool tool = new PngquantTool(toolDirPath,temp,this);
            tool.setStartTime(startTime);
            es.execute(tool);
        }
        //判断是否还有剩余的文件需要执行
        if(startIndex < pathList.size()){
            List<String> temp = pathList.subList(startIndex,pathList.size());
            PngquantTool tool = new PngquantTool(toolDirPath,temp,this);
            tool.setStartTime(startTime);
            es.execute(tool);
        }
        es.shutdown();

    }

    @Override
    public void onResult(boolean succ) {
        if(succ){
            writeModifiedTimeToFile();
        }
    }

    public boolean isUseModifiedTimeFilter() {
        return useModifiedTimeFilter;
    }

    public void setUseModifiedTimeFilter(boolean useModifiedTimeFilter) {
        this.useModifiedTimeFilter = useModifiedTimeFilter;
    }

    private void writeModifiedTimeToFile(){
        synchronized (fileLock) {
            try {
                File recordFile = new File(toolDirPath, RECORD_FILE_NAME);
                if (recordFile.exists()) {
                    FileWriter fw = new FileWriter(recordFile, false);
                    fw.write(System.currentTimeMillis() + "");
                    fw.flush();
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private long getLastModifiedTime(String toolPath){
        //在 toolPath 路径下搜索 RECORD_FILE_NAME 文件，没有的话创建
        try {
            File recordFile = new File(toolPath, RECORD_FILE_NAME);
            boolean isNew = false;
            if (!recordFile.exists()) {
                recordFile.createNewFile();
                isNew = true;
            }
            //不是新文件的话，读取文件中的记录时间
            if(!isNew && recordFile.exists()){
                FileReader fr = new FileReader(recordFile);
                char[] chars = new char[100];
                fr.read(chars);
                String timeStr = new String(chars).trim();
                if("".equals(timeStr))
                    return -1L;
                long modifiedTime = Long.parseLong(timeStr);
                fr.close();
                return modifiedTime;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return -1L;
    }



}
