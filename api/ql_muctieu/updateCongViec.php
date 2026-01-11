<?php
require_once('connect.php');
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['TaskID'], $_POST['DayPlanID'], $_POST['Title'], $_POST['Description'], $_POST['Priority'], $_POST['Status'], $_POST['StartTime'], $_POST['EndTime'])) {
    try {
        // Lấy tham số từ client
        $task_id = $_POST['TaskID'];
        $dayplan_id = $_POST['DayPlanID'];
        $tieude = $_POST['Title'];
        $mota = $_POST['Description'];
        $douutien = $_POST['Priority'];
        $trangthai = $_POST['Status'];
        $hoanthanh = $_POST['Progress'];
        $batdau = DateTime::createFromFormat('d/m/Y', $_POST['StartTime'])->format('Y-m-d');
        $ketthuc = DateTime::createFromFormat('d/m/Y', $_POST['EndTime'])->format('Y-m-d');

        // Câu SQL và thực thi
        $sql = "UPDATE tasks 
                SET Title = ?, Description = ?, Priority = ?, Status = ?, 
                    StartTime = ?, EndTime = ?, Progress=?, DayPlanID = ?
                WHERE TaskID = ?";
        $stmt = $conn->prepare($sql);
        $conn->beginTransaction();
        $stmt->execute([$tieude, $mota, $douutien, $trangthai, $batdau, $ketthuc, $hoanthanh, $dayplan_id, $task_id]);
        $conn->commit();

        $response = ["success" => 1, "message" => "Cập nhật thành công! OK La"];
    } catch (Exception $e) {
        $conn->rollBack();
        $$response["success"] = 0;
        $response["message"] = "Lỗi: " . $e->getMessage();
    }
}
header('Content-Type: application/json');
echo json_encode($response);
