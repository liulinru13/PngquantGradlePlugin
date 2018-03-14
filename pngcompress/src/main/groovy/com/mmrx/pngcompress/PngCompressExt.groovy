package com.mmrx.pngcompress

class PngCompressExt{
    Boolean enable;
    Boolean logEnable = false;//日志功能默认关闭

    String resDir;//资源文件上级目录
    String pngquantToolDir;//pngquant工具所在目录
    String doBeforeTaskName;//指定该任务执行的顺序在哪一个任务之前执行
}