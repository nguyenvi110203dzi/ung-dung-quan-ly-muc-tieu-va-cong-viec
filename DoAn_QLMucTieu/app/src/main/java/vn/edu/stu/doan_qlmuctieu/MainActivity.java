package vn.edu.stu.doan_qlmuctieu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

//    Android 9 (Pie) không hỗ trợ http nên phải cấp quyền cho nó
    final String LOGIN = "http://192.168.1.177/api/ql_muctieu/dangnhap.php";

    EditText etTaiKhoan, etPass;
    Button btnDangNhap;
    TextView tvDangKy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControlls();
        addEvents();
    }

    private void addControlls() {
        etTaiKhoan = findViewById(R.id.etTaiKhoan);
        etPass = findViewById(R.id.etMatKhau);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        tvDangKy = findViewById(R.id.tvDangKy);
    }

    private void addEvents() {
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangnhap(etTaiKhoan.getText().toString(),etPass.getText().toString());
            }
        });
        tvDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        MainActivity.this,
                        DangKy.class
                );
                startActivity(intent);
            }
        });
    }

    private void dangnhap(String Name,String Pass) {
        // Hàng đợi các request lên server
        RequestQueue requestQueue = Volley.newRequestQueue(
                MainActivity.this
        );

        // Lắng nghe kết quả trả về
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int id = jsonObject.getInt("id");
                    int result = jsonObject.getInt("success");
                    int role = jsonObject.getInt("role");
                    if (result == 1) {
                        Toast.makeText(
                                MainActivity.this,
                                "Đăng nhập thành công",
                                Toast.LENGTH_SHORT
                        ).show();
                        Intent intent = new Intent(MainActivity.this, MucTieu.class);
                        intent.putExtra("USERID", id);
                        intent.putExtra("ROLE",role);
                        startActivity(intent);
                    } else {
                        Toast.makeText(
                                MainActivity.this,
                                "Đăng nhập thất bại",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();  // In lỗi nếu có
                    Toast.makeText(MainActivity.this, "Lỗi phân tích dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Lắng nghe lỗi trả về (thường là lỗi kết nối)
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(
                        MainActivity.this,
                        error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        };

        // Tao url đến service
        Uri.Builder builder = Uri.parse(LOGIN).buildUpon();

        // Chèn thêm tham số cho url, dùng trong phương thức $_GET
//        builder.appendQueryParameter("Email", Email);
//        builder.appendQueryParameter("Password", Pass);
        String url = builder.build().toString();
        StringRequest request = new StringRequest(
                Request.Method.POST, // nếu dùng $_POST thì đổi thành POST
                url,
                responseListener,
                errorListener
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Trả về tham số sẽ được gửi trong body của POST
                Map<String, String> params = new HashMap<>();
                params.put("Name", Name);  // Truyền tham số Email
                params.put("Password", Pass); // Truyền tham số Password
                return params;
            }
        };
        requestQueue.add(request);
    }
}