package bpm.vanilla.platform.core.runtime.dao.platform;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.runtime.tools.Security;



public class SessionDAO  {
	private List<VanillaSession> sessions = new ArrayList<VanillaSession>();

	public List<VanillaSession> findAll() {
		return new ArrayList<VanillaSession>(sessions);
	}

	

	public VanillaSession createSession(User user) {
		VanillaSession session = null;
		String key = user.getId() + "_" + user.getLogin();
		String uuid = UUID.nameUUIDFromBytes(key.getBytes()).toString();;
		//check if the session already exists
		//if it does no need to create it
		for(VanillaSession s : sessions){
			if (s.getUuid().equals(uuid)){
				return s;
			}
		}
		synchronized (sessions) {
			
			session = new VanillaSession(user, uuid);
			sessions.add(session);
		}
		return session;
	}

	public boolean delete(VanillaSession d) {
		synchronized (sessions) {
			for(VanillaSession s : sessions){
				if (s.getUuid().equals(d.getUuid())){
					sessions.remove(s);
					return true;
				}
			}
		}
		return false;
	}
	
	public VanillaSession findForTicket(String ticket) {
		for(VanillaSession s : sessions){
			if (s.getUuid().equals(ticket)){
				return s;
			}
		}
		return null;
	}

	public List<VanillaSession> findActivesSessions(String date) {
		 List<VanillaSession> active = new ArrayList<VanillaSession>();
		for(VanillaSession s : sessions){
			if (s.isActive()){
				active.add(s);
			}
		}
		return active;
	}
	
	public void updateSession(VanillaSession session, String id) {
		VanillaSession old = null;
		synchronized (sessions) {
			for(VanillaSession s : sessions){
				if (s.getUuid().equals(id)){
					old = s;
				}
			}
			if(old != null) {
				sessions.remove(old);
			}
			sessions.add(session);
		}
	}



	public VanillaSession findForLoginPassword(String login, String password) {
		VanillaSession session = null;
		
		String md5password = password;
		if (!password.matches("[0-9a-f]{32}")) {
			md5password = Security.encode(password);
		}
		
		synchronized (sessions) {
			for(VanillaSession s : sessions){
				if(s.getUser() != null && s.getUser().getLogin().equals(login) && s.getUser().getPassword().equals(md5password)) {
					session = s;
					break;
				}
			}
		}
		return session;
	}
}
