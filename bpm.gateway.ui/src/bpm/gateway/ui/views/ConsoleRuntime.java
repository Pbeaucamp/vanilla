package bpm.gateway.ui.views;

import java.io.OutputStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleRuntime extends MessageConsole{
	private MessageConsoleStream inMessageStream;
	
	public ConsoleRuntime(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor);
		
		inMessageStream = newMessageStream();
	}
	
	public OutputStream getOutputStream(){
		
//		/
		return inMessageStream;
	}

	



	
	
}
