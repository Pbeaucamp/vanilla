package bpm.vanilla.platform.core.config;

public enum Customer {
	CLASSIC("Classic"),
	TSBN("TSBN"),
	VE("VE");
	
	private String name;
	
	private Customer(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public static Customer fromValue(String value) {
		if (value == null) {
    		return Customer.CLASSIC;
    	}

		for (Customer c : Customer.values()) {
			if (c.getName().equals(value)) {
				return c;
			}
		}
		return Customer.CLASSIC;
	}
}
