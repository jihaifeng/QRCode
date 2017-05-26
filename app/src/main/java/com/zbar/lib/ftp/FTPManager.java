package com.zbar.lib.ftp;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.zbar.lib.SPUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Func： 通过ftp上传文件
 * Desc:
 * Author：jihf
 * Date：2017-05-22 13:15
 * Mail：jihaifeng@raiyi.com
 */
public class FTPManager {
  public static final String TAG = FTPManager.class.getSimpleName();
  private static FTPManager instance;
  private static FTPClient ftpClient;

  private final int CONNECT_TIMEOUT = 10 * 1000;
  private boolean isConnected = false;
  private boolean isUpload = false;
  private static Context mContext;
  private SPUtils spUtils;

  private String url;
  private int port;
  private String serverPath;
  private String userName;
  private String userPass;

  public static FTPManager getInstance(Context context) {
    mContext = context;
    if (null == instance) {
      synchronized (FTPManager.class) {
        if (null == instance) {
          instance = new FTPManager();
        }
      }
    }
    ftpClient = new FTPClient();
    return instance;
  }

  /**
   * 实现上传文件的功能
   *
   * @param localPath 本地文件路径
   *
   * @return
   */
  public synchronized void uploadFile(final String localPath, final FTPListenter listenter) {
    // 上传文件之前，先判断本地文件是否存在
    if (isConnected) {
      new Thread(new Runnable() {
        @Override public void run() {
          upload(localPath, listenter);
        }
      }).start();
    } else {
      listenter.onFailure("ftp 连接失败");
    }
  }

  private void upload(String localPath, FTPListenter listenter) {
    try {

      File localFile = new File(localPath);
      if (!localFile.exists()) {
        Log.i(TAG, "uploadFile: " + "本地文件不存在");
        listenter.onFailure("本地文件不存在");
        return;
      }
      Log.i(TAG, "uploadFile: " + "本地文件存在，名称为：" + localFile.getName());
      // 如果文件夹不存在，创建文件夹
      //createDirectory(serverPath);
      //ftpClient.makeDirectory(serverPath);
      boolean flag = ftpClient.changeWorkingDirectory(serverPath);
      if (!flag) {
        ftpClient.makeDirectory(serverPath);
        ftpClient.changeWorkingDirectory(serverPath);
      }
      Log.i(TAG, "upload: " + flag);
      Log.i(TAG, "uploadFile: " + "服务器文件存放路径：" + serverPath + localFile.getName());
      String fileName = localFile.getName();
      // 如果本地文件存在，服务器文件也在，上传文件，这个方法中也包括了断点上传
      long localSize = localFile.length();
      // 本地文件的长度
      int i = 1;
      String ftpFileName = fileName;
      while (ftpClient.listFiles(ftpFileName).length != 0) {
        ftpFileName = fileName.substring(0, fileName.length() - 4) + "_" + i + ".txt";
        i++;
      }
      Log.i(TAG, "upload ftpFileName: " + ftpFileName);
      FTPFile[] files = ftpClient.listFiles(ftpFileName);
      long serverSize = 0;
      //if (files.length == 0) {
      //  Log.i(TAG, "uploadFile: " + "服务器文件不存在");
      //  serverSize = 0;
      //} else {
      //  serverSize = files[0].getSize(); // 服务器文件的长度
      //}
      //if (localSize <= serverSize) {
      //  if (ftpClient.deleteFile(fileName)) {
      //    Log.i(TAG, "uploadFile: " + "服务器文件存在,删除文件,开始重新上传");
      //    serverSize = 0;
      //  }
      //}

      RandomAccessFile raf = new RandomAccessFile(localFile, "r");
      // 进度
      long step = localSize % 100;
      long process = 0;
      long currentSize = 0;
      // 好了，正式开始上传文件
      ftpClient.enterLocalPassiveMode();
      ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
      ftpClient.setRestartOffset(serverSize);
      raf.seek(serverSize);
      OutputStream output = ftpClient.appendFileStream(ftpFileName);
      byte[] b = new byte[1024];
      int length = 0;
      while ((length = raf.read(b)) != -1) {
        output.write(b, 0, length);
        currentSize = currentSize + length;
        Log.i(TAG, "upload: " + step);
        if (currentSize / step != process) {
          process = currentSize / step;
          listenter.onProcess(process);
          if (process % 10 == 0) {
            Log.i(TAG, "uploadFile: " + "上传进度：" + process);
          }
        }
      }
      output.flush();
      output.close();
      raf.close();
      if (ftpClient.completePendingCommand()) {
        Log.i(TAG, "uploadFile: " + "文件上传成功");
        listenter.onSuccess();
      } else {
        Log.i(TAG, "uploadFile: " + "文件上传失败");
        listenter.onFailure("文件上传失败");
      }
      closeFTP();
    } catch (Exception e) {
      e.printStackTrace();
      Log.i(TAG, "uploadFile Exception: " + e);
      listenter.onFailure(TextUtils.isEmpty(e.getMessage()) ? "文件上传失败" : e.getMessage());
    }
  }

