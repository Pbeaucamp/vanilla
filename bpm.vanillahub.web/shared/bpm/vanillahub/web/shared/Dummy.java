package bpm.vanillahub.web.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.RulePatternComparison;

/**
 * For serialization purpose. Needed by GWT. Don't touch this.
 * You need to add a variable of each type that need to be add the serialization whitelist policy of GWT
 *
 */
public class Dummy implements IsSerializable {
	
    private ClassRule classRule;
    private RulePatternComparison rulePatternComparison;
    
    public Dummy() {
	}
    
    public ClassRule getClassRule() {
		return classRule;
	}
    
    public RulePatternComparison getRulePatternComparison() {
		return rulePatternComparison;
	}
}