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

import java.util.GregorianCalendar;

import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.server.support.CallContextWrapper;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.components.IGedComponent;

/**
 * A sample implementation of a CallContext wrapper that stores the request
 * timestamp as an additional value in the call context.
 */
public class FileBridgeCallContext extends CallContextWrapper {

    public final static String REQUEST_TIMESTAMP_KEY = "request.timestamp";
    private GregorianCalendar requestTimestamp;
    
    private IVanillaAPI vanillaApi;
    private IGedComponent gedComponent;
    private User currentUser;
	private Group currentGroup;
	private Repository currentRepository;

    public FileBridgeCallContext(CallContext context, IVanillaAPI vanillaApi, IGedComponent gedComponent, User currentUser, Group currentGroup, Repository currentRepository) {
        super(context);
        this.vanillaApi = vanillaApi;
        this.gedComponent = gedComponent;
        this.currentUser = currentUser;
        this.currentGroup = currentGroup;
        this.currentRepository = currentRepository;
    }

    @Override
    public Object get(String key) {
        // the timestamp can also be accessed via a key
        // that allows accessing this value even if a chain of wrappers is used
        if (REQUEST_TIMESTAMP_KEY.equals(key)) {
            return requestTimestamp;
        }

        return super.get(key);
    }

    /**
     * Convenience method to set the request timestamp.
     * 
     * @param requestTimestamp
     *            the timestamp
     */
    public void setRequestTimestamp(GregorianCalendar requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    /**
     * Convenience method to get the request timestamp.
     * 
     * @return the timestamp
     */
    public GregorianCalendar getRequestTimestamp() {
        return requestTimestamp;
    }
    
    public IVanillaAPI getVanillaApi() {
		return vanillaApi;
	}
    
    public IGedComponent getGedComponent() {
		return gedComponent;
	}

    public User getCurrentUser() {
		return currentUser;
	}
    
    public Group getCurrentGroup() {
		return currentGroup;
	}
    
    public Repository getCurrentRepository() {
		return currentRepository;
	}
}
