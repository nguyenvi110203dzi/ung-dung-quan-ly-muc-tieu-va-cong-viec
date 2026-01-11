#  Ứng Dụng Quản Lý Mục Tiêu & Công Việc

## 📋 Giới Thiệu
 Ứng dụng di động giúp người dùng xây dựng kỷ luật bản thân thông qua việc thiết lập và theo dõi mục tiêu. Người dùng có thể tạo các **Mục tiêu lớn**, chia nhỏ thành các **Mục tiêu con** và quản lý chi tiết từng **Công việc** hàng ngày để đạt được kết quả tốt nhất.

## Công Nghệ Sử Dụng

| Hạng mục | Công nghệ sử dụng |
| :--- | :--- |
| **Mobile App** | **Android Native (Java)**, Android Studio |
| **Backend API** | **PHP** (RESTful API thuần), JSON |
| **Database** | **MySQL** |
| **Server** | XAMPP / WAMP (Apache Server) |

---
## 🚀 Tính Năng Nổi Bật
1.  **Hệ thống Tài khoản:** Đăng ký, Đăng nhập bảo mật, Quản lý thông tin cá nhân.
2.  **Quản lý Mục tiêu (Goals):**
    * Tạo mục tiêu mới với thời gian bắt đầu - kết thúc cụ thể.
    * Theo dõi tiến độ hoàn thành.
3.  **Chia nhỏ công việc:**
    * Thêm các mục tiêu con để dễ thực hiện.
    * Quản lý Todo-list hàng ngày gắn với từng mục tiêu.
4.  **Cập nhật trạng thái:** Đánh dấu hoàn thành công việc, tự động cập nhật phần trăm tiến độ của mục tiêu lớn.
---
## ⚙️ Hướng Dẫn Cài Đặt (Local Development)
Để chạy được dự án, bạn cần thiết lập cả Server (PHP) và Client (Android).
### Bước 1: Cấu hình Backend (Server)
1.  Cài đặt **XAMPP**.
2.  Khởi động **Apache** và **MySQL** trong XAMPP Control Panel.
3.  Copy thư mục `api/ql_muctieu` vào thư mục `C:/xampp/htdocs/`.
4.  Mở trình duyệt gõ: `http://localhost/phpmyadmin/`.
5.  Tạo database mới và Import file SQL (nằm trong thư mục source).
6.  Kiểm tra kết nối trong file `connect.php`.

### Bước 2: Cấu hình Android App
1.  Mở **CMD** (Command Prompt) trên Windows, gõ lệnh `ipconfig` để lấy **IPv4 Address** của máy bạn (Ví dụ: `192.168.1.10`).
2.  Mở project Android trong **Android Studio**.
3.  Tìm file cấu hình API (thường là nơi khai báo URL kết nối, ví dụ trong `MainActivity.java` hoặc file Utils).
4.  Thay đổi `localhost` thành IP máy của bạn
5.  Chạy ứng dụng trên máy ảo hoặc thiết bị thật (Lưu ý: Điện thoại và Laptop phải bắt chung 1 mạng Wifi).
---
## 📸 Screenshots (Giao diện ứng dụng)
### 1. Xác thực & Màn hình chính
| Danh Sách Mục Tiêu | Thêm mục tiêu | Danh Sách Mục Tiêu Phấn đấu |
| :---: | :---: | :---: |
| <img width="359" height="782" alt="muctieu" src="https://github.com/user-attachments/assets/78eb1ac3-9b8f-45bd-beab-f6537e258d37" width="200" />|<img width="359" height="782" alt="themmuctieu" src="https://github.com/user-attachments/assets/ec4b9cf7-661b-41b8-9652-866a87eb4c22" width="200" />| <img width="359" height="782" alt="themmuctieuphandau" src="https://github.com/user-attachments/assets/00ab8c4f-f6f1-4cd9-b2b8-65f5029a441b" width="200">
## 👤 Author
*Nguyễn Thị Tử Vi*

**Role**: FullStack Developer

#### Contact:

**Email**: tuvi0304.gl@gmail.com

**LinkedIn**: linkedin.com/in/nguyễn-thị-tử-vi-8b4895399

