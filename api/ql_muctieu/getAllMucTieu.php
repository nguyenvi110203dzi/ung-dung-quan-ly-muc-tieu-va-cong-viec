<?php
include "connect.php";

// Mảng lưu kết quả
$manggoal = array();

try {
    // Truy vấn để tính Progress từ bảng dayplans
    $query = "
        SELECT g.GoalID, g.UserID, g.GoalTitle, g.Description, g.TargetDate, g.Visibility, g.Status,
               IFNULL(SUM(dp.Progress) / COUNT(dp.DayPlanID), 0) AS Progress
        FROM goals g
        LEFT JOIN dayplans dp ON g.GoalID = dp.GoalID
        GROUP BY g.GoalID, g.UserID, g.GoalTitle, g.Description, g.TargetDate, g.Visibility, g.Status
    ";

    $stmt = $conn->prepare($query);

    // Thực thi truy vấn
    $stmt->execute();

    // Lấy dữ liệu từ kết quả truy vấn
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        // Thêm dữ liệu vào mảng
        $manggoal[] = new Goal(
            $row['GoalID'],
            $row['UserID'],
            $row['GoalTitle'],
            $row['Description'],
            $row['TargetDate'],
            $row['Visibility'],
            $row['Status'],
            round($row['Progress'], 2) // Làm tròn Progress đến 2 chữ số thập phân
        );
    }

    // Trả về dữ liệu dưới dạng JSON
    header('Content-Type: application/json');
    echo json_encode($manggoal, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
} catch (PDOException $e) {
    // Xử lý lỗi nếu có vấn đề với truy vấn
    $response = array(
        "success" => 0,
        "message" => "Lỗi kết nối cơ sở dữ liệu: " . $e->getMessage()
    );
    echo json_encode($response);
}

// Định nghĩa lớp Goal
class Goal
{
    public function __construct($GoalID, $UserID, $GoalTitle, $Description, $TargetDate, $Visibility, $Status, $Progress)
    {
        $this->GoalID = $GoalID;
        $this->UserID = $UserID;
        $this->GoalTitle = $GoalTitle;
        $this->Description = $Description;
        $this->TargetDate = $TargetDate;
        $this->Visibility = $Visibility;
        $this->Status = $Status;
        $this->Progress = $Progress;
    }
}
