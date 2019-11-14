package com.example.aditya.result24;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class QuizCardAdapter extends ArrayAdapter<QuizCardModel> implements Filterable {
    public ArrayList<QuizCardModel> quizCardModelList = new ArrayList<>();

    public QuizCardAdapter(Context context) {
        super(context, R.layout.quiz_card_item);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.quiz_card_item, parent, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QuizCardModel model = getItem(position);


//            holder.quizImage.setImageResource(Integer.valueOf(model.getQuizAttr()[0]));
        holder.quizName.setText(Html.fromHtml(model.getQuizAttrFinal()[1]));
        holder.quizTotalQuestions.setText(Html.fromHtml(model.getQuizAttrFinal()[2]));
        holder.quizDescription.setText(Html.fromHtml(model.getQuizAttrFinal()[3]));
        holder.quizPostDate.setText(Html.fromHtml(model.getQuizAttrFinal()[4]));

        return convertView;
    }

    static class ViewHolder {
        ImageView quizImage;
        TextView quizName;
        TextView quizTotalQuestions;
        TextView quizDescription;
        TextView quizPostDate;


        ViewHolder(View view) {

            quizImage = (ImageView) view.findViewById(R.id.quiz_image);
            quizName = (TextView) view.findViewById(R.id.quiz_name_text);
            quizTotalQuestions = (TextView) view.findViewById(R.id.quiz_total_questions_text);
            quizDescription = (TextView) view.findViewById(R.id.quiz_description_text);
            quizPostDate = (TextView) view.findViewById(R.id.quiz_postdate_text);

        }
    }

    @Override
    public void addAll(QuizCardModel... quizCardModels) {
        super.addAll(quizCardModels);
        for (int i = 0; i < quizCardModels.length; i++) {
            quizCardModelList.add(quizCardModels[i]);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                if(constraint != null) {
                    List<QuizCardModel> filteredResult = new ArrayList<>();
                    for (int i = 0; i < quizCardModelList.size(); i++){
                        QuizCardModel currentQuizCard = quizCardModelList.get(i);
                        for (int j = 0; j < currentQuizCard.getQuizAttr().length; j++) {
                            int index = currentQuizCard.getQuizAttr()[j].toLowerCase().indexOf(constraint.toString().toLowerCase());
                            if (index > -1) {
                                String currString = currentQuizCard.getQuizAttr(j);
                                currentQuizCard.setQuizAttr(j,currString.replace(currString.substring(index,index+constraint.length()),
                                        "<font color='#1e6286'>"+currString.substring(index,index+constraint.length())+"</font>"));
                                filteredResult.add(currentQuizCard);
                                break;
                            }
                        }
                    }
                    // Now assign the values and count to the FilterResults object
                    oReturn.values = filteredResult;
                    oReturn.count = filteredResult.size();
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null) {
                    QuizCardAdapter.super.clear();
                    QuizCardAdapter.super.addAll((ArrayList<QuizCardModel>)results.values);
                    notifyDataSetChanged();
                }
                else
                    notifyDataSetInvalidated();
            }
        };
    }

    public void retrieveResults() {
        QuizCardAdapter.super.clear();
        for (QuizCardModel quizCardModel : quizCardModelList) {
            quizCardModel.setQuizAttr(quizCardModel.getQuizAttr());
        }

        QuizCardAdapter.super.addAll(quizCardModelList);
        notifyDataSetChanged();
    }
}
