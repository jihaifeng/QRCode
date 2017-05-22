package com.jihf.jihf.qrcode;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;
import com.wsh.base.lib.zxing.log.Config;
import com.wsh.base.lib.zxing.log.FileLogger;
import java.util.ArrayList;
import java.util.List;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2017-05-20 11:31
 * Mail：jihaifeng@raiyi.com
 */
public class ScanActivity extends AppCompatActivity implements View.OnClickListener, QRCodeView.Delegate {
  public static final String TAG = ScanActivity.class.getSimpleName();
  private ZBarView zbarview;
  private ToggleButton toggOpenLight;
  private Button btnCancel;
  private List<String> strings = new ArrayList<>();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scan);
    initView();
    zbarview.setDelegate(ScanActivity.this);
    zbarview.startSpot();
  }

  @Override protected void onStart() {
    super.onStart();
    zbarview.startCamera();
    zbarview.showScanRect();
  }

  @Override protected void onStop() {
    zbarview.stopCamera();
    super.onStop();
  }

  @Override protected void onDestroy() {
    zbarview.onDestroy();
    super.onDestroy();
  }

  private void initView() {
    zbarview = (ZBarView) findViewById(R.id.zbarview);
    toggOpenLight = (ToggleButton) findViewById(R.id.btn_open_light);
    toggOpenLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          zbarview.openFlashlight();
          toggOpenLight.setTextColor(Color.WHITE);
          toggOpenLight.setBackgroundResource(R.drawable.bg_circle_unclick);
        } else {
          zbarview.closeFlashlight();
          toggOpenLight.setTextColor(ContextCompat.getColor(ScanActivity.this, R.color.colorAccent));
          toggOpenLight.setBackgroundResource(R.drawable.bg_circle_clicked);
        }
      }
    });
    btnCancel = (Button) findViewById(R.id.btn_cancel);
    btnCancel.setOnClickListener(this);
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_cancel:
        onBackPressed();
        break;
    }
  }

  @Override public void onScanQRCodeSuccess(String result) {
    Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    vibrate();
    zbarview.startSpot();

    if (!TextUtils.isEmpty(result)) {
      Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
      if (!Config.getQrCodeData().contains(result)) {
        Config.getQrCodeData().add(result);
      }
    }
    //Config.setQrCodeData(strings);
    if (Config.isAutoExport()) {
      FileLogger.getInstance().saveCrashInfoFile(strings);
    }
    if (!Config.isAutoScan()) {
      this.finish();
    }

  }

  private void vibrate() {
    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    vibrator.vibrate(200);
  }

  @Override public void onScanQRCodeOpenCameraError() {
    Log.e(TAG, "打开相机出错");
  }
}
