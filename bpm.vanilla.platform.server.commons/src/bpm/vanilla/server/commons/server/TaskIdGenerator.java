package bpm.vanilla.server.commons.server;


public class TaskIdGenerator {

	private volatile long id = 0;
	
	synchronized public long generate(){
		return ++id;
	}

}
