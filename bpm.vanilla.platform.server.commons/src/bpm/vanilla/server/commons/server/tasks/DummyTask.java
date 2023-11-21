package bpm.vanilla.server.commons.server.tasks;

import java.util.Date;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.tasks.ITaskState;
import bpm.vanilla.platform.core.beans.tasks.TaskPriority;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.server.commons.server.Server;

public class DummyTask implements ITask {

	private static final long serialVersionUID = 1907856087029292675L;
	
	private Server server;
	private long taskId;
	private IObjectIdentifier objectIdentifier;
	private Date creationDate;

	public DummyTask(Server server, long taskId, IObjectIdentifier objectIdentifier, Date creationDate) {
		this.server = server;
		this.taskId = taskId;
		this.objectIdentifier = objectIdentifier;
		this.creationDate = creationDate;
	}

	@Override
	public IVanillaComponentIdentifier getComponentIdentifier() {
		return server.getComponentIdentifier();
	}

	@Override
	public int getGroupId() {
		return 0;
	}

	@Override
	public long getId() {
		return taskId;
	}

	@Override
	public IObjectIdentifier getObjectIdentifier() {
		return objectIdentifier;
	}

	@Override
	public TaskPriority getTaskPriority() {
		return TaskPriority.NORMAL_PRIORITY;
	}

	@Override
	public ITaskState getTaskState() {
		return s;
	}

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public boolean isStopped() {
		return false;
	}

	@Override
	public void join() throws Exception {
	}

	@Override
	public void startTask() {
	}

	@Override
	public void stopTask() throws Exception {
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String getSessionId() {
		return null;
	}

	private ITaskState s = new ITaskState() {

		private static final long serialVersionUID = 1L;

		@Override
		public Date getCreationDate() {
			return creationDate;
		}

		@Override
		public Long getDuration() {
			return null;
		}

		@Override
		public Long getElapsedTime() {
			return null;
		}

		@Override
		public String getFailingCause() {
			return "";
		}

		@Override
		public Date getStartedDate() {
			return null;
		}

		@Override
		public Date getStoppedDate() {
			return null;
		}

		@Override
		public ActivityResult getTaskResult() {
			return ActivityResult.UNDEFINED;
		}

		@Override
		public boolean hasFailed() {
			return false;
		}

		@Override
		public boolean hasSucceed() {
			return false;
		}

		@Override
		public boolean isStarted() {
			return false;
		}

		@Override
		public ActivityState getTaskState() {
			return ActivityState.WAITING;
		}

		@Override
		public boolean isStopped() {
			return false;
		}
	};

}
