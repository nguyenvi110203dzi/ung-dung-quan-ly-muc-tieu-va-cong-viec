<?php
require_once('connect.php');
/** Array for JSON response */
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Kiểm tra các tham số có tồn tại trong $_POST hay không
    if (isset($_POST['UserID'], $_POST['GoalTitle'], $_POST['Description'], $_POST['TargetDate'], $_POST['Visibility'], $_POST['Status'], $_POST['Progress'])) {

        // Lấy giá trị từ POST
        $userid = $_POST['UserID'];
        $tieude = $_POST['GoalTitle'];
        $mota = $_POST['Description'];
        $ngay = $_POST['TargetDate'];
        $hienthi = $_POST['Visibility'];
        $trangthai = $_POST['Status'];
        $hoanthanh = $_POST['Progress'];

        $dateObj = DateTime::createFromFormat('d/m/Y', $ngay);
        $formattedDate = $dateObj ? $dateObj->format('Y-m-d') : null;
        // Dữ liệu cần chuẩn bị
        $data = array($userid, $tieude, $mota, $formattedDate, $hienthi, $trangthai, $hoanthanh);

        try {
            // Chuẩn bị câu lệnh SQL để thêm dữ liệu vào cơ sở dữ liệu
            $stmta = $conn->prepare("INSERT INTO goals (UserID, GoalTitle, Description, TargetDate, Visibility, Status, Progress) VALUES (?, ?, ?, ?, ?, ?, ?)");

            // Bắt đầu giao dịch
            $conn->beginTransaction();

            // Thực thi câu lệnh
            $stmta->execute($data);

            // Lấy ID của mục tiêu đã thêm
            $response["success"] = 1;
            $response["message"] = "Thêm thành công!";

            // Commit giao dịch
            $conn->commit();
        } catch (PDOException $e) {
            // Rollback giao dịch nếu có lỗi
            $conn->rollBack();
            $response["success"] = 0;
            $response["message"] = "Thêm thất bại! Lỗi: " . $e->getMessage();
        }
    } else {
        // Trả về lỗi nếu thiếu tham số
        $response["success"] = 0;
        $response["message"] = "Vui lòng điền đủ thông tin.";
    }

    // Trả về kết quả dưới dạng JSON
    header('Content-Type: application/json');
    echo json_encode($response);
}
