package pl.pwojcik.drugmanager.ui.druglist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.github.ivbaranov.mli.MaterialLetterIcon;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 19.02.18.
 */

public class DrugListAdapter extends RecyclerView.Adapter<DrugListAdapter.DrugListViewHolder> {
   private List<DrugDb> drugsForTime;
    
    @Override
    public DrugListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.druglist_item_row, parent, false);
        return new DrugListViewHolder(itemView);
    }

    public DrugListAdapter(List<DrugDb> drugsForTime) {
        this.drugsForTime = drugsForTime;
    }

    @Override
    public void onBindViewHolder(DrugListViewHolder holder, int position) {

        System.out.println("drugs for time item" +drugsForTime.get(position).toString());
        String name = drugsForTime.get(position).getName();
        String description = drugsForTime.get(position).getUsageType()+" - "+
                drugsForTime.get(position).getProducer();

       holder.materialLetterIcon.setLetter(name);
       holder.materialLetterIcon.setLettersNumber(3);
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
    }
}
