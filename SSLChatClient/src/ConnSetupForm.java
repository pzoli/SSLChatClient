import java.awt.Button;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ConnSetupForm extends Dialog implements WindowListener, KeyListener, ActionListener {
	public Button btnOK;
	public Button btnCancel;
	public Button ModalResult;
	public TextField tfServerName;
	public TextField tfPort;
	public TextField tfLoginName;

	public void ModalReturn(Object res) {
		this.ModalResult = (Button) res;
		hide();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		ModalReturn(this.btnCancel);
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == 10) {
			ModalReturn(this.btnOK);
		} else if (e.getKeyCode() == 27) {
			ModalReturn(this.btnCancel);
		}
	}

	public void actionPerformed(ActionEvent ae) {
		ModalReturn(ae.getSource());
	}

	public void init() {
		setTitle("Connection setup");
		setSize(320, 240);
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		setFont(new Font("Helvetica", 0, 14));
		setLayout(gridbag);
		Insets lvIns = new Insets(5, 0, 5, 10);
		c.insets = lvIns;
		c.fill = 2;
		c.weightx = 1.0D;

		add(new Label("Server :"), c);
		c.gridwidth = 0;
		this.tfServerName = new TextField(15);
		this.tfServerName.addKeyListener(this);
		add(this.tfServerName, c);

		c.gridwidth = -1;
		add(new Label("Port :"), c);
		c.gridwidth = 0;
		this.tfPort = new TextField(6);
		this.tfPort.addKeyListener(this);
		add(this.tfPort, c);

		c.gridwidth = -1;
		add(new Label("Login name :"), c);
		c.gridwidth = 0;
		this.tfLoginName = new TextField(15);
		this.tfLoginName.addKeyListener(this);
		add(this.tfLoginName, c);

		c.fill = 0;
		lvIns.top = 20;
		lvIns.left = 10;
		lvIns.bottom = 10;
		lvIns.right = 10;

		c.gridwidth = -1;
		this.btnOK = new Button("OK");
		this.btnOK.addActionListener(this);
		this.btnOK.setSize(65, 45);
		add(this.btnOK, c);

		this.btnCancel = new Button("Cancel");
		this.btnCancel.addActionListener(this);
		this.btnCancel.setSize(65, 45);
		add(this.btnCancel, c);

		addWindowListener(this);
		setModal(true);
		validate();
	}

	public ConnSetupForm(Frame owner) {
		super(owner);
		init();
	}
}
