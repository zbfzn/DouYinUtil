package top.lyfzn.douyinutil;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetDouyinService extends Service {
    private ClipboardManager clipboardManager;
    private long time;
    private StringBuilder html;
    private String[] ss;
    private  String content;
    private boolean isErro=false;
    String[] strr;
    private ClipboardManager.OnPrimaryClipChangedListener cm;

    public GetDouyinService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm=new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {

                if (System.currentTimeMillis() - time >800||isErro) {


                    ClipData data = clipboardManager.getPrimaryClip();
                    ClipData.Item item = data.getItemAt(0);
                    content = item.getText().toString();

                    if (content.contains("v.douyin.com")) {
                        StringBuilder str = new StringBuilder(content.replace("http", "FZNZBhttp"));
                        str = new StringBuilder(str.toString().replace("复制此链接", "FZNZB"));
                         ss= str.toString().split("FZNZB");
                        if (ss.length == 3) {

                            new AsyncTask<Void, Void, Boolean>() {
                                @Override
                                protected Boolean doInBackground(Void... voids) {
                                    try {
                                        html=new StringBuilder("");
                                        HttpTest httpTest=new HttpTest(ss[1], "utf-8");
                                        httpTest.run();
                                        return true;
                                    } catch (Exception e) {
                                        return false;
                                    }
                                }

                                @Override
                                protected void onPostExecute(Boolean aBoolean) {
                                    if (aBoolean) {
                                        SecConnect(html.toString());
                                        isErro=false;
                                    }else{
                                        ClipData data1=ClipData.newPlainText("Label",content);
                                        clipboardManager.setPrimaryClip(data1);
                                        isErro=true;
                                    }
                                }
                            }.execute();
                            time = System.currentTimeMillis();
                        }
                    }
                }
            }
        };
        clipboardManager.addPrimaryClipChangedListener(cm);

    }

    public void SecConnect(String html1){
        StringBuilder url=new StringBuilder(html1.replace("href=\"","FZNZBH"));
        url=new StringBuilder(url.toString().replace("\">https://www","FZNZBH"));
        strr=url.toString().split("FZNZBH");

        if(strr.length==3) {
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        HttpTest httpTest = new HttpTest(strr[1], "utf-8");
                        httpTest.run();
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (aBoolean) {

                        StringBuilder strT = new StringBuilder(html.toString().replace("class=\"video-player\" src=\"", "FZNZBURL"));
                        strT = new StringBuilder(strT.toString().replace("\" preload=\"auto\"", "FZNZBURL"));
                        String strs[] = strT.toString().split("FZNZBURL");

                        ClipData data1 = ClipData.newPlainText("Label", strs[1]);
                        clipboardManager.setPrimaryClip(data1);
                        isErro = false;

                    } else {
                        ClipData data1 = ClipData.newPlainText("Label", content);
                        clipboardManager.setPrimaryClip(data1);
                        isErro = true;

                    }

                }
            }.execute();
        }

    }

    class HttpTest {
        private String u,encoding;
        public HttpTest(String u, String encoding) {
            this.u = u;
            this.encoding = encoding;
        }

        public void run() throws Exception {

            URL url = new URL(u);// 根据链接（字符串格式），生成一个URL对象

            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();// 打开URL
            urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Linux; Android 8.0.0; MI 6 Build/OPR1.170623.027; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/62.0.3202.84 Mobile Safari/537.36");

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream(), encoding));// 得到输入流，即获得了网页的内容
            String line; // 读取输入流的数据，并显示
            while ((line = reader.readLine()) != null) {
                html.append(line);clipboardManager.addPrimaryClipChangedListener(cm);
            }
        }
    }

    @Override
    public void onDestroy() {
        clipboardManager.removePrimaryClipChangedListener(cm);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }
}
