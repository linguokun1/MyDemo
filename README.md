闲余时间写各种app功能的小demo.

此Demo分别有5个模块功能:
一.模仿Wokamon的飞钻石效果.
   核心技术难点:1.需确保在快速点击的时候动画不卡顿,使用到了SurfaceView的高性能绘图.
             2.动画的弧线运动轨迹通过贝塞尔曲线+估值器实现.
二.计步器APP核心功能的模仿实现:
   此Demo有以下几个技术难点:
   1.计步传感器中,返回的数据是从开机至今的累计步数,那么如何获取到每一天或者最近三天(或多天)的步数.
   2.如何判断用户是在步行,还是在跑步,还是骑行或者坐车.
   3.如何在后台保持计步,并且当用户杀死程序时,后台计步功能却依然存在.
   
三.脸萌APP核心功能的模仿实现:
   此Demo有以下几个技术难点:
   1.如何实现换妆功能.
   2.众多表情的绘制过程中,如何减少内存的开销.
   3.如何正确保存换妆后的图片.
   
四.星星坠落效果:
   此Demo有以下几个技术难点:
   1.如何让图片运动,并且持续移动.
   2.如何在多个图片持续移动的情况下保持不卡顿.

五.运动轨迹的检测:
   此Demo是百度地图鹰眼sdk的使用的例子,是我下载百度官网上的Demo下来后,经过改动优化而成.
