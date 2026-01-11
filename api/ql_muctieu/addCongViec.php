<?php
require_once('connect.php');
/** Array for JSON response */
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Kiểm tra các tham số có tồn tại trong $_POST hay không
    if (isset($_POST['TaskID'], $_POST['DayPlanID'], $_POST['Title'], $_POST['Description'], $_POST['Priority'], $_POST['Status'], $_POST['Progress'], $_POST['StartTime'], $_POST['EndTime'])) {

        // Lấy giá trị từ POST
        $task_id = $_POST['TaskID'];
        $dayplan_id = $_POST['DayPlanID'];
        $tieude = $_POST['Title'];
        $mota = $_POST['Description'];
        $douutien = $_POST['Priority'];
        $trangthai = $_POST['Status'];
        $hoanthanh = $_POST['Progress'];
        $batdau = $_POST['StartTime'];
        $ketthuc = $_POST['EndTime'];

        $datebd = DateTime::createFromFormat('d/m/Y', $batdau);
        $fbatdau = $datebd ? $datebd->format('Y-m-d') : null;

        $datekt = DateTime::createFromFormat('d/m/Y', $ketthuc);
        $fketthuc = $datekt ? $datekt->format('Y-m-d') : null;
        // Dữ liệu cần chuẩn bị
        $data = array($task_id, $dayplan_id, $tieude, $mota, $douutien, $trangthai, $hoanthanh, $fbatdau, $fketthuc);

        try {
            // Chuẩn bị câu lệnh SQL để thêm dữ liệu vào cơ sở dữ liệu
            $stmta = $conn->prepare("INSERT INTO tasks (TaskID, DayPlanID, Title, Description, Priority, Status, Progress, StartTime, EndTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

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
