package com.wsh.base.lib.zxing;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.wsh.base.lib.zxing.camera.CameraManager;
import com.wsh.base.lib.zxing.decoding.CaptureActivityHandler;
import com.wsh.base.lib.zxing.decoding.InactivityTimer;
import com.wsh.base.lib.zxing.decoding.QrcodeFileDecoder;
import com.wsh.base.lib.zxing.log.Config;
import com.wsh.base.lib.zxing.log.FileLogger;
import com.wsh.base.lib.zxing.view.ViewfinderView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CaptureActivity extends BaseActivity implements Callback {
  private static final String TAG = "CaptureActivity";

  public static final String REQUEST_SCAN_QRCODE = "qr_code";

  private CaptureActivityHandler handler;
  private ViewfinderView viewfinderView;
  private boolean hasSurface;
  private Vector<BarcodeFormat> decodeFormats;
  private String characterSet;
  // private TextView txtResult;
  private InactivityTimer inactivityTimer;
  private MediaPlayer mediaPlayer;
  private boolean playBeep;
  private static final float BEEP_VOLUME = 0.10f;

  private static final int FILE_SELECT_CODE = 0;
  private boolean vibrate;

  private Button btn_splash;

  private List<String> strings = new ArrayList<>();

  /**
   * Called when the activity is first created.
   */
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_qrcode_zxing);
    // CameraManager
    CameraManager.init(getApplication());

    viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
    // txtResult = (TextView) findViewById(R.id.txtResult);
    hasSurface = false;
    inactivityTimer = new InactivityTimer(this);

    // 打开或者关闭闪光灯
    btn_splash = (Button) findViewById(R.id.btn_splash);
    btn_splash.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        switchSplash();
      }
    });
    // 取消按钮
    Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
    btn_cancel.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onBackPressed();
      }
    });

    // 文件扫描按钮
    findViewById(R.id.btn_scanfile).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        showFileChooser();
      }
    });
  }

  /**
   * 调用文件选择软件来选择文件
   **/
  private void showFileChooser() {
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("image/*");
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    try {
      startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), FILE_SELECT_CODE);
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * 获取文件路径,开始扫描文件
   **/
  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    // 如果返回失败并且请求码为打开文件,弹出错误提示并返回
    if (resultCode != Activity.RESULT_OK && requestCode == FILE_SELECT_CODE) {
      Toast.makeText(this, "打开文件失败", Toast.LENGTH_SHORT).show();
      return;
    }
    switch (requestCode) {
      case FILE_SELECT_CODE:
        Uri uri = data.getData();

        try {
          String[] proj = { MediaStore.Images.Media.DATA };

          Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);

          int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
          actualimagecursor.moveToFirst();

          String filename = actualimagecursor.getString(actual_image_column_index);

          doFileScan(filename);

          return;
        } catch (Exception e) {
          LogUtil.e(TAG, "", e);
        }
        Toast.makeText(this, "打开文件失败", Toast.LENGTH_SHORT).show();
        break;
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  /**
   * 根据文件路径扫描文件二维码
   **/
  protected void doFileScan(String filename) {
    try {
      QrcodeFileDecoder decoder = new QrcodeFileDecoder();
      Result r = decoder.decodeFile(this, filename);

      // 未发生错误,正常返回
      if (r != null && r.getText() != null) {
        Intent i = new Intent();
        String result = "" + r.getText();
        LogUtil.d(TAG, "scan file result: " + result);

        i.putExtra(REQUEST_SCAN_QRCODE, result);
        this.setResult(RESULT_OK, i);

        finish();

        return;
      }
    } catch (Exception e) {
      LogUtil.d(TAG, "", e);
    }

    // 解码出错,弹出提示并返回
    LogUtil.d(TAG, "scan file failed.");
    Toast.makeText(this, "文件解码失败", Toast.LENGTH_SHORT).show();

    return;
  }

  private boolean b;

  protected void switchSplash() {
    try {
      b = !b;
      if (b) {
        btn_splash.setBackgroundResource(R.drawable.ic_camera_splash_on);
      } else {
        btn_splash.setBackgroundResource(R.drawable.ic_camera_splash_off);
      }
      CameraManager.get().openFlashlight(b);
    } catch (Exception e) {
      LogUtil.e(TAG, "", e);
    }
  }

  @SuppressWarnings ("deprecation") @Override protected void onResume() {
    super.onResume();
    SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
    SurfaceHolder surfaceHolder = surfaceView.getHolder();
    if (hasSurface) {
      initCamera(surfaceHolder);
    } else {
      surfaceHolder.addCallback(this);
      surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    decodeFormats = null;
    characterSet = null;

    playBeep = true;
    AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
    if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
      playBeep = false;
    }
    initBeepSound();
    vibrate = true;
  }

  @Override protected void onPause() {
    super.onPause();
    if (handler != null) {
      handler.quitSynchronously();
      handler = null;
    }
    CameraManager.get().closeDriver();
  }

  @Override protected void onDestroy() {
    inactivityTimer.shutdown();
    super.onDestroy();
  }

  @Override public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_POWER || keyCode == KeyEvent.KEYCODE_MENU) {
      btn_splash.setBackgroundResource(R.drawable.ic_camera_splash_off);
      CameraManager.get().openFlashlight(false);
    }
    return super.onKeyDown(keyCode, event);
  }

  private void initCamera(SurfaceHolder surfaceHolder) {
    try {
      CameraManager.get().openDriver(surfaceHolder);
    } catch (Exception e) {
      Toast.makeText(this, "开启摄像头失败,请检查后重试", Toast.LENGTH_SHORT).show();

      if (handler != null) {
        handler.quitSynchronously();
        handler = null;
      }

      inactivityTimer.shutdown();

      CameraManager.get().closeDriver();

      this.finish();

      return;
    }
    if (handler == null) {
      handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
    }
  }

  @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

  }

  @Override public void surfaceCreated(SurfaceHolder holder) {
    if (!hasSurface) {
      hasSurface = true;
      initCamera(holder);
    }
  }

  @Override public void surfaceDestroyed(SurfaceHolder holder) {
    hasSurface = false;
  }

  public ViewfinderView getViewfinderView() {
    return viewfinderView;
  }

  public Handler getHandler() {
    return handler;
  }

  public void drawViewfinder() {
    viewfinderView.drawViewfinder();
  }

  public void handleDecode(Result obj, Bitmap barcode) {
    inactivityTimer.onActivity();
    // viewfinderView.drawResultBitmap(barcode);
    playBeepSoundAndVibrate();

    // txtResult.setText(obj.getBarcodeFormat().toString() + ":"
    // + obj.getText());

    Intent i = new Intent();

    i.putExtra(REQUEST_SCAN_QRCODE, obj.getText());

    this.setResult(RESULT_OK, i);

    if (null != obj.getText()) {
      String result = obj.getText();
      Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
      if (!strings.contains(result)) {
        strings.add(result);
      }
    }
    Config.setQrCodeData(strings);
    if (Config.isAutoExport()) {
      FileLogger.getInstance().saveCrashInfoFile(strings);
    }
    if (Config.isAutoScan()) {
      continuePreview();
    } else {
      this.finish();
    }
  }

  private void initBeepSound() {
    if (playBeep && mediaPlayer == null) {
      // The volume on STREAM_SYSTEM is not adjustable, and users found it
      // too loud,
      // so we now play on the music stream.
      setVolumeControlStream(AudioManager.STREAM_MUSIC);
      mediaPlayer = new MediaPlayer();
      mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      mediaPlayer.setOnCompletionListener(beepListener);

      AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
      try {
        mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
        file.close();
        mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
        mediaPlayer.prepare();
      } catch (IOException e) {
        mediaPlayer = null;
      }
    }
  }

  private static final long VIBRATE_DURATION = 200L;

  private void playBeepSoundAndVibrate() {
    if (playBeep && mediaPlayer != null) {
      mediaPlayer.start();
    }
    if (vibrate) {
      Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
      vibrator.vibrate(VIBRATE_DURATION);
    }
  }

  /**
   * When the beep has finished playing, rewind to queue up another one.
   */
  private final OnCompletionListener beepListener = new OnCompletionListener() {
    public void onCompletion(MediaPlayer mediaPlayer) {
      mediaPlayer.seekTo(0);
    }
  };

  private void continuePreview() {
    handler.postDelayed(new Runnable() {
      @Override public void run() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        initCamera(surfaceHolder);
        if (handler != null) {
          handler.restartPreviewAndDecode();
        }
      }
    }, 3 * 1000);
  }
}