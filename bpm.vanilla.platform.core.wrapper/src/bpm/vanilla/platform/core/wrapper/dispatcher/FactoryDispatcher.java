package bpm.vanilla.platform.core.wrapper.dispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.RunIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.wrapper.dispatcher.impl.FdRuntimeDispatcher;
import bpm.vanilla.platform.core.wrapper.dispatcher.impl.FreeMetricsManagerDispatcher;
import bpm.vanilla.platform.core.wrapper.dispatcher.impl.GatewayDispatcher;
import bpm.vanilla.platform.core.wrapper.dispatcher.impl.ReportingDispatcher;
import bpm.vanilla.platform.core.wrapper.dispatcher.impl.RepositoryDispatcher;
import bpm.vanilla.platform.core.wrapper.dispatcher.impl.UnitedOlapDispatcher;
import bpm.vanilla.platform.core.wrapper.dispatcher.impl.WorkflowDispatcher;

public class FactoryDispatcher {
	private HashMap<String, IDispatcher> dispatchersMap = new HashMap<String, IDispatcher>();

	private static List<RunIdentifier> asynchRuns = Collections.synchronizedList(new ArrayList<RunIdentifier>());

	private IVanillaComponentProvider component;

	public FactoryDispatcher(IVanillaComponentProvider component) {
		this.component = component;
	}

	public void addAsynchRun(RunIdentifier identifier) {
		asynchRuns.add(identifier);
	}

	public void removeAsynchRun(RunIdentifier identifier) {
		asynchRuns.remove(identifier);
	}

	public RunIdentifier getRunIdentifier(String key) {
		synchronized (asynchRuns) {
			for (RunIdentifier i : asynchRuns) {
				if (key.equals(i.getKey())) {
					return i;
				}
			}
		}

		return null;
	}

	public RunIdentifier getRunIdentifier(int taskId) {
		synchronized (asynchRuns) {
			for (RunIdentifier i : asynchRuns) {
				if (taskId == i.getTaskId()) {
					return i;
				}
			}
		}

		return null;
	}

	public List<IVanillaComponentIdentifier> getComponentsFor(IDispatcher dispatcher) {
		List<IVanillaComponentIdentifier> l = new ArrayList<IVanillaComponentIdentifier>();
		for (String k : dispatchersMap.keySet()) {
			if (dispatchersMap.get(k) == dispatcher) {
				try {
					l.addAll(component.getVanillaListenerComponent().getRegisteredComponents(k, false));
				} catch (Exception e) {
					Logger.getLogger(getClass()).error("Unable to look for the Components " + k + " - " + e.getMessage(), e);
				}
			}
		}
		return l;
	}

	public synchronized IDispatcher createDispatcher(String componentType, IVanillaComponentProvider component) throws Exception {

		if (dispatchersMap.get(componentType) != null) {
			return dispatchersMap.get(componentType);
		}
		
		IDispatcher dispatcher = null;
		if (componentType.equals(VanillaComponentType.COMPONENT_FREEDASHBOARD)) {
			dispatcher = new FdRuntimeDispatcher(component, this);

		}
		else if (componentType.equals(VanillaComponentType.COMPONENT_REPORTING)) {
			dispatcher = new ReportingDispatcher(component, this);
		}
		else if (componentType.equals(VanillaComponentType.COMPONENT_GATEWAY)) {
			dispatcher = new GatewayDispatcher(component, this);
		}
		else if (componentType.equals(VanillaComponentType.COMPONENT_WORKFLOW)) {
			dispatcher = new WorkflowDispatcher(component, this);
		}
		else if (componentType.equals(VanillaComponentType.COMPONENT_UNITEDOLAP)) {
			// dispatcher = new FaRuntimeDispatcher(this) ;
			dispatcher = new UnitedOlapDispatcher(component, this);
		}
		else if (componentType.equals(VanillaComponentType.COMPONENT_FREEMETRICS)) {
			dispatcher = new FreeMetricsManagerDispatcher(component, this);
		}
		else if (componentType.equals(VanillaComponentType.COMPONENT_REPOSITORY)) {
			dispatcher = new RepositoryDispatcher(component);
		}
		
		if (dispatcher != null) {
			synchronized (dispatchersMap) {
				System.out.println("in sync");
				dispatchersMap.put(componentType, dispatcher);
			}
		}

		return dispatcher;
	}
 }
