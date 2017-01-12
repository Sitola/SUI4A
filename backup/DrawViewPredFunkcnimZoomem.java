package com.sagetablet;

import java.util.List;

import com.sagetablet.android.XMLRPCClient;
import com.sagetablet.android.XMLRPCException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DrawView extends View {
	private static final double SCALED_SAGE_Y = Communicator.Y / Application.DRAW_SCALE;

	private static final double SCALED_SAGE_X = Communicator.X / Application.DRAW_SCALE;

	private Paint paint = new Paint();

	private Communicator communicator;

	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private static final int KILL = 3;
	private int mode = 0;

	private int current = -1;
	private double startX = 0;
	private double startY = 0;

	private double startX1, startY1, startX2, startY2;

	private double offsetX = 0;
	private double offsetY = 0;

	private double changeX = 0;
	private double changeY = 0;

	private double changeX1 = 0;
	private double changeY1 = 0;
	private double changeX2 = 0;
	private double changeY2 = 0;

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

	public DrawView(Context context, Communicator communicator) {
		super(context);
		this.communicator = communicator;

		images = new Bitmap[3];
		images[0] = BitmapFactory.decodeResource(getResources(), R.drawable.close);
		images[1] = BitmapFactory.decodeResource(getResources(), R.drawable.atlantis);
		images[2] = BitmapFactory.decodeResource(getResources(), R.drawable.refresh);

		appList = communicator.getAppList();
	}

	// !!!
	// Uplne zrusit DRAW_SCALE (pro zacatek nastavit na 1) a nahradit jej scale, mozna

	@Override
	public void onDraw(Canvas canvas) {
		canvas.scale(1, 1);
		drawIcons(canvas);
		canvas.scale((float) scale, (float) scale);
		paint.setStyle(Style.STROKE);

		appList = communicator.getAppList();

		drawGrid(canvas);
		for (int i = 0; i < appList.size(); i++) {
			float left = (float) appList.get(i).getLeft() + (float) offsetX;
			float right = (float) appList.get(i).getRight() + (float) offsetX;
			float top = (float) appList.get(i).getTop() + (float) offsetY;
			float bottom = (float) appList.get(i).getBottom() + (float) offsetY;
			paint.setColor(Color.BLACK);
			if (current == i) {
				paint.setStrokeWidth(4);
			} else {
				paint.setStrokeWidth(2);
			}
			canvas.drawRect(left, top, right, bottom, paint);
			paint.setStrokeWidth(1);
			canvas.drawText(appList.get(i).getID() + " - " + appList.get(i).getAppName(), left, top + 10, paint);
		}

		canvas.drawRect((float) offsetX, (float) offsetY, (float) ((SCALED_SAGE_X + offsetX)), (float) ((SCALED_SAGE_Y + offsetY)), paint);
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
				} else if (y > (sageTop + 2 * images[0].getHeight()) && (y < (sageTop + 3 * images[0].getHeight()))) {
					// communicator.synchronizeApps();

					appList = communicator.getAppList();
					for (int i = 0; i < appList.size(); i++) {
						System.out.println(appList.get(i));
					}

					communicator.resizeWindow(42, 100, 2000, 200, 3600);

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					appList = communicator.getAppList();
					for (int i = 0; i < appList.size(); i++) {
						System.out.println(appList.get(i));
					}
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
			for (int i = 0; i < appList.size(); i++) {
				left = (appList.get(i).getLeft() + offsetX) * scale;
				right = (appList.get(i).getRight() + offsetX) * scale;
				top = (appList.get(i).getTop() + offsetY) * scale;
				bottom = (appList.get(i).getBottom() + offsetY) * scale;
				if (left <= x && right >= x && top <= y && bottom >= y) {
					System.out.println("App: " + left + " " + right + " " + top + " " + bottom + " Point: " + x + " " + y + " Scale: " + scale);
					current = i;
					break;
				}
			}

			if (mode == KILL && current != -1) {
				communicator.shutdownAPP(appList.get(current).getID());
				appList.remove(current);
				current = -1;
				mode = 0;
			}

			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;

			System.out.println("Zoom mode, current: " + current);

			startX1 = event.getX(0);
			startY1 = event.getY(0);
			startX2 = event.getX(1);
			startY2 = event.getY(1);

			if (current != -1) {
				setPointerCorners(event);
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {

				x = event.getX();
				y = event.getY();

				if (current == -1) {
					offsetX += (x - startX) / scale;
					offsetY += (y - startY) / scale;
				} else {
					appList.get(current).changeLeft((x - startX) / scale);
					appList.get(current).changeTop((y - startY) / scale);
					appList.get(current).changeRight((x - startX) / scale);
					appList.get(current).changeBottom((y - startY) / scale);

					changeX += (x - startX) / scale;
					changeY += (y - startY) / scale;
				}

				startX = event.getX();
				startY = event.getY();

			} else if (mode == ZOOM) {
				x1 = event.getX(0);
				y1 = event.getY(0);
				x2 = event.getX(1);
				y2 = event.getY(1);

				if (current == -1) {
					double sq1 = Helper.distanceBetweenPoints(x1, y1, x2, y2);
					double sq2 = Helper.distanceBetweenPoints(startX1, startY1, startX2, startY2);
					double distance = sq1 - sq2;
					if (sq1 != 0 && sq2 != 0) {
						scale += ((distance) / (100));
						if (scale < SCALE_MIN) {
							scale = SCALE_MIN;
						} else if (scale > SCALE_MAX) {
							scale = SCALE_MAX;
						}
					}
				} else {

					if (pointerCorner1 == LEFT_TOP || pointerCorner1 == LEFT_BOTTOM) {
						appList.get(current).changeLeft((x1 - startX1) / scale);
					} else if (pointerCorner1 == RIGHT_TOP || pointerCorner1 == RIGHT_BOTTOM) {
						appList.get(current).changeRight((x1 - startX1) / scale);
					}

					if (pointerCorner1 == LEFT_TOP || pointerCorner1 == RIGHT_TOP) {
						appList.get(current).changeTop((y1 - startY1) / scale);
					} else if (pointerCorner1 == LEFT_BOTTOM || pointerCorner1 == RIGHT_BOTTOM) {
						appList.get(current).changeBottom((y1 - startY1) / scale);
					}

					if (pointerCorner2 == LEFT_TOP || pointerCorner2 == LEFT_BOTTOM) {
						appList.get(current).changeLeft((x2 - startX2) / scale);
					} else if (pointerCorner2 == RIGHT_TOP || pointerCorner2 == RIGHT_BOTTOM) {
						appList.get(current).changeRight((x2 - startX2) / scale);
					}

					if (pointerCorner2 == LEFT_TOP || pointerCorner2 == RIGHT_TOP) {
						appList.get(current).changeTop((y2 - startY2) / scale);
					} else if (pointerCorner2 == LEFT_BOTTOM || pointerCorner2 == RIGHT_BOTTOM) {
						appList.get(current).changeBottom((y2 - startY2) / scale);
					}

					/*
					 * Application app = appList.get(current); double dist1 = Helper.distanceBetweenPoints(x1, y1, startX1, startY1); double dist2 = Helper.distanceBetweenPoints(x2, y2, startX2, startY2); double appScaleHorizontal =
					 * (app.getRight() - app.getLeft()) / ((app.getBottom() - app.getTop()) + (app.getRight() - app.getLeft())); double appScaleVertical = (app.getBottom() - app.getTop()) / ((app.getBottom() - app.getTop()) +
					 * (app.getRight() - app.getLeft()));
					 * 
					 * //System.out.println("Distances:" + dist1 + " " + dist2); //System.out.println("Scales:" + appScaleHorizontal + " " + appScaleVertical);
					 * 
					 * if (pointerCorner1 == LEFT_TOP || pointerCorner1 == LEFT_BOTTOM) { appList.get(current).changeLeft((dist1 * appScaleHorizontal * -1) / scale); } else if (pointerCorner1 == RIGHT_TOP || pointerCorner1 == RIGHT_BOTTOM)
					 * { appList.get(current).changeRight((dist1 * appScaleHorizontal) / scale); }
					 * 
					 * if (pointerCorner1 == LEFT_TOP || pointerCorner1 == RIGHT_TOP) { appList.get(current).changeTop((dist1 * appScaleVertical * -1) / scale); } else if (pointerCorner1 == LEFT_BOTTOM || pointerCorner1 == RIGHT_BOTTOM) {
					 * appList.get(current).changeBottom((dist1 * appScaleVertical) / scale); }
					 * 
					 * if (pointerCorner2 == LEFT_TOP || pointerCorner2 == LEFT_BOTTOM) { appList.get(current).changeLeft((dist2 * appScaleHorizontal * -1) / scale); } else if (pointerCorner2 == RIGHT_TOP || pointerCorner2 == RIGHT_BOTTOM)
					 * { appList.get(current).changeRight((dist2 * appScaleHorizontal) / scale); }
					 * 
					 * if (pointerCorner2 == LEFT_TOP || pointerCorner2 == RIGHT_TOP) { appList.get(current).changeTop((dist2 * appScaleVertical * -1) / scale); } else if (pointerCorner2 == LEFT_BOTTOM || pointerCorner2 == RIGHT_BOTTOM) {
					 * appList.get(current).changeBottom((dist2 * appScaleVertical) / scale); }
					 */

					changeX1 += (x1 - startX1) / scale;
					changeY1 += (y1 - startY1) / scale;
					changeX2 += (x2 - startX2) / scale;
					changeY2 += (y2 - startY2) / scale;

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
				if (current != -1 && !debug) {

					System.out.println("Jdu poslat resize");
				
					Application app = appList.get(current);
					int cX1 = (int) (changeX1 * Application.DRAW_SCALE) / 2;
					int cY1 = (int) (changeY1 * Application.DRAW_SCALE * -1) / 2;
					int cX2 = (int) (changeX2 * Application.DRAW_SCALE) / 2;
					int cY2 = (int) (changeY2 * Application.DRAW_SCALE * -1) / 2;

					int l = (int) (app.getLeft() * Application.DRAW_SCALE);
					int r = (int) (app.getRight() * Application.DRAW_SCALE); 
					int b = (int) (Communicator.Y - (app.getBottom() * Application.DRAW_SCALE));
					int t = (int) (Communicator.Y - (app.getTop() * Application.DRAW_SCALE));
					// lrbt
					
					int cl = 0, cr = 0, cb = 0, ct = 0;
					/*
					if (pointerCorner1 == LEFT_TOP) {
						cl = cX1;
						ct = cY1;
					} else if (pointerCorner1 == RIGHT_TOP) {
						cr = cX1;
						ct = cY1;
					} else if (pointerCorner1 == RIGHT_BOTTOM) {
						cr = cX1;
						cb = cY1;
					} else if (pointerCorner1 == LEFT_BOTTOM) {
						cl = cX1;
						cb = cY1;
					}		
					
					if (pointerCorner2 == LEFT_TOP) {
						cl = cX2;
						ct = cY2;
					} else if (pointerCorner2 == RIGHT_TOP) {
						cr = cX2;
						ct = cY2;
					} else if (pointerCorner2 == RIGHT_BOTTOM) {
						cr = cX2;
						cb = cY2;
					} else if (pointerCorner2 == LEFT_BOTTOM) {
						cl = cX2;
						cb = cY2;
					}	
					*/
					System.out.println(cl + " " + cr + " " + cb + " " + ct);
					
					communicator.resizeWindow(app.getID(), l + cl, r + cr, b + cb, t + ct);
					
					/*-
					if (pointerCorner1 == LEFT_TOP) {
						communicator.resizeWindow(app.getID(), l + cX1, r, b, t + cY1);
					} else if (pointerCorner1 == RIGHT_TOP) {
						communicator.resizeWindow(app.getID(), l, r + cX1, b, t + cY1);
					} else if (pointerCorner1 == RIGHT_BOTTOM) {
						communicator.resizeWindow(app.getID(), l, r + cX1, b + cY1, t);
					} else if (pointerCorner1 == LEFT_BOTTOM) {
						communicator.resizeWindow(app.getID(), l + cX1, r, b + cY1, t);
					}
*/
					/*
					if (pointerCorner2 == LEFT_TOP) {
						communicator.resizeWindow(app.getID(), l + cX2, r, b, t + cY2);
					} else if (pointerCorner2 == RIGHT_TOP) {
						communicator.resizeWindow(app.getID(), l, r + cX2, b, t + cY2);
					} else if (pointerCorner2 == RIGHT_BOTTOM) {
						communicator.resizeWindow(app.getID(), l, r + cX2, b + cY2, t);
					} else if (pointerCorner2 == LEFT_BOTTOM) {
						communicator.resizeWindow(app.getID(), l + cX2, r, b + cY2, t);
					}
*/
					changeX1 = 0;
					changeY1 = 0;
					changeX2 = 0;
					changeY2 = 0;
				}
			}

			mode = 0;

			break;

		case MotionEvent.ACTION_UP:
			System.out.println("Mode: " + mode);
			if (mode == DRAG) {
				mode = 0;
				if (current != -1) {

					communicator.moveWindow(appList.get(current).getID(), (int) (changeX * Application.DRAW_SCALE),
							(int) (changeY * Application.DRAW_SCALE * -1));

					changeX = 0;
					changeY = 0;
				}
			} else if (mode == ZOOM) {
				mode = 0;
				if (current != -1) {

				}
			}

			current = -1;
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
		float displayX = (float) ((Communicator.X / Communicator.DISPLAYS_X) / Application.DRAW_SCALE);
		float displayY = (float) ((Communicator.Y / Communicator.DISPLAYS_Y) / Application.DRAW_SCALE);
		for (int i = 0; i < Communicator.DISPLAYS_Y; i++) {
			for (int j = 0; j < Communicator.DISPLAYS_X; j++) {
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

	public void setPointerCorners(MotionEvent event) {
		double x1 = event.getX(0);
		double y1 = event.getY(0);
		double x2 = event.getX(1);
		double y2 = event.getY(1);

		double left = (appList.get(current).getLeft() + offsetX) * scale;
		double right = (appList.get(current).getRight() + offsetX) * scale;
		double top = (appList.get(current).getTop() + offsetY) * scale;
		double bottom = (appList.get(current).getBottom() + offsetY) * scale;

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
}