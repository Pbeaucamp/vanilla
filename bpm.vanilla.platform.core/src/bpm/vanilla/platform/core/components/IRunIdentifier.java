package bpm.vanilla.platform.core.components;

/**
 * Represent a Task identifier that has been sumbited to a Runtime Component asynchronously.
 * It is used to gather the RuntimeComponent result once te Task is stopped  
 * @author ludo
 *
 */
public interface IRunIdentifier {
	public String getKey();
//	public int getTaskId();
}
