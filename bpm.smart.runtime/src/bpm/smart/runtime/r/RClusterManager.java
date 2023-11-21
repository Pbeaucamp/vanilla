package bpm.smart.runtime.r;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import bpm.smart.runtime.SmartManagerComponent;
import bpm.smart.runtime.SmartManagerService;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.Dataset;

public class RClusterManager {

	private SmartManagerComponent component;

	private List<RServer> servers = new CopyOnWriteArrayList<RServer>();

	// private Thread sessionCheckThread = new Thread() {
	// @Override
	// public void run() {
	//
	// boolean running = true;
	// while(running) {
	//
	// //check if users still exist
	// try {
	// List<VanillaSession> sessions =
	// component.getVanillaApi().getVanillaSystemManager().getActiveSessions();
	//
	// for(RServer server : servers) {
	// List<User> toRemove = new ArrayList<User>();
	// LOOK:for(User user : server.getUsers()) {
	// for(VanillaSession session : sessions) {
	// if(session.getUser().getId().intValue() == user.getId().intValue()) {
	// continue LOOK;
	// }
	// }
	// Logger.getLogger(getClass()).debug("User " + user.getLogin() +
	// " has no active session");
	// toRemove.add(user);
	// }
	// server.getUsers().removeAll(toRemove);
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// running = doSleep(180000);
	// }
	// }
	// };

	public RClusterManager(SmartManagerComponent smartManagerComponent) {
		this.component = smartManagerComponent;

		// sessionCheckThread.start();

	}

	private boolean doSleep(int time) {
		Logger.getLogger(getClass()).debug("Waiting " + time + " ms");

		try {
			Thread.sleep(time);
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void addRServer(String url) {
		RServer server = new RServer(url);
		servers.add(server);
	}

	public RServer getRserver(User user) throws Exception {
		boolean connected = false;
		LOOK: for (RServer server : servers) {
			for (User u : server.getUsers()) {
				if (u.getId().intValue() == user.getId().intValue() && u.getLogin().equals(user.getLogin())) {
					if (server.testconnect()) {
						//server.deconnect();
						connected = true;
						return server;
					}
					else {
						// server is not reachable
						//servers.remove(server); //TODO
						break LOOK;
					}
				}
			}
		}

		// XXX if the user has no server
		// For now only looking at the number of users using the server
		// we need to find a way to give weight to users for better load
		// balancing
		int min = Integer.MAX_VALUE;
		int minIndex = -1;
		int i = 0;
		for (RServer server : servers) {
			if (server.getUsers().size() < min) {
				min = server.getUsers().size();
				minIndex = i;
			}
			i++;
		}


		servers.get(minIndex).addUser(user);
		final User threadUser = user;
		// load user datasets in R
//		Thread dataLoad = new Thread(){
//			
//			@Override
//			public void run() {
		
				SmartManagerService service = new SmartManagerService(component, threadUser);
				try {
					List<Dataset> datasets = service.getPermittedDatasets(threadUser);
					
					for (Dataset d : datasets) {
						service.loadDatasettoR(d);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
//			}
//		};
//		if(!dataLoad.isAlive()){
//			dataLoad.start();
//		}

		return servers.get(minIndex);
	}

	public List<RServer> getServers() {
		return servers;
	}
}
