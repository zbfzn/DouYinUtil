package top.lyfzn.douyinutil;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
private Button start,stop;
private TextView textView,to;
private Intent intent=new Intent();
private long time=System.currentTimeMillis();
private ClipboardManager clipboardManager;
private ClipboardManager.OnPrimaryClipChangedListener cm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init() {
        intent.setAction("cn.myService");
        intent.setPackage(getPackageName());
        start=findViewById(R.id.start);
        stop=findViewById(R.id.stop);
        textView=findViewById(R.id.notice);
        to=findViewById(R.id.toSolve);

        View.OnClickListener oc=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.start:
                        startService(intent);
                        textView.setVisibility(View.VISIBLE);
                        start.setEnabled(false);
                        stop.setEnabled(true);
                        clipboardManager.addPrimaryClipChangedListener(cm);
                        Toast.makeText(MainActivity.this,"服务已启动",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.stop:
                        stopService(intent);
                        textView.setVisibility(View.GONE);
                        start.setEnabled(true);
                        stop.setEnabled(false);
                        clipboardManager.removePrimaryClipChangedListener(cm);
                        Toast.makeText(MainActivity.this,"服务已停止",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.toSolve:
                        stopService(intent);
                        textView.setVisibility(View.GONE);
                        start.setEnabled(true);
                        stop.setEnabled(false);
                        clipboardManager.removePrimaryClipChangedListener(cm);
                        Toast.makeText(MainActivity.this,"服务已停止",Toast.LENGTH_LONG).show();
                        Intent intent1=new Intent(Intent.ACTION_VIEW);
                        intent1.setData(Uri.parse("http://douyin.iiilab.com/"));
                        startActivity(intent1);
//                        MainActivity.this.finish();
                        break;
                }
            }
        };

        start.setOnClickListener(oc);
        stop.setOnClickListener(oc);
        to.setOnClickListener(oc);

        /**
         * 默认启动软件自动打开服务
         */
        startService(intent);
        textView.setVisibility(View.VISIBLE);
        start.setEnabled(false);
        stop.setEnabled(true);
        Toast.makeText(MainActivity.this,"服务已启动",Toast.LENGTH_LONG).show();

        otherOp();
    }

    private void otherOp() {

        clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        cm=new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {

                if(System.currentTimeMillis()-time>800){
                ClipData data=clipboardManager.getPrimaryClip();
                ClipData.Item item=data.getItemAt(0);
                 String content=item.getText().toString();

                    ClipData data1=ClipData.newPlainText("Label","");
                    clipboardManager.setPrimaryClip(data1);

                 StringBuilder sb=new StringBuilder(content.replace("\n",""));
                 sb=new StringBuilder(sb.toString().replace("/?video_id=","FZNZBID"));
                 sb=new StringBuilder(sb.toString().replace("&amp","FZNZBID"));
                 String con_id[]=sb.toString().split("FZNZBID");
                if(content.contains("https://aweme.snssdk.com")&&content.length()<200) {
                    Toast.makeText(MainActivity.this, "开始下载,请查看通知栏或下载管理", Toast.LENGTH_SHORT).show();

                    //创建下载任务,downloadUrl就是下载链接
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(content));
                    // 指定下载路径和下载文件名
                    if(con_id.length==3){
                        request.setDestinationInExternalPublicDir("/抖音工具解析/", "抖音视频_" + con_id[1] + ".mp4");
                    }else {
                        request.setDestinationInExternalPublicDir("/抖音工具解析/", "抖音视频_" + System.currentTimeMillis() + ".mp4");
                    }
                    // 获取下载管理器
                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    // 将下载任务加入下载队列，否则不会进行下载
                    downloadManager.enqueue(request);
                    time=System.currentTimeMillis();

                }

                }



            }
        };

        clipboardManager.addPrimaryClipChangedListener(cm);
        /**
         * 权限检查、声明
         */
        List<String> permisions=new ArrayList<>();



        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permisions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permisions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if(!permisions.isEmpty()){
            String []permisions_str=permisions.toArray(new String[permisions.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permisions_str,1);
        }else{

           /////
        }



    }



    @Override
    protected void onDestroy() {
        stopService(intent);
        textView.setVisibility(View.GONE);
        start.setEnabled(true);
        stop.setEnabled(false);
        clipboardManager.removePrimaryClipChangedListener(cm);
        Toast.makeText(MainActivity.this,"服务已销毁",Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(event.getAction()==KeyEvent.ACTION_DOWN&&keyCode==KeyEvent.KEYCODE_BACK){

            if(System.currentTimeMillis()-time<1500){
                MainActivity.this.finish();
                return true;
            }else{
                Toast.makeText(MainActivity.this,"再点一次退出程序",Toast.LENGTH_SHORT).show();
                time=System.currentTimeMillis();
                return false;
            }

        }else {
            return super.onKeyDown(keyCode, event);
        }

    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int result:grantResults){
                        if(result!= PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(MainActivity.this,"必须同意所有权限才能使用本软件",Toast.LENGTH_SHORT).show();
                            finish();

                            return;
                        }
                    }
                    //////
                }else{
                    Toast.makeText(MainActivity.this,"退出",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

}
