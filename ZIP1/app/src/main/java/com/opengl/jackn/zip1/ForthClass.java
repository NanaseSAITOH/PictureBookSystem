package com.opengl.jackn.zip1;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ForthClass extends Activity {
    public static int TextNum=1;
    private static ArrayList<Uri> picture,sound2;
    ImageView imageView;
    TextView textView;
    MediaPlayer mp;

    ThirdClass sou;
    SecoundClass sec;

    Button next,remake,make;

    int requestCode2=123;

    String FinalFileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forth);

        //pic = new SecoundClass();
        sou= new ThirdClass();
        //picture=new ArrayList<Uri>();
        sound2=new ArrayList<Uri>();
        //picture=pic.arrayList;
        sound2=sou.sound;

        imageView = findViewById(R.id.imageView3);

        imageView.setImageDrawable(null);
        Bitmap bitmap1 = null;
        try {
            bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), sou.picture.get(TextNum-1));
        }catch (IOException e) {
            e.printStackTrace();
        }
        setImage(bitmap1);
        MusicStart();

         next = findViewById(R.id.next3);
         make = findViewById(R.id.make);
         remake = findViewById(R.id.remake);
        textView = findViewById(R.id.counter3);
        System.out.println(sou.sound.size());
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(TextNum+"/"+sou.TextNum);
                if(TextNum<sou.TextNum) {
                    TextNum++;
                    textView.setText(String.valueOf(TextNum));
                    //imageView.setImageDrawable(null);
                    Bitmap bitmap1 = null;
                    try {
                        bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), sou.picture.get(TextNum - 1));
                    } catch (IOException e) {
                        e.printStackTrace();
                        //System.out.println("あかん");
                    }
                    setImage(bitmap1);
                    MusicStart();
                    if(TextNum==sou.TextNum){
                        make.setVisibility(View.VISIBLE);
                        remake.setVisibility(View.VISIBLE);
                        next.setVisibility(View.GONE);
                    }
                }
            }
        });

        make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeZip();
                setResult(RESULT_OK);
                finish();
                //moveTaskToBack(true);
            }
        });

        remake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remakeContent();
            }
        });
        make.setVisibility(View.GONE);
        remake.setVisibility(View.GONE);
        if(TextNum==sou.TextNum){
            make.setVisibility(View.VISIBLE);
            remake.setVisibility(View.VISIBLE);
            next.setVisibility(View.GONE);
        }
    }

    public void setImage(Bitmap bmp){
        int imageWidth = imageView.getWidth();
        int imageHeight = imageView.getHeight();


        imageView.setImageBitmap(resizeReadMatrix(bmp,sec.imageWidth,sec.imageHeight));
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

    public void MusicStart(){
        if(TextNum!=1) {
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
                mp.setDataSource(getPath2(this, sound2.get(TextNum - 1)));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                return;
            }
            try {
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.start();
        }catch(IndexOutOfBoundsException e){

        }
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


    public void makeZip(){
        DecimalFormat dformat = new DecimalFormat("000");
        // 入力ストリーム
        InputStream is = null;

        // ZIP形式の出力ストリーム
        ZipOutputStream zos = null;

        // 入出力用のバッファを作成
        byte[] buf = new byte[1024];

        // ZipOutputStreamオブジェクトの作成
        try {
            //helloをoutputFile : 出力先となるZIPファイルのファイル名に
            File zipFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"ZIP");
            zipFolder.mkdir();
            String fileName = new SimpleDateFormat(
                    "yyMMddHHmmss", Locale.US).format(new Date());
            FinalFileName=fileName;
            String filePath = String.format("%s/%s.zip", zipFolder.getPath(),fileName);
            zos = new ZipOutputStream(new FileOutputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < sou.picture.size(); i++) {
                // 入力ストリームのオブジェクトを作成
                String path;
                System.out.println("画像１"+sou.picture.get(0));
                //System.out.println("画像２"+sou.picture.get(1));
                path=getPath2(this,sou.picture.get(i));
                System.out.println("ok");
                File file = new File(path);
                is = new FileInputStream(file);

                // Setting Filename
                String filename = new SimpleDateFormat(
                        "yyMMddHHmmss", Locale.US).format(new Date());
                filename = String.format("%s%s.jpg", filename,dformat.format(i));

                // ZIPエントリを作成
                ZipEntry ze = new ZipEntry(filename);

                // 作成したZIPエントリを登録
                zos.putNextEntry(ze);

                // 入力ストリームからZIP形式の出力ストリームへ書き出す
                int len = 0;
                while ((len = is.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }

                // 入力ストリームを閉じる
                is.close();

                // エントリをクローズする
                zos.closeEntry();
                System.out.println("画像終わり");
            }

            for (int i = 0; i < sou.sound.size(); i++) {
                // 入力ストリームのオブジェクトを作成
                String path;
                try {
                    path = getPath2(this, sou.sound.get(i));
                    File file = new File(path);
                    is = new FileInputStream(file);

                    // Setting Filename
                    String filename = new SimpleDateFormat(
                            "yyMMddHHmmss", Locale.US).format(new Date());
                    filename = String.format("%s%s.mp3", filename,dformat.format(i));

                    // ZIPエントリを作成
                    ZipEntry ze = new ZipEntry(filename);

                    // 作成したZIPエントリを登録
                    zos.putNextEntry(ze);

                    // 入力ストリームからZIP形式の出力ストリームへ書き出す
                    int len = 0;
                    while ((len = is.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }

                    // 入力ストリームを閉じる
                    is.close();

                    // エントリをクローズする
                    zos.closeEntry();
                    System.out.println("音楽終わり");
                }catch (NullPointerException e){
                    //path="";
                }
            }

            // 出力ストリームを閉じる
            zos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        registerData();
    }

    public void remakeContent(){
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
                mp.release();
                mp = null;
            }
        }catch (NullPointerException e){

        }
        Intent intent = new Intent(this, remake.class);
        startActivityForResult(intent,requestCode2);
    }

    private database1 data;
    private database2 data2;
    private SQLiteDatabase db,db2;

    public void registerData(){
        if(data == null){
            data = new database1(getApplicationContext());
        }

        if(db == null){
            db = data.getWritableDatabase();
        }
        if(data2 == null){
            data2 = new database2(getApplicationContext());
        }

        if(db2 == null){
            db2 = data2.getWritableDatabase();
        }
        insertData(db);
        insertData2(db2);
    }
    private void insertData(SQLiteDatabase db){

        ContentValues values = new ContentValues();
        values.put("zipname", FinalFileName);
        //正規のパス
        values.put("NoOnePic", getPath2(this,sou.picture.get(0)));

        db.insert("testdb", null, values);
        readData();
    }

    private void insertData2(SQLiteDatabase dbb2){
        for(int i=0;i<sou.picture.size();i++) {
            ContentValues values = new ContentValues();
            values.put("zipname", FinalFileName);
            //正規のパス
            values.put("picname", getPath2(this, sou.picture.get(i)));
            try {
                values.put("mp3name", getPath2(this, sou.sound.get(i)));
            }catch(IndexOutOfBoundsException e){
                values.put("mp3name", "");
            }catch(NullPointerException e){
                values.put("mp3name", "");
            }

            dbb2.insert("db2", null, values);
        }
        readData2();
    }

    private void readData(){
        if(data == null){
            data = new database1(getApplicationContext());
        }

        if(db == null){
            db = data.getReadableDatabase();
        }
        Log.d("debug","**********Cursor");

        Cursor cursor = db.query(
                "testdb",
                new String[] { "zipname", "NoOnePic" },
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        StringBuilder sbuilder = new StringBuilder();

        for (int i = 0; i < cursor.getCount(); i++) {
            sbuilder.append(cursor.getString(0));
            sbuilder.append(": ");
            sbuilder.append(cursor.getString(1));
            sbuilder.append("\n");
            cursor.moveToNext();
        }

        // 忘れずに！
        cursor.close();

        //Log.d("debug","**********"+sbuilder.toString());
        System.out.println(sbuilder.toString());
    }
    private void readData2(){
        if(data2 == null){
            data2 = new database2(getApplicationContext());
        }

        if(db2 == null){
            db2 = data2.getReadableDatabase();
        }
        Log.d("debug","**********Cursor");

        Cursor cursor = db2.query(
                "db2",
                new String[] { "zipname", "picname", "mp3name" },
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        StringBuilder sbuilder = new StringBuilder();

        for (int i = 0; i < cursor.getCount(); i++) {
            sbuilder.append(cursor.getString(0));
            sbuilder.append("画像");
            sbuilder.append(cursor.getString(1));
            sbuilder.append("音楽\n");
            sbuilder.append(cursor.getString(2));
            sbuilder.append("\n");
            cursor.moveToNext();
        }

        // 忘れずに！
        cursor.close();

        //Log.d("debug","**********"+sbuilder.toString());
        System.out.println(sbuilder.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                TextNum=1;
                make.setVisibility(View.GONE);
                remake.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                textView.setText(String.valueOf(TextNum));
                //imageView.setImageDrawable(null);
                Bitmap bitmap1 = null;
                try {
                    bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), sou.picture.get(TextNum - 1));
                } catch (IOException e) {
                    e.printStackTrace();
                    //System.out.println("あかん");
                }
                setImage(bitmap1);
                MusicStart();

    }
}
