package fr.kinj14.orerun.utils;

public class WorldUtil {
	public static float clamp(float val, float min, float max) {
	    return Math.max(min, Math.min(max, val));
	}
}
