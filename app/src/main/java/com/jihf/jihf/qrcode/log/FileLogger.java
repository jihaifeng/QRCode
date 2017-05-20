package com.jihf.jihf.qrcode.log;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * @author Yu Jingbo
 * @Description: 输出文件日志
 * @date 2014-10-29 下午5:20:25
 */
public class FileLogger {
  public static final String TAG = FileLogger.class.getSimpleName().trim();

  private static FileLogger instance;
  //崩溃日志文件的名字，前缀，全称 （mFileName + 当天日期），默认txt文件
  private static String mFileName = "QRCode";
  //文件名之间的分隔符
  private static String SEPARATOR = "-";
  //崩溃日志文件夹的名字，默认为Crash
  private static String mfloadName = "114-QRCode";

  public FileLogger() {

  }

  public static FileLogger getInstance() {
    if (null == instance) {
      synchronized (FileLogger.class) {
        if (null == instance) {
          instance = new FileLogger();
        }
      }
    }
    return instance;
  }

  /**
   * 保存日志文件
   */
  public String saveCrashInfoFile(List<String> list) {
    if (FileUtils.isFileExist(FileLogger.getInstance().getFliePath())) {
      FileUtils.deleteFile(FileLogger.getInstance().getFliePath());
    }
    if (null == list || list.size() < 1) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    try {
      for (int i = 0; i < list.size(); i++) {
        int num = i + 1;
        sb.append(num).append("、").append(list.get(i)).append("\n");
      }
      Writer writer = new StringWriter();
      PrintWriter printWriter = new PrintWriter(writer);
      printWriter.flush();
      printWriter.close();
      String fileName = writeErrorToFile(sb.toString());
      Log.i(TAG, "saveCrashInfoFile: " + sb.toString());
      return fileName;
    } catch (Exception e) {
      e.printStackTrace();
      writeErrorToFile(sb.toString());
    }
    return null;
  }

  /**
   * 崩溃日志写入文件
   *
   * @param strError 崩溃日志信息
   *
   * @return 崩溃日志的文件名
   */
  private String writeErrorToFile(String strError) {

    String fileName = getFileFullName();
    try {
      if (FileUtils.hasSdcard()) {
        String path = getfloadPath();
        File dir = new File(path);
        if (!dir.exists()) {
          dir.mkdir();
        }
        FileOutputStream fos = new FileOutputStream(path + fileName, true);
        fos.write(strError.getBytes());
        fos.flush();
        fos.close();
      }
    } catch (Exception e) {
      Log.i(TAG, "writeErrorToFile: " + e.getMessage());
      e.printStackTrace();
    }
    return fileName;
  }

  /**
   * 获取崩溃日志的文件名(全称)
   *
   * @return 如 CrashLog-20160624.txt
   */
  private String getFileFullName() {
    String dateToday = DateTimeUtils.getCustomDateFormat();
    String fileName = dateToday + SEPARATOR + mFileName + ".txt";
    return fileName;
  }

  /**
   * 获取崩溃日志保存的文件夹
   *
   * @return
   */
  public String getfloadPath() {
    return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + mfloadName + File.separator;
  }

  /**
   * 获取崩溃日志保存的文件路径
   *
   * @return
   */
  public String getFliePath() {
    return getfloadPath() + getFileFullName();
  }
}
