package com.opengl.jackn.viewer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SecoundActivity extends Activity {
    public int TextNum=0;
    ImageView imageView;

    int imageWidth;
    int imageHeight;

    boolean FlagFinish=false;
    Bitmap destBmp;
    Bitmap bitmap1 = null;
    ViewTreeObserver observer;

    ArrayList<String> path,path2;

    MediaPlayer mp;

    MainActivity main;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secound);

         main=new MainActivity();


        final Button next = findViewById(R.id.button4);
        final Button previous = findViewById(R.id.button3);

        imageView=findViewById(R.id.imageView6);
        path=main.pathSoundContent;
        path2=main.pathPicContent;

       // bitmap1.recycle();
        bitmap1 = null;
        System.out.println(path2.get(0));
        bitmap1 = BitmapFactory.decodeFile(path2.get(TextNum));
        observer = imageView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageWidth = imageView.getWidth();
                imageHeight = imageView.getHeight();
                imageView.setImageBitmap(changeBMP(bitmap1));
            }
        });
        //setImage(bitmap1);
        previous.setVisibility(View.GONE);
        //MusicStart();

        mp = new MediaPlayer();
        try {
            try {
                mp.setDataSource(path.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mp.start();
        }catch(IndexOutOfBoundsException e){

        }

        //next.setText("<<");



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println(String.valueOf(path2.size()));
                previous.setVisibility(View.VISIBLE);
                if(TextNum<path2.size()-1) {
                    TextNum++;
                    //counter1.setText(String.valueOf(TextNum));
                    imageView.setImageBitmap(null);
                    bitmap1.recycle();
                    bitmap1 = null;
                    bitmap1 = BitmapFactory.decodeFile(path2.get(TextNum));
                    imageView.setImageBitmap(changeBMP(bitmap1));

                    //bitmap1 = BitmapFactory.decodeFile(path2.get(TextNum));

                   MusicStart();
                } if(TextNum>=path2.size()-1&&1<=TextNum){
                    //TextNum=0;
                    FlagFinish=true;
                    previous.setText("<<");
                    next.setVisibility(View.GONE);
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(1<=TextNum){
                    if(FlagFinish==true){
                        TextNum=0;
                        previous.setText("<");
                        next.setVisibility(View.VISIBLE);
                        FlagFinish=false;
                    }else{
                        TextNum--;
                    }
                    if(TextNum==0){
                        previous.setVisibility(View.GONE);
                    }
                    //counter1.setText(String.valueOf(TextNum));
                    imageView.setImageDrawable(null);
                    bitmap1.recycle();
                    bitmap1 = null;
                    bitmap1 = BitmapFactory.decodeFile(path2.get(TextNum));
                    imageView.setImageBitmap(changeBMP(bitmap1));

                    //setImage(bitmap1);
                    MusicStart();
                }

            }
        });

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

    public void MusicStart(){
        if (mp != null) {
            mp.reset();
            mp.release();
            mp = null;
            //mChronometer.stop();
        }
        System.out.println("パス"+path.get(TextNum));
        if(!(path.get(TextNum).equals(""))) {
            try {
                mp = new MediaPlayer();
                try {
                    mp.setDataSource(path.get(TextNum));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.start();
            } catch (IndexOutOfBoundsException e) {

            }
        }
    }
}
