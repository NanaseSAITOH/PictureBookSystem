package com.opengl.jackn.viewer;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends Activity {

    ArrayList<String> pathPic,ZipName;

    private final static int REQUEST_PERMISSION3 = 1003;
    int REQUEST_SECOUND=1004;
    public static ArrayList<String> pathPicContent,pathSoundContent;
    Bitmap bmp1,bmp2,bmp3,bmp4;
    ImageView imageView1,imageView2,imageView3,imageView4;
    int Flag=0;
    int ImageClick=0;

    int imageWidth;
    int imageHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File cameraFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"ZIPOPEN1");
        cameraFolder.mkdir();
        File zipFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"ZIPOPEN2");
        File delete = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/ZIPOPEN2");
        delete(delete);
        zipFolder.mkdir();

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        pathPic=new ArrayList<String>();
        ZipName=new ArrayList<String>();
        pathPicContent=new ArrayList<String>();
        pathSoundContent=new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 23) {
            checkStoragePermission();
        }
        registerData();
         imageView1=findViewById(R.id.imageView);
         imageView2=findViewById(R.id.imageView3);
         imageView3=findViewById(R.id.imageView4);
         imageView4=findViewById(R.id.imageView2);
        bmp1=null;
        bmp2=null;
        bmp3=null;
        bmp4=null;

        if(ZipName.size()==1){
            ImageClick=0; readData2();
        }

        ViewTreeObserver observer = imageView1.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageWidth = imageView1.getWidth();
                imageHeight = imageView1.getHeight();
                try {
                    bmp1= BitmapFactory.decodeFile(pathPic.get(4*Flag));
                    bmp1=changeBMP(bmp1);
                }catch (RuntimeException e){

                }try {
                    bmp2= BitmapFactory.decodeFile(pathPic.get(4*Flag+1));
                    bmp2=changeBMP(bmp2);
                }catch (RuntimeException e){

                }try {
                    bmp3= BitmapFactory.decodeFile(pathPic.get(4*Flag+2));
                    bmp3=changeBMP(bmp3);
                }catch (RuntimeException e){

                }try {
                    bmp4= BitmapFactory.decodeFile(pathPic.get(4*Flag+3));
                    bmp4=changeBMP(bmp4);
                }catch (RuntimeException e){

                }
                try {
                    imageView1.setImageBitmap(bmp1);
                }catch (NullPointerException e){
                    imageView1.setImageDrawable(null);
                }try {
                    imageView2.setImageBitmap(bmp2);
                }catch (NullPointerException e){
                    imageView2.setImageDrawable(null);
                }try {
                    imageView3.setImageBitmap(bmp3);
                }catch (NullPointerException e){
                    imageView3.setImageDrawable(null);
                }try {
                    imageView4.setImageBitmap(bmp4);
                }catch (NullPointerException e){
                    imageView4.setImageDrawable(null);
                }
            }
        });

        Button next = findViewById(R.id.button2);
        final Button previous =findViewById(R.id.button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(String.valueOf(pathPic.size()));
                if(4*(Flag+1)<pathPic.size()) {
                    nextG();
                }if(1<=Flag) {
                    previous.setVisibility(View.VISIBLE);
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(1<=Flag) {
                    previousG();
                }
            }
        });
        if(4>=pathPic.size()) {
            next.setVisibility(View.GONE);
        }else{
            next.setVisibility(View.VISIBLE);
        }
        previous.setVisibility(View.GONE);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 ImageClick=0; readData2();
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 ImageClick=1; readData2();
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 ImageClick=2; readData2();
            }
        });
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 ImageClick=3; readData2();
            }
        });

    }

    private static void delete(File f) {
        /*
         * ファイルまたはディレクトリが存在しない場合は何もしない
         */
        if(f.exists() == false) {
            return;
        }

        if(f.isFile()) {
            /*
             * ファイルの場合は削除する
             */
            f.delete();

        } else if(f.isDirectory()){
            /*
             * ディレクトリの場合は、すべてのファイルを削除する
             */

            /*
             * 対象ディレクトリ内のファイルおよびディレクトリの一覧を取得
             */
            File[] files = f.listFiles();

            /*
             * ファイルおよびディレクトリをすべて削除
             */
            for(int i=0; i<files.length; i++) {
                /*
                 * 自身をコールし、再帰的に削除する
                 */
                delete( files[i] );
            }
            /*
             * 自ディレクトリを削除する
             */
            f.delete();
        }
    }

    public void nextG(){
        Flag++;
        bmp1=null;
        bmp2=null;
        bmp3=null;
        bmp4=null;
        try {
            bmp1= BitmapFactory.decodeFile(pathPic.get(4*Flag));
            bmp1=changeBMP(bmp1);
        }catch (RuntimeException e){

        }try {
            bmp2= BitmapFactory.decodeFile(pathPic.get(4*Flag+1));
            bmp2=changeBMP(bmp2);
        }catch (RuntimeException e){

        }try {
            bmp3= BitmapFactory.decodeFile(pathPic.get(4*Flag+2));
            bmp3=changeBMP(bmp3);
        }catch (RuntimeException e){

        }try {
            bmp4= BitmapFactory.decodeFile(pathPic.get(4*Flag+3));
            bmp4=changeBMP(bmp4);
        }catch (RuntimeException e){

        }
        try {
            imageView1.setImageBitmap(changeBMP(bmp1));
        }catch (NullPointerException e){
            imageView1.setImageDrawable(null);
        }try {
            imageView2.setImageBitmap(changeBMP(bmp2));
        }catch (NullPointerException e){
            imageView2.setImageDrawable(null);
        }try {
            imageView3.setImageBitmap(changeBMP(bmp3));
        }catch (NullPointerException e){
            imageView3.setImageDrawable(null);
        }try {
            imageView4.setImageBitmap(changeBMP(bmp4));
        }catch (NullPointerException e){
            imageView4.setImageDrawable(null);
        }
    }

    public Bitmap changeBMP(Bitmap srcBmp){


        int height = srcBmp.getHeight();
        int width = srcBmp.getWidth();

        Matrix matrix = new Matrix();
        if (height > width) {
            float scaleWidth = imageHeight  / (float)width;
            float scaleHeight = imageWidth / (float)height;
            matrix.postScale(Math.min(scaleWidth, scaleHeight), Math.min(scaleWidth, scaleHeight));
        } else {
            float scaleWidth = imageWidth / (float)width;
            float scaleHeight = imageHeight / (float)height;
            matrix.postScale(Math.min(scaleWidth, scaleHeight), Math.min(scaleWidth, scaleHeight));
        }
        Bitmap destBmp = Bitmap.createBitmap(srcBmp, 0, 0, width, height, matrix, true);
        return destBmp;

    }

    public void previousG(){
        Flag--;
        bmp1=null;
        bmp2=null;
        bmp3=null;
        bmp4=null;
        try {
            bmp1= BitmapFactory.decodeFile(pathPic.get(4*Flag));
        }catch (RuntimeException e){

        }try {
            bmp2= BitmapFactory.decodeFile(pathPic.get(4*Flag+1));
        }catch (RuntimeException e){

        }try {
            bmp3= BitmapFactory.decodeFile(pathPic.get(4*Flag+2));
        }catch (RuntimeException e){

        }try {
            bmp4= BitmapFactory.decodeFile(pathPic.get(4*Flag+3));
        }catch (RuntimeException e){

        }
        try {
            imageView1.setImageBitmap(changeBMP(bmp1));
        }catch (NullPointerException e){
            imageView1.setImageDrawable(null);
        }try {
            imageView2.setImageBitmap(changeBMP(bmp2));
        }catch (NullPointerException e){
            imageView2.setImageDrawable(null);
        }try {
            imageView3.setImageBitmap(changeBMP(bmp3));
        }catch (NullPointerException e){
            imageView3.setImageDrawable(null);
        }try {
            imageView4.setImageBitmap(changeBMP(bmp4));
        }catch (NullPointerException e){
            imageView4.setImageDrawable(null);
        }
    }

    public void registerData(){
        readData();
    }

    private void readData(){

        File zipFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"ZIP");
        File[] zipFolderlist=zipFolder.listFiles();

        //zipフォルダの名前
        for(int i = 0; i < zipFolderlist.length; i++){
            if(zipFolderlist[i].isFile() && zipFolderlist[i].getName().endsWith(".zip")){
                if(zipFolderlist[i].getName().matches("\\d{12}.zip")) {
                    openzip1(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/ZIP/"+zipFolderlist[i].getName(),i,zipFolderlist[i].getName());
                    System.out.println("OK");
                }
                //pathPic.add(i,Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/ZIPOPEN1/"+getNameWithoutExtension(zipFolderlist[i])+".");
            }
            //System.out.println(ZipName.get(i));
        }
    }

    private void readData2(){
        String zipname = ZipName.get(ImageClick + 4 * Flag);
        System.out.println(zipname);
        openzip2(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/ZIP/"+zipname);
        nextintent();
    }

    public void openzip1(String filename, int i,String Zipname){
        ZipInputStream in = null;
        BufferedOutputStream out = null;

        ZipEntry zipEntry = null;
        int len = 0;

        try {
            in = new ZipInputStream(new FileInputStream(filename));

            // ZIPファイルに含まれるエントリに対して順にアクセス
            //while ((zipEntry = in.getNextEntry()) != null) {
                zipEntry = in.getNextEntry();
                File newfile = new File(zipEntry.getName());

                // 出力用ファイルストリームの生成
                out = new BufferedOutputStream(
                        new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/ZIPOPEN1/"  + newfile.getName())

                );
                if((newfile.getName().matches("\\d{15}.jpg"))||(newfile.getName().matches("\\d{15}.jpeg"))||(newfile.getName().matches("\\d{15}.png"))){
                    ZipName.add(Zipname);
                    pathPic.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/ZIPOPEN1/"+newfile.getName());
                }

                // エントリの内容を出力
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                //len = in.read(buffer);
                out.write(buffer, 0, len);
                }

                in.closeEntry();
                out.close();
                out = null;
            //}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openzip2(String filename){
        ZipInputStream in = null;
        BufferedOutputStream out = null;

        ZipEntry zipEntry = null;
        int len = 0;

        try {
            in = new ZipInputStream(new FileInputStream(filename));

            // ZIPファイルに含まれるエントリに対して順にアクセス
            while ((zipEntry = in.getNextEntry()) != null) {
            File newfile = new File(zipEntry.getName());

            // 出力用ファイルストリームの生成
            out = new BufferedOutputStream(
                    new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/ZIPOPEN2/"+newfile.getName())
            );

            // エントリの内容を出力
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                //len = in.read(buffer);
                out.write(buffer, 0, len);
            }

            in.closeEntry();
            out.close();
            out = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File zipFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"ZIPOPEN2");
        File[] zipFolderlist=zipFolder.listFiles();

        //zipフォルダの名前
        int a=0;
        for(int i = 0; i < zipFolderlist.length; i++){
            if(zipFolderlist[i].isFile() && ((zipFolderlist[i].getName().endsWith(".jpg"))||(zipFolderlist[i].getName().endsWith(".jpeg"))||(zipFolderlist[i].getName().endsWith(".png")))){
                pathPicContent.add(a,Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/ZIPOPEN2/"+zipFolderlist[i].getName());
                pathSoundContent.add(a,"");
                //System.out.println(pathPicContent.get(a));
                a++;
            }
        }
        a=0;
        //空白処理必要
        for(int i = 0; i < zipFolderlist.length; i++){
            if(zipFolderlist[i].isFile() && zipFolderlist[i].getName().endsWith(".mp3")){
                int b= Integer.parseInt(zipFolderlist[i].getName().substring(12,zipFolderlist[i].getName().length()-4));
                System.out.println(String.valueOf(b));
                pathSoundContent.add(b,Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/ZIPOPEN2/"
                        +zipFolderlist[i].getName());
                //System.out.println(pathSoundContent.get(b));
            }
        }
    }

    public void checkStoragePermission(){
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

        }else {
            requestLocationPermission2();
        }
    }

    private void requestLocationPermission2() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION3);
        }else{
            Toast toast = Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,}, REQUEST_PERMISSION3);
        }
    }

    public void nextintent(){
        bmp1=null;
        bmp2=null;
        bmp3=null;
        bmp4=null;
        System.out.println(String.valueOf(pathPicContent.size())+"/"+String.valueOf(pathSoundContent.size()));
        Intent intent = new Intent(this, SecoundActivity.class);
        intent.putExtra("path",pathPicContent);
        intent.putExtra("path2",pathSoundContent);
        startActivityForResult(intent,REQUEST_SECOUND);
        //finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SECOUND) {
            pathPicContent.clear();
            pathSoundContent.clear();
            File zipFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"ZIPOPEN2");
            File delete = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/ZIPOPEN2");
            delete(delete);
            zipFolder.mkdir();
        }
    }
}
