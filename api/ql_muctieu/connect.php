<?php
$hostname = "localhost";
$username = "root";
$password = "";
$databasename = "qlcongviec";

// Kết nối đến MySQL bằng PDO
try {
    $conn = new PDO("mysql:host=$hostname;dbname=$databasename", $username, $password);
    // Thiết lập chế độ lỗi của PDO
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    // Thiết lập mã hóa ký tự UTF-8 cho kết nối
    $conn->exec("SET NAMES 'utf8'");
} catch (PDOException $e) {
    echo "Kết nối thất bại: " . $e->getMessage();
}
?>
