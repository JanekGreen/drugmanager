package pl.pwojcik.drugmanager.ui.uicomponents;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.github.ivbaranov.mli.MaterialLetterIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pwojcik.pl.archcomponentstestproject.R;


/**
 * Created by pawel on 11.03.18.
 */

public class DayPicker extends LinearLayout implements View.OnClickListener {


    private static final int WEEK_SIZE = 7;

    public interface DaySelectionChangedListener {
        void onDaySelectionChanged(List<Integer> activeDays,List<String> selectedDaysNames);
    }

    private HashMap<Integer, String[]> names;
    private DaySelectionChangedListener daySelectionChangedListener;
    private HashMap<Integer,Day> days = new HashMap<>();

    public DayPicker(@NonNull Context context) {
        super(context);
    }


    public DayPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        names = new HashMap<>();
        names.put(6, new String[]{"Nd", "Niedziela"});
        names.put(0, new String[]{"Pon", "Poniedziałek"});
        names.put(1, new String[]{"Wt", "Wtorek"});
        names.put(2, new String[]{"Śr", "Środa"});
        names.put(3, new String[]{"Czw", "Czwartek"});
        names.put(4, new String[]{"Pt", "Piątek"});
        names.put(5, new String[]{"Sob", "Sobota"});

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.day_picker_view, this, true);

        for (int i = 0; i < WEEK_SIZE; i++) {
            MaterialLetterIcon mi = (MaterialLetterIcon) getChildAt(i);
            mi.setOnClickListener(this);
            mi.setLettersNumber(names.get(i)[0].length());
            mi.setLetter(names.get(i)[0]);
            mi.setLetterSize(12);

            Day day = new Day(i, names.get(i)[1], names.get(i)[0], mi, true);
            days.put(getChildAt(i).getId(),day);
        }

        handleStateChange();


    }

    public DayPicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DayPicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDaySelectionChangedListener(DaySelectionChangedListener daySelectionChangedListener) {
        this.daySelectionChangedListener = daySelectionChangedListener;
    }

    private void handleStateChange() {
        for (Day d : days.values()) {
            if (d.isActive) {
                d.materialLetterIcon.setShapeColor(getResources().getColor(R.color.colorAccent));
                d.materialLetterIcon.setLetterColor(Color.WHITE);
                d.materialLetterIcon.setBorder(true);
                d.materialLetterIcon.setBorderColor(Color.WHITE);
            } else {
                d.materialLetterIcon.setShapeColor(Color.WHITE);
                d.materialLetterIcon.setLetterColor(Color.BLACK);
                d.materialLetterIcon.setBorder(true);
                d.materialLetterIcon.setBorderColor(Color.BLACK);
            }
        }
    }

    @Override
    public void onClick(View v) {
      handLeDayClick(days.get(v.getId()));
    }

    private void handLeDayClick(Day day) {
        day.isActive = !day.isActive;
        handleStateChange();
        if (daySelectionChangedListener != null) {
            daySelectionChangedListener.onDaySelectionChanged(getActiveDaysList(),getActiveDaysNameList());
        }
    }

    private List<Integer> getActiveDaysList() {
        List<Integer> selectedDays = new ArrayList<>();
        for (Day day : days.values()) {
            if (day.isActive)
                selectedDays.add(day.getJavaOrder());
        }
        return selectedDays;
    }
    private List<String> getActiveDaysNameList() {
        List<String> selectedDays = new ArrayList<>();
        for (Day day : days.values()) {
            if (day.isActive)
                selectedDays.add(day.name);
        }
        return selectedDays;
    }

    public void setActiveDays(List<Integer> activeDays){
        for(Day d : days.values()){
            d.isActive = activeDays.contains(d.getJavaOrder());
        }
        handleStateChange();
    }

    class Day {
        int ord;
        String name;
        String abbrName;
        MaterialLetterIcon materialLetterIcon;
        boolean isActive;

        public Day(int ord, String name, String abbrName, MaterialLetterIcon materialLetterIcon, boolean isActive) {
            this.ord = ord;
            this.name = name;
            this.abbrName = abbrName;
            this.materialLetterIcon = materialLetterIcon;
            this.isActive = isActive;
        }

        private int getJavaOrder() {
            return ord < 6 ? ord + 2 : 1;
        }

    }
    @Override
    public Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putIntegerArrayList("activeDays", new ArrayList<>(this.getActiveDaysList())); // ... save stuff
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle)
        {
            Bundle bundle = (Bundle) state;
            ArrayList<Integer> activeDays = bundle.getIntegerArrayList("activeDays");
            setActiveDays(activeDays);
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

}