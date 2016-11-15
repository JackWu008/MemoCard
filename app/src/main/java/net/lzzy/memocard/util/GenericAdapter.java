package net.lzzy.memocard.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/4/27.
 * 适配器
 */
public abstract class GenericAdapter<T> extends BaseAdapter {
    private List<T> items;
    private Context context;
    private int layout;

    public GenericAdapter(Context context, int layout, List<T> items) {
        this.context = context;
        this.layout = layout;
        this.items = items;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.getInstance(context, null, layout, convertView);
        populate(holder, getItem(position));
        return holder.getConvertView();
    }

    public abstract void populate(ViewHolder holder, T t);

}
