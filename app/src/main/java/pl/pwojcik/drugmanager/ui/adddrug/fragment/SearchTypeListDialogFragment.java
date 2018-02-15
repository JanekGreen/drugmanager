package pl.pwojcik.drugmanager.ui.adddrug.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pwojcik.pl.archcomponentstestproject.R;

public class SearchTypeListDialogFragment extends BottomSheetDialogFragment {

    private Listener mListener;

    public static SearchTypeListDialogFragment newInstance() {
        return new SearchTypeListDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scantype_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ScanTypeAdapter());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface Listener {
        void onScanTypeClicked(int position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_scantype_list_dialog_item, parent, false));
            text = (TextView) itemView.findViewById(R.id.text);
            text.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onScanTypeClicked(getAdapterPosition());
                    dismiss();
                }
            });
        }

    }

    private class ScanTypeAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final String[] TYPE_NAMES={"Dodaj lek skanując kod kreskowy","Dodaj lek podając nazwę"};

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(String.valueOf(TYPE_NAMES[position]));
            if(position == 0){
               holder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_photo_camera_black_24dp, 0, 0, 0);
            }else{
                holder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_black_24dp, 0, 0, 0);
            }
        }

        @Override
        public int getItemCount() {
            return TYPE_NAMES.length;
        }

    }

}
