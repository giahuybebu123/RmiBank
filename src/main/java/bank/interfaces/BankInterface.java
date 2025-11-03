package bank.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BankInterface extends Remote {
    double getBalance(String accountId) throws RemoteException;
    void deposit(String accountId, double amount) throws RemoteException;
    void withdraw(String accountId, double amount) throws RemoteException;
    boolean transfer(String fromAccountId, String toAccountId, double amount) throws RemoteException;

    // Dùng để đồng bộ 2 server
    void updateBalance(String accountId, double newBalance) throws RemoteException;

    // Liên kết 2 server
    void setOtherServer(BankInterface other) throws RemoteException;
}
