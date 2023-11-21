package groupviewer.figure;

import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import bpm.vanilla.platform.core.beans.User;

public class UserFigure extends Figure implements MouseListener {

	private Clickable title;
	private Label titrelabel;
	private UserDetailFigure detail;
	private User userData;
	private Image userIco;
	/**
	 * Create a user's figure with a define User data.
	 * @param user
	 */
	public UserFigure(User user) {
		super();
		userData = user;
		initDetailPan();
		initTitle();
		setLayoutManager(new GridLayout());
		add(title, new GridData(GridData.FILL_HORIZONTAL, GridData.BEGINNING,
				true, false));
		//add(detail);
		title.addMouseListener(this);
	}
	/**
	 * Set the Figure's title.
	 * add icon.
	 * set title.
	 * add double click listener.
	 */
	private void initTitle() {
		ImageData userIcoData = new ImageData(getClass().getResourceAsStream(
				"/icons/user.png")); //$NON-NLS-1$
		userIco = new Image(null, userIcoData);
		titrelabel = new Label(makeTitle());
		titrelabel.setIcon(userIco);
		title = new Clickable(titrelabel);
		title.setBackgroundColor(ColorConstants.titleGradient);
		title.setOpaque(true);
	}
	/**
	 * Create the user title 
	 * ex : Username (id)
	 * @return
	 */
	private String makeTitle() {
		return getUserData().getName().toUpperCase() + " - " //$NON-NLS-1$
				+ getUserData().getSurname() + " ( ID = " //$NON-NLS-1$
				+ getUserData().getId() + ")"; //$NON-NLS-1$
	}
	/**
	 * Set up the user detail panel
	 * witch displays user informations.
	 */
	private void initDetailPan() {
		detail = new UserDetailFigure();
		User usrData = getUserData();
		detail.setUserBusinessMail(usrData.getBusinessMail());
		detail.setUserCellular(usrData.getCellular());
		detail.setUserCreationDate(usrData.getCreation().toString());
		detail.setUserFonction(usrData.getFunction());
		detail.setUserLogin(usrData.getLogin());
		detail.setUserPhone(usrData.getTelephone());
		detail.setUserPrivateMail(usrData.getPrivateMail());
		detail.setUserSkypeName(usrData.getSkypeName());
		detail.setUserSkypePhone(usrData.getSkypeNumber());
	}
	/**
	 * Get the user's data
	 * @return - the data.
	 */
	private User getUserData() {
		return this.userData;
	}
	/**
	 * If the user detail panel is showed than his remove
	 * otherwise is added.
	 */
	private void showDetail() {
		if (getChildren().contains(detail))
			remove(detail);
		else
			add(detail);
	}
	/**
	 * Handle double click on the figure's title.
	 */
	public void mouseDoubleClicked(MouseEvent me) {
		if (me.getSource() == title) {
			showDetail();
		}
	}
	public void mousePressed(MouseEvent me) {
	}
	public void mouseReleased(MouseEvent me) {
	}
	
}
