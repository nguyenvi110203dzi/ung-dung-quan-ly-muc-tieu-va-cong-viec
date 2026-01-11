<?php
include "connect.php";

// Mảng lưu kết quả
$mangdayplans = array();

try {
    // Truy vấn để tính Progress động dựa trên bảng tasks
    $query = "
        SELECT dp.DayPlanID, dp.GoalID, dp.Date, dp.Notes, dp.Status,
               IFNULL(SUM(t.Progress) / COUNT(t.TaskID), 0) AS Progress
        FROM dayplans dp
        LEFT JOIN tasks t ON dp.DayPlanID = t.DayPlanID
        GROUP BY dp.DayPlanID, dp.GoalID, dp.Date, dp.Notes, dp.Status
    ";

    $stmt = $conn->prepare($query);

    // Thực thi truy vấn
    $stmt->execute();

    // Lấy dữ liệu từ kết quả truy vấn
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        // Thêm dữ liệu vào mảng
        $mangdayplans[] = new DayPlan(
            $row['DayPlanID'],
            $row['GoalID'],
            $row['Date'],
            $row['Notes'],
            $row['Status'],
            round($row['Progress'], 2) // Làm tròn Progress đến 2 chữ số thập phân
        );
    }

    // Trả về dữ liệu dưới dạng JSON
    header('Content-Type: application/json');
    echo json_encode($mangdayplans);
} catch (PDOException $e) {
    // Xử lý lỗi nếu có vấn đề với truy vấn
    $response = array(
        "success" => 0,
        "message" => "Lỗi kết nối cơ sở dữ liệu: " . $e->getMessage()
    );
    echo json_encode($response);
}

// Định nghĩa lớp DayPlan
class DayPlan
{
    public function __construct($DayPlanID, $GoalID, $Date, $Notes, $Status, $Progress)
    {
        $this->DayPlanID = $DayPlanID;
        $this->GoalID = $GoalID;
        $this->Date = $Date;
        $this->Notes = $Notes;
        $this->Status = $Status;
        $this->Progress = $Progress;
    }
}
