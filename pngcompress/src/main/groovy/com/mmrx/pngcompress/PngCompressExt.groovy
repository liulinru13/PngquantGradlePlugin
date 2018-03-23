package com.mmrx.pngcompress

class PngCompressExt{
    Boolean enable;
    Boolean logEnable = false;//日志功能默认关闭
    Boolean useModifiedTimeFilter = false;//是否使用文件修改时间来过滤不需要压缩的文件
    String[] resDirs;//资源文件上级目录
    String pngquantToolDir;//pngquant工具所在目录
    String doBeforeTaskName;//指定该任务执行的顺序在哪一个任务之前执行
    String filePathRegex;//文件路径正则表达式 "/*\\\\111\\\\/*"
    String fileNameRegex;//文件名称正则表达式 "/*@3x.png$"

}