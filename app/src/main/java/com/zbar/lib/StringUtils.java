package com.zbar.lib;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jihf on 2016/7/15 0015.
 */
public class StringUtils {
  private static final String TAG = StringUtils.class.getSimpleName();

  /**
   * 当前只针对首页卡片管理
   *
   * @throws StreamCorruptedException
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public static ArrayList<String> stringToArrayList(String strList) {
    if (TextUtils.isEmpty(strList) || null == strList) {
      return null;
    }
    String[] temp = strList.split(",");
    ArrayList<String> tempList = new ArrayList<>();
    if (null != temp && temp.length != 0) {
      for (String key : temp) {
        tempList.add(key);
      }
    }
    if (strList.contains(",")) {
      Log.i(TAG, "stringToArrayList: " + tempList);
    }
    return tempList;
  }

  public static String listToString(List list) throws IOException {
    if (null == list || list.size() == 0) {
      return null;
    }
    // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    // 然后将得到的字符数据装载到ObjectOutputStream
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
    objectOutputStream.writeObject(list);
    // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
    String ListString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
    // 关闭objectOutputStream
    objectOutputStream.close();
    return ListString;
  }

  public static ArrayList stringToList(String list)
      throws StreamCorruptedException, IOException, ClassNotFoundException {
    byte[] mobileBytes = Base64.decode(list.getBytes(), Base64.DEFAULT);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
    ArrayList strList = (ArrayList) objectInputStream.readObject();
    objectInputStream.close();
    return strList;
  }
}
