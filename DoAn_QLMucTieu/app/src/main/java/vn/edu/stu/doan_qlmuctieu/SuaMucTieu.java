package vn.edu.stu.doan_qlmuctieu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import vn.edu.stu.doan_qlmuctieu.model.goal;

public class SuaMucTieu extends AppCompatActivity {

    EditText etTieude, etMota, etNgay;
    ImageButton ibtnLich;
    CheckBox cbHienThi;
    Spinner spTrangThai;
    Button btnSua;
    Calendar calendar;
    int userid = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sua_muc_tieu);
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
        btnSua = findViewById(R.id.btnSua);
        calendar = Calendar.getInstance();
    }

    private void getDataIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("CHON")){
            goal g = (goal) intent.getSerializableExtra("CHON");
            etTieude.setText(g.getGoalTitle());
            etMota.setText(g.getDescription());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String ngay = sdf.format(g.getTargetDate());
            etNgay.setText(ngay);
            cbHienThi.setChecked(g.isVisibility());
            spTrangThai.setSelection(g.getStatus());
        }
    }

    private void addEvents() {
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                goal chon = (goal) intent.getSerializableExtra("CHON");
                String tieude = etTieude.getText().toString();
                String mota = etMota.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngay;
                try {
                    ngay = sdf.parse(etNgay.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                boolean hienthi = cbHienThi.isChecked();
                int trangthai = spTrangThai.getSelectedItemPosition();
                goal g = new goal(chon.getGoalID(),userid,tieude,mota,ngay,hienthi,trangthai,0);
                intent.putExtra("TRA",g);
                setResult(115,intent);
                finish();
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
                SuaMucTieu.this,listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
        );
        datePickerDialog.show();
    }
}