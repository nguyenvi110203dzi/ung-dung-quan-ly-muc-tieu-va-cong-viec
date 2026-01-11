package vn.edu.stu.doan_qlmuctieu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import vn.edu.stu.doan_qlmuctieu.model.dayplan;
import vn.edu.stu.doan_qlmuctieu.model.goal;

public class ThemMucTieuCon extends AppCompatActivity {

    final String ADD = "http://192.168.1.177/api/ql_muctieu/addMucTieuCon.php";
    final String ALLMT = "http://192.168.1.177/api/ql_muctieu/getAllMucTieu.php";

    EditText etNgay, etMoTa;
    Spinner spMT, spTrangThai;
    ImageButton ibtnLich;
    Button btnThem;
    ArrayList<String> dsMT;
    ArrayList<Integer> dsIdMT;
    ArrayAdapter<String> adapter;
    int userid = -1;
    Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_muc_tieu_con);
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
        etNgay = findViewById(R.id.etNgay);
        etMoTa = findViewById(R.id.etMoTa);
        spMT = findViewById(R.id.spMucTieu);
        spTrangThai = findViewById(R.id.spTrangThai);
        btnThem = findViewById(R.id.btnThem);
        ibtnLich = findViewById(R.id.ibtnLich);
        calendar = Calendar.getInstance();
        dsIdMT = new ArrayList<>();
        dsMT = new ArrayList<>();
        hienthiDanhsachMT(userid);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dsMT);
        spMT.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        ArrayList<String> tt = new ArrayList<>();
        tt.add("Chưa bắt đầu");
        tt.add("Đang tiến hành");
        tt.add("Hoàn thành");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tt);
        spTrangThai.setAdapter(adapter);
    }

    private void addEvents() {
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayplan dayplan = new dayplan();
                dayplan.setDayPlanID(0);
                dayplan.setGoalID(dsIdMT.get(spMT.getSelectedItemPosition()));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngay;
                try {
                    ngay = sdf.parse(etNgay.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                dayplan.setDate(ngay);
                dayplan.setNotes(etMoTa.getText().toString());
                dayplan.setStatus(spTrangThai.getSelectedItemPosition());
                dayplan.setProgress(0);
                xuliThemMTC(dayplan);
            }
        });
        ibtnLich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyChonNgay();
            }
        });
    }

    private void xuLyChonNgay() {
        DatePickerDialog.OnDateSetListener listener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DATE, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        etNgay.setText(sdf.format(calendar.getTime())
                        );
                    }
                };
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ThemMucTieuCon.this,listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
        );
        datePickerDialog.show();
    }

    private void xuliThemMTC(dayplan dayplan) {
        RequestQueue requestQueue = Volley.newRequestQueue(
                ThemMucTieuCon.this
        );
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Server trả về một chuỗi response có dạng đối tượng JSON, nên ta ép nó thành JSONObject
                    JSONObject jsonObject = new JSONObject(response);
                    //{
                    //    "message": "true"
                    //}
                    int result = jsonObject.getInt("success");
                    if (result == 1) {
                        Toast.makeText(
                                ThemMucTieuCon.this,
                                "Thêm thành công",
                                Toast.LENGTH_SHORT
                        ).show();
                        Intent intent = getIntent();
                        intent.putExtra("TRA",dayplan);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(
                                ThemMucTieuCon.this,
                                "Thêm thất bại",
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
                        ThemMucTieuCon.this,
                        error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        };
        Uri.Builder builder = Uri.parse(ADD).buildUpon();
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
                params.put("DayPlanID", String.valueOf(dayplan.getDayPlanID()));
                params.put("GoalID", String.valueOf(dayplan.getGoalID()));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String ngay = sdf.format(dayplan.getDate());
                params.put("Date", ngay);
                params.put("Notes", dayplan.getNotes());
                params.put("Status", String.valueOf(dayplan.getStatus()));
                params.put("Progress", String.valueOf(dayplan.getProgress()));
                return params;
            }
        };
        requestQueue.add(request);
    }

    private void hienthiDanhsachMT(int userid) {
        // Hàng đợi các request lên server
        RequestQueue requestQueue = Volley.newRequestQueue(ThemMucTieuCon.this);

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
                            String tieude = jsonObject.getString("GoalTitle");
                            dsMT.add(tieude);
                            dsIdMT.add(ma);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Lắng nghe lỗi trả về (thường là lỗi kết nối)
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ThemMucTieuCon.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        // Tạo url đến service
        Uri.Builder builder = Uri.parse(ALLMT).buildUpon();

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
}