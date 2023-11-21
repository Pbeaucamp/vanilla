package bpm.update.manager.client.utils;

public class ExceptionManager {

	public static void handleException(Throwable caught, String message) {
		caught.printStackTrace();
		
		//TODO: Message
//		MessageHelper.openMessageError(message, caught);
	}
	
	public static void handleException(Throwable caught) {
		caught.printStackTrace();

		//TODO: Message
//		MessageHelper.openMessageError(caught.getMessage(), caught);
	}
}
