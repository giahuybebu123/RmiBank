package bank.server;

import bank.interfaces.BankInterface;
import java.rmi.Naming;
import java.util.Scanner;

public class LinkServers {
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("IP server 1: ");
            String ip1 = sc.nextLine().trim();
            System.out.print("Cổng 1: ");
            String port1 = sc.nextLine().trim();

            System.out.print("IP server 2: ");
            String ip2 = sc.nextLine().trim();
            System.out.print("Cổng 2: ");
            String port2 = sc.nextLine().trim();

            BankInterface s1 = (BankInterface) Naming.lookup("rmi://" + ip1 + ":" + port1 + "/BankService");
            BankInterface s2 = (BankInterface) Naming.lookup("rmi://" + ip2 + ":" + port2 + "/BankService");

            s1.setOtherServer(s2);
            s2.setOtherServer(s1);

            System.out.println("✅ Hai server đã liên kết thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
