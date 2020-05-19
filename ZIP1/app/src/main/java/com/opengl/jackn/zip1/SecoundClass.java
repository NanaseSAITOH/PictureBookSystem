package com.opengl.jackn.zip1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SecoundClass extends Activity {
    Bundle savedInstanceState2;

    Uri fullPhotoUri;
    private String filePath;
    public static String URL;
    private Uri cameraUri;
    public static int TextNum=1;

    public static ArrayList<Uri> arrayList;
    public static int imageWidth;
    public static int imageHeight;

    private final static int RESULT_CAMERA = 1001;
    private final static int REQUEST_PERMISSION = 1002;
    private final static int REQUEST_PERMISSION2 = 1003;
    int REQUEST_XML_GET =100;
    int REQUEST_PERMISSION3=1004;
    int REQUEST_THIRD=1113;

    ImageView imageView;

    Button camera,file,next,finish;

    ArrayList<Uri> uris = new ArrayList<Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceState2=savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secound);

         camera = findViewById(R.id.camera);
         file=findViewById(R.id.file);
         next = findViewById(R.id.next);
         finish = findViewById(R.id.finish);
         next.setVisibility(View.GONE);
         finish.setVisibility(View.GONE);
        imageView=findViewById(R.id.imageView);
        arrayList=new ArrayList<Uri>();
        final TextView counter1 = findViewById(R.id.counter1);

        if (Build.VERSION.SDK_INT >= 23) {
            checkStoragePermission();
        }

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkCameraPermission();
                }
                else {
                    camera();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next.setVisibility(View.GONE);
                finish.setVisibility(View.GONE);
                TextNum++;
                counter1.setText(String.valueOf(TextNum));
                imageView.setImageDrawable(null);
            }
        });

        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file();
            }
        });
        final Intent intent = new Intent(this, ThirdClass.class);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent,REQUEST_THIRD);
            }
        });
    }


    public void camera(){
        File cameraFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"EmailAndPhoto");
        cameraFolder.mkdir();

        // 保存ファイル名
        String fileName = new SimpleDateFormat(
                "ddHHmmss", Locale.US).format(new Date());
        filePath = String.format("%s/%s.jpg", cameraFolder.getPath(),fileName);
        Log.d("debug","filePath:"+filePath);
        //System.out.println(Uri.parse(filePath));
        // capture画像のファイルパス
        File cameraFile = new File(filePath);
        System.out.println(Uri.fromFile(cameraFile));
        arrayList.add(TextNum-1,Uri.fromFile(cameraFile));
        cameraUri = FileProvider.getUriForFile(
                SecoundClass.this,
                getApplicationContext().getPackageName() + ".provider",
                cameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, RESULT_CAMERA);
    }

    public void file(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/png");
        intent.setType("image/jpg");
        intent.setType("image/jpeg");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_XML_GET);
        }
    }
    public void checkCameraPermission(){
        // 既に許可している
       if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
           camera();
        }else {
            requestCameraPermission();
        }
    }
    public void checkStoragePermission(){
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

        }else {
            requestLocationPermission();
        }if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

        }else {
            requestLocationPermission2();
        }
    }

    private void requestCameraPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
            ActivityCompat.requestPermissions(SecoundClass.this,new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSION2);
        }else{
            Toast toast = Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA,}, REQUEST_PERMISSION2);
        }
    }

    private void requestLocationPermission2() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(SecoundClass.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION3);
        }else{
            Toast toast = Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,}, REQUEST_PERMISSION3);
        }
    }

    private void requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(SecoundClass.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }else{
            Toast toast = Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,}, REQUEST_PERMISSION);
        }
    }

    public void registerDatabase(String filePath) {
        ContentValues contentvalues = new ContentValues();
        ContentResolver contentResolver = SecoundClass.this.getContentResolver();
        contentvalues.put(MediaStore.Images.Media.MIME_TYPE,"image/jpg");
        contentvalues.put("_data",filePath);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentvalues);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_XML_GET && resultCode == RESULT_OK) {
            fullPhotoUri = data.getData();
            System.out.println(fullPhotoUri.toString());
            arrayList.add(TextNum-1,fullPhotoUri);
            System.out.println(getPath(this,fullPhotoUri));
        }else if (requestCode == RESULT_CAMERA&& resultCode==RESULT_OK) {

            if (cameraUri != null) {
                Bitmap bitmap1 = null;
                try {
                    // arrayList.add(TextNum-1,cameraUri);
                     bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), arrayList.get(TextNum-1));
                    System.out.println(getPath(this,arrayList.get(TextNum-1)));
                }catch (IOException e) {
                    e.printStackTrace();
                }
                setImage(bitmap1);
            } else{
                Log.d("debug", "cameraUri == null");
            }
        }else if(requestCode==REQUEST_THIRD&&resultCode==RESULT_OK){
            setResult(RESULT_OK);
            finish();
        } else if(resultCode != RESULT_OK) {
            // キャンセル時
            return ;
        }
    }

    public String getPath(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] columns = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        try {
            cursor.moveToFirst();
            String path = cursor.getString(0);
            cursor.close();
            File file = new File(uri.getPath());
            System.out.println(file);
            Bitmap bmp = BitmapFactory.decodeFile(path);
            setImage(bmp);
            //imageView.setImageBitmap(bmp);
            URL = path;
            return path;
        }catch(RuntimeException e){
            File file = new File(uri.getPath());
            return file.toString();
        }
    }

    public void setImage(Bitmap bmp){
        imageWidth = imageView.getWidth();
        imageHeight = imageView.getHeight();
        if(imageView!=null){
            next.setVisibility(View.VISIBLE);
            finish.setVisibility(View.VISIBLE);
        }
        imageView.setImageBitmap(resizeReadMatrix(bmp,imageWidth,imageHeight));

        registerDatabase(filePath);
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
}
