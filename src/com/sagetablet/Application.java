

package com.sagetablet;

public class Application {
	private String appName;
	private int ID;
	private int sailID;
	private int Z;
	private String stuff;
	private PerformanceInfo perfInfo;
	private double top;
	private double bottom;
	private double left;
	private double right;
	
	private boolean performanceStarted = false;
	
	public static final double DRAW_SCALE = 15;

	Application(String appName, int ID, double left, double right, double top, double bottom, int sailID, int Z, String stuff) {
		this.appName = appName;
		this.ID = ID;
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;		
		this.sailID = sailID;
		this.Z = Z;
		this.stuff = stuff;
		this.perfInfo= new PerformanceInfo();
	}

	@Override
	public String toString() {
		return "Application [appName=" + appName + ", ID=" + ID + ", sailID=" + sailID + ", Z=" + Z + ", stuff=" + stuff + ", top=" + top + ", bottom="
				+ bottom + ", left=" + left + ", right=" + right + "]";
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getStuff() {
		return stuff;
	}

	public void setStuff(String stuff) {
		this.stuff = stuff;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getSailID() {
		return sailID;
	}

	public void setSailID(int sailID) {
		this.sailID = sailID;
	}

	public int getZ() {
		return Z;
	}

	public void setZ(int z) {
		Z = z;
	}

	public double getTop() {
		return ((SAGE.Y - bottom) / DRAW_SCALE);
	}

	public void setTop(double top) {
		this.bottom = (SAGE.Y - (top * DRAW_SCALE));
	}

	public double getBottom() {
		return ((SAGE.Y - top) / DRAW_SCALE);
	}

	public void setBottom(double bottom) {
		this.top = (SAGE.Y - (bottom * DRAW_SCALE));
	}

	public double getLeft() {
		return (left / DRAW_SCALE);
	}

	public void setLeft(double left) {
		this.left = (left * DRAW_SCALE);
	}

	public double getRight() {
		return (right / DRAW_SCALE);
	}

	public void setRight(double right) {
		this.right = (right * DRAW_SCALE);
	}
	
	public void changeTop (double change) {
		bottom -= (change * DRAW_SCALE); //mozna neni dobre, ale snad jo
	}
	
	public void changeBottom (double change) {
		top -= (change * DRAW_SCALE);
	}
	
	public void changeLeft (double change) {
		left += (change * DRAW_SCALE);
	}
	
	public void changeRight (double change) {
		right += (change * DRAW_SCALE);
	}
	
	public PerformanceInfo getPerfInfo() {
		return perfInfo;
	}

	public void setPerfInfo(PerformanceInfo perfInfo) {
		this.perfInfo = perfInfo;
	}

	public boolean isPerformanceStarted() {
		return performanceStarted;
	}

	public void setPerformanceStarted(boolean performanceStarted) {
		this.performanceStarted = performanceStarted;
	}
}
