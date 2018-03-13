package pl.pwojcik.drugmanager.ui.druglist.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimeDao;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimesDays;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimesDaysDao;
import pl.pwojcik.drugmanager.utils.Misc;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 19.02.18.
 */

public  class MainListSpinnerAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
    private final ThemedSpinnerAdapter.Helper mDropDownHelper;
    private DefinedTimeDao definedTimeDao = DrugmanagerApplication.getDbInstance(getContext()).getDefinedTimesDao();
    private DefinedTimesDaysDao definedTimesDaysDao = DrugmanagerApplication.getDbInstance(getContext()).getDefinedTimesDaysDao();

    public MainListSpinnerAdapter(Context context, List<String> drugTakingTimes) {
        super(context, android.R.layout.simple_list_item_1, drugTakingTimes);
        mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null){
            LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
            view = inflater.inflate(R.layout.spinner_view, parent, false);
        } else {
            view = convertView;
        }

        TextView textView = view.findViewById(R.id.tvSpinTimeName);
        TextView textView2 = view.findViewById(R.id.tvSpinTimeDays);
        textView.setText(getItem(position));
        String query = getItem(position).substring(0,getItem(position).indexOf(" -")).trim();
        definedTimeDao.getDefinedTimeIdForName(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(definedTimeId -> definedTimesDaysDao.getDefinedTimeDaysForDefinedTime(definedTimeId)
                        .subscribeOn(Schedulers.io())
                        .flatMap(definedTimesDays -> io.reactivex.Observable.fromIterable(definedTimesDays)
                                .map(DefinedTimesDays::getDay)
                                .toList()
                                .map(Misc::getWeekDayNames)
                                .toMaybe())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(textView2::setText));


        return view;
    }

    @Override
    public Resources.Theme getDropDownViewTheme() {
        return mDropDownHelper.getDropDownViewTheme();
    }

    @Override
    public void setDropDownViewTheme(Resources.Theme theme) {
        mDropDownHelper.setDropDownViewTheme(theme);
    }
}