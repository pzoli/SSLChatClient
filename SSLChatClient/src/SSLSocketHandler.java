import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SSLSocketHandler
  extends SocketHandler
{
  protected String trustJks;
  protected String clientJks;
  
  public String getTrustJks() {
    return this.trustJks;
  }
  
  public void setTrustJks(String trustJks) {
    this.trustJks = trustJks;
  }
  
  public String getClientJks() {
    return this.clientJks;
  }
  
  public void setClientJks(String clientJks) {
    this.clientJks = clientJks;
  }

  
  private SSLContext createSSLContext(String trustJKSPassword, String clientJKSPassword) throws Exception {
    KeyManagerFactory mgrFact = KeyManagerFactory.getInstance("SunX509");
    KeyStore clientStore = KeyStore.getInstance("JKS");
    clientStore.load(new FileInputStream(this.clientJks), clientJKSPassword.toCharArray());
    mgrFact.init(clientStore, clientJKSPassword.toCharArray());

    
    TrustManagerFactory trustFact = TrustManagerFactory.getInstance("SunX509");
    KeyStore trustStore = KeyStore.getInstance("JKS");
    trustStore.load(new FileInputStream(this.trustJks), trustJKSPassword.toCharArray());
    trustFact.init(trustStore);

    
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(mgrFact.getKeyManagers(), trustFact.getTrustManagers(), null);
    return sslContext;
  }
  
  public void createSocket(String trustPwd, String clientPwd) throws Exception {
    this.Owner.Dispatch(this, "[COMMON]Creating Secure SocketHandler thread to " + this.Srv + ":" + this.Prt + " as " + this.login_name + " ...\n");
    if (trustPwd == null || clientPwd == null) {
      throw new Exception("missing password params!");
    }
    SSLContext sslContext = createSSLContext(trustPwd, clientPwd);
    SSLSocketFactory fact = sslContext.getSocketFactory();
    this.cs = fact.createSocket(this.Srv, Integer.parseInt(this.Prt));
    this.Owner.Dispatch(this, "[COMMON]Secure socket created.\n");
  }

  
  SSLSocketHandler(IMsg Ownr) {
    this.Owner = Ownr;
  }
}
