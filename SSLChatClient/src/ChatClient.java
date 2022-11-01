import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.text.Collator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ChatClient extends Frame implements WindowListener, KeyListener, ActionListener, IMsg {
	private static final long serialVersionUID = -3362221621304206137L;
	protected Collator coll;
	protected TextField ed;
	protected TextArea disp;
	protected List users;
	protected ConnSetupForm frmSetup = null;
	protected ChatClient Client;
	protected String host;
	protected String port;
	protected String name;
	protected String trustJks;
	protected String clientJks;
	protected String trustPwd;
	protected String clientPwd;
	protected boolean isSecureConnection;
	protected MenuItem miConnect;
	protected MenuItem miDisconnect;
	protected MenuItem miConnSetup;
	protected MenuItem miShutdownServer;
	protected MenuItem miExit;
	protected SocketHandler sh;

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		if (this.sh != null)
			Disconnect();
		System.exit(0);
	}
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == 10 && e.getSource().equals(this.ed)) {
			try {
				this.sh.sendMsg(this.ed.getText());
			} catch (Throwable ex) {
				DisplayMsg("ERROR while sending message:" + ex);
			}
			this.ed.setText("");
		} else if (e.getKeyCode() == 9) {
			//((Component) e.getSource()).transferFocus();
			e.getComponent().transferFocus();
		}
	}

	public void actionPerformed(ActionEvent ae) {
		Object s = ae.getSource();
		if (s.equals(this.miExit)) {
			if (this.sh != null) {
				Disconnect();
			}
			System.exit(0);
		} else if (s.equals(this.miConnect)) {
			try {
				Connect();
				this.users.removeAll();
			} catch (Throwable e) {
				DisplayMsg(e.toString());
			}
		} else if (s.equals(this.miDisconnect)) {
			Disconnect();
		} else if (s.equals(this.miConnSetup)) {
			if (this.frmSetup == null)
				this.frmSetup = new ConnSetupForm(this);
			Point p = getLocation();
			Dimension d = getSize();
			p.x = p.x + d.width / 2 - (this.frmSetup.getSize()).width / 2;
			p.y = p.y + d.height / 2 - (this.frmSetup.getSize()).height / 2;
			this.frmSetup.setLocation(p);
			this.frmSetup.tfServerName.setText(this.host);
			this.frmSetup.tfPort.setText(this.port);
			this.frmSetup.tfLoginName.setText(this.name);
			this.frmSetup.setVisible(true);
			if (this.frmSetup.ModalResult == this.frmSetup.btnOK) {
				this.host = this.frmSetup.tfServerName.getText();
				this.port = this.frmSetup.tfPort.getText();
				this.name = this.frmSetup.tfLoginName.getText();
			}
		} else if (s.equals(this.miShutdownServer)) {
			shutdownServer();
		}
	}

	protected void DisplayMsg(String Msg) {
		this.disp.append(Msg);
	}

	public void NewUser(String name) {
		int i = 0;
		for (; i < this.users.getItemCount() && this.coll.compare(name, this.users.getItem(i)) >= 0; i++)
			;

		this.users.add(name, i);
	}

	public boolean ProcessMsg(Object sender, String msg) {
		boolean result = true;
		if (msg.startsWith("[COMMON]")) {
			DisplayMsg(msg.substring(8, msg.length()));
		} else if (msg.startsWith("[USERS+]")) {
			NewUser(msg.substring(8, msg.length() - 1));
		} else if (msg.startsWith("[USERS-]")) {
			try {
				this.users.remove(msg.substring(8, msg.length() - 1));
			} catch (Exception e) {
				DisplayMsg(e.toString());
			}
		} else if (msg.startsWith("[DC]")) {
			Disconnect();
			Logout(sender);
		}
		return result;
	}

	public boolean Dispatch(Object Sender, String Msg) {
		return ProcessMsg(Sender, Msg);
	}

	public boolean Login(Object Sender) {
		setTitle("Connected as " + this.name);
		this.miConnect.setEnabled(false);
		this.miDisconnect.setEnabled(true);
		this.miConnSetup.setEnabled(false);
		InetAddress iAddress = this.sh.cs.getInetAddress();
		if (iAddress.isAnyLocalAddress() || iAddress.isLinkLocalAddress() || iAddress.isLoopbackAddress()) {
			this.miShutdownServer.setEnabled(true);
		}
		return true;
	}

	public boolean Logout(Object Sender) {
		boolean result = true;
		this.miConnect.setEnabled(true);
		this.miDisconnect.setEnabled(false);
		this.miConnSetup.setEnabled(true);
		this.miShutdownServer.setEnabled(false);
		this.ed.setEnabled(false);
		setTitle("ChatClient");
		return result;
	}

	public void Connect() throws Throwable {
		if (this.name != null && !this.name.matches("^$") && this.host != null && this.host.matches("^\\w+$")
				&& this.port != null && this.port.matches("^\\d+$")) {
			if (this.isSecureConnection) {
				this.sh = new SSLSocketHandler(this);
				((SSLSocketHandler) this.sh).setClientJks(this.clientJks);
				((SSLSocketHandler) this.sh).setTrustJks(this.trustJks);
				this.sh.setSrv(this.host);
				this.sh.setPrt(this.port);
				this.sh.setLoginName(this.name);
				((SSLSocketHandler) this.sh).createSocket(this.trustPwd, this.clientPwd);
			} else {
				this.sh = new SimpleSocketHandler(this);
				this.sh.setSrv(this.host);
				this.sh.setPrt(this.port);
				this.sh.setLoginName(this.name);
				((SimpleSocketHandler) this.sh).createSocket();
			}
			this.sh.start();
			this.ed.setEnabled(true);
			this.ed.requestFocus();
		} else {
			DisplayMsg("Please fill connection information!\n");
		}
	}

	public void Disconnect() {
		this.ed.setEnabled(false);
		this.sh.sendMsg("DC");
		try {
			this.sh.join();
		} catch (Exception ex) {
			System.out.println("Can't stop thread." + ex);
		}
	}

	public void shutdownServer() {
		this.ed.setEnabled(false);
		this.sh.sendMsg("[DC-ALL]");
	}

	public void initFrame() {
		setTitle("ChatClient");
		MenuBar mb = new MenuBar();
		Menu m = new Menu("Communication");
		m.setFont(new Font("Times New Roman", 0, 16));

		this.miConnect = new MenuItem("Connect");
		this.miConnect.setFont(new Font("Times New Roman", 0, 16));
		this.miConnect.addActionListener(this);
		m.add(this.miConnect);

		this.miDisconnect = new MenuItem("Disconnect");
		this.miDisconnect.setFont(new Font("Times New Roman", 0, 16));
		this.miDisconnect.addActionListener(this);
		this.miDisconnect.setEnabled(false);
		m.add(this.miDisconnect);

		this.miConnSetup = new MenuItem("Setup");
		this.miConnSetup.setFont(new Font("Times New Roman", 0, 16));
		this.miConnSetup.addActionListener(this);
		m.add(this.miConnSetup);

		this.miShutdownServer = new MenuItem("Server shutdown");
		this.miShutdownServer.setFont(new Font("Times New Roman", 0, 16));
		this.miShutdownServer.addActionListener(this);
		this.miShutdownServer.setEnabled(false);
		m.add(this.miShutdownServer);

		m.addSeparator();

		this.miExit = new MenuItem("Exit");
		this.miExit.setFont(new Font("Times New Roman", 0, 16));
		this.miExit.addActionListener(this);
		m.add(this.miExit);

		mb.add(m);
		setMenuBar(mb);
		addWindowListener(this);
		setSize(640, 480);
		validate();
	}

	public ChatClient() {
		this.coll = Collator.getInstance();
		setLayout(new BorderLayout());
		this.ed = new TextField(40);
		this.ed.setFont(new Font("Times New Roman", 0, 16));
		this.ed.addKeyListener(this);
		this.ed.setEnabled(false);
		this.disp = new TextArea();
		this.disp.setEditable(false);
		this.disp.setFont(new Font("Times New Roman", 0, 16));
		this.disp.addKeyListener(this);
		this.users = new List();
		this.users.setFont(new Font("Times New Roman", 0, 16));
		add("Center", this.disp);
		add("South", this.ed);
		add("East", this.users);
	}

	public static void main(String[] args) {
		ChatClient client = new ChatClient();
		client.setLocation(new Point(100, 100));
		try {
			GnuParser gnuParser = new GnuParser();
			Options options = getCommandLineOptions();
			CommandLine commandLine = null;
			try {
				commandLine = gnuParser.parse(options, args);
				client.host = commandLine.getOptionValue("host");
				client.port = commandLine.getOptionValue("port");
				client.name = commandLine.getOptionValue("name");
				client.trustJks = commandLine.getOptionValue("trustjks");
				client.clientJks = commandLine.getOptionValue("clientjks");
				client.trustPwd = commandLine.getOptionValue("trustpwd");
				client.clientPwd = commandLine.getOptionValue("clientpwd");
				client.isSecureConnection = (client.trustJks != null && client.clientJks != null);
			} catch (ParseException e) {
				printHelp(options);
				System.exit(-1);
			}

			client.initFrame();
			client.setVisible(true);
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("chatclient", options);
	}
	private static Options getCommandLineOptions() {
		Options options = new Options();
		OptionBuilder.isRequired(true);
		OptionBuilder.withArgName("host");
		OptionBuilder.withLongOpt("host");
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Chat server host (IP or name)");
		options.addOption(OptionBuilder.create());
		OptionBuilder.isRequired(true);
		OptionBuilder.withArgName("port");
		OptionBuilder.withLongOpt("port");
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Listening port number");
		options.addOption(OptionBuilder.create());
		OptionBuilder.isRequired(false);
		OptionBuilder.withArgName("name");
		OptionBuilder.withLongOpt("name");
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Default client name");
		options.addOption(OptionBuilder.create());
		OptionBuilder.isRequired(false);
		OptionBuilder.withArgName("trustjks");
		OptionBuilder.withLongOpt("trustjks");
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("JavaKeyStore location where Server public key stored.");
		options.addOption(OptionBuilder.create());
		OptionBuilder.isRequired(false);
		OptionBuilder.withArgName("clientjks");
		OptionBuilder.withLongOpt("clientjks");
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("JavaKeyStore location where client public and private key stored.");
		options.addOption(OptionBuilder.create());
		OptionBuilder.isRequired(false);
		OptionBuilder.withArgName("trustpwd");
		OptionBuilder.withLongOpt("trustpwd");
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("JavaKeyStore password for trust (Server) key stored.");
		options.addOption(OptionBuilder.create());
		OptionBuilder.isRequired(false);
		OptionBuilder.withArgName("clientpwd");
		OptionBuilder.withLongOpt("clientpwd");
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("JavaKeyStore password for client key stored.");
		options.addOption(OptionBuilder.create());

		return options;
	}
}
