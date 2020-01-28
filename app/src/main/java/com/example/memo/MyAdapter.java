package com.example.memo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyAdapter extends BaseAdapter {

    private List<Memo> lists;

    public MyAdapter(List<Memo> lists) {
        this.lists = lists;
    }

    public void updateData(List<Memo> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        //设置显示条目
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.name);
            holder.tv_date = (TextView) convertView.findViewById(R.id.date);
            holder.mCheckBox = (CheckBox)convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);   //将Holder存储到convertView中
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Memo memo = lists.get(position);
        if (memo.isShow()) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.GONE);
        }
        holder.tv_name.setText(memo.getTitleName());
        holder.tv_date.setText(getTime(memo));
        holder.mCheckBox.setChecked(memo.checked);
        return convertView;
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_date;
        public CheckBox mCheckBox;

    }

    public static String getTime(Memo memo) {
        String time = "";
        boolean isWeek = false;
        String[] weeklist = week();
        String date = memo.getDate();//yyyy-MM-dd HH:mm:ss

        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int memoyear = Integer.parseInt(date.substring(0, 4));
        String memodate = date.substring(0, 10);
        if (memoyear == currentYear) {
            for (int i = 0; i < weeklist.length; i++) {
                String s = weeklist[i].substring(0, 10);
                if (s.equals(memodate)) {
                    switch (i) {
                        case 0:
                            time = date.substring(11,16);//today
                            break;
                        case 1:
                            time = "yesterday";
                            break;
                        default:
                            time = weeklist[i].substring(20);//MON, TUE...

                    }
                    isWeek = true;
                }

            }
            if (!isWeek) {
                time = date.substring(5);
            }
        } else {
            time = date.substring(0, 10);
        }
        return time;
    }

    public static String[] week() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss E");
        Calendar c = Calendar.getInstance();
        String[] weekList =new String[7];
        for (int i = 0; i < 7; i++) {
            //过去七天
            c.setTime(new Date());
            c.add(Calendar.DATE, - i);
            Date d = c.getTime();
            String day = format.format(d);
            weekList[i] = day;
        }
        return weekList;
    }

}
