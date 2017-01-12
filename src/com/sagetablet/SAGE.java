package com.sagetablet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.sagetablet.android.XMLRPCClient;
import com.sagetablet.android.XMLRPCException;

import android.os.AsyncTask;

public class SAGE {
	private final static int LINEFEED = 10;
	private final static int NULL = 0;
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private Semaphore semaphore = new Semaphore(1);
	private String readMessage;
	private List<Application> appList = new LinkedList<Application>();
	public int maxZ = 0;
	/*
	 * public static int X = 11980; public static int Y = 5076; public static int DISPLAYS_X = 6; public static int DISPLAYS_Y = 4;
	 */
	public String IP = "";

	public static int X = 1;
	public static int Y = 1;

	public static int DISPLAYS_X = 1;
	public static int DISPLAYS_Y = 1;

	public SAGE(String IP, int port) throws IOException {

		// First the SAGE client connects to sage and sets socket, input and output for communication
		try {
			this.IP = IP;
			socket = new Socket(IP, port);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (socket.isConnected()) {
			System.out.println("Socket je connected");
		} else {
			System.out.println("Socket neni connected");
		}

		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// If all is ok, it is printed
		System.out.println("Inn's and Out's work");

		// Second, a listening thread that waits for application and performance info from SAGE is created
		Listener listener = new Listener();
		listener.execute();

		// If all is ok executed message is printed
		System.out.println("Listener executed ");

		// try {
		// Thread.sleep(5000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	public List<Application> getAppList() {
		return appList;
	}

	private void sendMessage(String distance, String mes, String data, String applicationCode, String read) {
		PrepareMessage pm = new PrepareMessage(distance, mes, data, applicationCode);
		String message = pm.getMessage();
		System.out.println(message);
		Sender sender = new Sender();
		sender.execute(message, read);
	}

	public void changeBackground(int r, int g, int b) {
		if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
			return;
		}
		String rgb = r + " " + g + " " + b;
		System.out.println(rgb);
		sendMessage("", "1007", rgb, "", "NonRead");
	}

	public void changeZValue(int appid, int newZValue) {
		sendMessage("", "1008", appid + " " + newZValue, "", "NonRead");
	}
	
	public void bringToFront(int appid) {
		sendMessage("", "1010", appid + "", "", "NonRead");
		
		for(Application app : appList) {
			if (app.getID() == appid) {
				app.setZ(0);
			} else {
				app.setZ(app.getZ() + 1);
			}
		}
	}

	public void shutdownAPP(int appid) {
		sendMessage("", "1002", appid + "", "", "NonRead");
	}

	public void moveWindow(int appid, int x, int y) {
		sendMessage("", "1003", appid + " " + x + " " + y, "", "NonRead");
	}

	public void resizeWindow(int appid, int left, int right, int bottom, int top) {
		System.out.println("Resize window: " + "L: " + left + " R: " + right + " B: " + bottom + " T: " + top);
		sendMessage("", "1004", appid + " " + left + " " + right + " " + bottom + " " + top, "", "NonRead");
	}

	public void getApps() {
		sendMessage("", "1000", "", "", "NonRead");
	}

	public void stopPerformance(int appid) {
		sendMessage("", "1006", appid + "", "", "NonRead");
	}

	public void startPerformance(int appid) {
		sendMessage("", "1005", appid + " 2", "", "NonRead");
	}

	public void stopAllPerformance() {
		sendMessage("", "1000", "", "", "NonRead");
	}

	public void startAtlantis() {
		XMLRPCClient client = new XMLRPCClient("http://" + IP + ":19010");
		try {
			Object s = client.call("startDefaultApp", "atlantis", IP, 20002, false, "default", false, false, "");
		} catch (XMLRPCException e) {
			e.printStackTrace();
		}
		Synchronizer synchronizer = new Synchronizer();
		synchronizer.execute();
	}

	public void synchronizeApps() {
		System.out.println("Jsem v synchronizeApps");
		// sendMessage("", "1000", "", "", "NonRead");
		// sendMessage()

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String appDataString = "";
		String firstLineSAGE;
		/*
		 * try { System.out.println("Jsem pred in"); firstLineSAGE = in.readLine(); System.out.println("firstLineSAGE: " + firstLineSAGE); String sageResolution = in.readLine(); System.out.println("sageResolution: " + sageResolution);
		 * String singleDisplayResolution = in.readLine(); System.out.println("singleDisplayResolution: " + singleDisplayResolution);
		 * 
		 * int readChar = 30; while (readChar != LINEFEED && in.ready()) { readChar = in.read(); appDataString = appDataString + ((char) readChar + ""); } } catch (IOException e) { e.printStackTrace(); }
		 */
		System.out.println("appDataString: " + appDataString);
		appList = AppDataParser.parseAppStringAndReturnAppList(appDataString);
	}

