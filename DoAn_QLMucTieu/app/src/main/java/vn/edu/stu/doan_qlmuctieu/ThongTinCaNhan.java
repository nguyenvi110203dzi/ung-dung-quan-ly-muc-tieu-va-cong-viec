package vn.edu.stu.doan_qlmuctieu;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ThongTinCaNhan extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    TextView  marqueeText;
    int userid = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thong_tin_ca_nhan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        marqueeText = findViewById(R.id.marquee_text);
        // Bắt đầu animation
        startMarqueeAnimation();
        getData();
        addControlls();
        addEvents();
    }

    private void startMarqueeAnimation() {
        // Lấy chiều rộng của màn hình
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        // Lấy chiều rộng của TextView
        marqueeText.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int textWidth = marqueeText.getMeasuredWidth();

        // Thiết lập animation
        ObjectAnimator animator = ObjectAnimator.ofFloat(marqueeText, "translationX", screenWidth, -textWidth);
        animator.setDuration(9000); // Thời gian chạy animation (5 giây)
        animator.setRepeatCount(ObjectAnimator.INFINITE); // Lặp lại vô hạn
        animator.setRepeatMode(ObjectAnimator.RESTART); // Khởi động lại animation
        animator.start();
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent.hasExtra("USERID")){
            userid = intent.getIntExtra("USERID",0);
        }
    }

    private void addControlls() {
        bottomNavigationView = findViewById(R.id.menubottom);
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
    }
}