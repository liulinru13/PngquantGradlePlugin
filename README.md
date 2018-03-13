# PngquantGradlePlugin
使用pngquant图片压缩命令行工具的android gradle插件

pngcompress 文件夹下是png压缩插件源码

##使用方式

1.项目`build.gradle`文件中添加插件依赖

```
classpath 'com.mmrx.pngcompress:pngcompress:1.0.0'
```

2.主 module 的`build.gradle`中添加插件依赖和配置

```
apply plugin: 'com.mmrx.pngcompress'
```

```
pngCompressExt{
    enable = true;
    resDir = uri("./src/main/res")
    pngquantToolDir = uri("../pnglib/")
    logEnable = true
}
```

其中，`enable`为`false`则插件无效；
`resDir`是要进行`png`图片压缩的路径；
`pngquantToolDir`是存放命令行工具的目录，需要自行下载添加；
`logEnable`是日志开关；

