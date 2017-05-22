package com.jihf.jihf.qrcode.ftp;

/**
 * Func：
 * Desc:
 * Author：jihf
 * Date：2017-05-22 15:45
 * Mail：jihaifeng@raiyi.com
 */
public interface FTPListenter {

  void onSuccess();

  void onProcess(long process);

  void onFailure(String errorMsg);
}
