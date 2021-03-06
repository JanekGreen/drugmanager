package pl.pwojcik.drugmanager.ui.adddrug.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimesDays;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.utils.Misc;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 15.02.18.
 */

public class DefinedTimeAdapter extends RecyclerView.Adapter<DefinedTimeAdapter.DefinedTimeViewHolder> {
    private List<DefinedTime> definedTimes;
    private Set<Long> drugTimes;
    private SwitchChangeCallback switchChangeCallback;
    private DrugViewModel drugViewModel;


    public DefinedTimeAdapter(List<DefinedTime> definedTimes) {
        this.definedTimes = definedTimes;
    }

    public DefinedTimeAdapter() {

    }

    public void setDrugTimes(Set<Long> drugTimes) {
        this.drugTimes = drugTimes;
    }

    public void setDrugViewModel(DrugViewModel drugViewModel) {
        this.drugViewModel = drugViewModel;
    }

    public void setSwitchChangeCallback(SwitchChangeCallback switchChangeCallback) {
        this.switchChangeCallback = switchChangeCallback;
    }

    public List<DefinedTime> getDefinedTimes() {
        return definedTimes;
    }

    public void setDefinedTimes(List<DefinedTime> definedTimes) {
        this.definedTimes = definedTimes;
    }

    @Override
    public DefinedTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.defined_times_list_row, parent, false);
        return new DefinedTimeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DefinedTimeViewHolder holder, int position) {
        System.out.println("OnBindViewHolder called " + definedTimes.get(position).getName());
        DefinedTime definedTime = definedTimes.get(position);
        holder.tvDefinedTime.setText(definedTime.getName());
        holder.tvTime.setText(definedTime.getTime());
        if (drugTimes.contains(definedTime.getId())) {
            holder.swSelected.setChecked(true);
            holder.tvTime.setTextColor(Color.BLACK);
        } else {
            holder.tvTime.setTextColor(Color.GRAY);
        }
        drugViewModel.getDefinedTimesDays(definedTimes.get(position).getId())
                .subscribeOn(Schedulers.io())
                .flatMap(definedTimesDays -> io.reactivex.Observable.fromIterable(definedTimesDays)
                        .map(DefinedTimesDays::getDay)
                        .toList()
                        .map(Misc::getWeekDayNames)
                        .toMaybe())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(daysNames -> holder.tvDrugDays.setText(daysNames));

    }

    @Override
    public int getItemCount() {
        return definedTimes == null ? 0 : definedTimes.size();
    }

    public interface SwitchChangeCallback {
        void onCheckedChangedCallback(long definedTimeId, boolean isSelected);

    }

     class DefinedTimeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvDefinedTime)
        TextView tvDefinedTime;

        @BindView(R.id.tvTime)
        TextView tvTime;

        @BindView(R.id.tvDefinedDays)
        TextView tvDrugDays;

        @BindView(R.id.swSelected)
        Switch swSelected;

        public DefinedTimeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnCheckedChanged(R.id.swSelected)
        public void onCheckedChanged(CompoundButton compoundButton, boolean isSelected) {
            if (isSelected) {
                tvTime.setTextColor(Color.BLACK);
            } else {
                tvTime.setTextColor(Color.GRAY);
            }
            switchChangeCallback.onCheckedChangedCallback(definedTimes.get(getAdapterPosition()).getId(), isSelected);


        }

    }
}
