<?php
require_once('connect.php');
/** Array for JSON response */
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $name = $_POST['Name'];
    $email = $_POST['Email'];
    $password = $_POST['Password'];
    $role = $_POST['Role'];

    $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

    try {
        $conn->beginTransaction();

        $stmta = $conn->prepare("INSERT INTO users (Name, Email, Password, Role) VALUES (?, ?, ?, ?)");

        $stmta->execute([$name, $email, $hashedPassword,$role]);

        $response["UserID"] = $conn->lastInsertId();
        $response["success"] = 1;
        $response["message"] = "Đăng ký thành công!";

        $conn->commit();
    } catch (PDOException $e) {
        $conn->rollBack();

        $response["success"] = 0;
        $response["message"] = "Đăng ký thất bại! Lỗi: " . $e->getMessage();
    }
    echo json_encode($response);
}
