package bpm.vanilla.api.controller.commons;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bpm.vanilla.api.controller.FWRController;
import bpm.vanilla.api.controller.GroupController;
import bpm.vanilla.api.controller.KPIController;
import bpm.vanilla.api.controller.MetadataController;
import bpm.vanilla.api.controller.OLAPController;
import bpm.vanilla.api.controller.RepositoriesController;
import bpm.vanilla.api.controller.RepositoryController;
import bpm.vanilla.api.controller.UserController;
import bpm.vanilla.api.controller.VanillaAPIController;
import bpm.vanilla.api.controller.ViewerController;
import bpm.vanilla.api.exception.VanillaApiAdvice;

@Configuration
public class ControllerConfig {

    @Bean 
    UserController userController() {
    	return new UserController();
    }
    
    @Bean 
    GroupController groupController(){
    	return new GroupController();
    }
    
    @Bean
    RepositoryController repositoryController() {
    	return new RepositoryController();
    }
    
    @Bean 
    VanillaApiAdvice vanillaApiAdviceController() {
    	return new VanillaApiAdvice();
    }
    
    
    @Bean
    KPIController kpiController() {
    	return new KPIController();
    }
    
    @Bean
    MetadataController metadataController() {
    	return new MetadataController();
    }
    
    @Bean
    RepositoriesController repositoriesController() {
    	return new RepositoriesController();
    }
    
    @Bean
    ViewerController viewerController() {
    	return new ViewerController();
    }
    
    @Bean
    OLAPController olapController() {
    	return new OLAPController();
    }
    
    @Bean
    FWRController fwrController() {
    	return new FWRController();
    }
    
    @Bean
    VanillaAPIController vanillaApiController() {
    	return new VanillaAPIController();
    }

}
