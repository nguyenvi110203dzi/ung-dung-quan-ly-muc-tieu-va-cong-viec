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

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.Map;

import vn.edu.stu.doan_qlmuctieu.adapter.CongViecAdapter;
import vn.edu.stu.doan_qlmuctieu.model.dayplan;
import vn.edu.stu.doan_qlmuctieu.model.goal;
import vn.edu.stu.doan_qlmuctieu.model.task;

public class CongViec extends AppCompatActivity {

    final String ALLCV = "http://192.168.1.177/api/ql_muctieu/getAllCongViec.php";

    BottomNavigationView bottomNavigationView;
    int userid = -1;
    Button btnThem;
    ListView lvCongViec;
    ArrayList<task> dsCV;
    CongViecAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cong_viec);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getData();
        addControlls();
        addEvents();
    }


    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent.hasExtra("USERID")){
            userid = intent.getIntExtra("USERID",0);
        }
    }

    private void addControlls() {
        bottomNavigationView = findViewById(R.id.menubottom);
        btnThem= findViewById(R.id.btnThem);
        lvCongViec = findViewById(R.id.lvMucTieu);
        hienthi();
    }

    private void hienthi() {
        dsCV = new ArrayList<>();
        hienthiDanhsachCV();
        adapter = new CongViecAdapter(this, R.layout.item_congviec, dsCV);
        lvCongViec.setAdapter(adapter);
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
                        CongViec.this,
                        ThemCongViec.class
                );
                intent.putExtra("USERID", userid);
                startActivityForResult(intent,102);
            }
        });
    }

    private void hienthiDanhsachCV() {
        // Hàng đợi các request lên server
        RequestQueue requestQueue = Volley.newRequestQueue(CongViec.this);

        // Lắng nghe kết quả trả về
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    dsCV.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                    JSONArray jsonArray = new JSONArray(response);
                    int len = jsonArray.length();
                    for (int i = 0; i < len; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int ma = jsonObject.getInt("TaskID");
                        int muctieunho_id = jsonObject.getInt("DayPlanID");
                        String tieude = jsonObject.getString("Title");
                        String mota = jsonObject.getString("Description");
                        int uutien = jsonObject.getInt("Priority");
                        int trangthai = jsonObject.getInt("Status");
                        int phantram = jsonObject.getInt("Progress");
                        String bd = jsonObject.getString("StartTime");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date ngaybd = sdf.parse(bd);
                        String kt = jsonObject.getString("EndTime");
                        Date ngaykt = sdf.parse(kt);
                        dsCV.add(new task(ma, muctieunho_id,tieude, uutien, mota,trangthai, phantram,ngaybd,ngaykt));
                    }
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
                Toast.makeText(CongViec.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        // Tạo url đến service
        Uri.Builder builder = Uri.parse(ALLCV).buildUpon();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 113 && resultCode == 115){
            if (data.hasExtra("TRA")){
                task t = (task) data.getSerializableExtra("TRA");
                if (t != null){
                    for (int i = 0; i < dsCV.size(); i++) {
                        if (dsCV.get(i).getTaskID() == t.getTaskID()){
                            dsCV.set(i,t);
                        }
                    };
                }
                adapter.notifyDataSetChanged();
            }
        }
        if (requestCode == 102 && resultCode == 103){
            if (data.hasExtra("CV")){
                task cv = (task) data.getSerializableExtra("CV");
                if (cv != null){
                    dsCV.add(cv);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}