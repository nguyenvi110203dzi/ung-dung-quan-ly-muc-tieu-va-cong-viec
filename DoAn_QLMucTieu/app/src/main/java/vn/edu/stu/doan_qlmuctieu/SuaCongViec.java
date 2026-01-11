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

import vn.edu.stu.doan_qlmuctieu.model.task;

public class SuaCongViec extends AppCompatActivity {

    final String ALLMTC = "http://192.168.1.177/api/ql_muctieu/getAllMucTieuCon.php";
    final String UPDATE = "http://192.168.1.177/api/ql_muctieu/updateCongViec.php";

    EditText etTieuDe, etMoTa, etNgayBD, etNgayKT, etHoanThanh;
    ImageButton ibtnLichBD, ibtnLichKT;
    Spinner spMTC, spDUT, spTrangThai;
    Button btnSua;
    int userid = -1;
    Calendar calendar;
    ArrayList<Integer> dsMTC;
    ArrayAdapter<Integer> adapterMTC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sua_cong_viec);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getData();
        addControlls();
        getDataIntent();
        addEvents();
    }

    private void getDataIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("CHON")){
            task task = (task) intent.getSerializableExtra("CHON");
            etTieuDe.setText(task.getTitle());
            etMoTa.setText(task.getDescription());
            spDUT.setSelection(task.getPriority());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String ngaybd = sdf.format(task.getStartTime());
            etNgayBD.setText(ngaybd);
            String ngaykt = sdf.format(task.getEndTime());
            etNgayKT.setText(ngaykt);
            spTrangThai.setSelection(task.getStatus());
        }
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent.hasExtra("USERID")){
            userid = intent.getIntExtra("USERID",0);
        }
    }

    private void addControlls() {
        etTieuDe = findViewById(R.id.etTieuDe);
        etMoTa = findViewById(R.id.etMoTa);
        etNgayBD = findViewById(R.id.etNgayBD);
        etNgayKT = findViewById(R.id.etNgayKT);
        spMTC = findViewById(R.id.spMTC);
        dsMTC = new ArrayList<>();
        hienthiDanhsachMTC();
        adapterMTC = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dsMTC);
        spMTC.setAdapter(adapterMTC);
        spDUT = findViewById(R.id.spUuTien);
        spTrangThai = findViewById(R.id.spTrangThai);
        ArrayList<String> ut = new ArrayList<>();
        ut.add("Thấp");
        ut.add("Trung bình");
        ut.add("Cao");
        ArrayAdapter<String> adapterUT = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ut);
        spDUT.setAdapter(adapterUT);
        ArrayList<String> tt = new ArrayList<>();
        tt.add("Chưa bắt đầu");
        tt.add("Đang tiến hành");
        tt.add("Hoàn thành");
        ArrayAdapter<String> adapterTT = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tt);
        spTrangThai.setAdapter(adapterTT);
        btnSua = findViewById(R.id.btnSua);
        ibtnLichBD = findViewById(R.id.ibtnLichBD);
        ibtnLichKT = findViewById(R.id.ibtnLichKT);
        etHoanThanh = findViewById(R.id.etHoanThanh);
        calendar = Calendar.getInstance();
    }

    private void addEvents() {
        ibtnLichBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyChonNgayBD();
            }
        });
        ibtnLichKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyChonNgayKT();
            }
        });
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                task task = (task) intent.getSerializableExtra("CHON");
                task cv = new task();
                cv.setTaskID(task.getTaskID());
                cv.setDayPlanID(Integer.parseInt(spMTC.getSelectedItem().toString()));
                cv.setTitle(etTieuDe.getText().toString());
                cv.setDescription(etMoTa.getText().toString());
                cv.setPriority(spDUT.getSelectedItemPosition());
                cv.setStatus(spTrangThai.getSelectedItemPosition());
                cv.setProgress(Integer.parseInt(etHoanThanh.getText().toString()));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngaybd, ngaykt;
                try {
                    ngaybd = sdf.parse(etNgayBD.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                try {
                    ngaykt = sdf.parse(etNgayKT.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                cv.setStartTime(ngaybd);
                cv.setEndTime(ngaykt);
                xuliCapnhatCV(cv);
                intent.putExtra("TRA",cv);
                setResult(115,intent);
                finish();
            }
        });
    }

    private void xuLyChonNgayBD() {
        DatePickerDialog.OnDateSetListener listener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DATE, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        etNgayBD.setText(sdf.format(calendar.getTime())
                        );
                    }
                };
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SuaCongViec.this,listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
        );
        datePickerDialog.show();
    }
    private void xuLyChonNgayKT() {
        DatePickerDialog.OnDateSetListener listener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DATE, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        etNgayKT.setText(sdf.format(calendar.getTime())
                        );
                    }
                };
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SuaCongViec.this,listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
        );
        datePickerDialog.show();
    }

    private void hienthiDanhsachMTC() {
        // Hàng đợi các request lên server
        RequestQueue requestQueue = Volley.newRequestQueue(SuaCongViec.this);

        // Lắng nghe kết quả trả về
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    dsMTC.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                    JSONArray jsonArray = new JSONArray(response);
                    int len = jsonArray.length();
                    for (int i = 0; i < len; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int ma = jsonObject.getInt("DayPlanID");
                        dsMTC.add(ma);
                    }
                    adapterMTC.notifyDataSetChanged();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Lắng nghe lỗi trả về (thường là lỗi kết nối)
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SuaCongViec.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        // Tạo url đến service
        Uri.Builder builder = Uri.parse(ALLMTC).buildUpon();

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

    private void xuliCapnhatCV(task t) {
        RequestQueue requestQueue = Volley.newRequestQueue(
                SuaCongViec.this
        );
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("success");
                    if (result == 1) {
                        Toast.makeText(
                                SuaCongViec.this,
                                "Cập nhật thành công",
                                Toast.LENGTH_LONG
                        ).show();
                    } else {
                        Toast.makeText(
                                SuaCongViec.this,
                                "Cập nhật thất bại",
                                Toast.LENGTH_LONG
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
                        SuaCongViec.this,
                        error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        };
        Uri.Builder builder = Uri.parse(UPDATE).buildUpon();

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
                params.put("TaskID", t.getTaskID() + "");
                params.put("DayPlanID",t.getDayPlanID()+"");
                params.put("Title", t.getTitle());
                params.put("Description", t.getDescription());
                params.put("Priority",  t.getPriority()+"");
                params.put("Status", t.getStatus() + "");
                params.put("Progress", String.valueOf(t.getProgress()));  // Truyền tham số Email
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String ngaybd = sdf.format(t.getStartTime());
                params.put("StartTime", ngaybd);
                String ngaykt = sdf.format(t.getEndTime());
                params.put("EndTime", ngaykt); // Truyền tham số Password
                return params;
            }
        };
        requestQueue.add(request);
    }
}