package com.remote.remote2d.engine.logic;

import java.util.Comparator;

public class CollisionComparator implements Comparator<Collision> {

	@Override
	public int compare(Collision arg0, Collision arg1) {
		return -((arg0.max-arg0.min)-(arg1.max-arg1.min));
	}
	
}
