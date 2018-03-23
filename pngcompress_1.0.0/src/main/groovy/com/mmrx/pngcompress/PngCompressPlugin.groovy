package com.mmrx.pngcompress

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class PngCompressPlugin implements Plugin<Project>{

    final String TASK_NAME = "pngCompressTask"
    final String DEFAULT_DO_BEFORE_TASK_NAME = "preBuild"

    @Override
    void apply(Project project) {
        project.extensions.create('pngCompressExt', PngCompressExt)
        project.tasks.create(TASK_NAME, PngCompressTask)
        PngCompressExt ext = project.pngCompressExt
        //添加执行顺序的设置
        if (ext.enable && ext.doBeforeTaskName != null) {
            Task task = project.tasks.findByName(ext.doBeforeTaskName)
            if(task != null){
                task.dependsOn(TASK_NAME)
            }
        }else {
            project.tasks.findByName(DEFAULT_DO_BEFORE_TASK_NAME).dependsOn(TASK_NAME)
        }
    }
}