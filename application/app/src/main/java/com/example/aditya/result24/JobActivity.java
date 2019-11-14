package com.example.aditya.result24;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;


public class JobActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Context context;
    LinearLayout linearLayout;
    TextView postNm;
    TextView rctBrd;
    TextView qual;
    TextView totVacc;
    TextView lastDt;
    WebView table;
    TextView brief;
    Integer width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);
        context = this;
        progressBar = findViewById(R.id.progressBar);
        linearLayout = findViewById(R.id.progress_layout);

        postNm = findViewById(R.id.job_name_text);
        rctBrd = findViewById(R.id.job_recruiter_text);
        qual = findViewById(R.id.job_qualification_text);
        totVacc = findViewById(R.id.job_vacancy_text);
        lastDt = findViewById(R.id.job_date_text);
        table = findViewById(R.id.table);
        brief = findViewById(R.id.brief);

        Integer pk = 1;

        Bundle bundle = getIntent().getExtras();
        if  (bundle != null) {
            pk = bundle.getInt("pk");
            postNm.setText(bundle.getString("postNm"));
            rctBrd.setText(bundle.getString("rctBrd"));
            qual.setText(bundle.getString("qual"));
            totVacc.setText(bundle.getString("totVacc"));
            lastDt.setText(bundle.getString("lastDt"));
        }

        String url = getResources().getString(R.string.get_jobs_detail) + "?pk=" + String.valueOf(pk);
        new JobDetailHttpGet(url).execute();



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_jobdetail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.back) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class JobDetailHttpGet extends AsyncTask<Void, Void, Void> {
        String url;
        String data = "";
        JobDetailHttpGet(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);
                in.close();
                data = response.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            try{
                JSONArray jArray = new JSONArray(data);

                JSONObject jObj = jArray.getJSONObject(0);
                JSONObject fields = jObj.getJSONObject("fields");
                brief.setText(fields.getString("brief"));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    brief.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);

                table.loadData(fields.getString("table"),"text/html; charset=utf-8", "utf-8");
                table.setInitialScale(getScale());

                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int getScale(){
        Point p = new Point();
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getSize(p);
        int width = p.x;
        int PIC_WIDTH = 500 + 70;
        Double val = new Double(width)/new Double(PIC_WIDTH);
        val = val * 100d;
        return val.intValue();
    }
}