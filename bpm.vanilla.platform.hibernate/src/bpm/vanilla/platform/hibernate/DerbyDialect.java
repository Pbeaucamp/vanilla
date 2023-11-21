package bpm.vanilla.platform.hibernate;

import java.sql.Types;

import org.hibernate.dialect.DerbyTenSevenDialect;

public class DerbyDialect extends DerbyTenSevenDialect {
    
	public DerbyDialect() {
        super();
        registerColumnType(Types.BOOLEAN, "INTEGER");
    }

	@Override
	public String toBooleanValueString(boolean bool) {
	    return bool ? "1" : "0";
	}
}