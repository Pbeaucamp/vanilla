package bpm.vanilla.api;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

@Service
public class VanillaUserDetailsService implements UserDetailsService {
	
	private IVanillaAPI vanillaApi;
	
	public VanillaUserDetailsService() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String root = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
	
		IVanillaContext vanillaCtx = new BaseVanillaContext(url, root, password);
		this.vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
	}

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = null;
		try {
			user = vanillaApi.getVanillaSecurityManager().getUserByLogin(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserPrincipal(user);
    }
}