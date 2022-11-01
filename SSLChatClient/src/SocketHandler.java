import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketHandler
  extends Thread
{
  protected String login_name;
  protected String Srv;
  protected String Prt;
  protected String received;
  protected BufferedReader input;
  protected PrintWriter output;
  protected Socket cs;
  protected IMsg Owner;
  
  public String getLogin_name() {
    return this.login_name;
  }
  
  public void setLoginName(String login_name) {
    this.login_name = login_name;
  }
  
  public String getSrv() {
    return this.Srv;
  }
  
  public void setSrv(String srv) {
    this.Srv = srv;
  }
  
  public String getPrt() {
    return this.Prt;
  }
  
  public void setPrt(String prt) {
    this.Prt = prt;
  }
  
  public String sendMsg(String Msg) {
    try {
      this.Owner.Dispatch(this, "[COMMON]<" + getName() + "> " + Msg + "\n");
      this.output.println(Msg);
      this.output.flush();
    } catch (Exception e) {
      this.Owner.Dispatch(this, "[COMMON]" + e.toString() + "\n");
    } 
    
    return Msg;
  }
  
  protected void OpenSocket() throws Exception {
    this.Owner.Dispatch(this, "[COMMON]Opening connection...\n");
    this.input = new BufferedReader(new InputStreamReader(this.cs.getInputStream()));
    this.output = new PrintWriter(this.cs.getOutputStream());
    this.output.println(getName());
    this.output.flush();
    this.received = "[COMMON]Connected.";
  }
  
  protected void CloseSocket() {
    try {
      this.input.close();
      this.output.close();
      this.cs.close();
      this.Owner.Dispatch(this, "[COMMON]Disconnected.\n");
    } catch (Exception e) {
      this.Owner.Dispatch(this, "[COMMON]" + e.toString() + "\n");
    } 
  }
  
  public void run() {
    try {
      this.Owner.Login(this);
      setName(this.login_name);
      OpenSocket();
      do {
        this.Owner.Dispatch(this, String.valueOf(this.received) + "\n");
        this.received = this.input.readLine();
      } while (!this.received.equals("DC") && !interrupted());
    } catch (Exception e) {
      this.Owner.Dispatch(this, "[COMMON]" + e.toString() + "\n");
    } 
    CloseSocket();
    this.Owner.Logout(this);
  }
}
