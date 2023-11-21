package bpm.vanilla.platform.core.wrapper.dispatcher.impl;

import java.io.FileNotFoundException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.impl.AbstractVanillaDispatcher;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.exceptions.VanillaComponentDownException;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.wrapper.dispatcher.FactoryDispatcher;
import bpm.vanilla.platform.core.wrapper.dispatcher.IDispatcher;

public class FreeMetricsManagerDispatcher extends AbstractVanillaDispatcher implements IDispatcher {

	
	private IVanillaComponentProvider componentProvider;
	private FactoryDispatcher factory;
	
	public FreeMetricsManagerDispatcher(IVanillaComponentProvider component, FactoryDispatcher factory) {

		this.componentProvider = component;
		this.factory = factory;
	}

	@Override
	public void dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<IVanillaComponentIdentifier> ids = factory.getComponentsFor(this);
		
		if (ids.isEmpty()){
			throw new VanillaException("No FreeMetrics Component " + " registered within Vanilla");
		}
		
		try{
			sendCopy(request, response, ids.get(0).getComponentUrl() + VanillaConstants.FREEMETRICS_MANAGER_URL);
		}catch(FileNotFoundException ex){
			throw new VanillaComponentDownException(ids.get(0), null);
		}
	}

	@Override
	public boolean needAuthentication() {
		return true;
	}

	@Override
	public void canBeRun(IRuntimeConfig conf) throws VanillaException,
			Exception {
		
		
	}

}
