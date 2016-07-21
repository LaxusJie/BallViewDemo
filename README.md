#BallView
-------------------

展示一个水波纹样式的小球，其实和早期的360监控流量的小球差不多啦，改了一种样式


![小球动态效果](http://img.blog.csdn.net/20160721175435160)

#引入到你的项目
-------------------
**copy** BallView.java 和 mipmap-xxhdpi里面的图片到项目中即可

#用法
-------------------
1.在xml中调用
```
    <com.app.ballviewdemo.BallView
        android:id="@+id/ballview"
        android:layout_width="250dp"
        android:layout_height="250dp"
        android:layout_centerHorizontal="true"/>
```
2.在Activity中调用
```
    private BallView ballView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ballView = (BallView) findViewById(R.id.ballview);
        //设置目标值
        ballView.setTarget(70);
        ballView.start();
    }
```
