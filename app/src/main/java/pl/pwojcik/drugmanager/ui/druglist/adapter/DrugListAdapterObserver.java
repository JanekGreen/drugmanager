package pl.pwojcik.drugmanager.ui.druglist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;

/**
 * Created by pawel on 22.03.18.
 */

public class DrugListAdapterObserver extends RecyclerView.AdapterDataObserver {
    private String activeFragment;
    private RecyclerView recyclerView;
    private HashMap<String,View> emptyViews;

    public DrugListAdapterObserver(RecyclerView rv, HashMap<String,View> emptyViews, String activeFragment) {
       this.emptyViews = emptyViews;
       this.recyclerView = rv;
       this.activeFragment = activeFragment;
        checkIfEmpty();
    }


    private void checkIfEmpty() {
        for(View v:  emptyViews.values())
            v.setVisibility(View.GONE);

        recyclerView.setVisibility(View.GONE);

        View emptyView = emptyViews.get(activeFragment);
        if (emptyView != null && recyclerView.getAdapter() != null) {
            boolean emptyViewVisible = recyclerView.getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(emptyViewVisible ? View.GONE : View.VISIBLE);
        }
    }

    public void onChanged() {
        checkIfEmpty();
    }

    public void onItemRangeInserted(int positionStart, int itemCount) {
        checkIfEmpty();
    }

    public void onItemRangeRemoved(int positionStart, int itemCount) {
        checkIfEmpty();
    }

    public void setActiveFragment(String activeFragment){
        this.emptyViews.get(this.activeFragment).setVisibility(View.GONE);
        this.activeFragment = activeFragment;
        checkIfEmpty();
    }
}
