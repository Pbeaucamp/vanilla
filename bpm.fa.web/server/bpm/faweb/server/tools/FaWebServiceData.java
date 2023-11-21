/*
 * Copyright 2007 BPM-conseil.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package bpm.faweb.server.tools;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.server.security.FaWebSession;
import bpm.faweb.shared.IDirectoryDTO;
import bpm.faweb.shared.IDirectoryItemDTO;
import bpm.faweb.shared.ItemCube;
import bpm.faweb.shared.ItemCubeView;
import bpm.faweb.shared.TreeParentDTO;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

/**
 * @author A Belgarde
 */
public class FaWebServiceData {

	public static TreeParentDTO getContentsByType(FaWebSession session, int keySession, IRepository repository, boolean loadCubes) {

		TreeParentDTO res = new TreeParentDTO();

		List<RepositoryDirectory> list = new ArrayList<RepositoryDirectory>();

		try {
			list = repository.getRootDirectories();
			for (RepositoryDirectory d : list) {

				IDirectoryDTO dir = new IDirectoryDTO(d.getName());
				dir.setId(d.getId());
				res.addChild(dir);

				buildChildsByType(session, keySession, dir, d, repository, loadCubes);

				for (RepositoryItem di : repository.getItems(d)) {
					IDirectoryItemDTO item = buildItem(session, keySession, dir, di, loadCubes);
					if (item != null) {
						dir.addChild(item);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;

	}

	private static void buildChildsByType(FaWebSession session, int keySession, IDirectoryDTO parentTree, RepositoryDirectory parent, IRepository repository, boolean loadCubes) {

		List<RepositoryDirectory> dirs = null;
		try {
			dirs = repository.getChildDirectories(parent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (RepositoryDirectory d : dirs) {
			IDirectoryDTO td = new IDirectoryDTO(d.getName());
			td.setId(d.getId());
			parentTree.addChild(td);

			List<RepositoryItem> items = null;
			try {
				items = repository.getItems(d);
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (RepositoryItem di : items) {
				IDirectoryItemDTO item = buildItem(session, keySession, td, di, loadCubes);
				if (item != null) {
					td.addChild(item);
				}
			}
			buildChildsByType(session, keySession, td, d, repository, loadCubes);
		}
	}

	private static IDirectoryItemDTO buildItem(FaWebSession session, int keySession, IDirectoryDTO parent, RepositoryItem item, boolean loadCubes) {
		try {
			IDirectoryItemDTO ti = new IDirectoryItemDTO(item.getItemName());
			ti.setType(IRepositoryApi.TYPES_NAMES[item.getType()]);
			ti.setId(item.getId());
			ti.setParent(parent);

			if (item.getType() == IRepositoryApi.FASD_TYPE && loadCubes) {
				List<String> listCubeNames = FaWebActions.loadModel(keySession, session.getCurrentGroup(), item.getId(), session, session.getLocation(), session.getInfosOlap(keySession));
				buildCubes(item, ti, listCubeNames, session.getRepositoryConnection());
			}
			return ti;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void buildCubes(RepositoryItem item, IDirectoryItemDTO itemFasd, List<String> cubes, IRepositoryApi sock) {
		for (String name : cubes) {
			try {
				List<RepositoryItem> views = sock.getRepositoryService().getCubeViews(name, item);

				ItemCube cube = new ItemCube(name);
				for (RepositoryItem view : views) {
					cube.addView(new ItemCubeView(view.getName()));
				}

				itemFasd.addCube(cube);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}