package com.wsh.base.lib.zxing.log;

import java.util.ArrayList;
import java.util.List;

/**
 * Func：
 * Desc:
 * Author：jihf
 * Date：2017-05-19 9:50
 * Mail：jihaifeng@raiyi.com
 */
public class Config {
  private static List<String> mQRCodeList = new ArrayList<>();

  public static boolean autoScan = false;
  public static boolean autoExport = false;

  public static void setQrCodeData(List<String> qrCodeData) {
    if (null != qrCodeData) {
      mQRCodeList = qrCodeData;
    }
  }

  public static List<String> getQrCodeData() {
    return mQRCodeList;
  }

  public static boolean isAutoScan() {
    return autoScan;
  }

  public static void setAutoScan(boolean autoScan) {
    Config.autoScan = autoScan;
  }

  public static boolean isAutoExport() {
    return autoExport;
  }

  public static void setAutoExport(boolean autoExport) {
    Config.autoExport = autoExport;
  }

  public static void clearData() {
    if (null != mQRCodeList) {
      mQRCodeList.clear();
    }
  }
}
