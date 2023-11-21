/**
 * 
 */
package bpm.freemetrics.api.digester.engine;

import java.io.ByteArrayInputStream;

import org.apache.commons.digester3.Digester;

import bpm.freemetrics.api.digester.beans.FmActions;
import bpm.freemetrics.api.digester.beans.FmDigAction;
import bpm.freemetrics.api.digester.beans.FmDigActionConfig;
import bpm.freemetrics.api.utils.Tools;

/**
 * @author Belgarde
 *
 */
public class DigestFM_DB {

	public static FmActions digesterActionsDefinition(String acDefinition){

		FmActions actions = null;
		
		if(Tools.isValid(acDefinition)){
			try {

				Digester digester = new Digester();
				digester.setValidating( false );

				String root = "actions";
				String serverLocInfo = "action";


				digester.addObjectCreate( root, FmActions.class );	

				digester.addObjectCreate( root +"/"+serverLocInfo, FmDigAction.class );

				digester.addBeanPropertySetter( root +"/"+serverLocInfo+"/component", "component" );
				
				digester.addObjectCreate( root +"/"+serverLocInfo+"/config", FmDigActionConfig.class );

				digester.addBeanPropertySetter( root +"/"+serverLocInfo+"/config/to", "recipient" );
				digester.addBeanPropertySetter( root +"/"+serverLocInfo+"/config/from", "sender" );
				digester.addBeanPropertySetter( root +"/"+serverLocInfo+"/config/subject", "subject" );
				digester.addBeanPropertySetter( root +"/"+serverLocInfo+"/config/text", "text" );
				digester.addSetNext( root +"/"+serverLocInfo+"/config", "setConfig" );

				digester.addSetNext(root +"/"+serverLocInfo, "addFmDigAction" );
			
				String myString = ""+acDefinition;
				byte currentXMLBytes[] = myString.getBytes();
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(currentXMLBytes); 
				
				actions  = (FmActions)digester.parse(byteArrayInputStream);

			} catch( Exception exc ) {
				exc.printStackTrace();
			}
		}
		return actions ;
	}
}
