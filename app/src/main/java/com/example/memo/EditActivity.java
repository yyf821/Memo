package com.example.memo;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {
    private Button btn, edit;
    AlertDialog.Builder builder;
    String id;
    private EditText title, textView3;
    private TextView textView2;
    SQLiteOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        title = (EditText) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (EditText) findViewById(R.id.textView3);
        btn = (Button) findViewById(R.id.button);
        edit = (Button) findViewById(R.id.editbtn);

        //通过Activity.getIntent()获取当前页面接收到的Intent。
        Intent intent = getIntent();
        //getXxxExtra方法获取Intent传递过来的数据
        String msg = intent.getStringExtra("data");

        dbHelper = new DatabaseHelper(getApplicationContext());

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("MEMO",
                    new String[]{"_id", "Name", "BODY", "TIME"},
                    "_id = ?",
                    new String[]{msg},
                    null, null, null);
            if (cursor.moveToFirst()) {
                String name = cursor.getString(1);
                String body = cursor.getString(2);
                String time = cursor.getString(3);
                id = cursor.getString(0);
                title.setText(name);
                textView2.setText(time);
                textView3.setText(body);

            }
        } catch (SQLiteException e) {
            Toast.makeText(EditActivity.this, "SQL error happened:\n" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        builder = new AlertDialog.Builder(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Alert");
                builder.setMessage("Are you sure you want to delete this memo?");
                //点击对话框以外的区域是否让对话框消失
                builder.setCancelable(true);
                //设置正面按钮
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                        finish();
                    }
                });
                //设置反面按钮
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                String time = sdf.format(new Date().getTime());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String memotitle= title.getText().toString();
                if ("".equals(memotitle)){
                    Toast.makeText(getApplicationContext(),"The title can not be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    ContentValues memoValues = new ContentValues();
                    memoValues.put("NAME", title.getText().toString());
                    memoValues.put("BODY", textView3.getText().toString());
                    memoValues.put("TIME", time);
                    int update = db.update("MEMO", memoValues, "_id=?", new String[]{id});
                    Toast.makeText(EditActivity.this, "updated successfully", Toast.LENGTH_LONG).show();
                    db.close();
                } catch (SQLiteException e) {
                    Toast.makeText(EditActivity.this, "SQL error happened:\n" + e.toString(), Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    public void delete() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //db.execSQL("delete from info where name=?",new Object[]{"小明"});
        try {
            db.delete("MEMO", "_id=?", new String[]{id});
            Toast.makeText(EditActivity.this, "deleted successfully", Toast.LENGTH_SHORT).show();
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(EditActivity.this, "SQL error happened:\n" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
