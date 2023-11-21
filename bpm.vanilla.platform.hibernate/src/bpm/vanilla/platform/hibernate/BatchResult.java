package bpm.vanilla.platform.hibernate;


public class BatchResult {

	public enum Result {
		INSERTION, ERROR, WAITING;
	}

	private Result result;
	private String error;

	private int numberOfFileTraited = 0;

	public BatchResult() {
	}

	public void setResult(Result result, String error) {
		this.result = result;
		this.error = error;
	}

	public void addFilesTraited(int numberOfFileTraited) {
		this.numberOfFileTraited += numberOfFileTraited;
	}

	public Result getResult() {
		return result;
	}

	public String getError() {
		return error;
	}

	public int getNumberOfFileTraited() {
		return numberOfFileTraited;
	}
}
