package vn.edu.stu.doan_qlmuctieu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import vn.edu.stu.doan_qlmuctieu.model.goal;

public class ThemMucTieu extends AppCompatActivity {

    final String ADD = "http://192.168.1.177/api/ql_muctieu/addMucTieu.php";

    EditText etTieude, etMota, etNgay;
    ImageButton ibtnLich;
    CheckBox cbHienThi;
    Spinner spTrangThai;
    Button btnThem;
    int userid = -1;
    Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_muc_tieu);
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
        etTieude = findViewById(R.id.etTieuDe);
        etMota = findViewById(R.id.etMoTa);
        etNgay = findViewById(R.id.etNgay);
        ibtnLich = findViewById(R.id.ibtnLich);
        cbHienThi = findViewById(R.id.cbHienThi);
        spTrangThai = findViewById(R.id.spTrangThai);
        ArrayList<String> tt = new ArrayList<>();
        tt.add("Chưa bắt đầu");
        tt.add("Đang tiến hành");
        tt.add("Hoàn thành");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tt);
        spTrangThai.setAdapter(adapter);
        btnThem = findViewById(R.id.btnThem);
        calendar = Calendar.getInstance();
    }

    private void addEvents() {
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goal goal = new goal();
                goal.setGoalID(0);
                goal.setUserID(userid);
                goal.setGoalTitle(etTieude.getText().toString());
                goal.setDescription(etMota.getText().toString());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngay;
                try {
                    ngay = sdf.parse(etNgay.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                goal.setTargetDate(ngay);
                goal.setVisibility(cbHienThi.isChecked());
                goal.setStatus(spTrangThai.getSelectedItemPosition());
                goal.setProgress(0);
                xuliThemMT(goal);
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
                ThemMucTieu.this,listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
        );
        datePickerDialog.show();
    }

    private void xuliThemMT(goal g) {
        RequestQueue requestQueue = Volley.newRequestQueue(
                ThemMucTieu.this
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
                                ThemMucTieu.this,
                                "Thêm thành công",
                                Toast.LENGTH_SHORT
                        ).show();
                        Intent intent = getIntent();
                        intent.putExtra("TRA",g);
                        setResult(115, intent);
                        finish();
                    } else {
                        Toast.makeText(
                                ThemMucTieu.this,
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
                        ThemMucTieu.this,
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
                params.put("UserID", String.valueOf(g.getUserID()));
                params.put("GoalTitle", g.getGoalTitle());
                params.put("Description", g.getDescription());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String ngay = sdf.format(g.getTargetDate());
                params.put("TargetDate", ngay);
                params.put("Visibility", g.isVisibility() ? "1":"0");
                params.put("Status", String.valueOf(g.getStatus()));
                params.put("Progress", String.valueOf(g.getProgress()));
                return params;
            }
        };
        requestQueue.add(request);
    }
}