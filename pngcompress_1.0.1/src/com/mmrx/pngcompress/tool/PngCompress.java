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
    private boolean useModifiedTimeFilter = false;//是否使用文件修改时间来过滤文件
    private Byte[] fileLock = new Byte[0];

    public PngCompress(String toolDirPath,String[] pngResDirs){
        this.toolDirPath = toolDirPath;
        this.pngResDirs = pngResDirs;
    }

    public void doCompress(){

        List<String> pathList = new ArrayList<>();
        long modifiedTime = getLastModifiedTime(toolDirPath);
        if(!useModifiedTimeFilter){
            modifiedTime = -1L;
        }
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
        while(startIndex+singleNum <= pathList.size()){
            List<String> temp = pathList.subList(startIndex,startIndex+singleNum);
            startIndex += singleNum;
            PngquantTool tool = new PngquantTool(toolDirPath,temp,this);
            es.execute(tool);
        }
        //判断是否还有剩余的文件需要执行
        if(startIndex < pathList.size()){
            List<String> temp = pathList.subList(startIndex,pathList.size());
            PngquantTool tool = new PngquantTool(toolDirPath,temp,this);
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
                long modifiedTime = Long.parseLong(new String(chars).trim());
                fr.close();
                return modifiedTime;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return -1L;
    }



}
