package top.lyfzn.douyinutil;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class FileUtils {
    private String SDPATH;

    private int FILESIZE = 4 * 1024;

    private Context context;

    static boolean isOutSuccess=false;

    public String getSDPATH() {
        return SDPATH;
    }

    public FileUtils(Context context) {
        // 得到当前外部存储设备的目录( /SDCARD )
        this.context=context;
        SDPATH = Environment.getExternalStorageDirectory() + "/";
    }

    /**
     * 在SD卡上创建文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     * @return
     */
    public File createSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     *
     * @param fileName
     * @return
     */
    public boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param path
     * @param fileName
     * @param input
     * @return
     */
    public File write2SDFromInput(String path, String fileName,
                                  InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            createSDDir(path);
            file = createSDFile(path+"/" + fileName);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[input.available()];
            while ((input.read(buffer)) != -1) {
                output.write(buffer);
            }
            output.flush();
            isOutSuccess=true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 在sd卡中读出文本文件 返回String * strFullPath 读取文件的完整路径
     */
    public String ReadSDFiled(String strFullPath) {

        File file = new File(SDPATH + strFullPath);
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e3) {
            e3.printStackTrace();
        }

        BufferedReader br = null;
        try {
            /* 转换编码 */
            br = new BufferedReader(new InputStreamReader(in, "gb2312"));
            /* 不转换编码 */
            // br = new BufferedReader(new InputStreamReader(in));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        StringBuffer sb = new StringBuffer();
        String tmp;
        try {
            while ((tmp = br.readLine()) != null) {
                sb.append(tmp + "\n");
            }
            br.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}