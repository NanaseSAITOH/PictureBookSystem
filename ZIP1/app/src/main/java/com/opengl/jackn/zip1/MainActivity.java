package com.opengl.jackn.zip1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    database1 data;
    SQLiteDatabase db;
    TextView textView;
    int REQUEST_FIRST=1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        registerData();

        final Intent intent = new Intent(this, SecoundClass.class);

        final Button Start = findViewById(R.id.start);
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent,REQUEST_FIRST);
            }
        });
    }

    public void registerData(){
        if(data == null){
            data = new database1(getApplicationContext());
        }

        if(db == null){
            db = data.getWritableDatabase();
        }
        readData();
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
        System.out.println(cursor.getCount());
        textView.setText(String.valueOf(cursor.getCount()));

        // 忘れずに！
        cursor.close();

        //Log.d("debug","**********"+sbuilder.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_FIRST&&resultCode==RESULT_OK){
            finish();
            moveTaskToBack(true);
        }
    }
}
