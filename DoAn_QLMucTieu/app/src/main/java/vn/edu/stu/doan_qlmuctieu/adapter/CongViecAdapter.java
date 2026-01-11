package vn.edu.stu.doan_qlmuctieu.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import vn.edu.stu.doan_qlmuctieu.R;
import vn.edu.stu.doan_qlmuctieu.SuaCongViec;
import vn.edu.stu.doan_qlmuctieu.model.goal;
import vn.edu.stu.doan_qlmuctieu.model.task;

public class CongViecAdapter extends ArrayAdapter<task> {

    final String DEL_CV = "http://192.168.1.177/api/ql_muctieu/deleteCongViecById.php";

    Activity context;
    int resource;
    ArrayList<task> ds;

    public CongViecAdapter(Activity context, int resource, ArrayList<task> ds) {
        super(context, resource, ds);
        this.context = context;
        this.resource = resource;
        this.ds = ds;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(resource,null);
        TextView ngaybd = row.findViewById(R.id.tvStart);
        TextView ngaykt = row.findViewById(R.id.tvEnd);
        TextView mota = row.findViewById(R.id.tvMoTa);
        TextView uutien = row.findViewById(R.id.tvUuTien);
        TextView tieude = row.findViewById(R.id.tvTieuDe);
        ProgressBar bar = row.findViewById(R.id.pbTienDo);
        Spinner tinhtrang = row.findViewById(R.id.spTrangThai);
        Button capnhat = row.findViewById(R.id.btnCapNhat);
        Button xoa = row.findViewById(R.id.btnXoa);

        task task = ds.get(position);
        tieude.setText(task.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String start = sdf.format(task.getStartTime());
        ngaybd.setText(start);
        String end = sdf.format(task.getEndTime());
        ngaykt.setText(end);
        mota.setText(task.getDescription());
        if (task.getPriority() == 0){
            uutien.setText("LOW");
            uutien.setBackgroundColor(Color.parseColor("#FF1000"));
        }else if (task.getPriority() == 1){
            uutien.setText("MEDIUM");
            uutien.setBackgroundColor(Color.parseColor("#FFEB3B"));
        }else {
            uutien.setText("HIGH");
            uutien.setBackgroundColor(Color.parseColor("#4CAF50"));
        }
        bar.setProgress(task.getProgress());
        ArrayList<String> tt = new ArrayList<>();
        tt.add("Chưa bắt đầu");
        tt.add("Đang tiến hành");
        tt.add("Hoàn thành");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, tt);
        tinhtrang.setAdapter(adapter);
        tinhtrang.setSelection(task.getStatus());
        capnhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        context,
                        SuaCongViec.class
                );
                intent.putExtra("CHON", task);
                context.startActivityForResult(intent,113);
            }
        });
        xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Thông báo")
                        .setMessage("Đây là một hộp thoại thông báo!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                xuliXoaMT(task);
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Thực hiện hành động khi người dùng nhấn "Hủy"
                                dialog.cancel();
                            }
                        });
                // Tạo và hiển thị AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return row;
    }

    private void xuliXoaMT(task t) {
        RequestQueue requestQueue = Volley.newRequestQueue(
                context
        );
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("success");
                    if (result == 1) {
                        Toast.makeText(
                                context,
                                "Xóa thành công",
                                Toast.LENGTH_SHORT
                        ).show();
                        ds.remove(t);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(
                                context,
                                "Xóa thất bại",
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
                        context,
                        error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        };
        Uri.Builder builder = Uri.parse(DEL_CV).buildUpon();
        builder.appendQueryParameter("TaskID", t.getTaskID() + "");
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
