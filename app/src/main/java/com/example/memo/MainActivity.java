package com.example.memo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Intent intent;
    ListView lv;
    LinearLayout group;
    int count = 0;
    private List<Memo> lists;
    FloatingActionButton fab;
    EditText search;
    TextView textDel;
    String searchTarget;
    MyAdapter adapter;
    ArrayList<Memo> sellList;
    Button delBtn, cancelBtn, all_sel;
    SQLiteOpenHelper dbHelper;
    Boolean deletemode = false;
    Boolean isShow = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.fab);
        lv = (ListView) findViewById(R.id.listView);
        group = (LinearLayout) findViewById(R.id.btnGroup);
        textDel = (TextView) findViewById(R.id.textDel);
        delBtn = (Button) findViewById(R.id.delButton);
        cancelBtn = (Button) findViewById(R.id.cancelButton);
        search = (EditText) findViewById(R.id.search);
        searchTarget = search.getText().toString();
        all_sel = (Button) this.findViewById(R.id.all_sel);

        all_sel.setOnClickListener(this);
        delBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        lists = new ArrayList<Memo>();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (deletemode) {
                    setDelList(position);
                    return;
                }
                Memo item = lists.get(position);
                String msg = item.getId();
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("data", msg);
                startActivity(intent);
            }
        });

        sellList = new ArrayList<Memo>();
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isShow) {
                    isShow = true;
                    for (int i = 0; i < lists.size(); i++) {
                        Memo item = lists.get(i);
                        item.setShow(true);
                    }
                }
                deletemode = true;
                textDel.setVisibility(View.VISIBLE);
                group.setVisibility(View.VISIBLE);
                setDelList(position);
                return true;
            }
        });
        dbHelper = new DatabaseHelper(getApplicationContext());

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //这个方法被调用，说明在s字符串中，从start位置开始的count个字符即将被长度为after的新文本所取代。在这个方法里面改变s，会报错。
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //这个方法被调用，说明在s字符串中，从start位置开始的count个字符刚刚取代了长度为before的旧文本。在这个方法里面改变s，会报错。

            }

            @Override
            public void afterTextChanged(Editable s) {
                //这个方法被调用，那么说明s字符串的某个地方已经被改变。
                searchTarget = search.getText().toString();
                showData();
            }
        });

    }

    protected void onResume() {
        super.onResume();
        search.setText("");
        isShow = false;
        deletemode = false;
        group.setVisibility(View.GONE);
        textDel.setVisibility(View.GONE);
        sellList.clear();
        count = 0;
        showData();
    }

    public void showData() {
        lists.clear();
        SQLiteOpenHelper dbHelper = new DatabaseHelper(getApplicationContext());
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("MEMO",
                    new String[]{"_id", "NAME", "TIME"},
                    "NAME" + " LIKE ?",
                    new String[]{"%" + searchTarget + "%"},
                    null, null, "TIME" + " DESC");
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Memo memo = new Memo();
                    memo.setId(cursor.getString(0));
                    memo.setTitleName(cursor.getString(1));
                    memo.setDate(cursor.getString(2));
                    memo.setShow(false);

                    lists.add(memo);
                }
                lv.setAdapter(adapter = new MyAdapter(lists));
            } else {
                lv.setAdapter(null);
            }
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(MainActivity.this, "SQL error happened:\n" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setDelList(int position) {
        Memo item = lists.get(position);

        if (!sellList.contains(item)) {
            item.checked = true;
            sellList.add(item);
            count = count + 1;
        } else {
            item.checked = false;
            sellList.remove(item);
            count = count - 1;
        }
        adapter.notifyDataSetChanged();
        String s = "Select " + count + " items";
        textDel.setText(s);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //设置全部选中
            case R.id.all_sel:
                for (int i = 0; i < lists.size(); i++) {
                    Memo item = lists.get(i);
                    item.checked = true;
                }
                sellList = new ArrayList<Memo>(lists);
                count = sellList.size();
                adapter.notifyDataSetChanged();
                String s = "Select " + count + " items";
                textDel.setText(s);
                break;
            //取消全部选中
            case R.id.cancelButton:
                for (int i = 0; i < lists.size(); i++) {
                    Memo item = lists.get(i);
                    item.checked = false;
                    item.setShow(false);
                }
                adapter.notifyDataSetChanged();
                sellList.clear();
                group.setVisibility(View.GONE);
                textDel.setVisibility(View.GONE);
                count = 0;
                isShow = false;
                deletemode = false;
                break;
            case R.id.delButton:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    for (Memo i : sellList) {
                        String itemId = i.getId();
                        db.delete("MEMO", "_id=?", new String[]{itemId});
                        lists.remove(i);
                    }
                    Toast.makeText(MainActivity.this, "deleted successfully", Toast.LENGTH_SHORT).show();
                    db.close();
                } catch (SQLiteException e) {
                    Toast.makeText(MainActivity.this, "SQL error happened:\n" + e.toString(), Toast.LENGTH_SHORT).show();
                }
                for (int i = 0; i < lists.size(); i++) {
                    Memo item = lists.get(i);
                    item.setShow(false);
                }
                sellList.clear();
                group.setVisibility(View.GONE);
                textDel.setVisibility(View.GONE);
                count = 0;
                isShow = false;
                deletemode = false;
        }
    }
}
