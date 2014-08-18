UniversalFramwork
=================

Android开发的通用框架，主要包含的功能有 1、图片缓存 2、Http请求处理 3、依赖注入 4、网络状况判断 5、多线程下载 6、实现数据库orm 7、线程调度


1、图片缓存
图片缓存采用Bitmapfun实现，并对Bitmap进行了一些修改，提高了BitmapFun的加载效率
 //创建ImageFetcher对象，需要传递Context和图片尺寸
    mFetcher=new ImageFetcher(this ,mGridViewItemSize);
    //创建缓存所需要的参数
    ImageCacheParams cacheParams = new ImageCacheParams(this, "thumb1");
    cacheParams.setMemCacheSizePercent(0.25f);
    //设置ImageView在Loading过程中显示的图片
    mFetcher.setLoadingImage(R.drawable.empty_photo);
    //为mFetcher添加缓存，如果不调用这句，那么图片时无法加载的，mFetcher中的磁盘缓存的锁一直没有释放
    mFetcher.addImageCache(this.getSupportFragmentManager(), cacheParams);
    mAdapter=new ImageAdapter(this, mFetcher);
    mGridView.setAdapter(mAdapter);
    
    
    ...
    
     @Override
  public View getView(int position, View contentView, ViewGroup parent)
  {
    if(contentView==null)
    {
      contentView=new RecyclingImageView(context);
      ((ImageView)contentView).setScaleType(ImageView.ScaleType.CENTER_CROP);
      if(params.height!=mItemHeight)
      {
        params.height=mItemHeight;
      }
      contentView.setLayoutParams(params);
    }
    fetcher.loadImage(Images.imageUrls[position], (ImageView)contentView);
    return contentView;
  }
  
  2、Httt 请求，下载
  Http请求主要是参考了AsyncHttpClinet以及ThinkAndroid来实现，并添加了单线程和多线程下载的功能
  Http使用方法如下，以get为例
  public void onClickBaidu(View view)
  {
    mHttp.get(this, "http://www.baidu.com", new TextHttpResponseHandler(){
      
      @Override
      public void onStart()
      {
        super.onStart();
        if(dialog==null)
        {
          dialog=new ProgressDialog(HttpActivity.this);
          dialog.setMessage("加载数据中...");
        }
        dialog.show();
      }
      @Override
      public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
      {
        super.onSuccess(statusCode, headers, responseBody);
        if(dialog!=null)
        {
          dialog.dismiss();
        }
        
        tv_baidu.setText(new String(responseBody));
        
      }
      
      @Override
      public void onFailure(String responseBody, Throwable error)
      {
        super.onFailure(responseBody, error);
        if(dialog!=null)
        {
          dialog.dismiss();
        }
        
        tv_baidu.setText(error.getMessage());
      }
     
    });
  下载功能参照Demo
  
  
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
  该开源提供了数据库连接池，当需要操作数据库时，只需要从池中取出一个连接即可，该连接池有一个初始值和一个最大值，用户可以根据
  需要进行设置，并实现了简单的orm功能
   public void onClickInsert(View view)
  {
    initSQLiteDataBase();
    if(mSQLiteDataBase!=null)
    {
      Person person=new Person();
      person.setName("gavin");
      person.setAddr("shanghai");
      person.setSex("man");
      mSQLiteDataBase.insert(person);
      showData();
    }
    releaseSQLiteDataBase();
  }
  
  5、线程调度
  通过使用该框架，避免线程泛滥，该调度器可以动态的添加线程和杀死线程，当线程在合适范围内不够用时，会适当添加线程，的那个线程都处于等待，并到一定
  时间时，线程将被杀死
  
  
  
    
