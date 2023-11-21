package bpm.fmloader.client.images;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

public interface Images extends ImageBundle {
	
	@Resource("bpm/fmloader/client/images/apply.png")
	public AbstractImagePrototype apply();
	
	@Resource("bpm/fmloader/client/images/next.png")
	public AbstractImagePrototype next();
	
	@Resource("bpm/fmloader/client/images/back.png")
	public AbstractImagePrototype back();
	
	@Resource("bpm/fmloader/client/images/cancel.png")
	public AbstractImagePrototype cancel();

	@Resource("bpm/fmloader/client/images/save.png")
	public AbstractImagePrototype save();
	
	@Resource("bpm/fmloader/client/images/gomme.png")
	public AbstractImagePrototype gomme();
	
	@Resource("bpm/fmloader/client/images/refresh.png")
	public AbstractImagePrototype refresh();
}
