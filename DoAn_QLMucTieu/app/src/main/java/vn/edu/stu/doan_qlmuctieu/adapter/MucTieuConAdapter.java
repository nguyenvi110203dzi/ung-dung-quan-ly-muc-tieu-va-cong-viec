package vn.edu.stu.doan_qlmuctieu.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import vn.edu.stu.doan_qlmuctieu.model.dayplan;

public class MucTieuConAdapter extends ArrayAdapter<dayplan> {

    final String TRANGTHAI = "http://192.168.1.177/api/ql_muctieu/capnhatTrangThai.php";
    final String DEL_MT = "http://192.168.1.177/api/ql_muctieu/deleteMucTieuConById.php";

    Activity context;
    int resource;
    ArrayList<dayplan> ds;

    public MucTieuConAdapter(Activity context, int resource, ArrayList<dayplan> ds) {
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

        TextView ngay = row.findViewById(R.id.tvNgay);
        TextView mota = row.findViewById(R.id.tvMoTa);
        TextView phantram = row.findViewById(R.id.tvPhanTram);
        Spinner trangthai = row.findViewById(R.id.spTrangThai);
        Button capnhat = row.findViewById(R.id.btnCapNhat);
        Button xoa = row.findViewById(R.id.btnXoa);

        dayplan dayplan = ds.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String n = sdf.format(dayplan.getDate());
        ngay.setText(n);
        mota.setText(dayplan.getNotes());
        phantram.setText(dayplan.getProgress()+"%");
        ArrayList<String> tt = new ArrayList<>();
        tt.add("Chưa bắt đầu");
        tt.add("Đang tiến hành");
        tt.add("Hoàn thành");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, tt);
        trangthai.setAdapter(adapter);
        trangthai.setSelection(dayplan.getStatus());
        capnhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayplan da = new dayplan();
                da.setDayPlanID(dayplan.getDayPlanID());
                da.setGoalID(dayplan.getGoalID());
                da.setDate(dayplan.getDate());
                da.setNotes(dayplan.getNotes());
                da.setStatus(trangthai.getSelectedItemPosition());
                da.setProgress(Integer.parseInt(phantram.getText().toString().replace("%", "")));
                xuliCapnhatTT(da);
                ds.set(position,da);
                notifyDataSetChanged();
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
                                xuliXoaMT(dayplan);
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

    private void xuliXoaMT(dayplan dayplan) {
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
                        ds.remove(dayplan);
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
        builder.appendQueryParameter("DayPlanID", dayplan.getDayPlanID() + "");
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

    private void xuliCapnhatTT(dayplan dayplan) {
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
                params.put("MTC", "up");
                params.put("DayPlanID", dayplan.getDayPlanID() + "");
                params.put("Status", dayplan.getStatus() + "");
                params.put("Progress", dayplan.getProgress()+"");
                return params;
            }
        };
        requestQueue.add(request);
    }
}
