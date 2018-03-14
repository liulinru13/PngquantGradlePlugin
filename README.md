#PngquantGradlePlugin
使用pngquant图片压缩命令行工具的android gradle插件

pngcompress 文件夹下是png压缩插件源码

##使用方式

1.项目`build.gradle`文件中添加插件依赖

```
compile 'com.mmrx.pngcompress:pngcompress:1.0.0'
```

2.主 module 的`build.gradle`中添加插件依赖和配置

```
apply plugin: 'com.mmrx.pngcompress'
```

```
pngCompressExt{
    enable = true
    resDir = uri("./src/main/res")
    pngquantToolDir = uri("../pnglib/")
    logEnable = true
    doBeforeTaskName = 'preDebugBuild'
}
```

`enable`为`false`则插件无效；
`resDir`是要进行`png`图片压缩的路径；
`pngquantToolDir`是存放命令行工具的目录，需要自行下载添加,命令行工具文件名不允许修改 [地址](https://github.com/liulinru13/PngquantGradlePlugin/tree/master/pnglib)；
`logEnable`是日志开关；
`doBeforeTaskName` 插件压缩的`task`在指定名称的`task`之前执行，默认为 `preBuild`，默认情况下，主工程执行build任务第一个执行的就是压缩图片的这个插件任务。

##感谢
[pngquant](https://pngquant.org/)

