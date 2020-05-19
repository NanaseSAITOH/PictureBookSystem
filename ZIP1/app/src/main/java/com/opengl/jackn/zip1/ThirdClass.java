package com.opengl.jackn.zip1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ThirdClass extends Activity {
    public static int TextNum=1;
    ImageView imageView;
    Button rokuon;

    Uri fullPhotoUri;
    public static String URL2;
    private String filePathSound;
    private Uri rokuonUri;
    public static ArrayList<Uri> picture,sound;
    MediaPlayer mp;
    MediaRecorder recorder;

    int REQUEST_XML_GET =100;
    int REQUEST_PERMISSION=2222;
    int REQUEST_FORTH=2224;
    private static final int REQ_CODE_MIC = 0;

    Boolean rokuonFlag=false;

    SecoundClass pic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        pic = new SecoundClass();
        picture=new ArrayList<Uri>();
        sound=new ArrayList<Uri>();
        picture=pic.arrayList;

        rokuon = findViewById(R.id.rokuon);
        final Button file=findViewById(R.id.file2);
        final Button next = findViewById(R.id.next2);
        final Button finish = findViewById(R.id.finish2);
        imageView=findViewById(R.id.imageView2);

        final TextView counter1 = findViewById(R.id.counter2);
        Bitmap bitmap1;
        //System.out.println(picture.get(0));
        fullnull();

        try {
            bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), picture.get(0));
            setImage(bitmap1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        rokuon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                }
                else {
                    if(rokuonFlag==false) {
                        rokuon();
                        rokuon.setText("録音停止");
                        next.setVisibility(View.GONE);
                        finish.setVisibility(View.GONE);
                        file.setVisibility(View.GONE);
                        rokuonFlag=true;
                    }else{
                        rokuonTeisi();
                        rokuon.setText("録音");
                        rokuonFlag=false;
                        next.setVisibility(View.VISIBLE);
                        finish.setVisibility(View.VISIBLE);
                        file.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        final Intent intent = new Intent(this, ForthClass.class);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=TextNum;i<pic.TextNum;i++) {
                    try{sound.get(TextNum-1);}catch (IndexOutOfBoundsException e){
                        sound.add(TextNum-1,null);
                    }
                }
                TextNum=pic.TextNum;
                try {
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.reset();
                        mp.release();
                        mp = null;
                    }
                }catch (NullPointerException e){

                }
                startActivityForResult(intent,REQUEST_FORTH);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextNum<pic.TextNum) {
                    try{sound.get(TextNum-1);}catch (IndexOutOfBoundsException e){
                        sound.add(TextNum-1,null);
                    }
                    TextNum++;
                    counter1.setText(String.valueOf(TextNum));
                    imageView.setImageDrawable(null);
                    Bitmap bitmap1 = null;
                    try {
                        bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), picture.get(TextNum - 1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setImage(bitmap1);
                }
            }
        });

        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file();
            }
        });
    }

    public void file(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mp3");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_XML_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_XML_GET && resultCode == RESULT_OK) {
            fullPhotoUri = data.getData();
            System.out.println(fullPhotoUri.toString());
            //System.out.println(getPath(this,fullPhotoUri));
            sound.add(TextNum-1,fullPhotoUri);
            mp = new MediaPlayer();
            try {
                mp.setDataSource(getPath(this,fullPhotoUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.start();
        }else if(requestCode==REQUEST_FORTH&& resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }

    private void rokuon(){
        File cameraFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"EmailAndPhoto");
        cameraFolder.mkdir();

        // 保存ファイル名
        String fileNameSound = new SimpleDateFormat(
                "yyMMddHHmmss", Locale.US).format(new Date());
        filePathSound = String.format("%s/%s.mp3", cameraFolder.getPath(),fileNameSound);
        Log.d("debug","filePathSound:"+filePathSound);

        // capture画像のファイルパス
        File cameraFile = new File(filePathSound);
        sound.add(TextNum-1,Uri.fromFile(cameraFile));

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        //保存先
        String filePath = Environment.getExternalStorageDirectory() + "/audio.3gp";
        recorder.setOutputFile(filePathSound);

        //録音準備＆録音開始
        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        recorder.start();   //録音開始

    }
    public void checkPermission(){
        // 既に許可している
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED){
            if(rokuonFlag==false) {
                rokuon();
                rokuon.setText("録音停止");
                rokuonFlag=true;
            }else{
                rokuonTeisi();
                rokuon.setText("録音");
                rokuonFlag=false;
            }
        }else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            requestRECORDPermission();
        }
    }

    private void requestRECORDPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)){
            ActivityCompat.requestPermissions(ThirdClass.this,new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_PERMISSION);
        }else{
            Toast toast = Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,}, REQUEST_PERMISSION);
        }
    }

    public static String getPath(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] columns = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        cursor.moveToFirst();
        String path = cursor.getString(0);
        cursor.close();
        File file = new File(uri.getPath());
        System.out.println(file);
        //不安要素
        //sound.add(uri);
        URL2=path;
        return path;
    }

    public String getPath2(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] columns = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        try {
            cursor.moveToFirst();
            String path = cursor.getString(0);
            cursor.close();
            File file = new File(uri.getPath());
            System.out.println(file);
            return path;
        }catch(RuntimeException e){
            File file = new File(uri.getPath());
            return file.toString();
        }
    }

    public void setImage(Bitmap bmp){
        int imageWidth = imageView.getWidth();
        int imageHeight = imageView.getHeight();


        imageView.setImageBitmap(resizeReadMatrix(bmp,pic.imageWidth,pic.imageHeight));
    }

    private Bitmap resizeReadMatrix(Bitmap srcBmp, int outputWidth, int outputHeight) {


        int height = srcBmp.getHeight();
        int width = srcBmp.getWidth();

        Matrix matrix = new Matrix();
        if (height > width) {
            float scaleWidth = outputHeight  / (float)width;
            float scaleHeight = outputWidth / (float)height;
            matrix.postScale(Math.min(scaleWidth, scaleHeight), Math.min(scaleWidth, scaleHeight));
        } else {
            float scaleWidth = outputWidth / (float)width;
            float scaleHeight = outputHeight / (float)height;
            matrix.postScale(Math.min(scaleWidth, scaleHeight), Math.min(scaleWidth, scaleHeight));
        }
        System.out.println(height+","+width+","+outputHeight+","+outputWidth);
        Bitmap destBmp = Bitmap.createBitmap(srcBmp, 0, 0, width, height, matrix, true);
        return destBmp;

    }

    public  void rokuonTeisi(){
        recorder.stop();
        recorder.reset();
        recorder.release();
    }

    public void fullnull(){
        for(int i=0;i<picture.size();i++){
            sound.add(null);
        }
    }
}
