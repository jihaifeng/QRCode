package com.jihf.jihf.qrcode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.jihf.jihf.qrcode.ftp.FTPManager;

/**
 * Func：
 * Desc:
 * Author：jihf
 * Date：2017-05-22 18:07
 * Mail：jihaifeng@raiyi.com
 */
public class SettingActivity extends AppCompatActivity {
  private String url = "220.250.65.22";
  private int port = -1;
  private String serverPath = "invoice/";
  private String userName = "sina";
  private String userPass = "r7Wd@H0ld";

  private EditText editUrl;
  private EditText editPort;
  private EditText editPath;
  private EditText editUserName;
  private EditText editUserPass;
  private Button btnSet;
  private Button btnReset;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    initView();
  }

  private void initView() {
    editUrl = (EditText) findViewById(R.id.edit_url);
    editPort = (EditText) findViewById(R.id.edit_port);
    editPath = (EditText) findViewById(R.id.edit_path);
    editUserName = (EditText) findViewById(R.id.edit_userName);
    editUserPass = (EditText) findViewById(R.id.edit_userPass);
    btnSet = (Button) findViewById(R.id.btn_set);
    btnReset = (Button) findViewById(R.id.btn_reset);
    btnReset.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        FTPManager.getInstance().setUrl(url);

        FTPManager.getInstance().setPort(port);

        FTPManager.getInstance().setServerPath(serverPath);
        FTPManager.getInstance().setUserName(userName);

        FTPManager.getInstance().setUserPass(userPass);
        FTPManager.getInstance().connect();
        finish();
      }
    });
    btnSet.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (!TextUtils.isEmpty(editUrl.getText().toString())) {
          FTPManager.getInstance().setUrl(editUrl.getText().toString());
        }

        FTPManager.getInstance()
            .setPort(TextUtils.isEmpty(editPort.getText().toString()) ? -1
                : Integer.parseInt(editPort.getText().toString()));

        if (!TextUtils.isEmpty(editPath.getText().toString())) {
          FTPManager.getInstance().setServerPath(editPath.getText().toString());
        }
        if (!TextUtils.isEmpty(editUserName.getText().toString())) {
          FTPManager.getInstance().setUserName(editUserName.getText().toString());
        }
        if (!TextUtils.isEmpty(editUserPass.getText().toString())) {

          FTPManager.getInstance().setUserPass(editUserPass.getText().toString());
        }
        FTPManager.getInstance().connect();
        finish();
      }
    });
  }
}
