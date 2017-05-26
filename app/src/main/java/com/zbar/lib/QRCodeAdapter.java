package com.zbar.lib;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Func：
 * Desc:
 * Author：jihf
 * Date：2017-05-19 9:30
 * Mail：jihaifeng@raiyi.com
 */
public class QRCodeAdapter extends BaseAdapter {
  private List<String> list;

  public void updateData(List<String> stringList) {
    if (null == list) {
      list = new ArrayList<>();
    }
    if (list.size() != 0) {
      list.clear();
    }
    if (null != stringList) {
      list.addAll(stringList);
    }
    Log.i("MainActivity", "updateData: " + list);
    notifyDataSetChanged();
  }

  public void clearData() {
    if (null == list) {
      list = new ArrayList<>();
    }
    if (list.size() != 0) {
      list.clear();
    }
    notifyDataSetChanged();
  }

  @Override public int getCount() {
    return null == list ? 0 : list.size();
  }

  @Override public Object getItem(int position) {
    return list.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qrcode, null);
    TextView tvQrCode = (TextView) view.findViewById(R.id.tv_qrCode);
    int num = position + 1;
    tvQrCode.setText(num + "、" + list.get(position));
    return view;
  }
}
