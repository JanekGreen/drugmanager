package pl.pwojcik.drugmanager.ui.druginfo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 15.02.18.
 */

public class ActiveSubstanceAdapter extends RecyclerView.Adapter<ActiveSubstanceAdapter.DefinedTimeViewHolder> {

   private List<String> activeSubstances;

    public ActiveSubstanceAdapter(){

    }

    public List<String> getActiveSubstances() {
        return activeSubstances;
    }

    public void setActiveSubstances(List<String> activeSubstances) {
        this.activeSubstances = activeSubstances;
    }

    @Override
    public DefinedTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.active_substances_list_row, parent, false);
        return new DefinedTimeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DefinedTimeViewHolder holder, int position) {
        holder.tvActiveSubstance.setText(activeSubstances.get(position));
    }

    @Override
    public int getItemCount() {
        return activeSubstances == null ? 0 : activeSubstances.size();
    }



     public class DefinedTimeViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvActiveSubstance)
        TextView tvActiveSubstance;


        public DefinedTimeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
