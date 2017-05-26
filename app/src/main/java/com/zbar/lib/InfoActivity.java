package com.zbar.lib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.zbar.lib.ftp.FTPConfig;

/**
 * Func：
 * Desc:
 * Author：jihf
 * Date：2017-05-22 18:19
 * Mail：jihaifeng@raiyi.com
 */
public class InfoActivity extends AppCompatActivity {
  private TextView tvInfo;
  SPUtils spUtils;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    spUtils = new SPUtils("114Scan", this);
    setContentView(R.layout.activity_info);
    tvInfo = (TextView) findViewById(R.id.tv_info);
    tvInfo.setText("url："
        + spUtils.getString(FTPConfig.URL)
        + "\n"
        + "port："
        + spUtils.getInt(FTPConfig.PORT)
        + "\n"
        + "path："
        + spUtils.getString(FTPConfig.PATH)
        + "\n"
        + "userName："
        + spUtils.getString(FTPConfig.USERNAME)
        + "\n"
        + "userPass："
        + spUtils.getString(FTPConfig.USERPASS)
        + "\n");
  }
}
