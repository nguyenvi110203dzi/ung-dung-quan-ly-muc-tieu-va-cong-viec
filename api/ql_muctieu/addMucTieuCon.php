<?php
require_once('connect.php');
/** Array for JSON response */
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Kiểm tra các tham số có tồn tại trong $_POST hay không
    if (isset($_POST['DayPlanID'], $_POST['GoalID'], $_POST['Date'], $_POST['Notes'], $_POST['Status'], $_POST['Progress'])) {

        // Lấy giá trị từ POST
        $dayplan_id = $_POST['DayPlanID'];
        $goal_id = $_POST['GoalID'];
        $ngay = $_POST['Date'];
        $mota = $_POST['Notes'];
        $trangthai = $_POST['Status'];
        $hoanthanh = $_POST['Progress'];

        $dateObj = DateTime::createFromFormat('d/m/Y', $ngay);
        $formattedDate = $dateObj ? $dateObj->format('Y-m-d') : null;
        // Dữ liệu cần chuẩn bị
        $data = array($dayplan_id, $goal_id, $formattedDate, $mota, $trangthai, $hoanthanh);

        try {
            // Chuẩn bị câu lệnh SQL để thêm dữ liệu vào cơ sở dữ liệu
            $stmta = $conn->prepare("INSERT INTO dayplans (DayPlanID, GoalID, Date, Notes, Status, Progress) VALUES (?, ?, ?, ?, ?, ?)");

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
