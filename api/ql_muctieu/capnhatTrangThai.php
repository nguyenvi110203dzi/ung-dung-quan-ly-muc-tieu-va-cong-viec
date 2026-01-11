<?php
require_once('connect.php');
$response = array();

// Kiểm tra phương thức POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    try {
        // Kiểm tra và cập nhật bảng 'goals'
        if (isset($_POST['MT'])) {
            // Kiểm tra các tham số cần thiết
            if (isset($_POST['GoalID'], $_POST['Visibility'], $_POST['Status'], $_POST['Progress'])) {
                $goal_id = htmlspecialchars(trim($_POST['GoalID']));
                $hienthi = htmlspecialchars(trim($_POST['Visibility']));
                $trangthai = htmlspecialchars(trim($_POST['Status']));
                $phantram = htmlspecialchars(trim($_POST['Progress']));

                // Chuẩn bị câu lệnh SQL
                $data = array($hienthi, $trangthai, $phantram, $goal_id);
                $stmta = $conn->prepare("UPDATE goals SET Visibility=?, Status=?, Progress=? WHERE GoalID=?");
            } else {
                throw new Exception("Thiếu thông tin GoalID, Visibility, Status hoặc Progress.");
            }
        } 
        // Kiểm tra và cập nhật bảng 'dayplans'
        else if (isset($_POST['MTC'])) {
            if (isset($_POST['DayPlanID'], $_POST['Status'], $_POST['Progress'])) {
                $dayplan_id = htmlspecialchars(trim($_POST['DayPlanID']));
                $trangthai = htmlspecialchars(trim($_POST['Status']));
                $phantram = htmlspecialchars(trim($_POST['Progress']));

                $data = array($trangthai, $phantram, $dayplan_id);
                $stmta = $conn->prepare("UPDATE dayplans SET Status=?, Progress=? WHERE DayPlanID=?");
            } else {
                throw new Exception("Thiếu thông tin DayPlanID, Status hoặc Progress.");
            }
        } else {
            throw new Exception("Tham số MT hoặc MTC không hợp lệ.");
        }

        // Thực thi câu lệnh SQL
        $conn->beginTransaction();
        $stmta->execute($data);
        $conn->commit();

        // Trả về kết quả thành công
        $response["success"] = 1;
        $response["message"] = "Cập nhật thành công.";
    } catch (PDOException $e) {
        $conn->rollBack();
        $response["success"] = 0;
        $response["message"] = "Lỗi cơ sở dữ liệu: " . $e->getMessage();
    } catch (Exception $e) {
        $response["success"] = 0;
        $response["message"] = $e->getMessage();
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Phương thức không hợp lệ. Chỉ hỗ trợ POST.";
}

// Trả về phản hồi dưới dạng JSON
header('Content-Type: application/json; charset=UTF-8');
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
?>
