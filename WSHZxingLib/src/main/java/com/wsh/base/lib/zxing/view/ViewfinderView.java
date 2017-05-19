/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wsh.base.lib.zxing.view;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.wsh.base.lib.zxing.R;
import com.wsh.base.lib.zxing.camera.CameraManager;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 */
public final class ViewfinderView extends View {
	private static final String TAG = "log";
	/**
	 * 刷新界面的时间
	 */
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;

	/**
	 * 四个绿色边角对应的长度
	 */
	private int ScreenRate;

	/**
	 * 四个绿色边角对应的宽度
	 */
	private static final int CORNER_WIDTH = 10;
	/**
	 * 扫描框中的中间线的宽度
	 */
	private static final int MIDDLE_LINE_WIDTH = 6;

	/**
	 * 扫描框中的中间线的与扫描框左右的间隙
	 */
	private static final int MIDDLE_LINE_PADDING = 5;

	/**
	 * 中间那条线每次刷新移动的距离
	 */
	private static final int SPEEN_DISTANCE = 5;

	/**
	 * 手机的屏幕密度
	 */
	private static float density;
	/**
	 * 字体大小
	 */
	private static final int TEXT_SIZE = 16;
	/**
	 * 字体距离扫描框下面的距离
	 */
	private static final int TEXT_PADDING_TOP = 30;

	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	/**
	 * 中间滑动线的最顶端位置
	 */
	private int slideTop;

	/**
	 * 中间滑动线的最底端位置
	 */
	private int slideBottom;

	/**
	 * 将扫描的二维码拍下来，这里没有这个功能，暂时不考虑
	 */
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;

	private final int resultPointColor;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;

	boolean isFirst;

	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		density = context.getResources().getDisplayMetrics().density;
		// 将像素转换成dp
		ScreenRate = (int) (20 * density);

		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);

		resultPointColor = resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}

	@Override
	public void onDraw(Canvas canvas) {
		// 中间的扫描框，你要修改扫描框的大小，去CameraManager里面修改
		Rect frame = CameraManager.get().getFramingRect();
		if (frame == null) {
			return;
		}

		// 获取屏幕的宽和高
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// Draw the exterior (i.e. outside the framing rect) darkened
		int middle = frame.height() / 2 + frame.top;
		int middle_width = frame.width() / 2 + frame.left;

		// int w1 = frame.left;
		// int w2 = frame.right;
		// int h1 = middle - frame.width() / 2;
		// int h2 = middle + frame.width() / 2;

		int bleft = frame.left;
		int bright = frame.right;
		int btop = middle - frame.width() / 2;
		int bbottom = middle + frame.width() / 2;

		// 初始化中间线滑动的最上边和最下边
		if (!isFirst) {
			isFirst = true;
			slideTop = btop;
			slideBottom = bbottom;
		}

		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		canvas.drawRect(0, 0, width, btop, paint);
		canvas.drawRect(0, btop, bleft, bbottom + 1, paint);
		canvas.drawRect(bright + 1, btop, width, bbottom + 1, paint);
		canvas.drawRect(0, bbottom + 1, width, height, paint);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {

			// 画扫描框边上的角，总共8个部分

			paint.setColor(Color.GREEN);
			canvas.drawRect(bleft, btop, bleft + ScreenRate, btop
					+ CORNER_WIDTH, paint);
			canvas.drawRect(bleft, btop, bleft + CORNER_WIDTH, btop
					+ ScreenRate, paint);
			canvas.drawRect(bright - ScreenRate, btop, bright, btop
					+ CORNER_WIDTH, paint);
			canvas.drawRect(bright - CORNER_WIDTH, btop, bright, btop
					+ ScreenRate, paint);
			canvas.drawRect(bleft, bbottom - CORNER_WIDTH, bleft + ScreenRate,
					bbottom, paint);
			canvas.drawRect(bleft, bbottom - ScreenRate, bleft + CORNER_WIDTH,
					bbottom, paint);
			canvas.drawRect(bright - ScreenRate, bbottom - CORNER_WIDTH,
					bright, bbottom, paint);
			canvas.drawRect(bright - CORNER_WIDTH, bbottom - ScreenRate,
					bright, bbottom, paint);

			// 绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
			slideTop += SPEEN_DISTANCE;
			if (slideTop >= bbottom) {
				slideTop = btop;
			}
			canvas.drawRect(bleft + MIDDLE_LINE_PADDING, slideTop
					- MIDDLE_LINE_WIDTH / 2, bright - MIDDLE_LINE_PADDING,
					slideTop + MIDDLE_LINE_WIDTH / 2, paint);

			// // 画扫描框下面的字
			// paint.setColor(Color.WHITE);
			// paint.setTextSize(TEXT_SIZE * density);
			// paint.setAlpha(0x40);
			// paint.setTypeface(Typeface.create("System", Typeface.BOLD));
			// canvas.drawText(
			// getResources().getString(R.string.qrcode_tx_info),
			// frame.left,
			// (float) (bbottom + (float) TEXT_PADDING_TOP * density),
			// paint);

			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible) {
					canvas.drawCircle(bleft + point.getX(),
							btop + point.getY(), 6.0f, paint);
				}
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(bleft + point.getX(),
							btop + point.getY(), 3.0f, paint);
				}
			}

			// 只刷新扫描框的内容，其他地方不刷新
			postInvalidateDelayed(ANIMATION_DELAY, bleft, btop, bright, bbottom);

		}
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
