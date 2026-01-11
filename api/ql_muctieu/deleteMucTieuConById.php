<?php
require_once('connect.php');

// Mảng lưu kết quả
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    // Lấy dữ liệu từ POST
    $dayplan_id = $_GET['DayPlanID'];

    // Chuẩn bị dữ liệu và câu lệnh SQL
    try {

        $stmt = $conn->prepare("SELECT * FROM tasks WHERE DayPlanID = :DayPlanID");
        $stmt->bindParam(':DayPlanID', $dayplan_id, PDO::PARAM_INT);
        $stmt->execute();
        $tasks = $stmt->fetchAll(PDO::FETCH_ASSOC);
        if ($tasks) {
            $response["success"] = 0;
            $response["message"] = "Xóa thất bại!";
        } else {
            // Sử dụng PDO để chuẩn bị câu lệnh SQL
            $stmt = $conn->prepare("DELETE FROM dayplans WHERE DayPlanID = :DayPlanID");

            // Liên kết tham số
            $stmt->bindParam(':DayPlanID', $dayplan_id, PDO::PARAM_INT);

            // Bắt đầu giao dịch
            $conn->beginTransaction();

            // Thực thi câu lệnh
            $stmt->execute();

            // Commit giao dịch
            $conn->commit();

            // Thông báo thành công
            $response["success"] = 1;
            $response["message"] = "Xóa thành công!";
        }
    } catch (PDOException $e) {
        // Rollback nếu có lỗi
        $conn->rollBack();

        // Thông báo lỗi
        $response["success"] = 0;
        $response["message"] = "Xóa thất bại! Lỗi: " . $e->getMessage();
    }

    // Trả về kết quả dưới dạng JSON
    echo json_encode($response);
}