	private class Listener extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			System.out.println("Listener executed (From inside listener)");

			boolean started = false;
			/*
			 * if(started == false){ for (Application currentApplication : appList) { int appID = currentApplication.getID(); startPerformance(appID); System.out.println("Started performance for appID: " + appID + " appName: " +
			 * currentApplication.getAppName()); }
			 * 
			 * }
			 */

			// startPerformance(1);
			sendMessage("", "1000", "", "", "NonRead");
			MainActivity.communicator.synchronizeApps();

			System.out.println("Message to start sending data sent ");

			// performance doesn't work, tried to fix this by manually recording app 6
			// startPerformance(6);
			// System.out.println("Started performance recording for app 6");

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// If no messages are yet on the input, the listener thread waits first
			System.out.println("Before in is ready");

			try {
				while (!in.ready()) {
					System.out.println("Waiting till in is ready");

				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Got through in is ready");

			try {
				// int refreshApplicationCounter = 0;
				// In cycle, the listener thread catches all performance and other info, discards performance, keeps the rest
				int found = 0;
				while (true) {

					String receivedLine = "";
					int readChar = 0;

					// reads the input in full lines, some nerfs had to be done due to mad SAGE formatting (last line
					// does not end with a linefeed, it actually doesn't end with anything)
					while (readChar != LINEFEED && in.ready()) {
						readChar = in.read();
						receivedLine = receivedLine + ((char) readChar + "");
					}

					System.out.println("Received line is: " + receivedLine);

					// we are reading anything, therefore, we must check whether there even has been a line in place
					if (!receivedLine.equals("")) {

						// we then check, whether the line was performance info, if it was, we discard it too
						// System.out.println("Checked that the line is not a space, passing on");
						// else
						if (AppDataParser.isDisplayInfo(receivedLine)) {
							//System.out.println("Got to DisplayInfo");
							AppDataParser.parseAndSetDisplayInfo(receivedLine);
							//second line is tiled info
							String tiledInfo = in.readLine();
							AppDataParser.parseAndSetDisplayResolution(tiledInfo);
							//third line is single display info - discarder
							String singleInfo = in.readLine();
						}
						/* DEPRECATED
						}
						// else
						if (AppDataParser.isSingleDisplayResolution(receivedLine)) {
							System.out.println("Got to SingleDisplayRes");
						}

						// else
						if (AppDataParser.isTiledDisplayResolution(receivedLine)) {
							System.out.println("Got to TiledDisplayRes");
							AppDataParser.parseAndSetDisplayResolution(receivedLine);
						}*/
							
							
						// finally it can be either display info or appDataString. As of now, we are only handling
						// the appDataString - more later, so it is otherwise discarded
						// else
						if (AppDataParser.isAppInfoString(receivedLine)) {
							System.out.println("Got to appListRes");
							appList = AppDataParser.parseAppStringAndReturnAppList(receivedLine);
							Collections.sort(appList, new ApplicationComparatorByZ());
						}

						if (AppDataParser.isPerformanceInfo(receivedLine)) {

							int ID = AppDataParser.getAppID(receivedLine);

							String display = in.readLine();
							//System.out.println("Display Info is: " + display);
							String rendering = "";
							while (readChar != NULL && in.ready()) {
								readChar = in.read();
								rendering = rendering + ((char) readChar + "");
							}
							//System.out.println("Rendering Info is: " + rendering);

							for (Application application : appList) {
								if (application.getID() == ID) {
									application.getPerfInfo().parseReceivedInfo(rendering, display);

								}
							}

						}

					} else {
						System.out.println("The received line was empty. Putting the thread to sleep for five seconds");
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					System.out.println("Got through first cycle iteration");

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "done";

		}
	}

	private class Sender extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				semaphore.acquire();
				// System.out.println("Dostal jsem se do postExecute, posilam: " + params[0]);
				out.println(params[0]);

				try {
					Thread.sleep(1200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				semaphore.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
				// } catch (IOException e) {
				// e.printStackTrace();
			}
			return params[0];
		}

		protected void onPostExecute(String message) {
			try {

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public BufferedReader getIn() {
		return in;
	}
}