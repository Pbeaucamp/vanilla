package bpm.gateway.runtime2.tools;


public class IdGenerator {

	/**
	 * return 
	 * @param min
	 * @param max
	 * @param step
	 * @param last
	 * @return
	 */
	
	
	
	public static long generateId(long min, long max, long step, long last){
		long l = (last + step ) % max ;
		
		return l < min? l+min:l;
		
	}
	
	
	
}
