package com.sagetablet;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.sagetablet.android.XMLRPCClient;
import com.sagetablet.android.XMLRPCException;

import android.app.Activity;
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

public class FileChooserDrawView extends View {

	private Paint paint = new Paint();

	private Bitmap images[];

	private int height;

	private float startX, startY;
	private int startI;

	private double offsetY = 10;
	private double changeY = 0;

	private int items = 0;

	private static double OFFSET_Y_MIN = -500;
	private static final double OFFSET_Y_MAX = 10;

	private static final int FOLDER = 0;
	private static final int FILE = 1;

	private XMLRPCClient appLauncher = new XMLRPCClient("http://"+MainActivity.communicator.IP+":19010");
	private XMLRPCClient fileServer = new XMLRPCClient("http://"+MainActivity.communicator.IP+":8800");
	private FileTree fileTree = null;
	
	private Context context;

	public FileChooserDrawView(Context context) {
		super(context);
		this.context = context;

		try {
			Object files = fileServer.call("GetFiles");
			fileTree = new FileTree((HashMap<String, Object[]>) files);
			HashMap<String, Object[]> filesHM = (HashMap<String, Object[]>) files;
			FileTree fileTree = new FileTree(filesHM);

		} catch (XMLRPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		images = new Bitmap[1];
		images[0] = BitmapFactory.decodeResource(getResources(), R.drawable.archivesmall);
	}

	@Override
	public void onDraw(Canvas canvas) {

		paint.setTextSize((int) (32));
		items = 0;

		LinkedList<NameTypeTuple> dirsandfiles = fileTree.getCurrentDirectoriesAndFiles();

		if (!fileTree.isInRoot()) {
			drawItem(canvas, "..", 0);
		}
		for (NameTypeTuple fileNameType : dirsandfiles) {
			String name = fileNameType.getName();
			int type = fileNameType.getType();

			drawItem(canvas, name, type);
		}

		height = getHeight();
		OFFSET_Y_MIN = (items * -70) - 10 + height;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();

		float x, y;

		switch (eventaction & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			startY = event.getY();
			y = event.getY();

			startI = (int) ((y - offsetY) / 70);
			System.out.println(startI);

			changeY = 0;

			break;

		case MotionEvent.ACTION_MOVE:
			y = event.getY();
			offsetY += (y - startY);
			changeY += (y - startY);
			if (offsetY < OFFSET_Y_MIN)
				offsetY = OFFSET_Y_MIN;
			if (offsetY > OFFSET_Y_MAX)
				offsetY = OFFSET_Y_MAX;

			startY = event.getY();
			break;

		case MotionEvent.ACTION_UP:
			x = event.getX();
			y = event.getY();

			int newI = (int) ((y - offsetY) / 70);

			if ((startI == newI) && (startI < items) && (changeY < 5) && (changeY > -5)) {

				// FileInfo note
				// 0 = fileType
				// 1 = size tuple
				// 2 = fullPath
				// 3 = appName
				// 4 = params
				// 5 = fileExists

				if (fileTree.isInRoot()) {
					fileTree.getLevelLower(startI);
				} else {
					if (startI == 0) {
						fileTree.getLevelHigher();
					} else {

						if (fileTree.clickedIsFile(startI -1) == 1) {

							FileTreeNode clickedNode = fileTree.getActiveNode().getSuccessors().get(startI - 1);

							try {
								//System.out.println("name: " + clickedNode.getName() + " path: " + clickedNode.getPath());

								Object fileInformation = fileServer.call("GetFileInfo", clickedNode.getName(), 0, clickedNode.getPath());

								//System.out.println("Dostali jsme se za getfileinfo");
								

								Object[] fileInfo = (Object[]) fileInformation;
					

								String appName = (String) fileInfo[3];
								Object[] size = (Object[]) fileInfo[1];
								String width = size[0].toString();
								String height = size[1].toString();

								String fileType = fileInfo[0].toString();

								String optionalArgsImage = fileInfo[2] + " " + width + " " + height + " " + fileInfo[4];
								String optionalArgsVideo = fileInfo[2] + " " + fileInfo[4];
								
								Object resultOfExecution = null;

								if (fileType.equals("image")) {
									resultOfExecution = appLauncher.call("startDefaultApp", appName, MainActivity.communicator.IP, 20002, false, "default", false, false,
											optionalArgsImage);
								} else if (fileType.equals("video") || (fileType.equals("pdf"))) {
									resultOfExecution = appLauncher.call("startDefaultApp", appName, MainActivity.communicator.IP, 20002, false, "default", false, false,
											optionalArgsVideo);
								}
								String appId = resultOfExecution.toString();
								Synchronizer synchronizer = new Synchronizer();
								synchronizer.execute(appId);
								//MainActivity.communicator.synchronizeApps();
								
								System.out.println("The ID of the executed app is: " + resultOfExecution);
								((Activity)context).finish();
								
							} catch (XMLRPCException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return true;
							}

						} else {
							fileTree.getLevelLower(startI - 1);
						}

					}

				}
			}
			break;
		}

		invalidate();
		return true;
	}

	public void drawItem(Canvas canvas, String name, int type) {
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.LTGRAY);
		paint.setStrokeWidth(3);
		canvas.drawRect(10, items * 70 + (float) offsetY, 1000, items * 70 + 70 + (float) offsetY, paint);
		paint.setStrokeWidth(0);
		paint.setTextSize((int) (32));
		paint.setColor(Color.BLACK);
		canvas.drawText(name, 65, items * 70 + 45 + (float) offsetY, paint);
		if (type == FOLDER) {
			canvas.drawBitmap(images[0], 15, items * 70 + 15 + (float) offsetY, paint);
		}

		items++;
	}
}