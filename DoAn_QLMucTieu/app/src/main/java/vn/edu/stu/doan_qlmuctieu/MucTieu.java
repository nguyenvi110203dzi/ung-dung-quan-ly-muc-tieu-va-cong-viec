package vn.edu.stu.doan_qlmuctieu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vn.edu.stu.doan_qlmuctieu.adapter.MucTieuAdapter;
import vn.edu.stu.doan_qlmuctieu.model.goal;

public class MucTieu extends AppCompatActivity {

    final String ALL = "http://192.168.1.177/api/ql_muctieu/getAllMucTieu.php";
    final String UPDATE = "http://192.168.1.177/api/ql_muctieu/updateMucTieu.php";

    BottomNavigationView bottomNavigationView;
    int userid = -1;
    Button btnThem;
    ListView lvMucTieu;
    ArrayList<goal> dsMT;
    MucTieuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_muc_tieu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getData();
        addControlls();
        addEvents();

    }

    private void getData() {
        Intent intent = getIntent();
        if (intent.hasExtra("USERID")){
            userid = intent.getIntExtra("USERID",0);
        }
    }

    private void addControlls() {
        bottomNavigationView = findViewById(R.id.menubottom);
        btnThem = findViewById(R.id.btnThem);
        lvMucTieu = findViewById(R.id.lvMucTieu);
        hienthi();
    }

    private void hienthi() {
        dsMT = new ArrayList<>();
        hienthiDanhsachMT(userid);
        adapter = new MucTieuAdapter(this,R.layout.item_muctieu, dsMT);
        lvMucTieu.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void addEvents() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.muctieu){
                    Intent intent = new Intent(
                            getApplicationContext(),
                            MucTieu.class
                    );
                    intent.putExtra("USERID", userid);
                    startActivity(intent);
                }
                if(id == R.id.kehoachngay){
                    Intent intent = new Intent(
                            getApplicationContext(),
                            MucTieuCon.class
                    );
                    intent.putExtra("USERID", userid);
                    startActivity(intent);
                }
                if(id == R.id.congviec){
                    Intent intent = new Intent(
                            getApplicationContext(),
                            CongViec.class
                    );
                    intent.putExtra("USERID", userid);
                    startActivity(intent);
                }
                if(id == R.id.canhan){
                    Intent intent = new Intent(
                            getApplicationContext(),
                            ThongTinCaNhan.class
                    );
                    intent.putExtra("USERID", userid);
                    startActivity(intent);
                }
                return true;
            }
        });
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        MucTieu.this,
                        ThemMucTieu.class
                );
                intent.putExtra("USERID", userid);
                startActivityForResult(intent, 114);
            }
        });
    }

    private void hienthiDanhsachMT(int userid) {
        // Hàng đợi các request lên server
        RequestQueue requestQueue = Volley.newRequestQueue(MucTieu.this);

        // Lắng nghe kết quả trả về
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    dsMT.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                    JSONArray jsonArray = new JSONArray(response);
                    int len = jsonArray.length();
                    for (int i = 0; i < len; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (userid == jsonObject.getInt("UserID")) {
                            int ma = jsonObject.getInt("GoalID");
                            int userID = jsonObject.getInt("UserID");
                            String tieude = jsonObject.getString("GoalTitle");
                            String mota = jsonObject.getString("Description");
                            String n = jsonObject.getString("TargetDate");

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date ngay = sdf.parse(n);
                            boolean hienthi = jsonObject.getInt("Visibility") == 1;
                            int trangthai = jsonObject.getInt("Status");
                            int phantram = jsonObject.getInt("Progress");

                            dsMT.add(new goal(ma, userID, tieude, mota, ngay, hienthi, trangthai, phantram));
                        }
                    }
                    // Gọi notifyDataSetChanged sau khi cập nhật dsMT
                    adapter.notifyDataSetChanged();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Lắng nghe lỗi trả về (thường là lỗi kết nối)
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MucTieu.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        // Tạo url đến service
        Uri.Builder builder = Uri.parse(ALL).buildUpon();

        // Thêm tham số cho url
        String url = builder.build().toString();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                responseListener,
                errorListener
        );
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(request);
    }

    private void xuliCapnhatMT(goal g) {
        RequestQueue requestQueue = Volley.newRequestQueue(
                MucTieu.this
        );
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("success");
                    if (result == 1) {
                        Toast.makeText(
                                MucTieu.this,
                                "Cập nhật thành công",
                                Toast.LENGTH_SHORT
                        ).show();
                        hienthiDanhsachMT(userid);
                    } else {
                        Toast.makeText(
                                MucTieu.this,
                                "Cập nhật thất bại",
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
                        MucTieu.this,
                        error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        };
        Uri.Builder builder = Uri.parse(UPDATE).buildUpon();
        builder.appendQueryParameter("GoalID", g.getGoalID() + "");
        builder.appendQueryParameter("GoalTitle", g.getGoalTitle());
        builder.appendQueryParameter("Description", g.getDescription());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String ngay = sdf.format(g.getTargetDate());
        builder.appendQueryParameter("TargetDate", ngay);
        builder.appendQueryParameter("Visibility", g.isVisibility()?"1":"0");
        builder.appendQueryParameter("Status", String.valueOf(g.getStatus()));
        String url = builder.build().toString();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                responseListener,
                errorListener
        );
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==114 && resultCode==115){
            if (data.hasExtra("TRA")){
                goal g = (goal) data.getSerializableExtra("TRA");
                if (g != null){
                    dsMT.add(g);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}