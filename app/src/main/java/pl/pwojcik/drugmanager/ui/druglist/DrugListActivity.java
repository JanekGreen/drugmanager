package pl.pwojcik.drugmanager.ui.druglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pwojcik.drugmanager.ui.adddrug.AddDrugActivity;
import pwojcik.pl.archcomponentstestproject.R;

public class DrugListActivity extends AppCompatActivity {


    @BindView(R.id.navigation)
     BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_morning:
                        return true;
                    case R.id.navigation_noon:
                        return true;
                    case R.id.navigation_afternoon:
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_list);
        ButterKnife.bind(this);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @OnClick(R.id.fabAdd)
     void onAddClick(){
        Intent intent = new Intent(this,AddDrugActivity.class);
        startActivity(intent);
    }
}
