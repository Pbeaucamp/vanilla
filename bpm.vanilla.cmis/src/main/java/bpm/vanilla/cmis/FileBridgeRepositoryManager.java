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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.components.IGedComponent;

/**
 * Manages all repositories.
 */
public class FileBridgeRepositoryManager {

    private final Map<String, VanillaBridgeRepository> repositories;

    public FileBridgeRepositoryManager() {
        repositories = new HashMap<String, VanillaBridgeRepository>();
    }

    /**
     * Adds a repository object.
     */
    public void addRepository(VanillaBridgeRepository fsr) {
        if (fsr == null || fsr.getRepositoryId() == null) {
            return;
        }

        repositories.put(fsr.getRepositoryId(), fsr);
    }

    /**
     * Gets a repository object by id.
     */
    public VanillaBridgeRepository getRepository(String repositoryId, IVanillaAPI vanillaApi, IGedComponent gedComponent, User currentUser, Group currentGroup, Repository currentRepository) {
        VanillaBridgeRepository result = repositories.get(repositoryId);
        if (result == null) {
            throw new CmisObjectNotFoundException("Unknown repository '" + repositoryId + "'!");
        }
        
        result.setVanillaApi(vanillaApi, gedComponent, currentUser, currentGroup, currentRepository);

        return result;
    }

    /**
     * Returns all repository objects.
     */
    public Collection<VanillaBridgeRepository> getRepositories() {
        return repositories.values();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (VanillaBridgeRepository repository : repositories.values()) {
            sb.append('[');
            sb.append(repository.getRepositoryId());
            sb.append(" -> ");
            sb.append("Aklabox");
            sb.append(']');
        }

        return sb.toString();
    }
}
