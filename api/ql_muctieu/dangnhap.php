<?php
require_once('connect.php');
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Lấy giá trị từ POST
    $name = $_POST['Name'];
    $password = $_POST['Password'];

    if (!empty($name) && !empty($password)) {
        try {
            // Chuẩn bị câu lệnh truy vấn với PDO
            $stmt = $conn->prepare("SELECT UserID, Role, Password FROM users WHERE Name = ?");
            $stmt->bindParam(1, $name, PDO::PARAM_STR);

            // Thực thi câu lệnh
            $stmt->execute();

            // Kiểm tra xem có kết quả trả về không
            if ($stmt->rowCount() > 0) {
                $row = $stmt->fetch(PDO::FETCH_ASSOC);

                // Kiểm tra mật khẩu đã mã hóa
                if (password_verify($password, $row['Password'])) {
                    $response["id"] = $row['UserID'];
                    $response["success"] = 1;
                    $response["message"] = "Đăng nhập thành công!";
                    $response["role"] = $row['Role'];
                } else {
                    $response["success"] = 0;
                    $response["message"] = "Đăng nhập thất bại! Mật khẩu không đúng.";
                }
            } else {
                $response["success"] = 0;
                $response["message"] = "Đăng nhập thất bại! Email không tồn tại.";
            }
        } catch (PDOException $e) {
            // Xử lý lỗi PDO
            $response["success"] = 0;
            $response["message"] = "Lỗi kết nối cơ sở dữ liệu: " . $e->getMessage();
        }
    } else {
        $response["success"] = 0;
        $response["message"] = "Vui lòng điền đủ thông tin.";
    }

    // Trả về kết quả dưới dạng JSON
    header('Content-Type: application/json');
    echo json_encode($response);
}
