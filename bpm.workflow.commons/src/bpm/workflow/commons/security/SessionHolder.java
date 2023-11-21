package bpm.workflow.commons.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import bpm.vanilla.platform.core.beans.User;

/**
 * This class is responsible for Managing the currentSession within the
 * component.
 * 
 * A session is created and stored when a call to RepositoryContentServlet or
 * ConnectionServlet is made. The sessionId is returned in the OutputSTream sent
 * to the client(the sessionId attribute of the XML Root element).
 * 
 * The SessionHolder contains an active Thread that monitor Idle connection, if
 * the session has been used (a call to SessionContent.setUsedTime) for more
 * than SessionContent.maxDurationSession, the session will be deleted.
 * 
 * The cleaner is started by a call to the method startCleaning.
 * 
 * WHen stopCleaning is called, the cleaner Thread is asked to stop and joined.
 * Once this is done, this SessionHolder should not be used anymore
 * 
 * 
 * @author ludo
 * 
 */
public class SessionHolder<T extends IComponent, E extends AbstractSession> {
	private Collection<E> sessions = Collections.synchronizedCollection(new ArrayList<E>());
	private T component;

	private SessionCleaner cleaner;

	public SessionHolder(T component) {
		this.component = component;
		this.cleaner = new SessionCleaner(this);
	}

	public void startCleaning() {
		component.getLogger().info("Starting cleaning...");
		cleaner.start();
	}

	public void stopCleaning() {
		component.getLogger().info("Stopping cleaning...");
		cleaner.setDisabled();
		try {
			cleaner.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cleaner = null;
		component.getLogger().info("Stopped cleaning");
	}

	public E getSession(String sessionId) throws Exception {
		synchronized (sessions) {
			for (E s : sessions) {
				if (s.getSessionId().equals(sessionId)) {
					return s;
				}
			}
		}

		component.getLogger().warn("session " + sessionId + " not found.");

		throw new Exception("The session has expired.");
	}

	public E createSession(E session, User user) {
		synchronized (sessions) {
			String sessionId = null;
			while (sessionId == null) {
				sessionId = UUID.randomUUID().toString();

				for (AbstractSession s : sessions) {
					if (s.getSessionId().equals(sessionId)) {
						sessionId = null;
						break;
					}
				}
			}
			session.setInfos(sessionId, user);
			sessions.add(session);
		}
		component.getLogger().info("session " + session.getSessionId() + " created");
		return session;

	}

	protected void clean() {
		List<AbstractSession> toRemove = new ArrayList<AbstractSession>();

		synchronized (sessions) {

			for (AbstractSession s : sessions) {
				if (s.timeOutReached()) {
					toRemove.add(s);
				}
			}

			for (AbstractSession s : toRemove) {
				sessions.remove(s);
				component.getLogger().info("Session " + s.getSessionId() + " removed because timeout expired");
			}
		}

	}

	private static class SessionCleaner extends Thread {
		private SessionHolder<?, ?> sessionHolder;
		private boolean enabled = false;

		public SessionCleaner(SessionHolder<?, ?> sessionHolder) {
			this.sessionHolder = sessionHolder;
		}

		public void run() {
			enabled = true;
			sessionHolder.component.getLogger().info("Session cleaner started");
			while (enabled) {

				try {
					Thread.sleep(AbstractSession.maxDurationSession);

					sessionHolder.clean();
				} catch (InterruptedException e) {
					sessionHolder.component.getLogger().warn("Cannot perform sleep");
					setDisabled();
				}

			}
		}

		public void setDisabled() {
			enabled = false;
			try {
				this.interrupt();
			} catch (Exception ex) {
				try {
					this.interrupt();
				} catch (Exception ex2) {

				}
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		if (cleaner != null && cleaner.isAlive()) {
			try {
				cleaner.interrupt();
			} catch (Exception ex) {

			}
		}
		super.finalize();
	}

	public List<E> getSessions() {
		return new ArrayList<E>(sessions);
	}
}
