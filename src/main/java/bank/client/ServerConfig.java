package bank.client;

/**
 * Singleton class để lưu trữ thông tin kết nối 2 server RMI
 * Dùng để share IP và port giữa các controller
 */
public class ServerConfig {
    private static ServerConfig instance;
    
    // Thông tin 2 server
    private String server1IP = "localhost";
    private int server1Port = 1099;
    
    private String server2IP = "localhost";
    private int server2Port = 1100;
    
    private ServerConfig() {}
    
    public static ServerConfig getInstance() {
        if (instance == null) {
            instance = new ServerConfig();
        }
        return instance;
    }
    
    /**
     * Thiết lập thông tin 2 server
     */
    public void setServerInfo(String ip1, int port1, String ip2, int port2) {
        this.server1IP = ip1;
        this.server1Port = port1;
        this.server2IP = ip2;
        this.server2Port = port2;
    }
    
    public String getServer1URL() {
        return "rmi://" + server1IP + ":" + server1Port + "/BankService";
    }
    
    public String getServer2URL() {
        return "rmi://" + server2IP + ":" + server2Port + "/BankService";
    }
    
    // Getters
    public String getServer1IP() { return server1IP; }
    public int getServer1Port() { return server1Port; }
    public String getServer2IP() { return server2IP; }
    public int getServer2Port() { return server2Port; }
}

