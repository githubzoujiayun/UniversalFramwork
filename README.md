UniversalFramwork
=================

Android开发的通用框架，主要包含的功能有 1、图片缓存 2、Http请求处理 3、依赖注入 4、网络状况判断 5、多线程下载 6、实现数据库orm 7、线程调度


1、图片缓存
图片缓存采用Bitmapfun实现，并对Bitmap进行了一些修改，提高了BitmapFun的加载效率,采用了本地线程和网络线程分别加载图片

  
2、Http请求，下载

   Http请求主要参考了AsyncHttpClient和ThinkAndorid等项目，并进行了优化
  
  
3、依赖注入
  
 使用该框架时，所有的Activity都需要继承BaseActivity这个类，从而实现依赖注入
   @InjectView(id=R.id.btn_imgs,click="onClickImg")
  private Button btnImages;
  @InjectView(id=R.id.btn_db,click="onClickDb")
  private Button btnDB;
  @InjectView(id=R.id.btn_job,click="onClickJob")
  private Button btnJobManager;
  @InjectView(id=R.id.btn_http,click="onClickHttp")
  private Button btnHttp;
  @InjectView(id=R.id.btn_down,click="onClickDownLoad")
  private Button btnDownLoad;
  @InjectView(id=R.id.btn_multi_down,click="onClickMultiDownLoad")
  private Button btnMultiDownLoad;
  
  4、数据库
  该开源提供了数据库连接池，当需要操作数据库时，只需要从池中取出一个连接即可，该连接池有一个初始值和一个最大值，用户可以根据需求进行设定

  
  5、线程调度
  通过使用该框架，避免线程泛滥，该调度器可以动态的添加线程和杀死线程，当线程在合适范围内不够用时，会适当添加线程，的那个线程都处于等待，并到一定
  时间时，线程将被杀死
  
  
  
    
