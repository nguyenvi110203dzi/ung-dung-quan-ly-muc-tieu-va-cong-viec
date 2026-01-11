<?php
require_once('connect.php');

// Mảng lưu kết quả
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
	// Lấy dữ liệu từ GET
	$goal_id = $_GET['GoalID'];

	try {
		// Kiểm tra nếu GoalID tồn tại trong bảng dayplans
		$checkStmt = $conn->prepare("SELECT COUNT(*) AS count FROM dayplans WHERE GoalID = :GoalID");
		$checkStmt->bindParam(':GoalID', $goal_id, PDO::PARAM_INT);
		$checkStmt->execute();
		$result = $checkStmt->fetch(PDO::FETCH_ASSOC);

		if ($result['count'] > 0) {
			// GoalID tồn tại trong dayplans, không cho phép xóa
			$response["success"] = 0;
			$response["message"] = "Không thể xóa! Mục tiêu đang được sử dụng trong bảng dayplans.";
		} else {
			// Thực hiện xóa GoalID trong bảng goals
			$deleteStmt = $conn->prepare("DELETE FROM goals WHERE GoalID = :GoalID");
			$deleteStmt->bindParam(':GoalID', $goal_id, PDO::PARAM_INT);

			// Bắt đầu giao dịch
			$conn->beginTransaction();

			// Thực thi câu lệnh xóa
			$deleteStmt->execute();

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
} else {
	// Yêu cầu không hợp lệ
	$response["success"] = 0;
	$response["message"] = "Yêu cầu không hợp lệ!";
	echo json_encode($response);
}
