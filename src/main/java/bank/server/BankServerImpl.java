package bank.server;

import bank.interfaces.BankInterface;
import bank.model.Account;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class BankServerImpl extends UnicastRemoteObject implements BankInterface {

    private final Map<String, Account> accounts = new HashMap<>();
    private BankInterface otherServer;

    public BankServerImpl() throws RemoteException {
        super();
        // Dữ liệu mẫu - 3 tài khoản cho 3 user
        accounts.put("A001", new Account("A001", 1000));
        accounts.put("A002", new Account("A002", 800));
        accounts.put("A003", new Account("A003", 1500));
        System.out.println("✅ Server khởi tạo với 3 tài khoản: A001, A002, A003");
    }

    @Override
    public double getBalance(String accountId) throws RemoteException {
        Account acc = accounts.get(accountId);
        return acc != null ? acc.getBalance() : 0;
    }

    @Override
    public void deposit(String accountId, double amount) throws RemoteException {
        Account acc = accounts.get(accountId);
        if (acc != null) {
            acc.deposit(amount);
        }
    }

    @Override
    public void withdraw(String accountId, double amount) throws RemoteException {
        Account acc = accounts.get(accountId);
        if (acc != null) {
            acc.withdraw(amount);
        }
    }

    @Override
    public boolean transfer(String fromId, String toId, double amount) throws RemoteException {
        Account from = accounts.get(fromId);
        
        // Kiểm tra điều kiện
        if (from == null) {
            System.err.println("❌ Tài khoản gửi không tồn tại: " + fromId);
            return false;
        }
        
        if (amount <= 0) {
            System.err.println("❌ Số tiền phải lớn hơn 0: " + amount);
            return false;
        }
        
        if (from.getBalance() < amount) {
            System.err.println("❌ Không đủ số dư. Số dư hiện tại: " + from.getBalance() + ", cần: " + amount);
            return false;
        }
        
        // Thực hiện chuyển tiền
        System.out.println("💸 Đang chuyển " + amount + " từ " + fromId + " đến " + toId);
            from.withdraw(amount);

        // Nếu người nhận ở server này
        if (accounts.containsKey(toId)) {
                Account to = accounts.get(toId);
            to.deposit(amount);
            System.out.println("✅ Chuyển tiền nội bộ thành công! " + fromId + " → " + toId + " = " + amount);
            return true;
        }
        
        // Nếu người nhận ở server khác → đồng bộ qua otherServer
        if (otherServer != null) {
            try {
                double currentBalance = otherServer.getBalance(toId);
                otherServer.updateBalance(toId, currentBalance + amount);
                System.out.println("✅ Chuyển tiền liên server thành công! " + fromId + " → " + toId + " (server khác) = " + amount);
                return true;
            } catch (Exception e) {
                // Rollback nếu đồng bộ thất bại
                System.err.println("❌ Lỗi đồng bộ với server khác: " + e.getMessage());
                System.err.println("⚠️ ROLLBACK: Hoàn tiền cho " + fromId);
                from.deposit(amount); // Hoàn lại tiền
                return false;
            }
        } else {
            // Không có server khác và tài khoản nhận không tồn tại
            System.err.println("❌ Tài khoản nhận không tồn tại và không có server khác để tìm!");
            from.deposit(amount); // Hoàn lại tiền
        return false;
        }
    }

    @Override
    public void updateBalance(String accountId, double newBalance) throws RemoteException {
        Account acc = accounts.get(accountId);
        if (acc == null) {
            acc = new Account(accountId, newBalance);
            accounts.put(accountId, acc);
        } else {
            acc.setBalance(newBalance);
        }
        System.out.println(" Đồng bộ tài khoản " + accountId + " = " + newBalance);
    }

    @Override
    public void setOtherServer(BankInterface other) throws RemoteException {
        this.otherServer = other;
        System.out.println("🔗 Đã kết nối với server còn lại!");
    }
}
