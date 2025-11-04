package bank.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class BankServer {
    public static void main(String[] args) {
        Scanner sc = null;
        try {
            sc = new Scanner(System.in);
            System.out.print("Nhập tên server (server1/server2): ");
            String name = sc.nextLine().trim();

            System.out.print("Nhập IP máy hiện tại: ");
            String ip = sc.nextLine().trim();
            
            // QUAN TRỌNG: Set RMI hostname để ép RMI dùng IP đúng
            // Nếu không set, RMI có thể tự động dùng localhost/127.0.1.1
            System.setProperty("java.rmi.server.hostname", ip);
            System.out.println("✅ Đã set RMI hostname = " + ip);

            int port = name.equals("server1") ? 1099 : 1100;
            System.out.println("📡 Đang khởi tạo RMI Registry tại port " + port + "...");
            LocateRegistry.createRegistry(port);
            System.out.println("✅ RMI Registry đã sẵn sàng!");

            BankServerImpl server = new BankServerImpl();
            String rmiURL = "rmi://" + ip + ":" + port + "/BankService";
            System.out.println("🔗 Đang bind service tại: " + rmiURL);
            Naming.rebind(rmiURL, server);

            System.out.println("✅✅✅ " + name + " ĐANG CHẠY TẠI: " + rmiURL);
            System.out.println("📊 Server sẵn sàng nhận kết nối!");
            System.out.println("\n💡 Nhấn Enter để tắt server...");
            sc.nextLine();
            
        } catch (Exception e) {
            System.err.println("❌ LỖI: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
    }
}
