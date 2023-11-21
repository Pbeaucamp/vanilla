package bpm.vanillahub.runtime.specifics;

public class Launcher {

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			throw new Exception("First argument should be type (carif, onisep).");
		}
		
		String type = args[0];

		if (type.equalsIgnoreCase("carif")) {
			CarifJsonExploder.carif(args);
		}
		else if (type.equalsIgnoreCase("onisep")) {
			OnisepMetierXMLExploder.onisep(args);
		}
		else {
			throw new Exception("First argument should be type (carif, onisep).");
		}
	}

}
