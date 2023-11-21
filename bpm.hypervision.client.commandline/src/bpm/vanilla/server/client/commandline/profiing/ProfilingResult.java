package bpm.vanilla.server.client.commandline.profiing;


public class ProfilingResult {
	public double avgPublishingTime;
	public double avgRunningTime;
	public double avgWaitingTime;
	public Integer taskNumber;
	public Integer repositoryConnectionNumber;
	
	public ProfilingResult(double avgPublishingTime, double avgRunningTime,	double avgWaitingTime, Integer taskNumber, Integer repositoryConnectionNumber) {
		super();
		this.avgPublishingTime = avgPublishingTime;
		this.avgRunningTime = avgRunningTime;
		this.avgWaitingTime = avgWaitingTime;
		this.taskNumber = taskNumber;
		this.repositoryConnectionNumber = repositoryConnectionNumber;
	}

	public void dump() {
		StringBuilder buf = new StringBuilder();
		buf.append("======================================\n");
		buf.append("Result:\n");
		
		buf.append("Configured TaskNumber=" + taskNumber + "\n");
		buf.append("Configured RepositoryConnectionNumber=" +repositoryConnectionNumber + "\n");
//		buf.append("Configured Report Model Number=" + initialCOnfiguration.getValue("reportPoolSize") + "\n");
		buf.append("Average Publishing Time : " + avgPublishingTime + "\n");
		buf.append("Average Running Time    : " + avgRunningTime + "\n");
		buf.append("Average Waiting Time    : " + avgWaitingTime + "\n");
		
		buf.append("======================================\n\n");
		
		System.out.println(buf.toString());
		
	}
	
	
}

