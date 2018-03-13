package pl.pwojcik.drugmanager.ui.druglist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimesDays;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimesDaysDao;
import pl.pwojcik.drugmanager.utils.Misc;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 15.02.18.
 */

public class NewDefinedTimeAdapter extends RecyclerView.Adapter<NewDefinedTimeAdapter.DefinedTimeViewHolder> {

    private final Context context;
    private List<DefinedTime> definedTimes;
    private OnNewDefinedTimesAdapterItemClick onNewDefinedTimesAdapterItemClick;
    private DefinedTimesDaysDao definedTimesDaysDao;

    public interface OnNewDefinedTimesAdapterItemClick {
        void onDefinedTimeAdapterItemClick(int position);
    }
    public NewDefinedTimeAdapter(Context context){
        this.context = context;
        definedTimesDaysDao = DrugmanagerApplication.getDbInstance(context).getDefinedTimesDaysDao();
    }

    public List<DefinedTime> getDefinedTimes() {
        return definedTimes;
    }

    public void setOnNewDefinedTimesAdapterItemClick(OnNewDefinedTimesAdapterItemClick onNewDefinedTimesAdapterItemClick) {
        this.onNewDefinedTimesAdapterItemClick = onNewDefinedTimesAdapterItemClick;
    }

    public void setDefinedTimes(List<DefinedTime> definedTimes) {
        this.definedTimes = definedTimes;
    }

    @Override
    public DefinedTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_defined_times_list_row, parent, false);
        return new DefinedTimeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DefinedTimeViewHolder holder, int position) {

        DefinedTime definedTime = definedTimes.get(position);
        holder.tvDefinedTime.setText(definedTime.getName());
        holder.tvTime.setText(definedTime.getTime());

        definedTimesDaysDao.getDefinedTimeDaysForDefinedTime(definedTimes.get(position).getId())
                .subscribeOn(Schedulers.io())
                .flatMap(definedTimesDays -> io.reactivex.Observable.fromIterable(definedTimesDays)
                        .map(DefinedTimesDays::getDay)
                        .toList()
                        .map(Misc::getWeekDayNames)
                        .toMaybe())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(daysNames -> {
                    holder.tvDefinedDays.setText(daysNames);
                });
    }

    @Override
    public int getItemCount() {
        return definedTimes == null ? 0 : definedTimes.size();
    }

    public DefinedTime removeItem(int position){
        DefinedTime removedItem = definedTimes.remove(position);
        notifyItemRemoved(position);
        return  removedItem;
    }

    public void restoreItem(DefinedTime item, int position){
        definedTimes.add(position, item);
        notifyItemInserted(position);
    }

     public class DefinedTimeViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvDefinedTime)
        TextView tvDefinedTime;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.tvDefinedDays)
        TextView tvDefinedDays;

        public DefinedTimeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
         @OnClick(R.id.newDefinedTimeAdapterItem)
         public void onNewDefinedTimeItemClicked(){
            if(onNewDefinedTimesAdapterItemClick!=null)
                onNewDefinedTimesAdapterItemClick.onDefinedTimeAdapterItemClick(getAdapterPosition());
         }

    }
}
