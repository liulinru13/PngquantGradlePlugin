package com.mmrx.pngcompress

import com.mmrx.pngcompress.tool.LocalLog
import com.mmrx.pngcompress.tool.PngquantTool
import org.gradle.api.DefaultTask
import com.mmrx.pngcompress.tool.*
import org.gradle.api.tasks.TaskAction

class PngCompressTask extends DefaultTask{

    @TaskAction
    void doPngCompress(){
        PngCompressExt ext = project.extensions.pngCompressExt
        if(ext.enable) {
            if(ext.resDirs != null){
                for(int i=0;i<ext.resDirs.length;i++){
                    if(ext.resDirs[i].startsWith("file:/")){
                        ext.resDirs[i] = ext.resDirs[i].substring(5);
                    }
                    LocalLog.log("doPngCompress resDirs = ", ext.resDirs[i]);
                }
            }
            if (ext.pngquantToolDir != null && ext.pngquantToolDir.startsWith("file:/")) {
                ext.pngquantToolDir = ext.pngquantToolDir.substring(5)
                LocalLog.log("doPngCompress pngquantToolDir = ", ext.pngquantToolDir);

            }
            LocalLog.IS_ON = ext.logEnable;
            PngCompress compress = new PngCompress(ext.pngquantToolDir,ext.resDirs
                    ,ext.filePathRegex,ext.fileNameRegex);
            compress.setUseModifiedTimeFilter(ext.useModifiedTimeFilter == null ?
                    false : ext.useModifiedTimeFilter)
            compress.doCompress();
        }else{
            LocalLog.log("doPngCompress","do nothing");
        }
    }
}