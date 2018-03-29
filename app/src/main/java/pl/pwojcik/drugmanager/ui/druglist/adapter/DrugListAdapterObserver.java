package pl.pwojcik.drugmanager.ui.druglist.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;

import pl.pwojcik.drugmanager.ui.druglist.fragment.DrugListFragment;
import pl.pwojcik.drugmanager.utils.Constants;

/**
 * Created by pawel on 22.03.18.
 */

public class DrugListAdapterObserver extends RecyclerView.AdapterDataObserver {
    private DrugListFragment fragment;
    private String activeFragment;
    private RecyclerView recyclerView;
    private HashMap<String,View> emptyViews;

    public DrugListAdapterObserver(RecyclerView rv, HashMap<String,View> emptyViews, String activeFragment) {
       this.emptyViews = emptyViews;
       this.recyclerView = rv;
       this.activeFragment = activeFragment;
        System.out.println("constructor");
        //checkIfEmpty();
    }
    public DrugListAdapterObserver(DrugListFragment fragment, RecyclerView rv, HashMap<String,View> emptyViews, String activeFragment) {
        this.emptyViews = emptyViews;
        this.recyclerView = rv;
        this.activeFragment = activeFragment;
        this.fragment = fragment;
        //checkIfEmpty();
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

            if (fragment != null) {
                if (emptyViewVisible) {
                    fragment.setLayoutForView(Constants.EMPTY_VIEW);
                } else {
                    fragment.setLayoutForView(Constants.BUSY_VIEW);
                }
            }
        }
    }

    public void onChanged() {
        System.out.println("onChanged");
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
    }
}
