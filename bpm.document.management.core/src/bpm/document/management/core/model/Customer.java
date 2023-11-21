package bpm.document.management.core.model;

public enum Customer {
	CLASSIC("Classic"),
	SDIS25("SDIS25"),
	AO("AO");
	
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
