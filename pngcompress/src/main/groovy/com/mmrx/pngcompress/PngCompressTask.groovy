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
            if (ext.resDir != null && ext.resDir.startsWith("file:/")) {
                ext.resDir = ext.resDir.substring(5)
                LocalLog.log("doPngCompress resDir = ", ext.resDir);
            }
            if (ext.pngquantToolDir != null && ext.pngquantToolDir.startsWith("file:/")) {
                ext.pngquantToolDir = ext.pngquantToolDir.substring(5)
                LocalLog.log("doPngCompress pngquantToolDir = ", ext.pngquantToolDir);

            }
            List<String> paths = ImageFileFilter.getInstance().getAllImageFileAbsPath(ext.resDir);
            PngquantTool tool = new PngquantTool(ext.pngquantToolDir);
            LocalLog.IS_ON = ext.logEnable;
            tool.pngCompress(paths);
            LocalLog.log("doPngCompress","finish");
        }else{
            LocalLog.log("doPngCompress","do nothing");
        }
    }
}