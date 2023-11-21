package groupviewer.figure;

import groupviewer.Messages;

import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;

public class UserDetailFigure extends Panel {
	// Data Label
	private Label labLogin;
	private Label labFonction;
	private Label labPhone;
	private Label labCellular;
	private Label labSkypeName;
	private Label labSkypePhone;
	private Label labBusinessMail;
	private Label labPrivateMail;
	private Label labCreationDate;
	// User Data
	private Label userLogin;
	private Label userFonction;
	private Label userPhone;
	private Label userCellular;
	private Label userSkypeName;
	private Label userSkypePhone;
	private Label userBusinessMail;
	private Label userPrivateMail;
	private Label userCreationDate;

	public UserDetailFigure() {
		initLabels();
		initLayout();
		addLabels();
	}

	private void initLabels() {
		labLogin = new Label(Messages.UserDetailFigure_0);
		labFonction = new Label(Messages.UserDetailFigure_1);
		labPhone = new Label(Messages.UserDetailFigure_2);
		labCellular = new Label(Messages.UserDetailFigure_3);
		labSkypeName = new Label(Messages.UserDetailFigure_4);
		labSkypePhone = new Label(Messages.UserDetailFigure_5);
		labBusinessMail = new Label(Messages.UserDetailFigure_6);
		labPrivateMail = new Label(Messages.UserDetailFigure_7);
		labCreationDate = new Label(Messages.UserDetailFigure_8);

		userLogin = new Label();
		userFonction = new Label();
		userPhone = new Label();
		userCellular = new Label();
		userSkypeName = new Label();
		userSkypePhone = new Label();
		userBusinessMail = new Label();
		userPrivateMail = new Label();
		userCreationDate = new Label();
	}

	private void initLayout() {
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		setLayoutManager(layout);
	}

	private void addLabels() {
		add(labLogin);
		add(userLogin);

		add(labFonction);
		add(userFonction);

		add(labPhone);
		add(userPhone);

		add(labCellular);
		add(userCellular);

		add(labSkypeName);
		add(userSkypeName);

		add(labSkypePhone);
		add(userSkypePhone);

		add(labBusinessMail);
		add(userBusinessMail);

		add(labPrivateMail);
		add(userPrivateMail);

		add(labCreationDate);
		add(userCreationDate);
	}

	/*
	 * Setters
	 */
	public void setUserLogin(String userLogin) {
		this.userLogin.setText(userLogin);
	}

	public void setUserFonction(String userFonction) {
		this.userFonction.setText(userFonction);
	}

	public void setUserPhone(String userPhone) {
		this.userPhone.setText(userPhone);
	}

	public void setUserCellular(String userCellular) {
		this.userCellular.setText(userCellular);
	}

	public void setUserSkypeName(String userSkypeName) {
		this.userSkypeName.setText(userSkypeName);
	}

	public void setUserSkypePhone(String userSkypePhone) {
		this.userSkypePhone.setText(userSkypePhone);
	}

	public void setUserBusinessMail(String userBusinessMail) {
		this.userBusinessMail.setText(userBusinessMail);
	}

	public void setUserPrivateMail(String userPrivateMail) {
		this.userPrivateMail.setText(userPrivateMail);
	}

	public void setUserCreationDate(String userCreationDate) {
		this.userCreationDate.setText(userCreationDate);
	}
}
