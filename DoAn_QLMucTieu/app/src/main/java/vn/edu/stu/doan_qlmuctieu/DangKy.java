package vn.edu.stu.doan_qlmuctieu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vn.edu.stu.doan_qlmuctieu.model.user;

public class DangKy extends AppCompatActivity {

    final String REGISTER = "http://192.168.1.177api/ql_muctieu/dangky.php";

    EditText etTen, etEmail, etPass;
    RadioButton rdbUser, rdbAdmin;
    Button btnDangKy;
    TextView tvDangNhap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ky);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControlls();
        addEvents();
    }

    private void addControlls() {
        etTen = findViewById(R.id.etTen);
        etEmail = findViewById(R.id.etTaiKhoan);
        etPass = findViewById(R.id.etPass);
        rdbUser = findViewById(R.id.rdbUser);
        rdbAdmin = findViewById(R.id.rdbAdmin);
        btnDangKy = findViewById(R.id.btnDangKy);
        tvDangNhap = findViewById(R.id.tvDangNhap);
    }

    private void addEvents() {
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int role = -1;
                if (rdbAdmin.isChecked())
                    role = 1;
                else if (rdbUser.isChecked())
                    role = 0;
                else
                    Toast.makeText(DangKy.this, "Lỗi Role", Toast.LENGTH_SHORT).show();
                user user = new user(
                        etTen.getText().toString(),
                        etEmail.getText().toString(),
                        etPass.getText().toString(),
                        role);
                xuli_dangky(user);
            }
        });
        tvDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        DangKy.this,
                        MainActivity.class
                );
                startActivity(intent);
            }
        });
    }

    private void xuli_dangky(user u) {
        RequestQueue requestQueue = Volley.newRequestQueue(
                DangKy.this
        );
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Server trả về một chuỗi response có dạng đối tượng JSON, nên ta ép nó thành JSONObject
                    JSONObject jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("success");
                    if (result == 1) {
                        Toast.makeText(
                                DangKy.this,
                                "Đăng ký thành công",
                                Toast.LENGTH_SHORT
                        ).show();
                        etTen.setText("");
                        etEmail.setText("");
                        etPass.setText("");
                    } else {
                        Toast.makeText(
                                DangKy.this,
                                "Đăng ký thất bại",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                } catch (Exception ex) {
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(
                        DangKy.this,
                        error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        };
        Uri.Builder builder = Uri.parse(REGISTER).buildUpon();
//        builder.appendQueryParameter("action", "insert");
//        builder.appendQueryParameter("masv", sv.getMaSV() + "");
//        builder.appendQueryParameter("tensv", sv.getTenSV());

        //http://192.168.192.67/ws/api.php?action=insert&masv=3&tensv=thb

        String url = builder.build().toString();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                responseListener,
                errorListener
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Trả về tham số sẽ được gửi trong body của POST
                Map<String, String> params = new HashMap<>();
                params.put("Name", u.getName());
                params.put("Email", u.getEmail());
                params.put("Password", u.getPassword());
                params.put("Role", String.valueOf(u.getRole()));
                return params;
            }
        };
        requestQueue.add(request);
    }
}