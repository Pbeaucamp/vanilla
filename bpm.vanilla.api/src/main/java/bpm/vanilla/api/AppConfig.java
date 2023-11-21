package bpm.vanilla.api;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import bpm.vanilla.api.controller.commons.ControllerConfig;

@Configuration
@Import({ControllerConfig.class,WebSecurityConfig.class,VanillaRegisterModuleConfig.class,VanillaUserDetailsService.class,VanillaAuthenticationProvider.class})
public class AppConfig {

}
