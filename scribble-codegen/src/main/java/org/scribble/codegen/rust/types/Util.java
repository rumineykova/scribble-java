package org.scribble.codegen.rust.types;

import java.util.HashMap;
import java.util.Map;

import org.scribble.type.name.Role;

public class Util { 
	private static Map<Role, Integer> counterMap = new HashMap<>(); 

	public static int getNextCounter(Role r) {
		if (!counterMap.containsKey(r)) {
			counterMap.put(r, 0);
		}
		int count = counterMap.get(r);
		counterMap.put(r, count+1);
		return count;
	}
}
