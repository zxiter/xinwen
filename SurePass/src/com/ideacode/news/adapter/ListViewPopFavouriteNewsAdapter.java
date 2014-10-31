package com.ideacode.news.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ideacode.news.R;
import com.ideacode.news.common.util.StringUtils;

public class ListViewPopFavouriteNewsAdapter extends BaseAdapter {

    private final Context                     context;//����������
    private final ArrayList<Map<String,Object>>                  listItems;//���ݼ���
    private final LayoutInflater              listContainer;//��ͼ����
    private final int                         itemViewResource;//�Զ�������ͼԴ
    static class ListItemView{              //�Զ���ؼ�����  
        public TextView title;  
        public TextView date; 
        public ImageView flag;
        public ImageView heatFlag;
    } 

    public ListViewPopFavouriteNewsAdapter(Context context, ArrayList<Map<String,Object>> data,int resource) {
        this.context = context;         
        this.listContainer = LayoutInflater.from(context);  //������ͼ����������������
        this.itemViewResource = resource;
        this.listItems = data;
    }

    public void addNewData(ArrayList<Map<String,Object>> data) {
        listItems.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView  listItemView = null;

        if (convertView == null) {
            //��ȡlist_item�����ļ�����ͼ
            convertView = listContainer.inflate(this.itemViewResource, null);

            listItemView = new ListItemView();
            //��ȡ�ؼ�����
            listItemView.title = (TextView)convertView.findViewById(R.id.news_listitem_title);
            listItemView.date = (TextView) convertView.findViewById(R.id.news_listitem_date);
            listItemView.flag = (ImageView) convertView.findViewById(R.id.news_listitem_flag);
            listItemView.heatFlag = (ImageView) convertView.findViewById(R.id.news_listitem_heatflag);

            //���ÿؼ�����convertView
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }

        Map map = listItems.get(position);
        
        listItemView.title.setText(map.get("title").toString());
        listItemView.title.setTag(map);//�������ز���(ʵ����)
        listItemView.date.setText(StringUtils.friendly_time(map.get("date").toString()));
        if (StringUtils.isToday(map.get("date").toString()))
            listItemView.flag.setVisibility(View.VISIBLE);
        else
            listItemView.flag.setVisibility(View.GONE);
        if ((Boolean) map.get("heatFlag")) {
            listItemView.heatFlag.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
