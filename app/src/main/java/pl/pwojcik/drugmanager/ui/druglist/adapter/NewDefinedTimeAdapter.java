package pl.pwojcik.drugmanager.ui.druglist.adapter;

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

public class NewDefinedTimeAdapter extends RecyclerView.Adapter<NewDefinedTimeAdapter.DefinedTimeViewHolder> {

   private List<DefinedTime> definedTimes;
   private OnNewDefinedTimesAdapterItemClick onNewDefinedTimesAdapterItemClick;

    public interface OnNewDefinedTimesAdapterItemClick {
        void onDefinedTimeAdapterItemClick(int position);
    }
    public NewDefinedTimeAdapter(){

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
