package com.jihf.jihf.qrcode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.jihf.jihf.qrcode.ftp.FTPConfig;

/**
 * Func：
 * Desc:
 * Author：jihf
 * Date：2017-05-22 18:07
 * Mail：jihaifeng@raiyi.com
 */
public class SettingActivity extends AppCompatActivity {
  private EditText editUrl;
  private EditText editPort;
  private EditText editPath;
  private EditText editUserName;
  private EditText editUserPass;
  private Button btnSet;
  private Button btnReset;
  SPUtils spUtils;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    spUtils = new SPUtils("114Scan", this);
    initView();
  }

  private void initView() {
    editUrl = (EditText) findViewById(R.id.edit_url);
    editUrl.setText(spUtils.getString(FTPConfig.URL));

    editPort = (EditText) findViewById(R.id.edit_port);
    editPort.setText(spUtils.getInt(FTPConfig.PORT) == -1 ? "" : String.valueOf(spUtils.getInt(FTPConfig.PORT)));

    editPath = (EditText) findViewById(R.id.edit_path);
    editPath.setText(spUtils.getString(FTPConfig.PATH));

    editUserName = (EditText) findViewById(R.id.edit_userName);
    editUserName.setText(spUtils.getString(FTPConfig.USERNAME));

    editUserPass = (EditText) findViewById(R.id.edit_userPass);
    editUserPass.setText(spUtils.getString(FTPConfig.USERPASS));

    btnSet = (Button) findViewById(R.id.btn_set);
    btnReset = (Button) findViewById(R.id.btn_reset);
    btnReset.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        spUtils.put(FTPConfig.URL, FTPConfig.default_url);
        spUtils.put(FTPConfig.PORT, FTPConfig.default_port);
        spUtils.put(FTPConfig.PATH, FTPConfig.default_serverPath);
        spUtils.put(FTPConfig.USERNAME, FTPConfig.default_userName);
        spUtils.put(FTPConfig.USERPASS, FTPConfig.default_userPass);
        finish();
      }
    });
    btnSet.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        spUtils.put(FTPConfig.URL,
            TextUtils.isEmpty(editUrl.getText().toString()) ? FTPConfig.default_url : editUrl.getText().toString());

        spUtils.put(FTPConfig.PORT, TextUtils.isEmpty(editPort.getText().toString()) ? FTPConfig.default_port
            : Integer.parseInt(editPort.getText().toString()));

        spUtils.put(FTPConfig.PATH, TextUtils.isEmpty(editPath.getText().toString()) ? FTPConfig.default_serverPath
            : editPath.getText().toString());

        spUtils.put(FTPConfig.USERNAME,
            TextUtils.isEmpty(editUserName.getText().toString()) ? FTPConfig.default_userName
                : editUserName.getText().toString());

        spUtils.put(FTPConfig.USERPASS,
            TextUtils.isEmpty(editUserPass.getText().toString()) ? FTPConfig.default_userPass
                : editUserPass.getText().toString());
        finish();
      }
    });
  }
}
