package com.sagetablet;

public class PerformanceInfo {

	private int CURRENT = 0;
	private int AVERAGE = 1;
	private int MIN = 2;
	private int MAX = 3;

	private int BW = 4;
	private int FPS = 8;

	private int SNAPS = 30;

	private double[] renderingBW = new double[4];
	private double[] renderingFPS = new double[4];
	private double[] displayBW = new double[4];
	private double[] displayFPS = new double[4];

	private double[] renderingBWGraph = new double[SNAPS];
	private double[] renderingFPSGraph = new double[SNAPS];
	private double[] displayBWGraph = new double[SNAPS];
	private double[] displayFPSGraph = new double[SNAPS];

	private int snapCounter;

	public PerformanceInfo() {
		snapCounter = 0;

		for (int i = 0; i < SNAPS; i++) {
			renderingBWGraph[i] = 0;
		}
		for (int i = 0; i < SNAPS; i++) {
			renderingFPSGraph[i] = 0;
		}
		for (int i = 0; i < SNAPS; i++) {
			displayBWGraph[i] = 0;
		}
		for (int i = 0; i < SNAPS; i++) {
			displayBWGraph[i] = 0;
		}

	}

	public double computeAverage(double[] Field) {
		double total = 0;
		for (int i = 0; i < SNAPS; i++)
			total += Field[i];

		return (total / SNAPS);
	}

	public boolean isNonBlank(String word) {
		return (word.length() > 1);
	}

	public void parseReceivedInfo(String rendering, String display) {
		String[] renderingInfo = rendering.split(" ");

		int parsing = 0;
		double currentRendBW = 0;
		double currentRendFPS = 0;

		for (int i = 0; i < renderingInfo.length; i++) {
			if (isNonBlank(renderingInfo[i])) {
				// System.out.println("Nonblank info is: " + renderingInfo[i]);
				if (parsing == 0) {
					parsing++;
				} else if (parsing == 1) {
					currentRendBW = Double.parseDouble(renderingInfo[i]);
					parsing++;
				} else if (parsing == 2) {
					currentRendFPS = Double.parseDouble(renderingInfo[i]);
					parsing++;
				} else if (parsing > 2)
					break;
			}
		}

		// System.out.println("Rendering info(1): "+ currentRendBW + " (2): " + currentRendFPS );

		// double currentRendBW = Double.parseDouble(renderingInfo[BW]);
		renderingBWGraph[snapCounter % SNAPS] = currentRendBW;
		renderingBW[CURRENT] = currentRendBW;
		renderingBW[AVERAGE] = computeAverage(renderingBWGraph);
		renderingBW[MIN] = Math.min(renderingBW[MIN], currentRendBW);
		renderingBW[MAX] = Math.max(renderingBW[MAX], currentRendBW);
		/*
		 System.out.println("Max: "+ renderingBW[MAX] );
		 System.out.println("Min: "+ renderingBW[MIN] );
		 System.out.println("Current: "+ renderingBW[CURRENT] );
		 System.out.println("Average: "+ renderingBW[AVERAGE] );
		 
		 for(int i= 0; i< renderingBWGraph.length;i++)
			 System.out.println("Rendering bw n. "+i+" is: "+ renderingBWGraph[i]);
		*/
		// double currentRendFPS = Double.parseDouble(renderingInfo[FPS]);
		renderingFPSGraph[snapCounter % SNAPS] = currentRendFPS;
		renderingFPS[CURRENT] = currentRendFPS;
		renderingFPS[AVERAGE] = computeAverage(renderingFPSGraph);
		renderingFPS[MIN] = Math.min(renderingFPS[MIN], currentRendFPS);
		renderingFPS[MAX] = Math.max(renderingFPS[MAX], currentRendFPS);

		String[] displayInfo = display.split(" ");

		parsing = 0;
		double currentDisplayBW = 0;
		double currentDisplayFPS = 0;

		for (int i = 0; i < displayInfo.length; i++) {
			if (isNonBlank(displayInfo[i])) {
				// System.out.println("Nonblank info is: " + renderingInfo[i]);
				if (parsing == 0) {
					parsing++;
				} else if (parsing == 1) {
					currentDisplayBW = Double.parseDouble(displayInfo[i]);
					parsing++;
				} else if (parsing == 2) {
					currentDisplayFPS = Double.parseDouble(displayInfo[i]);
					parsing++;
				} else if (parsing > 2)
					break;
			}
		}

		// double currentDisplayBW = Double.parseDouble(displayInfo[BW]);
		displayBWGraph[snapCounter % SNAPS] = currentDisplayBW;
		displayBW[CURRENT] = currentDisplayBW;
		displayBW[AVERAGE] = computeAverage(displayBWGraph);
		displayBW[MIN] = Math.min(displayBW[MIN], currentDisplayBW);
		displayBW[MAX] = Math.max(displayBW[MAX], currentDisplayBW);

		// double currentDisplayFPS = Double.parseDouble(renderingInfo[FPS]);
		displayFPSGraph[snapCounter % SNAPS] = currentDisplayFPS;
		displayFPS[CURRENT] = currentDisplayFPS;
		displayFPS[AVERAGE] = computeAverage(renderingFPSGraph);
		displayFPS[MIN] = Math.min(displayFPS[MIN], currentDisplayFPS);
		displayFPS[MAX] = Math.max(displayFPS[MAX], currentDisplayFPS);

		snapCounter++;
	}

	public double[] getRenderingBW() {
		return renderingBW;
	}

	public double[] getRenderingFPS() {
		return renderingFPS;
	}

	public double[] getDisplayBW() {
		return displayBW;
	}

	public double[] getDisplayFPS() {
		return displayFPS;
	}

}
