可见光传输三层协议模型之视频层

## 功能
- Compose：添加首帧，以给定帧数将数据帧合成视频
- Decompose：分解视频，鉴别首帧，提取首帧信息（包括有效帧数量，帧率）


## 环境
- 本地安装FFMPEG，jre1.8
- 下载完整的[bin文件夹](https://github.com/SuperAbee/Mailman/tree/master/bin)，双击startup.bat运行后台程序

## 使用
- Compose：调用接口 http://localhost:9123/video/compose?fps=10&numOfPictures=100&workplace=D:/testpath&imagePrefix=tmp&deleteImages=false

- Decompose：http://localhost:9123/video/decompose?workplace=D:/testpath&imagePrefix=tmp&videoName=output.mp4

## 参数
**Compose**
- fps指定合成视频的帧数；
- numOfPictures是要合入视频的图像数量；
- workplace为工作区，即生成的视频和生成视频所需的图片都存放在该路径下
- imagePrefix为提供图像的前缀，满足前缀+数字+.jpg（数字从1开始并且连续）的图像会参与合成，如tmp1.jpg，tmp2.jpg，tmp3.jpg
- deleteImages，合成后是否删除图像文件

**Decompose**
- workplace，参与分解的视频和生成的图像都存放在该目录下
- imagePrefix，生成图像以该前缀命名
- videoName，解析视频的名字，如output.mp4

## 接口
其他语言（C，C++，C#，Python）调用该接口的实例程序，请下载[interface](https://github.com/SuperAbee/Mailman/tree/master/interface)，并附有详细说明

## 测试
样例视频和图像下载[test-videolayer-interface](https://github.com/SuperAbee/Mailman/tree/master/interface/test-videolayer-interface)



