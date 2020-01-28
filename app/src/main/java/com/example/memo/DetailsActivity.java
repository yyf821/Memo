package com.example.memo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity  extends AppCompatActivity {
    Button submit;
    EditText title, body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        submit = (Button) findViewById(R.id.btnSubmit);
        title = (EditText) findViewById(R.id.title);
        body = (EditText) findViewById(R.id.body);

        final SQLiteOpenHelper dbHelper = new DatabaseHelper(getApplicationContext());
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String time = sdf.format(new Date().getTime());
                String memotitle= title.getText().toString();
                if ("".equals(memotitle)){
                    Toast.makeText(getApplicationContext(),"The title can not be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                insertMemo(db, memotitle, body.getText().toString(), time);
                db.close();
                finish();
            }
        });
    }
    private static void insertMemo(SQLiteDatabase db, String name,
                                    String body, String time) {
        ContentValues memoValues = new ContentValues();
        memoValues.put("NAME", name);
        memoValues.put("BODY", body);
        memoValues.put("TIME", time);
        db.insert("MEMO", null, memoValues);
    }
}
