package com.example.aditya.result24;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.Thread.sleep;

public class QuizFragment extends Fragment {

    static Boolean endOfPage;
    static QuizCardAdapter adapter;
    static Context context;
    int limit = 10, currPage;

    ListView lvCards;
    Boolean click;
    View mProgressBarFooter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quiz_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = this.getContext();
        lvCards = view.findViewById(R.id.quiz_list_cards);

        mProgressBarFooter = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.quiz_fragment_footer, null, false);
        lvCards.addFooterView(mProgressBarFooter);

        adapter = new QuizCardAdapter(view.getContext());
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
                    Log.d("","End reached for Quiz: " + String.valueOf(endOfPage));
                    if (!endOfPage) {
                        Log.d("","Quiz Next Page: " + String.valueOf(currPage));
                        new QuizFragment.QuizCardHttpGet(getResources().getString(R.string.get_quizzes) + "?page=" + currPage + "&limit=" + limit).execute();
                        currPage++;
                    }
                }
            }
        });

        lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer pk = adapter.getItem(position).pk;
                Intent intent = new Intent(context, QuizActivity.class);
                Bundle b = new Bundle();
                b.putInt("pk",pk);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    public class QuizCardHttpGet extends AsyncTask<Void, Void, Void> {
        String url;
        String data = "";
        QuizCardHttpGet(String url) {
            this.url = url;
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
                    QuizFragment.endOfPage = Boolean.TRUE;
                    lvCards.removeFooterView(mProgressBarFooter);
                }

                JSONArray jArray = jObject.getJSONArray("data");
                for (int i = 0; i < jArray.length(); i++) {
                    String quizNm = jArray.getJSONObject(i).getJSONObject("fields").getString("quizNm");
                    String brief = jArray.getJSONObject(i).getJSONObject("fields").getString("brief");
                    String totQues = jArray.getJSONObject(i).getJSONObject("fields").getString("totQues");
                    String time = jArray.getJSONObject(i).getJSONObject("fields").getString("time");
                    String postDt = jArray.getJSONObject(i).getJSONObject("fields").getString("postDt");
                    Integer pk = jArray.getJSONObject(i).getInt("pk");

                    String quizAttr[] = {String.valueOf(R.drawable.launcher_background),quizNm,"Total Questions : " + totQues,brief,postDt};
                    QuizFragment.adapter.addAll(new QuizCardModel(pk,quizAttr));
                }

                if (HomeActivity.searchView != null) {
                    JobFragment.adapter.getFilter().filter(HomeActivity.searchView.getQuery());
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        }
    }
}