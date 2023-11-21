/*
 * Copyright 2014 Florian Müller & Jay Brown
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * This code is based on the Apache Chemistry OpenCMIS FileShare project
 * <http://chemistry.apache.org/java/developing/repositories/dev-repositories-fileshare.html>.
 *
 * It is part of a training exercise and not intended for production use!
 *
 */
package bpm.vanilla.cmis;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractServiceFactory;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.server.support.wrapper.CallContextAwareCmisService;
import org.apache.chemistry.opencmis.server.support.wrapper.CmisServiceWrapperManager;
import org.apache.chemistry.opencmis.server.support.wrapper.ConformanceCmisServiceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;

/**
 * FileShare Service Factory.
 */
public class FileBridgeCmisServiceFactory extends AbstractServiceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(FileBridgeCmisServiceFactory.class);

    /** Default maxItems value for getTypeChildren()}. */
    private static final BigInteger DEFAULT_MAX_ITEMS_TYPES = BigInteger.valueOf(50);

    /** Default depth value for getTypeDescendants(). */
    private static final BigInteger DEFAULT_DEPTH_TYPES = BigInteger.valueOf(-1);

    /**
     * Default maxItems value for getChildren() and other methods returning
     * lists of objects.
     */
    private static final BigInteger DEFAULT_MAX_ITEMS_OBJECTS = BigInteger.valueOf(200);

    /** Default depth value for getDescendants(). */
    private static final BigInteger DEFAULT_DEPTH_OBJECTS = BigInteger.valueOf(10);

    /** Each thread gets its own {@link FileBridgeCmisService} instance. */
    private ThreadLocal<CallContextAwareCmisService> threadLocalService = new ThreadLocal<CallContextAwareCmisService>();
    // new wrapperManager
    private CmisServiceWrapperManager wrapperManager;

    private FileBridgeRepositoryManager repositoryManager;
    private FileBridgeTypeManager typeManager;

    @Override
    public void init(Map<String, String> parameters) {
        LOG.debug("[FileBridgeCmisServiceFactory] init");

        // New for Chameleon **
        wrapperManager = new CmisServiceWrapperManager();
        wrapperManager.addWrappersFromServiceFactoryParameters(parameters);
        wrapperManager.addOuterWrapper(ConformanceCmisServiceWrapper.class, DEFAULT_MAX_ITEMS_TYPES,
                DEFAULT_DEPTH_TYPES, DEFAULT_MAX_ITEMS_OBJECTS, DEFAULT_DEPTH_OBJECTS);

        repositoryManager = new FileBridgeRepositoryManager();
        typeManager = new FileBridgeTypeManager();

        VanillaBridgeRepository fsr = new VanillaBridgeRepository("VanillaRepositoryId", typeManager);
        repositoryManager.addRepository(fsr);
    }

    @Override
    public void destroy() {
        threadLocalService = null;
    }

    @Override
    public CmisService getService(CallContext context) {
        // authenticate the user
        // if the authentication fails, authenticate() throws a
        // CmisPermissionDeniedException

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String runtimeUrl = config.getVanillaServerUrl();
		int groupId = Integer.parseInt(config.getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID));
		int repositoryId = Integer.parseInt(config.getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID));
		
		String username = context.getUsername();
		String password = context.getPassword();
		
		IVanillaContext vanillaCtx = new BaseVanillaContext(runtimeUrl, username, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
		
    	User user = null;
    	Group group = null;
    	Repository repository = null;
		try {
			user = vanillaApi.getVanillaSecurityManager().authentify("", context.getUsername(), context.getPassword(), false);
			group = vanillaApi.getVanillaSecurityManager().getGroupById(groupId);
			repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(repositoryId);
		} catch (Exception e) {
			e.printStackTrace();
            throw new CmisPermissionDeniedException("Invalid username or password.");
		}
		
		IGedComponent gedComponent = new RemoteGedComponent(vanillaCtx);

        // get service object for this thread
        CallContextAwareCmisService service = threadLocalService.get();
        if (service == null) {
            FileBridgeCmisService fileShareService = new FileBridgeCmisService(repositoryManager);
            // wrap it with the chain of wrappers
            service = (CallContextAwareCmisService) wrapperManager.wrap(fileShareService);
            threadLocalService.set(service);
        }

        // Stash any object into the call context and then pass it to our
        // service so that it can be shared with any extensions.
        // Here is where you would put in a reference to a native API object if
        // needed.
        FileBridgeCallContext fileBridgeCallContext = new FileBridgeCallContext(context, vanillaApi, gedComponent, user, group, repository);
        fileBridgeCallContext.setRequestTimestamp(new GregorianCalendar());

        service.setCallContext(fileBridgeCallContext);

        return service;
    }
}
