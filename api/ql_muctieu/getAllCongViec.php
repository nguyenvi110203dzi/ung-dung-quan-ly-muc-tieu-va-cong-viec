<?php
include "connect.php";

// Mảng lưu kết quả
$manggoal = array();

try {
    // Kết nối PDO với cơ sở dữ liệu
    $query = "SELECT * FROM tasks";
    $stmt = $conn->prepare($query);

    // Thực thi truy vấn
    $stmt->execute();

    // Lấy dữ liệu từ kết quả truy vấn
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        // Thêm dữ liệu vào mảng
        $manggoal[] = new task(
            $row['TaskID'],
            $row['DayPlanID'],
            $row['Title'],
            $row['Description'],
            $row['Priority'],
            $row['Status'],
            $row['Progress'],
            $row['StartTime'],
            $row['EndTime']
        );
    }

    // Trả về dữ liệu dưới dạng JSON
    header('Content-Type: application/json');
    echo json_encode($manggoal);
} catch (PDOException $e) {
    // Xử lý lỗi nếu có vấn đề với truy vấn
    $response = array(
        "success" => 0,
        "message" => "Lỗi kết nối cơ sở dữ liệu: " . $e->getMessage()
    );
    echo json_encode($response);
}

// Định nghĩa lớp goal
class task
{
    public function __construct($TaskID, $DayPlanID, $Title, $Description, $Priority, $Status, $Progress, $StartTime, $EndTime)
    {
        $this->TaskID  = $TaskID;
        $this->DayPlanID  = $DayPlanID;
        $this->Title = $Title;
        $this->Description = $Description;
        $this->Priority = $Priority;
        $this->Status = $Status;
        $this->Progress = $Progress;
        $this->StartTime = $StartTime;
        $this->EndTime = $EndTime;
    }
}
