package com.example.aditya.result24;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static java.lang.Thread.sleep;


public class JobFragment extends Fragment {

    static Boolean endOfPage;
    static String param;
    static JobCardAdapter adapter;
    static Context context;
    int limit = 10, currPage;

    FloatingActionButton floatingActionButton;
    ListView lvCards;
    LinearLayout buttonLayout;
    View mProgressBarFooter;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    static ProgressBar filterProgressBar;
    PopupWindow popup;
    View layout;
    int  width, height, conversionFac;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.jobs_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = this.getContext();
        lvCards = view.findViewById(R.id.job_list_cards);
        floatingActionButton = view.findViewById(R.id.fab);
        buttonLayout = view.findViewById(R.id.button_layout);
        param = "";

        new AsyncTask<Void, Void, Void>() {
            String url = getResources().getString(R.string.get_filters_detail);
            String data = "";
            @Override
            protected void onPreExecute() {
                expandableListDetail = new HashMap<String, List<String>>();
                popup = new PopupWindow(context);
                layout = getLayoutInflater().inflate(R.layout.filter, null);
                filterProgressBar = layout.findViewById(R.id.filter_spinner);
                expandableListView = (ExpandableListView) layout.findViewById(R.id.expandableListView);
                JobFragment.filterProgressBar.setVisibility(View.VISIBLE);
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
                    while ((inputLine = in.readLine()) != null) response.append(inputLine);
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
                    for (int i = 0; i < jArray.length(); i++) {
                        String title = jArray.getJSONObject(i).getJSONObject("fields").getString("filterTitle");
                        String subtitle = jArray.getJSONObject(i).getJSONObject("fields").getString("subtitle");
                        String match = jArray.getJSONObject(i).getJSONObject("fields").getString("match");
                        if(expandableListDetail.containsKey(title)) {
                            expandableListDetail.get(title).add(subtitle + "<-->" + match);
                        } else {
                            List<String> s = new ArrayList<>();
                            s.add(subtitle + "<-->" + match);
                            expandableListDetail.put(title, s);
                        }
                    }

                    expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                    expandableListAdapter = new CustomExpandableListAdapter(context, expandableListTitle, expandableListDetail);

                    expandableListView.setAdapter(expandableListAdapter);
                    conversionFac  = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

                    DisplayMetrics metrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    width = metrics.widthPixels;
                    height = metrics.heightPixels;
                    expandableListView.setIndicatorBoundsRelative(width/2 - 150, width/2 - 50);
                    filterProgressBar.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popup.setContentView(layout);
                popup.setHeight(Math.min(8, expandableListTitle.size() + 1) * 50 * conversionFac);
                popup.setWidth(width / 2);
                popup.setOutsideTouchable(true);
                popup.setFocusable(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popup.setElevation(20);
                }
                popup.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup));
                popup.showAsDropDown(buttonLayout);
                popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        currPage = 1;
                        endOfPage = false;
                        lvCards.addFooterView(mProgressBarFooter);
                        adapter.clear();
                        adapter.jobCardModelList.clear();
                        new JobCardHttpGet(getResources().getString(R.string.get_jobs) + "?page=" + currPage + "&limit=" + limit).execute();
                    }
                });
            }
        });

        mProgressBarFooter = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.job_fragment_footer, null, false);
        lvCards.addFooterView(mProgressBarFooter);

        adapter = new JobCardAdapter(view.getContext());
        lvCards.setAdapter(adapter);

        currPage = 1;
        endOfPage = false;

        lvCards.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(adapter.getCount()==0 || (firstVisibleItem + visibleItemCount == totalItemCount)) {
                    Log.d("","End reached for Job: " + String.valueOf(endOfPage));
                    if (!endOfPage) {
                        Log.d("","Job Next Page : " + String.valueOf(currPage));
                        new JobCardHttpGet(getResources().getString(R.string.get_jobs) + "?page=" + currPage + "&limit=" + limit).execute();
                        currPage++;
                    }
                }
            }
        });

        lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer pk = adapter.getItem(position).pk;
                Intent intent = new Intent(context, JobActivity.class);
                Bundle b = new Bundle();
                b.putInt("pk",pk);
                b.putString("postNm",adapter.getItem(position).getJobAttr(1));
                b.putString("rctBrd",adapter.getItem(position).getJobAttr(2));
                b.putString("qual",adapter.getItem(position).getJobAttr(3));
                b.putString("totVacc",adapter.getItem(position).getJobAttr(4));
                b.putString("lastDt",adapter.getItem(position).getJobAttr(5));
                intent.putExtras(b);
                startActivity(intent);
            }
        });

    }

    public class JobCardHttpGet extends AsyncTask<Void, Void, Void> {
        String url;
        String data = "";

        JobCardHttpGet(String url) {
            this.url = url + "&" + param;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                sleep(1000);
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
                currPage--;
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            try{
                JSONObject jObject = new JSONObject(data);
                if(jObject.getBoolean("next") == false) {
                    JobFragment.endOfPage = Boolean.TRUE;
                    lvCards.removeFooterView(mProgressBarFooter);
                }

                JSONArray jArray = jObject.getJSONArray("data");
                for (int i = 0; i < jArray.length(); i++) {
                    String postNm = jArray.getJSONObject(i).getJSONObject("fields").getString("postNm");
                    Integer pk = jArray.getJSONObject(i).getInt("pk");
                    String rctBrd = "-";
                    String qual = "-";
                    String lastDt = "-";
                    String postDt = "-";
                    try{
                        JSONArray job = new JSONArray(jArray.getJSONObject(i).getJSONObject("fields").getString("job"));
                        rctBrd = job.getJSONObject(0).getJSONObject("fields").getString("rctBrd");
                        qual = job.getJSONObject(0).getJSONObject("fields").getString("qual");
                        lastDt = job.getJSONObject(0).getJSONObject("fields").getString("lastDt");
                        postDt = job.getJSONObject(0).getJSONObject("fields").getString("postDt").replace('/','-');
                    } catch (Exception e) {
                        Log.e("GET","Error : " + e);
                    }

                    String totVacc = jArray.getJSONObject(i).getJSONObject("fields").getString("totVacc");
                    String jobAttr[] = {String.valueOf(R.drawable.launcher_background),postNm,rctBrd,qual,"Total Vacancy :" + totVacc,lastDt,postDt};
                    JobFragment.adapter.addAll(new JobCardModel(pk,jobAttr));
                    if (HomeActivity.searchView != null) {
                        JobFragment.adapter.getFilter().filter(HomeActivity.searchView.getQuery());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        }
    }

}