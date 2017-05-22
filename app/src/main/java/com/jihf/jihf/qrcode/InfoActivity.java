package com.jihf.jihf.qrcode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.jihf.jihf.qrcode.ftp.FTPManager;

/**
 * Func：
 * Desc:
 * Author：jihf
 * Date：2017-05-22 18:19
 * Mail：jihaifeng@raiyi.com
 */
public class InfoActivity extends AppCompatActivity {
  private TextView tvInfo;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_info);
    tvInfo = (TextView) findViewById(R.id.tv_info);
    tvInfo.setText("url："
        + FTPManager.getInstance().getUrl()
        + "\n"
        + "port："
        + FTPManager.getInstance().getPort()
        + "\n"
        + "path："
        + FTPManager.getInstance().getServerPath()
        + "\n"
        + "userName："
        + FTPManager.getInstance().getUserName()
        + "\n"
        + "userPass："
        + FTPManager.getInstance().getUserPass()
        + "\n");
  }
}
