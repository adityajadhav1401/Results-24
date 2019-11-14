package com.example.aditya.result24;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.transitionseverywhere.TransitionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.Thread.sleep;


public class QuizActivity extends AppCompatActivity {

    QuestionModel quesList[];
    String chosenAns[];
    Context context;
    QuestionModel currentQ;
    TextView idQuestion, txtQuestion;
    RadioButton rda, rdb, rdc, rdd;
    TextView a, b, c, d;
    CardView cda, cdb, cdc, cdd;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    ViewGroup transitionsContainer;
    Button butNext;
    Boolean marked = false;
    int qid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        context = this;
        progressBar = findViewById(R.id.progressBar);
        linearLayout = findViewById(R.id.progress_layout);
        transitionsContainer = (ViewGroup) findViewById(R.id.question_layout);

        Integer pk = 1;

        Bundle bundle = getIntent().getExtras();
        if  (bundle != null) {
            pk = bundle.getInt("pk");
        }

        String url = getResources().getString(R.string.get_quizzes_detail) + "?pk=" + String.valueOf(pk);
        new QuestionHttpGet(url).execute();

        txtQuestion=(TextView)findViewById(R.id.question);
        idQuestion=(TextView)findViewById(R.id.question_number);
        rda=(RadioButton)findViewById(R.id.r_opt_a);
        rdb=(RadioButton)findViewById(R.id.r_opt_b);
        rdc=(RadioButton)findViewById(R.id.r_opt_c);
        rdd=(RadioButton)findViewById(R.id.r_opt_d);

        a=(TextView)findViewById(R.id.opt_a);
        b=(TextView)findViewById(R.id.opt_b);
        c=(TextView)findViewById(R.id.opt_c);
        d=(TextView)findViewById(R.id.opt_d);

        cda=(CardView) findViewById(R.id.a_card);
        cdb=(CardView)findViewById(R.id.b_card);
        cdc=(CardView)findViewById(R.id.c_card);
        cdd=(CardView)findViewById(R.id.d_card);

        butNext=(Button)transitionsContainer.findViewById(R.id.next_button);

        butNext.setOnClickListener(new View.OnClickListener() {

            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(transitionsContainer);

                RadioGroup grp=(RadioGroup)findViewById(R.id.radioGroup);
                RadioButton answer=(RadioButton)findViewById(grp.getCheckedRadioButtonId());
                grp.clearCheck();

                if (answer == null) chosenAns[qid-1] = "";
                else chosenAns[qid-1] = (String) answer.getText();

                if(qid < quesList.length){

                    currentQ = quesList[qid];
                    setQuestionView();

                } else {
                    int score = 0;
                    for (int i=0; i<quesList.length; i++) {
                        if(chosenAns[i] != null && chosenAns[i].equals(quesList[i].getAnswer())) score++;
                    }

                    Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("score", score);   //Your score
                    intent.putExtras(b);        //Put your score to your next Intent
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_quiz, menu);
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

    private void setQuestionView() {
        cda.setCardBackgroundColor(getResources().getColor(R.color.white));
        cdb.setCardBackgroundColor(getResources().getColor(R.color.white));
        cdc.setCardBackgroundColor(getResources().getColor(R.color.white));
        cdd.setCardBackgroundColor(getResources().getColor(R.color.white));

        txtQuestion.setText(currentQ.getQuestion());
        idQuestion.setText("Q" + String.valueOf(currentQ.getId()) + ".");
        rda.setText(currentQ.getOptA());
        rdb.setText(currentQ.getOptB());
        rdc.setText(currentQ.getOptC());
        rdd.setText(currentQ.getOptD());

        a.setText(currentQ.getOptA());
        b.setText(currentQ.getOptB());
        c.setText(currentQ.getOptC());
        d.setText(currentQ.getOptD());

        qid++;
        marked = false;
    }

    public void checkA(View view) {
        if (!marked) {
            if (a.getText().equals(currentQ.getAnswer()))
                cda.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            else cda.setCardBackgroundColor(getResources().getColor(R.color.colorQuizPrimary));
            if (b.getText().equals(currentQ.getAnswer()))
                cdb.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            if (c.getText().equals(currentQ.getAnswer()))
                cdc.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            if (d.getText().equals(currentQ.getAnswer()))
                cdd.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            rda.setChecked(true);
            marked = true;
        }
    }

    public void checkB(View view) {
        if (!marked) {
            if (a.getText().equals(currentQ.getAnswer()))
                cda.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            if (b.getText().equals(currentQ.getAnswer()))
                cdb.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            else cdb.setCardBackgroundColor(getResources().getColor(R.color.colorQuizPrimary));
            if (c.getText().equals(currentQ.getAnswer()))
                cdc.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            if (d.getText().equals(currentQ.getAnswer()))
                cdd.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            rdb.setChecked(true);
            marked = true;
        }
    }

    public void checkC(View view) {
        if (!marked) {
            if (a.getText().equals(currentQ.getAnswer()))
                cda.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            if (b.getText().equals(currentQ.getAnswer()))
                cdb.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            if (c.getText().equals(currentQ.getAnswer()))
                cdc.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            else cdc.setCardBackgroundColor(getResources().getColor(R.color.colorQuizPrimary));
            if (d.getText().equals(currentQ.getAnswer()))
                cdd.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            rdc.setChecked(true);
            marked = true;
        }
    }

    public void checkD(View view) {
        if (!marked) {
            if (a.getText().equals(currentQ.getAnswer()))
                cda.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            if (b.getText().equals(currentQ.getAnswer()))
                cdb.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            if (c.getText().equals(currentQ.getAnswer()))
                cdc.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            if (d.getText().equals(currentQ.getAnswer()))
                cdd.setCardBackgroundColor(getResources().getColor(R.color.colorQuizAccent));
            else cdd.setCardBackgroundColor(getResources().getColor(R.color.colorQuizPrimary));
            rdd.setChecked(true);
            marked = true;
        }
    }

    public class QuestionHttpGet extends AsyncTask<Void, Void, Void> {
        String url;
        String data = "";
        QuestionHttpGet(String url) {
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
                quesList = new QuestionModel[jArray.length()];
                chosenAns = new String[jArray.length()];
                for (int i = 0; i < jArray.length(); i++) {
                    String question = jArray.getJSONObject(i).getJSONObject("fields").getString("question");
                    String optA = jArray.getJSONObject(i).getJSONObject("fields").getString("optA");
                    String optB = jArray.getJSONObject(i).getJSONObject("fields").getString("optB");
                    String optC = jArray.getJSONObject(i).getJSONObject("fields").getString("optC");
                    String optD = jArray.getJSONObject(i).getJSONObject("fields").getString("optD");
                    String answer = jArray.getJSONObject(i).getJSONObject("fields").getString("answer");

                    quesList[i] = new QuestionModel(i+1,question,optA,optB,optC,optD,answer);
                }

                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                currentQ=quesList[qid];
                setQuestionView();


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        }
    }

}