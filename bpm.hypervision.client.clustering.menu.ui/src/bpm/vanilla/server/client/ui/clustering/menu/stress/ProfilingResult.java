package bpm.vanilla.server.client.ui.clustering.menu.stress;

import bpm.vanilla.server.client.ui.clustering.menu.Messages;


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

	/**
	 * @return the avgPublishingTime
	 */
	public double getAvgPublishingTime() {
		return avgPublishingTime;
	}

	/**
	 * @return the avgRunningTime
	 */
	public double getAvgRunningTime() {
		return avgRunningTime;
	}

	/**
	 * @return the avgWaitingTime
	 */
	public double getAvgWaitingTime() {
		return avgWaitingTime;
	}

	/**
	 * @return the taskNumber
	 */
	public Integer getTaskNumber() {
		return taskNumber;
	}

	/**
	 * @return the repositoryConnectionNumber
	 */
	public Integer getRepositoryConnectionNumber() {
		return repositoryConnectionNumber;
	}

	public void dump() {
		StringBuilder buf = new StringBuilder();
		buf.append("======================================\n"); //$NON-NLS-1$
		buf.append("Result:\n"); //$NON-NLS-1$
		
		buf.append("Configured TaskNumber=" + taskNumber + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("Configured RepositoryConnectionNumber=" +repositoryConnectionNumber + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
//		buf.append("Configured Report Model Number=" + initialCOnfiguration.getValue("reportPoolSize") + "\n");
		buf.append("Average Publishing Time : " + avgPublishingTime + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("Average Running Time    : " + avgRunningTime + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("Average Waiting Time    : " + avgWaitingTime + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		
		buf.append("======================================\n\n"); //$NON-NLS-1$
		
		System.out.println(buf.toString());
		
	}
	
	
}

