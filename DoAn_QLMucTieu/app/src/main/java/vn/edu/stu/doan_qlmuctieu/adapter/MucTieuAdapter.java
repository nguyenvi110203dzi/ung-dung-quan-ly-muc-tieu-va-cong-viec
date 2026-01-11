package vn.edu.stu.doan_qlmuctieu.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.Map;

import vn.edu.stu.doan_qlmuctieu.R;
import vn.edu.stu.doan_qlmuctieu.SuaMucTieu;
import vn.edu.stu.doan_qlmuctieu.model.goal;

public class MucTieuAdapter extends ArrayAdapter<goal> {
    private Activity context;
    private int resource;
    private ArrayList<goal> ds;

    final String TRANGTHAI = "http://192.168.1.177/api/ql_muctieu/capnhatTrangThai.php";
    final String DEL_MT = "http://192.168.1.177/api/ql_muctieu/deleteMucTieuById.php";

    public MucTieuAdapter(Activity context, int resource, ArrayList<goal> ds) {
        super(context, resource, ds);
        this.context = context;
        this.resource = resource;
        this.ds = ds;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(resource, null);
        TextView title = row.findViewById(R.id.tvTitle);
        TextView mota = row.findViewById(R.id.tvMoTa);
        TextView phantram = row.findViewById(R.id.tvPhanTram);
        TextView ngay = row.findViewById(R.id.tvNgay);
        CheckBox hienthi = row.findViewById(R.id.cbHienThi);
        Spinner trangthai = row.findViewById(R.id.spTrangThai);
        Button capnhat = row.findViewById(R.id.btnCapNhat);
        Button xoa = row.findViewById(R.id.btnXoa);

        goal goal = ds.get(position);

        title.setText(goal.getGoalTitle().toString());
        mota.setText(goal.getDescription().toString());
        phantram.setText(goal.getProgress()+"%");
        hienthi.setChecked(goal.isVisibility());
        SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdfDisplay.format(goal.getTargetDate());
        ngay.setText(formattedDate);

        ArrayList<String> tt = new ArrayList<>();
        tt.add("Chưa bắt đầu");
        tt.add("Đang tiến hành");
        tt.add("Hoàn thành");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, tt);
        trangthai.setAdapter(adapter);
        trangthai.setSelection(goal.getStatus());

        capnhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goal chon = new goal();
                chon.setGoalID(goal.getGoalID());
                chon.setGoalTitle(goal.getGoalTitle());
                chon.setDescription(goal.getDescription());
                chon.setTargetDate(goal.getTargetDate());
                chon.setVisibility(hienthi.isChecked());
                chon.setStatus(trangthai.getSelectedItemPosition());
                chon.setProgress(goal.getProgress());
                xuliCapnhatTT(chon);
                ds.set(position,chon);
                notifyDataSetChanged();
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        context,
                        SuaMucTieu.class
                );
                intent.putExtra("USERID", goal.getUserID());
                intent.putExtra("CHON", goal);
                context.startActivityForResult(intent, 113);
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
                                xuliXoaMT(goal);
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

    private void xuliCapnhatTT(goal g) {
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
                                "Cập nhật thành công",
                                Toast.LENGTH_SHORT
                        ).show();
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(
                                context,
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
                        context,
                        error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        };
        Uri.Builder builder = Uri.parse(TRANGTHAI).buildUpon();

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
                params.put("MT", "up");
                params.put("GoalID", g.getGoalID() + "");
                params.put("Visibility", g.isVisibility() ? "1" : "0");
                params.put("Status", g.getStatus() + "");
                params.put("Progress", g.getProgress()+"");
                return params;
            }
        };
        requestQueue.add(request);
    }

    private void xuliXoaMT(goal g) {
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
                        ds.remove(g);
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
        Uri.Builder builder = Uri.parse(DEL_MT).buildUpon();
        builder.appendQueryParameter("GoalID", g.getGoalID() + "");
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