  // 实现下载文件功能，可实现断点下载

  public synchronized boolean downloadFile(String localPath) {
    // 先判断服务器文件是否存在
    try {
      FTPFile[] files = ftpClient.listFiles(serverPath);
      if (files.length == 0) {
        Log.i(TAG, "uploadFile: " + "服务器文件不存在");
        return false;
      }
      Log.i(TAG, "uploadFile: " + "远程文件存在,名字为：" + files[0].getName());
      localPath = localPath + files[0].getName();
      // 接着判断下载的文件是否能断点下载
      long serverSize = files[0].getSize(); // 获取远程文件的长度
      File localFile = new File(localPath);
      long localSize = 0;
      if (localFile.exists()) {
        localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
        if (localSize >= serverSize) {
          Log.i(TAG, "uploadFile: " + "文件已经下载完了");
          File file = new File(localPath);
          file.delete();
          Log.i(TAG, "uploadFile: " + "本地文件存在，删除成功，开始重新下载");
          return false;
        }
      }
      // 进度
      long step = serverSize / 100;
      long process = 0;
      long currentSize = 0;
      // 开始准备下载文件
      ftpClient.enterLocalActiveMode();
      ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
      OutputStream out = new FileOutputStream(localFile, true);
      ftpClient.setRestartOffset(localSize);
      InputStream input = ftpClient.retrieveFileStream(serverPath);
      byte[] b = new byte[1024];
      int length = 0;
      while ((length = input.read(b)) != -1) {
        out.write(b, 0, length);
        currentSize = currentSize + length;
        if (currentSize / step != process) {
          process = currentSize / step;
          if (process % 10 == 0) {
            Log.i(TAG, "uploadFile: " + "下载进度：" + process);
          }
        }
      }
      out.flush();
      out.close();
      input.close();
      // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
      if (ftpClient.completePendingCommand()) {
        Log.i(TAG, "uploadFile: " + "文件下载成功");
        return true;
      } else {
        Log.i(TAG, "uploadFile: " + "文件下载失败");
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  // 如果ftp上传打开，就关闭掉
  public static void closeFTP() {
    if (null != ftpClient && ftpClient.isConnected()) {
      try {
        ftpClient.disconnect();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // 创建文件夹
  private boolean createDirectory(String path) {
    try {
      boolean bool = false;
      String directory = path.substring(0, path.lastIndexOf("/") + 1);
      int start = 0;
      int end = 0;
      if (directory.startsWith("/")) {
        start = 1;
      }
      end = directory.indexOf("/", start);
      while (!ftpClient.makeDirectory(path)) {
        String subDirectory = directory.substring(start, end);
        if (!ftpClient.changeWorkingDirectory(subDirectory)) {
          ftpClient.makeDirectory(subDirectory);
          ftpClient.changeWorkingDirectory(subDirectory);
          bool = true;
        }
        start = end + 1;
        end = directory.indexOf("/", start);
        if (end == -1) {
          break;
        }
      }
      return bool;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Func：连接到ftp服务器
   * Desc:
   * Author：jihf
   * Date：2017-05-22 13:29
   * Mail：jihaifeng@raiyi.com
   */
  public synchronized void connect(final FTPListenter listenter) {
    initConfig();
    new Thread(new Runnable() {
      @Override public void run() {
        try {
          if (ftpClient.isConnected()) {//判断是否已登陆
            ftpClient.disconnect();
          }
          ftpClient.setDataTimeout(CONNECT_TIMEOUT);//设置连接超时时间
          ftpClient.setControlEncoding("utf-8");
          if (port == -1) {
            ftpClient.connect(url);
          } else {
            ftpClient.connect(url, port);
          }
          if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            // 登录ftp服务器
            if (ftpClient.login(userName, userPass)) {
              isConnected = true;
              Log.i(TAG, "connect: ftp连接成功");
              listenter.onSuccess();
            } else {
              Log.i(TAG, "ftp登录失败");
            }
          }
        } catch (Exception e) {
          Log.i(TAG, "connect: " + e);
          e.printStackTrace();
          listenter.onFailure("ftp连接失败");
        }
      }
    }).start();
  }

  private void initConfig() {
    spUtils = new SPUtils("114Scan", mContext);
    url = spUtils.getString(FTPConfig.URL, FTPConfig.default_url);
    port = spUtils.getInt(FTPConfig.PORT, FTPConfig.default_port);
    serverPath = spUtils.getString(FTPConfig.PATH, FTPConfig.default_serverPath);
    userName = spUtils.getString(FTPConfig.USERNAME, FTPConfig.default_userName);
    userPass = spUtils.getString(FTPConfig.USERPASS, FTPConfig.default_userPass);
  }

  public boolean isConnected() {
    return isConnected;
  }

  /**
   * 检查文件夹是否存在
   *
   * @param dir
   *
   * @return
   */
  public boolean isDirExist(String dir) {
    try {
      //ftpClient.i(dir);
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
