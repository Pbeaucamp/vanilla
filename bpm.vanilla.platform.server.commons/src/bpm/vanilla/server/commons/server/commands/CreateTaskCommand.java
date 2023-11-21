package bpm.vanilla.server.commons.server.commands;

import java.io.Serializable;
import java.util.Date;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.tasks.DummyTask;
import bpm.vanilla.server.commons.server.tasks.ITask;

public abstract class CreateTaskCommand implements Serializable {

	private static final long serialVersionUID = 1658632300786246551L;

	private transient Server server;
	private IObjectIdentifier objectIdentifier;
	// the id that will be used for the generated task
	protected Long taskId;

	private transient DummyTask dummy;
	private Date creation = new Date();

	public CreateTaskCommand() {

	}

	/**
	 * Look for all methods using MessagePropertyName.class annotation if such
	 * method is found, this method is called with the
	 * message.getPropertyValue(MessagePropertyName.propertyName()) as parameter
	 * 
	 * @param server
	 * @param message
	 */
	public CreateTaskCommand(Server server, IObjectIdentifier objectIdentifier) {
		this.server = server;
		this.objectIdentifier = objectIdentifier;
		this.taskId = server.generateTaskId();

		this.dummy = new DummyTask(server, taskId, objectIdentifier, creation);
	}

	public long addTaskToQueue() throws Exception {
		try {
			getServer().getTaskManager().addTaskToQueue(this);
			return taskId;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Error when adding task to server's queue :" + ex.getMessage());
		}
	}

	public abstract ITask createTask() throws Exception;

	final public ITask getDummy() {
		if (dummy == null) {
			dummy = new DummyTask(getServer(), taskId, objectIdentifier, creation);
		}
		return dummy;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public long getTaskId() {
		return taskId;
	}
}
