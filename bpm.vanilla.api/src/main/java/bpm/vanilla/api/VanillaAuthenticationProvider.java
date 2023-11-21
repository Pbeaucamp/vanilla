package bpm.vanilla.api;

import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class VanillaAuthenticationProvider implements AuthenticationProvider {

	private IVanillaAPI vanillaApi;
	
	public VanillaAuthenticationProvider() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String root = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
	
		IVanillaContext vanillaCtx = new BaseVanillaContext(url, root, password);
		this.vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String login = authentication.getPrincipal().toString();
		String password = authentication.getCredentials().toString();

		User user = null;
		try {
			user = vanillaApi.getVanillaSecurityManager().authentify(null, login, password, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadCredentialsException(e.getMessage());
		}
        if (user == null) {
            throw new UsernameNotFoundException(login);
        }
        return new UsernamePasswordAuthenticationToken(login, password, new ArrayList<>());
	}

	@Override
	public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}