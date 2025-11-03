package bank.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class BankServer {
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Nhập tên server (server1/server2): ");
            String name = sc.nextLine().trim();

            System.out.print("Nhập IP máy hiện tại: ");
            String ip = sc.nextLine().trim();

            int port = name.equals("server1") ? 1099 : 1100;
            LocateRegistry.createRegistry(port);

            BankServerImpl server = new BankServerImpl();
            Naming.rebind("rmi://" + ip + ":" + port + "/BankService", server);

            System.out.println(" " + name + " đang chạy tại rmi://" + ip + ":" + port + "/BankService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
