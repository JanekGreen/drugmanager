package pl.pwojcik.drugmanager.ui.druglist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.github.ivbaranov.mli.MaterialLetterIcon;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 19.02.18.
 */

public class DrugListAdapter extends RecyclerView.Adapter<DrugListAdapter.DrugListViewHolder> {
   private List<DrugDb> drugsForTime;
   private OnDrugListAdapterItemClick onDrugListAdapterItemClick;


    public interface OnDrugListAdapterItemClick {
       void onAdapterItemClick(int position, View sharedElement);
    }

    public void setOnDrugListAdapterItemClick(OnDrugListAdapterItemClick onDrugListAdapterItemClick) {
        this.onDrugListAdapterItemClick = onDrugListAdapterItemClick;
    }

    @Override
    public DrugListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.druglist_item_row, parent, false);
        return new DrugListViewHolder(itemView);
    }

    public DrugListAdapter(List<DrugDb> drugsForTime) {
        this.drugsForTime = drugsForTime;
        notifyDataSetChanged();
    }
    public DrugListAdapter() {
        this.drugsForTime = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setDrugsForTime(List<DrugDb> drugsForTime) {
        this.drugsForTime = drugsForTime;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(DrugListViewHolder holder, int position) {

        //System.out.println("drugs for time item" +drugsForTime.get(position).toString());
        String name = drugsForTime.get(position).getName();
        String description = drugsForTime.get(position).getUsageType()+" - "+
                drugsForTime.get(position).getProducer();

       holder.materialLetterIcon.setLetter(name);
       holder.materialLetterIcon.setTransitionName(drugsForTime.get(position).getName());
       holder.tvDrugName.setText(name);
       holder.tvDescription.setText(description);

    }

    public DrugDb removeItem(int position){
        DrugDb removedItem = drugsForTime.remove(position);
        notifyItemRemoved(position);
        return  removedItem;
    }

    public void restoreItem(DrugDb item, int position){
        drugsForTime.add(position, item);
        notifyItemInserted(position);
    }

    public void clearData(){
        drugsForTime.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return drugsForTime != null? drugsForTime.size(): 0;
    }

    public class DrugListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvDrugName)
        TextView tvDrugName;
        @BindView(R.id.tvDescription)
        TextView tvDescription;
        @BindView(R.id.initialLetterIcon)
        MaterialLetterIcon materialLetterIcon;

        public DrugListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.adapter_item)
        void onAdapterItemClick(){
            onDrugListAdapterItemClick.onAdapterItemClick(getAdapterPosition(), materialLetterIcon);
        }
    }
}
