# AlarmDemo

#### 模拟三种方式设置定时器，同时研究Service的生命周期
 
 1、在Activity中以setRepeating方式设置定时器，要持锁保持不熄屏Alarm才能正常触发
 
 2、通过startService启动一个服务，在服务中以set方式设置定时器，然后通过循环启动服务来保证任务在后台定时执行
 
 3、通过bindService绑定一个服务，在服务中以setRepeating方式设置定时器，服务在后台执行因此不需要持锁Alarm就能正常触发
