package bpm.fd.api.core.model;

import java.util.List;

public interface IStatuable {
	public static final int OK = 1;
	public static final int UNDEFINED = 0;
	public static final int ERROR = 2;
	
	public int getStatus();
	public List<Exception> getProblems();
	
}
