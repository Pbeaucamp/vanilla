package bpm.es.gedmanager.utils;

import java.io.PrintStream;

final class Timer
{
	private long	startTime;
	private long	endTime;

	public Timer()
	{
		reset();
	}

	public void start()
	{
		startTime =
			System.currentTimeMillis();
	}

	public void end()
	{
		endTime =
			System.currentTimeMillis();
	}

	public long duration()
	{
		return (endTime - startTime);
	}

	public void printDuration( PrintStream out )
	{
		long elapsedTimeInSecond =
			duration() / 1000;
		long remainderInMillis =
			duration() % 1000;

		out.println("\nTotal execution time:" //$NON-NLS-1$
			+ elapsedTimeInSecond
			+ "." //$NON-NLS-1$
			+ remainderInMillis
			+ " seconds"); //$NON-NLS-1$
	}

	public void reset()
	{
		startTime = 0;
		endTime = 0;
	}
}

