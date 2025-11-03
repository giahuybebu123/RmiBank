# 🏦 HƯỚNG DẪN SỬ DỤNG HỆ THỐNG BANK RMI

## 📋 TỔNG QUAN

Hệ thống ngân hàng phân tán với 2 server RMI tự động đồng bộ dữ liệu.

**Tính năng:**

- ✅ 2 server RMI hoạt động độc lập
- ✅ Tự động đồng bộ dữ liệu giữa 2 server
- ✅ Hỗ trợ failover (nếu server 1 lỗi, tự động chuyển sang server 2)
- ✅ 3 user: A001, A002, A003
- ✅ Chuyển tiền liên server

---

## 🚀 HƯỚNG DẪN TRIỂN KHAI

### **Kịch bản 1: Test trên 1 máy (2 server cùng localhost)**

#### **Bước 1: Khởi động Server 1**

```bash
# Terminal 1
cd /Users/ttcenter/IdeaProjects/RmiBank
mvn compile
mvn exec:java -Dexec.mainClass="bank.server.BankServer"

# Nhập:
# Tên server: server1
# IP: localhost
```

#### **Bước 2: Khởi động Server 2**

```bash
# Terminal 2 (mở terminal mới)
cd /Users/ttcenter/IdeaProjects/RmiBank
mvn exec:java -Dexec.mainClass="bank.server.BankServer"

# Nhập:
# Tên server: server2
# IP: localhost
```

#### **Bước 3: Khởi động Client**

```bash
# Terminal 3
mvn javafx:run

# Hoặc trong IntelliJ: Run MainApp.java
```

#### **Bước 4: Cấu hình trong Client**

- **Server 1 IP:** localhost
- **Server 1 Port:** 1099
- **Server 2 IP:** localhost
- **Server 2 Port:** 1100
- Click **"Kết nối và Đồng bộ"**

✅ Hệ thống sẽ tự động link 2 server!

#### **Bước 5: Đăng nhập**

- **Tài khoản:** A001, A002, hoặc A003
- **Mật khẩu:** 123

---

### **Kịch bản 2: Chạy trên 2 máy ảo / 2 máy khác nhau**

#### **Máy ảo 1 (Server 1) - IP: 192.168.1.100**

```bash
# Khởi động Server 1
mvn exec:java -Dexec.mainClass="bank.server.BankServer"

# Nhập:
# Tên server: server1
# IP: 192.168.1.100  ← IP thực của máy này
```

#### **Máy ảo 2 (Server 2) - IP: 192.168.1.101**

```bash
# Khởi động Server 2
mvn exec:java -Dexec.mainClass="bank.server.BankServer"

# Nhập:
# Tên server: server2
# IP: 192.168.1.101  ← IP thực của máy này
```

#### **Máy Client (có thể là máy thứ 3 hoặc 1 trong 2 máy server)**

```bash
mvn javafx:run
```

**Cấu hình trong Client:**

- **Server 1 IP:** 192.168.1.100
- **Server 1 Port:** 1099
- **Server 2 IP:** 192.168.1.101
- **Server 2 Port:** 1100

---

### **Kịch bản 3: Nhiều client cùng lúc (2-3 user)**

Sau khi đã khởi động 2 server, bạn có thể chạy **nhiều client** từ các máy khác nhau:

**Client 1 (Máy A):**

- Đăng nhập A001

**Client 2 (Máy B):**

- Đăng nhập A002

**Client 3 (Máy C):**

- Đăng nhập A003

Tất cả client cấu hình **CÙNG** IP của 2 server.

---

## 💰 DỮ LIỆU TÀI KHOẢN MẪU

| Tài khoản | Mật khẩu | Số dư ban đầu |
| --------- | -------- | ------------- |
| A001      | 123      | 1,000 VND     |
| A002      | 123      | 800 VND       |
| A003      | 123      | 1,500 VND     |

---

## 🔄 KIỂM TRA ĐỒNG BỘ

### **Test 1: Chuyển tiền cùng server**

1. Client 1 đăng nhập A001 (server 1)
2. Client 2 đăng nhập A002 (server 1)
3. A001 chuyển 200 VND cho A002
4. ✅ Cả 2 client đều thấy số dư cập nhật

### **Test 2: Chuyển tiền liên server**

Giả sử A001 ở server 1, A002 ở server 2:

1. A001 chuyển 300 VND cho A002
2. ✅ Server 1 trừ tiền A001
3. ✅ Server 2 cộng tiền A002 (tự động đồng bộ)

### **Test 3: Failover**

1. Đăng nhập client
2. Tắt Server 1
3. Refresh client
4. ✅ Tự động chuyển sang Server 2

---

## 🛠️ KHẮC PHỤC SỰ CỐ

### **Lỗi: "Connection refused"**

- ✅ Kiểm tra server đã chạy chưa
- ✅ Kiểm tra IP và Port đúng chưa
- ✅ Kiểm tra firewall (tắt hoặc cho phép port 1099, 1100)

### **Lỗi: "NotBound"**

- ✅ Server chưa khởi động xong
- ✅ Đợi vài giây rồi thử lại

### **Lỗi: Không link được 2 server**

- ✅ Client vẫn hoạt động bình thường
- ✅ Server hoạt động độc lập
- ✅ Không chuyển tiền liên server được

---

## 📦 CẤU TRÚC DỰ ÁN

```
RmiBank/
├── src/main/java/bank/
│   ├── interfaces/
│   │   └── BankInterface.java      # RMI interface
│   ├── model/
│   │   ├── Account.java            # Model tài khoản
│   │   └── Transaction.java
│   ├── server/
│   │   ├── BankServer.java         # Khởi động server
│   │   ├── BankServerImpl.java     # Implementation
│   │   └── LinkServers.java        # (Không cần dùng nữa)
│   └── client/
│       ├── MainApp.java            # Entry point
│       ├── ServerConfig.java       # Lưu config 2 server
│       └── controllers/
│           ├── ServerConfigController.java  # Màn hình cấu hình
│           ├── LoginController.java         # Đăng nhập
│           ├── DashboardController.java     # Dashboard
│           └── TransferController.java      # Chuyển tiền
└── src/main/resources/
    ├── serverconfig.fxml           # Giao diện config server
    ├── login.fxml
    ├── dashboard.fxml
    └── transfer.fxml
```

---

## 🎯 ĐIỂM MỚI SO VỚI PHIÊN BẢN CŨ

### **Trước đây:**

❌ Phải chạy riêng `LinkServers.java` để kết nối 2 server  
❌ Client chỉ kết nối 1 server (hardcode localhost)  
❌ Không có failover

### **Bây giờ:**

✅ Client **TỰ ĐỘNG** link 2 server khi khởi động  
✅ Client nhập IP của **CẢ 2 server**  
✅ **Failover tự động** nếu 1 server lỗi  
✅ Giao diện đẹp hơn, dễ sử dụng hơn

---

## 📞 LIÊN HỆ

Nếu có vấn đề gì, kiểm tra:

1. Console output của server (xem log)
2. Console output của client (xem kết nối)
3. Network connectivity (ping IP)
