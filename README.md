# PngquantGradlePlugin
使用pngquant图片压缩命令行工具的android gradle插件

pngcompress 文件夹下是png压缩插件源码

## 使用方式

1.项目`build.gradle`文件中添加插件依赖

仓库地址 https://dl.bintray.com/mmrx/pngquantGradlePlugin


```
compile 'com.mmrx.pngcompress:pngcompress:1.1.0'

```

```
maven{
    url "https://dl.bintray.com/mmrx/pngquantGradlePlugin"
}
```

2.主 module 的`build.gradle`中添加插件依赖和配置

```
apply plugin: 'com.mmrx.pngcompress'
```

> 1.0.0 版本插件配置选项

```
pngCompressExt{
    enable = true
    resDir = uri("./src/main/res")
    pngquantToolDir = uri("../pnglib/")
    logEnable = true
    doBeforeTaskName = 'preDebugBuild'
}
```

> 1.1.0 版本插件配置选项

```
pngCompressExt{
    enable = true
    resDirs = [uri('./src/main/res/'),uri('./src/main/java/')]
    pngquantToolDir = uri("../pnglib/")
    logEnable = true
    useModifiedTimeFilter = false
    doBeforeTaskName = 'preDebugBuild'
    filePathRegex = '/*\\-hdpi$'
    fileNameRegex = '/*_round.png'
}

```

`enable`为`false`则插件无效；

`resDir`是要进行`png`图片压缩的路径；(1.1.0版本中字段名修改为 `resDirs`)

`pngquantToolDir`是存放命令行工具的目录，需要自行下载添加,命令行工具文件名不允许修改 [地址](https://github.com/liulinru13/PngquantGradlePlugin/tree/master/pnglib)；

`logEnable`是日志开关；

`doBeforeTaskName` 插件压缩的`task`在指定名称的`task`之前执行，默认为 `preBuild`，默认情况下，主工程执行build任务第一个执行的就是压缩图片的这个插件任务；

> 1.1.0 新增内容

`useModifiedTimeFilter`是否使用文件修改时间来过滤上次压缩过的图片文件，默认为false；

`filePathRegex`文件路径正则表达式，用于过滤匹配此正则表达式的文件夹路径；

`fileNameRegex`文件名称正则表达式，同上；


## 更新日志
### 1.1.0版本

1. 增加文件路径、文件名过滤的策略，避免压缩某些不允许压缩的图片

2. 增加待压缩图片文件夹多路径支持

3. 增加按cpu核数进行多线程压缩图片的策略，单线程图片量太大了会很慢，公司的电脑压缩将近800图片用了5分钟左右

## TODO

待添加

## 感谢

[pngquant](https://pngquant.org/)

