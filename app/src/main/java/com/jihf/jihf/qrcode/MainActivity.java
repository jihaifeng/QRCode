package com.jihf.jihf.qrcode;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.jihf.jihf.qrcode.ftp.FTPListenter;
import com.jihf.jihf.qrcode.ftp.FTPManager;
import com.wsh.base.lib.zxing.CaptureActivity;
import com.wsh.base.lib.zxing.log.Config;
import com.wsh.base.lib.zxing.log.FileLogger;
import com.wsh.base.lib.zxing.log.FileUtils;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
  public static final String TAG = MainActivity.class.getSimpleName();
  public static final int SCAN_PERMISSION_CODE = 9999;
  public static final int STORAGE_PERMISSION_CODE = 9998;
  public static final int SCAN_RESOULT_CODE = 1001;
  private QRCodeAdapter qrCodeAdapter;
  private Button btnScan;
  private Button btnClear;
  private Button btnExport;
  private Button btnUpload;
  private ToggleButton toggAutoScan;
  private ToggleButton toggAutoExport;
  private ListView listView;
  private FTPManager manager;
  private SPUtils spUtils;
  private String SP_TAG = "114Data";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    manager = FTPManager.getInstance().connect();
    spUtils = new SPUtils("114Scan", this);
    initView();
  }

  private void initView() {
    btnScan = (Button) findViewById(R.id.btn_scan);
    btnScan.setOnClickListener(this);
    btnClear = (Button) findViewById(R.id.btn_clear);
    btnClear.setOnClickListener(this);
    btnExport = (Button) findViewById(R.id.btn_export);
    btnExport.setOnClickListener(this);
    btnUpload = (Button) findViewById(R.id.btn_upload);
    btnUpload.setOnClickListener(this);

    toggAutoScan = (ToggleButton) findViewById(R.id.togg_auto_scan);
    toggAutoScan.setChecked(Config.autoScan);
    toggAutoScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Config.setAutoScan(isChecked);
        switchToggleBg();
      }
    });
    toggAutoExport = (ToggleButton) findViewById(R.id.togg_auto_export);
    toggAutoExport.setChecked(Config.autoExport);
    toggAutoExport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Config.setAutoExport(isChecked);
        switchToggleBg();
      }
    });
    switchToggleBg();

    listView = (ListView) findViewById(R.id.lv_data);
    qrCodeAdapter = new QRCodeAdapter();
    listView.setAdapter(qrCodeAdapter);
    String dataCache = spUtils.getString(SP_TAG);
    try {
      if (!TextUtils.isEmpty(dataCache)) {
        Config.setQrCodeData(StringUtils.stringToList(dataCache));
        qrCodeAdapter.updateData(StringUtils.stringToList(dataCache));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void switchToggleBg() {
    toggAutoScan.setBackgroundResource(Config.isAutoScan() ? R.drawable.bg_round_unclick : R.drawable.bg_round_clicked);
    toggAutoExport.setBackgroundResource(
        Config.isAutoExport() ? R.drawable.bg_round_unclick : R.drawable.bg_round_clicked);
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_scan:
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
          ActivityCompat.requestPermissions(this,
              new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },
              SCAN_PERMISSION_CODE);
        } else {
          startZbarScan();
        }
        break;
      case R.id.btn_clear:
        dialog();
        break;
      case R.id.btn_export:
        String fileName = FileLogger.getInstance().saveCrashInfoFile(Config.getQrCodeData());
        if (!TextUtils.isEmpty(fileName)) {
          Toast.makeText(this, "文件导出成功", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(this, "文件导出失败", Toast.LENGTH_SHORT).show();
        }
        break;
      case R.id.btn_upload:
        FileLogger.getInstance().saveCrashInfoFile(Config.getQrCodeData());
        manager.uploadFile(FileLogger.getInstance().getFliePath(), new FTPListenter() {
          @Override public void onSuccess() {
            Log.i(TAG, "onSuccess: ");
            Looper.prepare();
            Toast.makeText(MainActivity.this, "文件上传成功", Toast.LENGTH_SHORT).show();
            Looper.loop();
          }

          @Override public void onProcess(long process) {
            Log.i(TAG, "onProcess: " + process);
          }

          @Override public void onFailure(String errorMsg) {
            Log.i(TAG, "onFailure: " + errorMsg);
            Looper.prepare();
            Toast.makeText(MainActivity.this, "errorMsg：" + errorMsg, Toast.LENGTH_SHORT).show();
            Looper.loop();
          }
        });
        break;
    }
  }

  protected void dialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("确认清楚今天的文件么？");
    builder.setTitle("提示");
    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        boolean isDelect = FileUtils.deleteFile(FileLogger.getInstance().getFliePath());
        qrCodeAdapter.clearData();
        spUtils.put(SP_TAG, "");
        Config.clearData();
        Toast.makeText(MainActivity.this, isDelect ? "文件删除成功" : "文件删除失败", Toast.LENGTH_SHORT).show();
      }
    });
    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    builder.create().show();
  }

  private void startZbarScan() {
    Intent intent = new Intent(this, ScanActivity.class);
    startActivityForResult(intent, SCAN_RESOULT_CODE);
  }

  private void startZxingScan() {
    Intent intent = new Intent(this, CaptureActivity.class);
    startActivityForResult(intent, SCAN_RESOULT_CODE);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case SCAN_RESOULT_CODE:
        List<String> list = Config.getQrCodeData();
        Log.i(TAG, "onActivityResult: " + list);
        if (null != list) {
          qrCodeAdapter.updateData(list);
        }
        break;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == SCAN_PERMISSION_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        startZbarScan();
      } else {
        Toast.makeText(this, "缺少扫描权限，请授权后重试", Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    try {
      if (null != Config.getQrCodeData() && Config.getQrCodeData().size() != 0) {
        spUtils.put(SP_TAG, StringUtils.listToString(Config.getQrCodeData()));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    Log.i(TAG, "onDestroy: ");
  }
}
