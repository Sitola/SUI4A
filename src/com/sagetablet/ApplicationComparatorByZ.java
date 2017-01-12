package com.sagetablet;

import java.util.Comparator;

public class ApplicationComparatorByZ implements Comparator<Application> {

	@Override
	public int compare(Application app1, Application app2) {
		if (app1.getZ() == app2.getZ()) {
			return 0;
		} else if (app1.getZ() > app2.getZ()) {
			return -1;
		} else {
			return 1;
		}
	}

}
