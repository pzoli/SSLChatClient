import java.net.Socket;

public class SimpleSocketHandler
  extends SocketHandler
{
  SimpleSocketHandler(IMsg Ownr) {
    this.Owner = Ownr;
  }
  
  public void createSocket() throws Exception {
    this.Owner.Dispatch(this, "[COMMON]Creating SimpleSocketHandler thread to " + this.Srv + ":" + this.Prt + " as " + this.login_name + " ...\n");
    
    this.cs = new Socket(this.Srv, Integer.parseInt(this.Prt));
    this.Owner.Dispatch(this, "[COMMON]Simple socket created.\n");
  }
}
