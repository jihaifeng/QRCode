package cn.bingoogolapple.qrcode.zbar;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import cn.bingoogolapple.qrcode.core.QRCodeView;

public class ZBarView extends QRCodeView {

  static {
    System.loadLibrary("iconv");
  }

  private ImageScanner mScanner;

  public ZBarView(Context context, AttributeSet attributeSet) {
    this(context, attributeSet, 0);
  }

  public ZBarView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setupScanner();
  }

  public void setupScanner() {
    mScanner = new ImageScanner();
    mScanner.setConfig(0, Config.X_DENSITY, 3);
    mScanner.setConfig(0, Config.Y_DENSITY, 3);

    mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
    for (BarcodeFormat format : BarcodeFormat.ALL_FORMATS) {
      mScanner.setConfig(format.getId(), Config.ENABLE, 1);
    }
  }

  @Override public String processData(byte[] data, int width, int height, boolean isRetry) {
    String result = null;
    Image barcode = new Image(width, height, "Y800");

    Rect rect = mScanBoxView.getScanBoxAreaRect(height);
    if (rect != null && !isRetry && rect.left + rect.width() <= width && rect.top + rect.height() <= height) {
      barcode.setCrop(rect.left, rect.top, rect.width(), rect.height());
    }

    //barcode.setData(data);
    //result = processData(barcode);
    result = new DecodeUtils(DecodeUtils.DECODE_MODE_ZBAR).decodeWithZbar(data, width, height, rect);

    return result;
  }

  private String processData(Image barcode) {
    String result = null;
    if (mScanner.scanImage(barcode) != 0) {
      SymbolSet syms = mScanner.getResults();
      for (Symbol sym : syms) {
        String symData = sym.getData();
        if (!TextUtils.isEmpty(symData)) {
          result = symData;
          break;
        }
      }
    }
    return result;
  }

  private boolean isRightScore(int score) {
    boolean flag = true;
    int num1 = 3 * 86;
    int num2 = 4 * 63;
    if (score < num1) {
      if (score % 3 == 2) {
        flag = false;
      }
    } else if (score > num1 && score < num1 + num2) {
      int num3 = score - num1;
      if (num3 % 4 == 3) {
        flag = false;
      }
    } else {
      flag = false;
    }
    return flag;
  }
}