package metadata.client.helper;

import java.util.ArrayList;
import java.util.List;

import metadataclient.Activator;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class GroupHelper {

	public static List<String> getGroups(int begin, int step){
			
		
		List<String> res = new ArrayList<String>();
		try{
			IVanillaAPI api = new RemoteVanillaPlatform(Activator.getDefault().getVanillaContext());
			List<Group> grp = api.getVanillaSecurityManager().getGroups();
			for (Group g : grp) {
				res.add(g.getName());
			}
		}catch(Exception e){
		}
		
		
		return res;

	}

	public static List<User> getUsers(int begin, int step){
		try{
			IVanillaAPI api = new RemoteVanillaPlatform(Activator.getDefault().getVanillaContext());
			return api.getVanillaSecurityManager().getUsers();
		}catch(Exception e){
			return new ArrayList<>();
		}
	}
}
