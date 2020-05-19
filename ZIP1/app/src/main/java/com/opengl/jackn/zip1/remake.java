package com.opengl.jackn.zip1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class remake extends Activity {
    private static ArrayList<Uri> picture;

    int TextNum=1;

    ThirdClass sou;
    SecoundClass pic;
    ForthClass forth;

    MediaPlayer mp;

    Uri fullPhotoUri;
    private String filePath;

    private Uri cameraUri;
    private final static int RESULT_CAMERA = 1001;
    private final static int REQUEST_PERMISSION = 1002;
    int REQUEST_XML_GET =100;
    int REQUEST_XML_GET_FILE =200;
    private static final int REQ_CODE_MIC = 0;

    Boolean rokuonFlag=false;
    Boolean FlagTop=true;

    ImageView imageView;
    MediaRecorder recorder;

    Button rokuon;
    Button music;

    TextView textView;

    private String filePathSound;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remake);
        music = findViewById(R.id.reSound);
        textView=findViewById(R.id.recounter);
        pic = new SecoundClass();
        sou= new ThirdClass();
        forth=new ForthClass();

        picture=new ArrayList<Uri>();


        imageView=findViewById(R.id.reimageView);

        Bitmap bitmap1 = null;
        try {
            bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), sou.picture.get(TextNum-1));
        }catch (IOException e) {
            e.printStackTrace();
        }
        setImage(bitmap1);

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicStart();
            }
        });

        Button camera=findViewById(R.id.recamera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                }
                else {
                    cameraclass();
                }
            }
        });

        Button cameraFile = findViewById(R.id.recamerafile);
        cameraFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/png");
                intent.setType("image/jpg");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_XML_GET);
                }
            }
        });
        rokuon=findViewById(R.id.rokuon);
        rokuon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission2();
                }
                else {
                    if(rokuonFlag==false) {
                        rokuonclass();
                        rokuon.setText("録音停止");
                        rokuonFlag=true;
                    }else{
                        rokuonTeisi();
                        rokuon.setText("録音");
                        rokuonFlag=false;
                    }
                }
            }
        });
        Button rokuonFile=findViewById(R.id.rerokuon);
        rokuonFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/mp3");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_XML_GET_FILE);
                }
            }
        });

        Button refinish = findViewById(R.id.refinish);
        refinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.reset();
                        mp.release();
                        mp = null;
                    }
                }catch (NullPointerException e){

                }
                finish();
            }
        });

        final Button next = findViewById(R.id.renext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextNum<pic.TextNum) {
                    TextNum++;
                    textView.setText(String.valueOf(TextNum));
                    Bitmap bitmap1 = null;
                    try {
                        bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), sou.picture.get(TextNum - 1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setImage(bitmap1);
                    if(TextNum==pic.TextNum) {
                        next.setVisibility(View.GONE);
                    }
                    if(sou.sound.get(TextNum-1)==null){
                        hideRokuon();
                    }else{
                        music.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        //refinish.setVisibility(View.GONE);
        if(TextNum==pic.TextNum) {
            next.setVisibility(View.GONE);
        }
        if(sou.sound.get(0)==null){
            hideRokuon();
        }

    }

    public void checkPermission2(){
        // 既に許可している
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED){
            if(rokuonFlag==false) {
                rokuonclass();
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
            ActivityCompat.requestPermissions(remake.this,new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_PERMISSION);
        }else{
            Toast toast = Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,}, REQUEST_PERMISSION);
        }
    }

    public void MusicStart(){
        if(FlagTop==false){
        if (mp.isPlaying()) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
        }
        try {
            mp = new MediaPlayer();
            try {
                mp.setDataSource(getPath2(this, sou.sound.get(TextNum - 1)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.start();
            FlagTop=false;
        }catch(IndexOutOfBoundsException e){

        }catch (NullPointerException e){
           return;
        }
    }

    public  void cameraclass(){
        File cameraFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"EmailAndPhoto");
        cameraFolder.mkdir();

        // 保存ファイル名
        String fileName = new SimpleDateFormat(
                "yyMMddHHmmss", Locale.US).format(new Date());
        filePath = String.format("%s/%s.jpg", cameraFolder.getPath(),fileName);
        Log.d("debug","filePath:"+filePath);

        // capture画像のファイルパス
        File cameraFile = new File(filePath);
        sou.picture.set(TextNum-1,Uri.fromFile(cameraFile));
        cameraUri = FileProvider.getUriForFile(
                remake.this,
                getApplicationContext().getPackageName() + ".provider",
                cameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, RESULT_CAMERA);
    }

    public void checkPermission(){
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            cameraclass();
        }
        // 拒否していた場合
        else{
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(remake.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }else{
            Toast toast = Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,}, REQUEST_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_XML_GET && resultCode == RESULT_OK) {
            fullPhotoUri = data.getData();
            System.out.println(fullPhotoUri.toString());
            sou.picture.set(TextNum-1,fullPhotoUri);
            Bitmap bitmap1 = null;
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), sou.picture.get(TextNum-1));
            }catch (IOException e) {
                e.printStackTrace();
            }
            setImage(bitmap1);
        }else if (requestCode == RESULT_CAMERA&&resultCode==RESULT_OK) {

            if (cameraUri != null) {
                Bitmap bitmap1 = null;
                try {
                    //sou.picture.set(TextNum-1,cameraUri);
                    bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), sou.picture.get(TextNum-1));
                }catch (IOException e) {
                    e.printStackTrace();
                }
                setImage(bitmap1);
            } else{
                Log.d("debug", "cameraUri == null");
            }
        }else if(requestCode == RESULT_CAMERA&&resultCode != RESULT_OK) {
            // キャンセル時
            return ;
        }else if (requestCode == REQUEST_XML_GET_FILE && resultCode == RESULT_OK) {
            fullPhotoUri = data.getData();
            System.out.println(fullPhotoUri.toString());
            //System.out.println(getPath(this,fullPhotoUri));
            try {
                sou.sound.set(TextNum - 1, fullPhotoUri);
            }catch(IndexOutOfBoundsException e){
                sou.sound.add(TextNum - 1, fullPhotoUri);
            }
        }else if(resultCode != RESULT_OK) {
            // キャンセル時
            return ;
        }
        if(sou.sound.get(TextNum-1)!=null){
            music.setVisibility(View.VISIBLE);
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
        Bitmap destBmp = Bitmap.createBitmap(srcBmp, 0, 0, width, height, matrix, true);
        return destBmp;

    }

    public void rokuonclass(){
        File cameraFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"EmailAndPhoto");
        cameraFolder.mkdir();

        // 保存ファイル名
        String fileNameSound = new SimpleDateFormat(
                "yyMMddHHmmss", Locale.US).format(new Date());
        filePathSound = String.format("%s/%s.mp3", cameraFolder.getPath(),fileNameSound);
        Log.d("debug","filePathSound:"+filePathSound);

        // capture画像のファイルパス
        File cameraFile = new File(filePathSound);
        try {
            sou.sound.set(TextNum - 1, Uri.fromFile(cameraFile));
        }catch(IndexOutOfBoundsException e){
            sou.sound.add(TextNum - 1, Uri.fromFile(cameraFile));
        }
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

    public  void rokuonTeisi(){
        recorder.stop();
        recorder.reset();
        recorder.release();
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
                mp.release();
                mp = null;
            }
        }catch (NullPointerException e){

        }
    }

    public void hideRokuon(){
        music.setVisibility(View.GONE);
    }

    public String getPath(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] columns = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        cursor.moveToFirst();
        String path = cursor.getString(0);
        cursor.close();
        File file = new File(uri.getPath());
        System.out.println(file);
        //不安要素
        //sound2.add(uri);
        //URL2=path;
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

}
