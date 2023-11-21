package bpm.vanilla.server.gateway.server.commands;

import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.tasks.ITask;
import bpm.vanilla.server.gateway.server.tasks.TaskGateway;

/**
 * a simple ServerCommad that will return a Message on the Server state (on/off)
 * 
 * @author ludo
 * 
 */
public class GetGatewayStatsCommand {
	
	private Server server;
	private long taskId = -1;

	public GetGatewayStatsCommand(Server server, long taskId) {
		this.server = server;
		this.taskId = taskId;
	}

	public GatewayRuntimeState perform() throws Exception {
		try {
			ITask task = server.getTaskManager().getTask(taskId);

			if (task == null) {
				throw new Exception("No task found for id=" + taskId);
			}

			if (task instanceof TaskGateway) {
				return ((TaskGateway) task).getStepsInfosMessage();
			}
			else {
				throw new Exception("Cannot get Gateway Steps Informations on a non TaskGateway");
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}
