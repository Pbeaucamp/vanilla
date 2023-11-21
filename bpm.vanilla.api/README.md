# OSGI Spring Vanilla API

To produce bundle run `mvn clean install`. It will be available in `target/deploy`.
Or Run as -> maven install
To add bundle in vanilla_runtime in eclipse run
    
    install file:/PATH/bpm.vanilla.api/target/deploy/bpm.vanilla.api-0.0.1.jar
    
Example
		
		install file:/C:/Users/svi/.m2/repository/bpm/vanilla/bpm.vanilla.api/0.0.1/bpm.vanilla.api-0.0.1.jar
		install file:/C:/BPM/workspaces/workspace_vanilla/bpm.vanilla.api/target/deploy/bpm.vanilla.api-0.0.1.jar

		lb
		start BUNDLE_ID

To uninstall

    lb
    uninstall BUNDLE_ID
    
Documentation
> La documentation swagger se trouve sur http://localhost:9015/api/1.0/swagger-ui.html

> Sa version json se trouve sur : http://localhost:9015/api/1.0/v3/api-docs