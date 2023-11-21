package bpm.vanillahub.core.beans.managers;

public class ConnectorResult {

	public enum Result {
		SUCCESS, ERROR, IGNORED, WAITING;
	}

	private String fileName;

	private Result result;
	private String error;

	private int batch;

	public ConnectorResult(Result result, String error, int batch) {
		this.result = result;
		this.error = error;
		this.batch = batch;
	}

	public ConnectorResult(String fileName, Result result, String error) {
		this.fileName = fileName;
		this.result = result;
		this.error = error;
	}

	public String getFileName() {
		return fileName;
	}

	public Result getResult() {
		return result;
	}

	public String getError() {
		return error;
	}

	public int getBatch() {
		return batch;
	}
}
