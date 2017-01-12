package com.sagetablet;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import com.sagetablet.android.XMLRPCClient;
import com.sagetablet.android.XMLRPCException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DrawView extends View {
	private static double SCALED_SAGE_Y = SAGE.Y / Application.DRAW_SCALE;

	private static double SCALED_SAGE_X = SAGE.X / Application.DRAW_SCALE;

	private Paint paint = new Paint();

	private SAGE communicator;

	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private static final int KILL = 3;
	private int mode = 0;

	// private int current = -1;
	private int currentID = -1;
	private int lastID = -1;
	private double startX = 0;
	private double startY = 0;

	private double startX1, startY1, startX2, startY2;

	private double offsetX = 0;
	private double offsetY = 0;
	private static final double OFFSET_X_MIN = -300;
	private static final double OFFSET_X_MAX = 1000;
	private static final double OFFSET_Y_MIN = -300;
	private static final double OFFSET_Y_MAX = 500;

	private double changeX = 0;
	private double changeY = 0;

	private double scale = 1;
	private static final double SCALE_MIN = 1;
	private static final double SCALE_MAX = 6;

	private static final int LEFT_TOP = 1;
	private static final int RIGHT_TOP = 2;
	private static final int RIGHT_BOTTOM = 3;
	private static final int LEFT_BOTTOM = 4;

	private int pointerCorner1 = 0;
	private int pointerCorner2 = 0;

	private Bitmap images[];

	private List<Application> appList;
	private List<Application> appListToDraw;

	private ApplicationComparatorByZ appCompByZ = new ApplicationComparatorByZ();

	private DecimalFormat twoDForm = new DecimalFormat("#.##");

	public DrawView(Context context, SAGE communicator) {
		super(context);
		System.out.println("Jsem v konstruktoru DrawView");

		this.communicator = communicator;

		images = new Bitmap[4];
		images[0] = BitmapFactory.decodeResource(getResources(), R.drawable.close);
		images[1] = BitmapFactory.decodeResource(getResources(), R.drawable.atlantis);
		images[2] = BitmapFactory.decodeResource(getResources(), R.drawable.refresh);
		images[3] = BitmapFactory.decodeResource(getResources(), R.drawable.archive);

		appList = communicator.getAppList();
	}

	// !!!
	// Uplne zrusit DRAW_SCALE (pro zacatek nastavit na 1) a nahradit jej scale, mozna

	@Override
	public void onDraw(Canvas canvas) {
		SCALED_SAGE_Y = SAGE.Y / Application.DRAW_SCALE;
		SCALED_SAGE_X = SAGE.X / Application.DRAW_SCALE;
		canvas.scale(1, 1);
		paint.setStyle(Style.STROKE);
		drawIcons(canvas);
		canvas.scale((float) scale, (float) scale);
		paint.setStyle(Style.STROKE);

		appList = communicator.getAppList();
		if (appList == null) {
			System.out.println("Je to null !");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		drawGrid(canvas);
		Collections.sort(appList, appCompByZ);

		for (int i = 0; i < appList.size(); i++) {
			// for (int i = appList.size() - 1; i >= 0; i--) {
			// System.out.println(appList.get(i));

			float left = (float) appList.get(i).getLeft() + (float) offsetX;
			float right = (float) appList.get(i).getRight() + (float) offsetX;
			float top = (float) appList.get(i).getTop() + (float) offsetY;
			float bottom = (float) appList.get(i).getBottom() + (float) offsetY;
			paint.setColor(Color.BLACK);
			if (currentID == appList.get(i).getID()) {
				paint.setStrokeWidth(4);
			} else {
				paint.setStrokeWidth(2);
			}

			// vypln
			paint.setColor(Color.LTGRAY);
			paint.setStyle(Style.FILL);
			canvas.drawRect(left, top, right, bottom, paint);
			// obrys
			paint.setColor(Color.BLACK);
			paint.setStyle(Style.STROKE);
			canvas.drawRect(left, top, right, bottom, paint);

			paint.setStrokeWidth(1);
			paint.setStyle(Style.FILL);
			paint.setTextSize((int) (20));
			canvas.drawText(appList.get(i).getID() + " - " + appList.get(i).getAppName(), left + 5, top + 20, paint);
		}

		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		canvas.drawRect((float) offsetX, (float) offsetY, (float) ((SCALED_SAGE_X + offsetX)), (float) ((SCALED_SAGE_Y + offsetY)), paint);

		drawPerformanceInfo(canvas);
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();
		double x, y;

		double x1, y1, x2, y2;

		appList = communicator.getAppList();

		switch (eventaction & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			startX = event.getX();
			startY = event.getY();
			x = event.getX();
			y = event.getY();

			float sageRight = (float) ((SCALED_SAGE_X + offsetX) * scale);
			float sageTop = (float) (offsetY * scale);
			if ((x > (sageRight + 15)) && (x < (sageRight + 15 + images[0].getWidth()))) {
				if ((y > sageTop) && (y < (sageTop + images[0].getHeight()))) {
					if (mode == KILL) {
						mode = DRAG;
					} else {
						mode = KILL;
					}
				} else if (y > (sageTop + images[0].getHeight()) && (y < (sageTop + 2 * images[0].getHeight()))) {
					communicator.startAtlantis();
					//
				} else if (y > (sageTop + 2 * images[0].getHeight()) && (y < (sageTop + 3 * images[0].getHeight()))) {
					communicator.synchronizeApps();

					appList = communicator.getAppList();
					for (int i = 0; i < appList.size(); i++) {
						System.out.println(appList.get(i));
					}
				} else if (y > (sageTop + 3 * images[0].getHeight()) && (y < (sageTop + 4 * images[0].getHeight()))) {
					Context context = getContext();
					Intent FileChooser = new Intent(context, FileChooserActivity.class);
					context.startActivity(FileChooser);
					// return true;
				}
			} else {
				if (mode != KILL) {
					mode = DRAG;
				}
			}

			double left,
			right,
			top,
			bottom;
			for (int i = (appList.size() - 1); i >= 0; i--) {
				// for (int i = 0; i < appList.size(); i++) {
				left = (appList.get(i).getLeft() + offsetX) * scale;
				right = (appList.get(i).getRight() + offsetX) * scale;
				top = (appList.get(i).getTop() + offsetY) * scale;
				bottom = (appList.get(i).getBottom() + offsetY) * scale;
				if (left <= x && right >= x && top <= y && bottom >= y) {
					System.out.println("App: " + left + " " + right + " " + top + " " + bottom + " Point: " + x + " " + y + " Scale: " + scale);
					appList.get(i).setZ(MainActivity.communicator.maxZ + 1);
					// Collections.sort(appList, new ApplicationComparatorByZ());

					MainActivity.communicator.maxZ++;
					communicator.bringToFront(appList.get(i).getID());
					// current = i;
					currentID = appList.get(i).getID();
					lastID = appList.get(i).getID();
					break;
				}
			}

			if (mode == KILL && currentID != -1) {
				communicator.shutdownAPP(currentID);
				appList.remove(findByID(currentID));
				currentID = -1;
				mode = 0;
			}

			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;

			System.out.println("Zoom mode, current: " + currentID);

			startX1 = event.getX(0);
			startY1 = event.getY(0);
			startX2 = event.getX(1);
			startY2 = event.getY(1);

			if (currentID != -1) {
				setPointerCorners(event);
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {

				x = event.getX();
				y = event.getY();

				if (currentID == -1) {
					offsetX += (x - startX) / scale;
					offsetY += (y - startY) / scale;

					if (offsetX < OFFSET_X_MIN)
						offsetX = OFFSET_X_MIN;
					if (offsetX > OFFSET_X_MAX)
						offsetX = OFFSET_X_MAX;
					if (offsetY < OFFSET_Y_MIN)
						offsetY = OFFSET_Y_MIN;
					if (offsetY > OFFSET_Y_MAX)
						offsetY = OFFSET_Y_MAX;
				} else {
					findByID(currentID).changeLeft((x - startX) / scale);
					findByID(currentID).changeTop((y - startY) / scale);
					findByID(currentID).changeRight((x - startX) / scale);
					findByID(currentID).changeBottom((y - startY) / scale);

					changeX += (x - startX) / scale;
					changeY += (y - startY) / scale;
				}

				startX = event.getX();
				startY = event.getY();

			} else if (mode == ZOOM) {
				// polofunkcni zachovavani aspect ratio mam v backup/DrawView... (problem s tim, ze se zvetsuje i pri zmensovani)

				x1 = event.getX(0);
				y1 = event.getY(0);
				x2 = event.getX(1);
				y2 = event.getY(1);

				if (currentID == -1) {
					double sq1 = Helper.distanceBetweenPoints(x1, y1, x2, y2);
					double sq2 = Helper.distanceBetweenPoints(startX1, startY1, startX2, startY2);
					double distance = sq1 - sq2;
					if (sq1 != 0 && sq2 != 0) {
						scale += ((distance) / (200));
						if (scale < SCALE_MIN) {
							scale = SCALE_MIN;
						} else if (scale > SCALE_MAX) {
							scale = SCALE_MAX;
						}
					}
				} else {
					boolean keepAspectRatio = MainActivity.sharedPref.getBoolean("KeepAspectRatio", true);
					Application app = findByID(currentID);

					if (keepAspectRatio) {
						double dist1 = Helper.distanceBetweenPoints(x1, y1, startX1, startY1);
						double dist2 = Helper.distanceBetweenPoints(x2, y2, startX2, startY2);

						double appScaleHorizontal = (app.getRight() - app.getLeft()) / ((app.getBottom() - app.getTop()) + (app.getRight() - app.getLeft()));
						double appScaleVertical = (app.getBottom() - app.getTop()) / ((app.getBottom() - app.getTop()) + (app.getRight() - app.getLeft()));

						appScaleHorizontal *= 1.5; // just for bigger zoom
						appScaleVertical *= 1.5;

						int sign1 = Helper.getSign(pointerCorner1, x1, y1, startX1, startY1);
						int sign2 = Helper.getSign(pointerCorner2, x2, y2, startX2, startY2);

						// System.out.println("Distances:" + dist1 + " " + dist2); //System.out.println("Scales:" + appScaleHorizontal + " " + appScaleVertical);

						if (pointerCorner1 == LEFT_TOP || pointerCorner1 == LEFT_BOTTOM) {
							app.changeLeft((dist1 * appScaleHorizontal * sign1) / scale * -1);
						} else if (pointerCorner1 == RIGHT_TOP || pointerCorner1 == RIGHT_BOTTOM) {
							app.changeRight((dist1 * appScaleHorizontal * sign1) / scale);
						}

						if (pointerCorner1 == LEFT_TOP || pointerCorner1 == RIGHT_TOP) {
							app.changeTop((dist1 * appScaleVertical * sign1) / scale * -1);
						} else if (pointerCorner1 == LEFT_BOTTOM || pointerCorner1 == RIGHT_BOTTOM) {
							app.changeBottom((dist1 * appScaleVertical * sign1) / scale);
						}

						if (pointerCorner2 == LEFT_TOP || pointerCorner2 == LEFT_BOTTOM) {
							app.changeLeft((dist2 * appScaleHorizontal * sign2) / scale * -1);
						} else if (pointerCorner2 == RIGHT_TOP || pointerCorner2 == RIGHT_BOTTOM) {
							app.changeRight((dist2 * appScaleHorizontal * sign2) / scale);
						}

						if (pointerCorner2 == LEFT_TOP || pointerCorner2 == RIGHT_TOP) {
							app.changeTop((dist2 * appScaleVertical * sign2) / scale * -1);
						} else if (pointerCorner2 == LEFT_BOTTOM || pointerCorner2 == RIGHT_BOTTOM) {
							app.changeBottom((dist2 * appScaleVertical * sign2) / scale);
						}
					} else {
						if (pointerCorner1 == LEFT_TOP || pointerCorner1 == LEFT_BOTTOM) {
							app.changeLeft((x1 - startX1) / scale);
						} else if (pointerCorner1 == RIGHT_TOP || pointerCorner1 == RIGHT_BOTTOM) {
							app.changeRight((x1 - startX1) / scale);
						}

						if (pointerCorner1 == LEFT_TOP || pointerCorner1 == RIGHT_TOP) {
							app.changeTop((y1 - startY1) / scale);
						} else if (pointerCorner1 == LEFT_BOTTOM || pointerCorner1 == RIGHT_BOTTOM) {
							app.changeBottom((y1 - startY1) / scale);
						}

						if (pointerCorner2 == LEFT_TOP || pointerCorner2 == LEFT_BOTTOM) {
							app.changeLeft((x2 - startX2) / scale);
						} else if (pointerCorner2 == RIGHT_TOP || pointerCorner2 == RIGHT_BOTTOM) {
							app.changeRight((x2 - startX2) / scale);
						}

						if (pointerCorner2 == LEFT_TOP || pointerCorner2 == RIGHT_TOP) {
							app.changeTop((y2 - startY2) / scale);
						} else if (pointerCorner2 == LEFT_BOTTOM || pointerCorner2 == RIGHT_BOTTOM) {
							app.changeBottom((y2 - startY2) / scale);
						}
					}

				}

				startX1 = event.getX(0);
				startY1 = event.getY(0);
				startX2 = event.getX(1);
				startY2 = event.getY(1);
			}

			break;

		case MotionEvent.ACTION_POINTER_UP:
			if (mode == ZOOM) {
				System.out.println("Jsem v Mode == ZOOM");
				mode = 0;
				boolean debug = false;
				if (currentID != -1 && !debug) {

					System.out.println("Jdu poslat resize");

					Application app = findByID(currentID);

					int l = (int) (app.getLeft() * Application.DRAW_SCALE);
					int r = (int) (app.getRight() * Application.DRAW_SCALE);
					int b = (int) (SAGE.Y - (app.getBottom() * Application.DRAW_SCALE));
					int t = (int) (SAGE.Y - (app.getTop() * Application.DRAW_SCALE));
					// lrbt

					communicator.resizeWindow(app.getID(), l, r, b, t);
				}
			}

			mode = 0;

			break;

		case MotionEvent.ACTION_UP:
			System.out.println("Mode: " + mode);
			if (mode == DRAG) {
				mode = 0;
				if (currentID != -1) {

					communicator.moveWindow(currentID, (int) (changeX * Application.DRAW_SCALE), (int) (changeY * Application.DRAW_SCALE * -1));

					changeX = 0;
					changeY = 0;
				}
			} else if (mode == ZOOM) {
				mode = 0;
				if (currentID != -1) {

				}
			}

			currentID = -1;
			break;
		}

		invalidate();
		return true;
	}

	public boolean onKeyDown(int i, KeyEvent ke) {
		return true;
	}

	public void drawGrid(Canvas canvas) {
		paint.setColor(Color.LTGRAY);
		paint.setStrokeWidth(1);
		float displayX = (float) ((SAGE.X / SAGE.DISPLAYS_X) / Application.DRAW_SCALE);
		float displayY = (float) ((SAGE.Y / SAGE.DISPLAYS_Y) / Application.DRAW_SCALE);
		for (int i = 0; i < SAGE.DISPLAYS_Y; i++) {
			for (int j = 0; j < SAGE.DISPLAYS_X; j++) {
				canvas.drawRect(j * displayX + (float) offsetX, i * displayY + (float) offsetY, (j + 1) * displayX + (float) offsetX, (i + 1) * displayY
						+ (float) offsetY, paint);
			}
		}
	}

	public void drawIcons(Canvas canvas) {
		float sageRight = (float) ((SCALED_SAGE_X + offsetX) * scale);
		float sageTop = (float) (offsetY * scale);
		for (int i = 0; i < images.length; i++) {
			canvas.drawBitmap(images[i], sageRight + 15, sageTop + (i * images[i].getHeight()), paint);
		}

		if (mode == KILL) {
			paint.setColor(Color.RED);
			paint.setStrokeWidth(4);
			canvas.drawRect(sageRight + 15, sageTop, sageRight + 15 + images[0].getWidth(), sageTop + images[0].getHeight(), paint);
		}
	}

	private void drawPerformanceInfo(Canvas canvas) {
		if (MainActivity.sharedPref.getBoolean("ViewPerformanceInfo", true) && lastID != -1) {
			if (lastID == -1) {
				return;
			}
			
			double[][] perfInfo = new double[4][4];
			String[] performanceHeader = { "Cur", "Avg", "Min", "Max" };
			String[] performanceHeader2 = { "Perf id - " + lastID, "Rendering BW", "Rendering FPS", "Display BW", "Display FPS" };

			float sageBottom = (float) ((SCALED_SAGE_Y + offsetY));
			float sageLeft = (float) (offsetX);

			paint.setStrokeWidth(1);
			paint.setStyle(Style.FILL);
			paint.setTextSize((int) (20));

			Application app = findByID(lastID);
			if (app != null) {
				if (!app.isPerformanceStarted()) {
					communicator.startPerformance(lastID);
					app.setPerformanceStarted(true);
				}
				PerformanceInfo pi = app.getPerfInfo();
				perfInfo[0] = pi.getRenderingBW();
				perfInfo[1] = pi.getRenderingFPS();
				perfInfo[2] = pi.getDisplayBW();
				perfInfo[3] = pi.getDisplayFPS();

				paint.setFakeBoldText(true);
				for (int i = 0; i < performanceHeader2.length; i++) {
					canvas.drawText(performanceHeader2[i], sageLeft + 2, sageBottom + 28 + i * 30, paint);
				}

				for (int i = 0; i < performanceHeader.length; i++) {
					canvas.drawText(performanceHeader[i], sageLeft + i * 80 + 152, sageBottom + 28, paint);
				}

				paint.setFakeBoldText(false);
				for (int i = 0; i < perfInfo.length; i++) {
					for (int j = 0; j < perfInfo[0].length; j++) {
						canvas.drawText(twoDForm.format(perfInfo[j][i]), sageLeft + i * 80 + 152, sageBottom + 58 + j * 30, paint);
					}
				}

				for (int i = 0; i < 6; i++) {
					canvas.drawLine(sageLeft, sageBottom + 5 + i * 30, sageLeft + 470, sageBottom + 5 + i * 30, paint);
				}

				for (int i = 0; i < 5; i++) {
					canvas.drawLine(sageLeft + i * 80 + 150, sageBottom + 5, sageLeft + i * 80 + 150, sageBottom + 155, paint);
				}

				canvas.drawLine(sageLeft, sageBottom + 5, sageLeft, sageBottom + 155, paint);

			}
		}
	}

	public void setPointerCorners(MotionEvent event) {
		double x1 = event.getX(0);
		double y1 = event.getY(0);
		double x2 = event.getX(1);
		double y2 = event.getY(1);

		Application app = findByID(currentID);
		double left = (app.getLeft() + offsetX) * scale;
		double right = (app.getRight() + offsetX) * scale;
		double top = (app.getTop() + offsetY) * scale;
		double bottom = (app.getBottom() + offsetY) * scale;

		double distance1 = Helper.distanceBetweenPoints(x1, y1, left, top);
		double distance2 = Helper.distanceBetweenPoints(x1, y1, right, top);
		double distance3 = Helper.distanceBetweenPoints(x1, y1, right, bottom);
		double distance4 = Helper.distanceBetweenPoints(x1, y1, left, bottom);

		System.out.println("Distances 1: " + distance1 + " " + distance2 + " " + distance3 + "" + distance4);

		if (Helper.isMinimal(distance1, distance2, distance3, distance4)) {
			pointerCorner1 = LEFT_TOP;
		} else if (Helper.isMinimal(distance2, distance1, distance3, distance4)) {
			pointerCorner1 = RIGHT_TOP;
		} else if (Helper.isMinimal(distance3, distance1, distance2, distance4)) {
			pointerCorner1 = RIGHT_BOTTOM;
		} else {
			pointerCorner1 = LEFT_BOTTOM;
		}

		distance1 = Helper.distanceBetweenPoints(x2, y2, left, top);
		distance2 = Helper.distanceBetweenPoints(x2, y2, right, top);
		distance3 = Helper.distanceBetweenPoints(x2, y2, right, bottom);
		distance4 = Helper.distanceBetweenPoints(x2, y2, left, bottom);

		System.out.println("Distances 2: " + distance1 + " " + distance2 + " " + distance3 + "" + distance4);
		if (Helper.isMinimal(distance1, distance2, distance3, distance4)) {
			pointerCorner2 = LEFT_TOP;
		} else if (Helper.isMinimal(distance2, distance1, distance3, distance4)) {
			pointerCorner2 = RIGHT_TOP;
		} else if (Helper.isMinimal(distance3, distance1, distance2, distance4)) {
			pointerCorner2 = RIGHT_BOTTOM;
		} else {
			pointerCorner2 = LEFT_BOTTOM;
		}

		System.out.println("Pointer cornercs: " + pointerCorner1 + " " + pointerCorner2);
	}

	public Application findByID(int id) {
		for (int i = 0; i < appList.size(); i++) {
			if (id == appList.get(i).getID()) {
				return appList.get(i);
			}
		}
		return null;
	}
}