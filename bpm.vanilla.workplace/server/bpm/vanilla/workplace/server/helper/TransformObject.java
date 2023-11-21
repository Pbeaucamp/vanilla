package bpm.vanilla.workplace.server.helper;

import bpm.vanilla.workplace.core.IPackage;
import bpm.vanilla.workplace.core.IPackageType;
import bpm.vanilla.workplace.core.IProject;
import bpm.vanilla.workplace.core.IUser;
import bpm.vanilla.workplace.core.model.PlacePackage;
import bpm.vanilla.workplace.core.model.PlaceProject;
import bpm.vanilla.workplace.core.model.PlaceUser;
import bpm.vanilla.workplace.shared.model.PlaceWebPackage;
import bpm.vanilla.workplace.shared.model.PlaceWebProject;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;

public class TransformObject {

	public static IUser transformToCoreUser(PlaceWebUser user){
		return new PlaceUser(user.getId(), user.getName(), user.getPassword(), 
				user.getMail(), user.getIsAdmin(), user.getCreationDate());
	}

	public static IProject transformToCoreProject(PlaceWebProject project, IUser creator){
		return new PlaceProject(project.getId(), project.getName(), project.getVersion(), 
				creator, project.getCreationDate());
	}

	public static IPackage transformToCorePackage(PlaceWebPackage pack, IUser creator){
		return new PlacePackage(pack.getId(), pack.getName(), pack.getVersion(), 
				pack.getVanillaVersion(), IPackageType.values()[pack.getType()], creator, 
				pack.getCreationDate(), pack.getProjectId(), pack.getPath(), 
				pack.getValid(), pack.getCertified(), pack.getDocumentationUrl(), 
				pack.getSiteWebUrl(), pack.getPrerequisUrl());
	}
	
	public static PlaceWebUser transformToWebUser(IUser user){
		return new PlaceWebUser(user.getId(), user.getName(), user.getPassword(), 
				user.getMail(), user.getIsAdmin(), user.getCreationDate());
	}
	
	public static PlaceWebProject transformToWebProject(IProject project){
		return new PlaceWebProject(project.getName(), project.getVersion(), project.getCreator().getId(), 
				project.getCreationDate());
	}
	
	public static PlaceWebPackage transformToWebPackage(IPackage pack){
		return new PlaceWebPackage(pack.getName(), pack.getVersion(), pack.getVanillaVersion(), 
				pack.getProjectId(), pack.getCreator().getId(), pack.getType().getType(), 
				pack.getCreationDate(), pack.getPath(), pack.isValid(), pack.isCertified(),
				pack.getDocumentationUrl(), pack.getSiteWebUrl(), pack.getPrerequisUrl());
	}
}
