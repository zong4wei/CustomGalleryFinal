package cn.finalteam.galleryfinal.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import cn.finalteam.galleryfinal.GalleryFinal;

/**
 * Created by ShiWeiZong
 * date 2016/7/511:36
 * email zong4wei@163.com
 */
public class DownloadFile extends AsyncTask<String, Integer, Long> {

    private Activity activity;
    private File saveFile;

    public DownloadFile(Activity activity) {
        this.activity = activity;
    }

    ProgressDialog mProgressDialog = new ProgressDialog(activity);

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setMessage("Downloading");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }

    @Override
    protected Long doInBackground(String... aurl) {
        int count;

        try {
            URL url = new URL((String) aurl[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            String targetFileName = url.toString().substring(url.toString().lastIndexOf("/") + 1, url.toString().length());//Change name and subname
            int lenghtOfFile = conexion.getContentLength();
            String PATH = GalleryFinal.getCoreConfig().getTakePhotoFolder().getAbsolutePath() + "downImage";
            File folder = new File(PATH);
            if (!folder.exists()) {
                folder.mkdir();//If there is no folder it will be created.
            }
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(PATH + "/" + targetFileName);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress((int) (total * 100 / lenghtOfFile));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            saveFile = new File(PATH + "/" + targetFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mProgressDialog.setProgress(values[0]);
        if (mProgressDialog.getProgress() == mProgressDialog.getMax()) {
            mProgressDialog.dismiss();
            Toast.makeText(activity, "图片下载完成", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);

        ContentValues localContentValues = new ContentValues();
        if (saveFile != null) {
            localContentValues.put("_data", saveFile.toString());
        }
        localContentValues.put("description", "save image ---");
        localContentValues.put("mime_type", "image/jpg");
        ContentResolver localContentResolver = activity.getContentResolver();
        Uri localUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        localContentResolver.insert(localUri, localContentValues);

    }
}