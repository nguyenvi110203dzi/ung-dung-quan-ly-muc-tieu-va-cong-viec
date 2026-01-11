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

import vn.edu.stu.doan_qlmuctieu.adapter.MucTieuConAdapter;
import vn.edu.stu.doan_qlmuctieu.model.dayplan;

public class MucTieuCon extends AppCompatActivity {

    final String ALL = "http://192.168.1.177/api/ql_muctieu/getAllMucTieuCon.php";
    final String UPDATE = "http://192.168.1.177/api/ql_muctieu/updateMucTieuCon.php";

    BottomNavigationView bottomNavigationView;
    int userid = -1;
    Button btnThem;
    ListView lvMucTieu;
    ArrayList<dayplan> dsMTC;
    MucTieuConAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_muc_tieu_con);
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
        if (intent.hasExtra("USERID")) {
            userid = intent.getIntExtra("USERID", 0);
        }
    }

    private void addControlls() {
        bottomNavigationView = findViewById(R.id.menubottom);
        btnThem = findViewById(R.id.btnThem);
        lvMucTieu = findViewById(R.id.lvMucTieu);
        hienthi();
    }

    private void hienthi() {
        dsMTC = new ArrayList<>();
        hienthiDanhsachMT();
        adapter = new MucTieuConAdapter(this, R.layout.item_muctieucon, dsMTC);
        lvMucTieu.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void addEvents() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent intent = null;
                if (id == R.id.muctieu) {
                    intent = new Intent(getApplicationContext(), MucTieu.class);
                } else if (id == R.id.kehoachngay) {
                    intent = new Intent(getApplicationContext(), MucTieuCon.class);
                } else if (id == R.id.congviec) {
                    intent = new Intent(getApplicationContext(), CongViec.class);
                } else if (id == R.id.canhan) {
                    intent = new Intent(getApplicationContext(), ThongTinCaNhan.class);
                }
                if (intent != null) {
                    intent.putExtra("USERID", userid);
                    startActivity(intent);
                }
                return true;
            }
        });

        btnThem.setOnClickListener(v -> {
            Intent intent = new Intent(MucTieuCon.this, ThemMucTieuCon.class);
            intent.putExtra("USERID", userid);
            startActivityForResult(intent,114);
        });
    }

    private void hienthiDanhsachMT() {
        RequestQueue requestQueue = Volley.newRequestQueue(MucTieuCon.this);

        Response.Listener<String> responseListener = response -> {
            try {
                dsMTC.clear();
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int ma = jsonObject.getInt("DayPlanID");
                    int muctieu_id = jsonObject.getInt("GoalID");
                    String n = jsonObject.getString("Date");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date ngay = sdf.parse(n);
                    String ghichu = jsonObject.getString("Notes");
                    int trangthai = jsonObject.getInt("Status");
                    int phantram = jsonObject.getInt("Progress");

                    dsMTC.add(new dayplan(ma, muctieu_id, ngay, ghichu, trangthai, phantram));
                }
                adapter.notifyDataSetChanged();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        Response.ErrorListener errorListener = error ->
                Toast.makeText(MucTieuCon.this, "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_LONG).show();

        Uri.Builder builder = Uri.parse(ALL).buildUpon();
        String url = builder.build().toString();

        StringRequest request = new StringRequest(Request.Method.GET, url, responseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
    }

    private void xuliCapnhatMT(dayplan day) {
        RequestQueue requestQueue = Volley.newRequestQueue(MucTieuCon.this);

        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                int result = jsonObject.getInt("success");
                if (result == 1) {
                    Toast.makeText(MucTieuCon.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    hienthiDanhsachMT(); // Lấy lại danh sách mới sau khi cập nhật
                } else {
                    Toast.makeText(MucTieuCon.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        Response.ErrorListener errorListener = error ->
                Toast.makeText(MucTieuCon.this, "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_LONG).show();

        Uri.Builder builder = Uri.parse(UPDATE).buildUpon();
        builder.appendQueryParameter("DayPlanID", String.valueOf(day.getDayPlanID()));
        builder.appendQueryParameter("GoalID", String.valueOf(day.getGoalID()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String ngay = sdf.format(day.getDate());
        builder.appendQueryParameter("Date", ngay);
        builder.appendQueryParameter("Notes", day.getNotes());
        builder.appendQueryParameter("Status", String.valueOf(day.getStatus()));

        String url = builder.build().toString();

        StringRequest request = new StringRequest(Request.Method.GET, url, responseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 114 && resultCode == RESULT_OK) {
            if (data.hasExtra("TRA")) {
                dayplan d = (dayplan) data.getSerializableExtra("TRA");
                dsMTC.add(d);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
