package com.wsh.base.lib.zxing.decoding;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.wsh.base.lib.zxing.LogUtil;

/**
 * 
 * ClassName: QrcodeFileDecoder<br>
 * Description: 解码二维码文件.输入文件,输出二维码
 * 
 * @author gaojian<br>
 *         email:gaojian@raiyi.com
 * @version
 * @since Ver 1.1
 * @Date 2014 2014-3-20 下午2:53:29
 * @see
 */
public class QrcodeFileDecoder {

	private static final String TAG = "QrcodeFileDecoder";

	// 从文件解码图片
	public Result decodeFile(Context context, String filename) throws Exception {
		// 文件名为空,或者文件不存在,返回null
		if (TextUtils.isEmpty(filename) || !new File(filename).exists()) {
			return null;
		}

		// 从文件读取Bitmap,解码并返回
		Bitmap bmp = BitmapFactory.decodeFile(filename);
		Result r = decodeBitmap(bmp);
		bmp.recycle();

		return r;
	}

	/**
	 * 1.解码Bitmap图片. <br>
	 * 2.如果直接解码失败,则尝试将Bitmap图片放大后,再解码.为解决扫描无白边的二维码失败问题.
	 */
	public Result decodeBitmap(final Bitmap bmp) throws Exception {
		Result r = null;
		try {
			// 直接解码
			r = scanning(bmp);
		} catch (Exception e) {
			LogUtil.e(TAG, "", e);
		}

		if (r == null) {
			// 放大后解码
			Bitmap bmp2 = convert2BiggerBitmap(bmp, 0);
			r = scanning(bmp2);
			bmp2.recycle();
		}

		if (r != null) {
			LogUtil.d(TAG, "result: " + r.getText());
		}

		return r;
	}

	/**
	 * 解码Bitmap图片
	 */
	private Result scanning(Bitmap bmp) throws Exception {
		// 开始对图像资源解码

		try {
			MultiFormatReader multiFormatReader = new MultiFormatReader();

			// 解码的参数
			Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(
					2);
			// 可以解析的编码类型
			Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
			if (decodeFormats == null || decodeFormats.isEmpty()) {
				decodeFormats = new Vector<BarcodeFormat>();

				// 这里设置可扫描的类型，我这里选择了都支持
				decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
				decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
				decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
			}
			hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

			// 设置继续的字符编码格式为UTF8
			hints.put(DecodeHintType.CHARACTER_SET, "UTF8");

			// 设置解析配置参数
			multiFormatReader.setHints(hints);

			BinaryBitmap bbmp = new BinaryBitmap(new HybridBinarizer(
					new BitmapLuminanceSource(bmp)));
			Result rawResult = multiFormatReader.decodeWithState(bbmp);

			return rawResult;

		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 放大图片至原始尺寸2倍
	 */
	private Bitmap convert2BiggerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth() * 2,
				bitmap.getHeight() * 2, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();

		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();

		final Rect rect = new Rect(0, 0, width * 2, height * 2);
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		final int color = 0xffffffff;
		paint.setColor(color);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		canvas.drawBitmap(bitmap, width / 2, height / 2, paint);
		bitmap.recycle();

		return output;
	}
}
