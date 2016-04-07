# LyCommon
### A quick framework for Android.Include network,database,image cache.Without anyother dependencies.

###关于网络
1. 新建一个任务(Task)：
```
/**
*@Param int 网络访问类型，这里是Post，Get类型不需要参数
*@Param HttpExecuteLinstener 回调，网络访问的各种状态都在这里回调
**/
DefaultHttpTask task = DefaultHttpTask.create(HttpTask.EXECUTE_TYPE_POST, url, new DefaultHttpTask.HttpExecuteLinstener() {

            @Override
            public void onSuccess(String result) {//网络访问成功，result为服务器返回的字符串
                Log.v("000", "result=" + result);
                textView.setText("result=" + result);
            }

            @Override
            public void onError(int code, String msg) {//网络访问失败，code:错误代码，msg:错误提示
                textView.setText("code=" + code + ", msg=" + msg);
            }

            @Override
            public void onProgress(int progress) {//访问进度，0<=progress<=100
                Log.v("000", "onProgress, progress=" + progress);
            }
        });
```
2. 为Task添加参数(Get方法不需要)：
```
task.addParam("param0", "param0").addParam("param1", "param1");
```
3. 执行Task：
```
HttpUtil.create().execute(task);
```

###关于数据库
以Model类为例

1. 首先需要为你的Model类添加一个id字段：
```
public class Model{
    @Id//添加注解，很重要
    private int id;
    private String yourParam;
    ...
    public Model(){//必须要有一个空构造方法
    }
    //必须为每一个字段添加get和set方法
    public void setId(int id){
        this.id=id;
    }
    public int getId(){
        return id;
    }
    ...
}
```

2. 保存到数据库：
```
LyDB.instance(mContext).save(model);
```
3. 取出Model：
```
LyDB.instance(mContext).query(Model.class);
```

###关于图片异步加载
