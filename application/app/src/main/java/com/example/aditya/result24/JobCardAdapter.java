package com.example.aditya.result24;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
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

public class JobCardAdapter extends ArrayAdapter<JobCardModel> implements Filterable {
    public ArrayList<JobCardModel> jobCardModelList = new ArrayList<>();

    public JobCardAdapter(Context context) {
        super(context, R.layout.job_card_item);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.job_card_item, parent, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JobCardModel model = getItem(position);


//      holder.jobImage.setImageResource(Integer.valueOf(model.getJobAttrFinal()[0]));
        holder.jobName.setText(Html.fromHtml(model.getJobAttrFinal()[1]));
        holder.jobRecruiter.setText(Html.fromHtml(model.getJobAttrFinal()[2]));
        holder.jobQualification.setText(Html.fromHtml(model.getJobAttrFinal()[3]));
        holder.jobVacancy.setText(Html.fromHtml(model.getJobAttrFinal()[4]));
        holder.jobLastDate.setText(Html.fromHtml(model.getJobAttrFinal()[5]));
        holder.jobPostDate.setText(Html.fromHtml(model.getJobAttrFinal()[6]));

        return convertView;
    }

    static class ViewHolder {
        ImageView jobImage;
        TextView jobName;
        TextView jobRecruiter;
        TextView jobQualification;
        TextView jobVacancy;
        TextView jobLastDate;
        TextView jobPostDate;

        ViewHolder(View view) {

            jobImage = (ImageView) view.findViewById(R.id.job_image);
            jobName = (TextView) view.findViewById(R.id.job_name_text);
            jobRecruiter = (TextView) view.findViewById(R.id.job_recruiter_text);
            jobQualification = (TextView) view.findViewById(R.id.job_qualification_text);
            jobVacancy = (TextView) view.findViewById(R.id.job_vacancy_text);
            jobLastDate = (TextView) view.findViewById(R.id.job_date_text);
            jobPostDate = (TextView) view.findViewById(R.id.job_postdate_text);

        }
    }

    @Override
    public void addAll(JobCardModel... jobCardModels) {
        super.addAll(jobCardModels);
        for (int i = 0; i < jobCardModels.length; i++) {
            jobCardModelList.add(jobCardModels[i]);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                if(constraint != null) {
                    List<JobCardModel> filteredResult = new ArrayList<>();
                    for (int i = 0; i < jobCardModelList.size(); i++){
                        JobCardModel currentJobCard = jobCardModelList.get(i);
                        for (int j = 0; j < currentJobCard.getJobAttr().length; j++) {
                            int index = currentJobCard.getJobAttr()[j].toLowerCase().indexOf(constraint.toString().toLowerCase());
                            if (index > -1) {
                                String currString = currentJobCard.getJobAttr(j);
                                currentJobCard.setJobAttr(j,currString.replace(currString.substring(index,index+constraint.length()),
                                        "<font color='#1e6286'>"+currString.substring(index,index+constraint.length())+"</font>"));
                                filteredResult.add(currentJobCard);
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
                    JobCardAdapter.super.clear();
                    JobCardAdapter.super.addAll((ArrayList<JobCardModel>)results.values);
                    notifyDataSetChanged();
                }
                else
                    notifyDataSetInvalidated();
            }
        };
    }

    public void retrieveResults() {
        JobCardAdapter.super.clear();
        for (JobCardModel jobCardModel : jobCardModelList) {
            jobCardModel.setJobAttr(jobCardModel.getJobAttr());
        }

        JobCardAdapter.super.addAll(jobCardModelList);
        notifyDataSetChanged();
    }
}
