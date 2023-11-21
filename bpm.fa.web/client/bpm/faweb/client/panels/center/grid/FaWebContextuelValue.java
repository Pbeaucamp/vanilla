package bpm.faweb.client.panels.center.grid;

import java.util.HashMap;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public class FaWebContextuelValue extends PopupPanel {
	private final MenuBar menu = new MenuBar(true);

	private HashMap<String, String> urls;
	
	private MenuItem separator1 = new MenuItem("<HR>", true,(Command)null);
	private MenuItem separator2 = new MenuItem("<HR>", true,(Command)null);
	private MenuItem separator3 = new MenuItem("<HR>", true,(Command)null);

	public FaWebContextuelValue(HashMap<String, String> r){
		super(true);
		this.urls = r;
		
		menu.addStyleName("gwt-MenuBar");
		separator1.addStyleName("separator");
		separator2.addStyleName("separator");
		separator3.addStyleName("separator");
		
		for (String url : urls.keySet()) {
			FaWebCommand c = new FaWebCommand(urls.get(url));
			menu.addItem(url, c);
		}
		
		this.add(menu);
	}


	/**This method aim to open a new page with the given url as target
	 * @param url the target 
	 */
	public static native void doRedirect(String url)/*-{
		$wnd.open(url);
	}-*/;
	

	public class FaWebCommand implements Command {
		private String url;
		
		public FaWebCommand(String u) {
			this.url = u;
		}

		public void execute() {
			doRedirect(url);
		}
		
	}

}

