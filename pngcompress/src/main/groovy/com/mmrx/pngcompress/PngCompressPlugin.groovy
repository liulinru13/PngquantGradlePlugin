package com.mmrx.pngcompress

import org.gradle.api.Plugin
import org.gradle.api.Project

class PngCompressPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        project.extensions.create('pngCompressExt',PngCompressExt)
        project.tasks.create("pngCompressTask",PngCompressTask)
    }
}