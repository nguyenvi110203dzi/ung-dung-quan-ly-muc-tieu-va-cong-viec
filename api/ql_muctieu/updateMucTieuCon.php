<?php
require_once('connect.php');
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    // Lấy tham số từ GET
    if (isset($_GET['DayPlanID'], $_GET['GoalID'], $_GET['Date'], $_GET['Notes'], $_GET['Status'])) {
        $dayplan_id = $_GET['DayPlanID'];
        $goal_id = $_GET['GoalID'];
        $ngay = $_GET['Date'];
        $ghichu = $_GET['Notes'];
        $trangthai = $_GET['Status'];

        $datebd = DateTime::createFromFormat('d/m/Y', $ngay);
        $bd = $datebd ? $datebd->format('Y-m-d') : null;
        // Chuẩn bị dữ liệu cho câu lệnh SQL
        $data = array($bd, $ghichu, $trangthai, $goal_id, $dayplan_id);

        try {
            // Chuẩn bị câu lệnh UPDATE với PDO
            $stmta = $conn->prepare("UPDATE dayplans SET Date=?, Notes=?, Status=? WHERE GoalID=? AND DayPlanID=?");

            // Bắt đầu giao dịch
            $conn->beginTransaction();

            // Thực thi câu lệnh
            $stmta->execute($data);

            // Cập nhật thành công
            $response["success"] = 1;
            $response["message"] = "Cập nhật thành công!";

            // Commit giao dịch
            $conn->commit();
        } catch (PDOException $e) {
            // Xử lý lỗi trong trường hợp có ngoại lệ
            $conn->rollBack();  // Hủy giao dịch nếu có lỗi
            $response["success"] = 0;
            $response["message"] = "Cập nhật thất bại! Lỗi: " . $e->getMessage();
        }

        // Trả về kết quả dưới dạng JSON
        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "Dữ liệu không hợp lệ!";
        echo json_encode($response);
    }
}
