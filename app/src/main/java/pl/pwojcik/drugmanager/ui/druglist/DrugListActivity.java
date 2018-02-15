package pl.pwojcik.drugmanager.ui.druglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pwojcik.drugmanager.ui.adddrug.AddDrugActivity;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.SearchTypeListDialogFragment;
import pl.pwojcik.drugmanager.utils.Constants;
import pwojcik.pl.archcomponentstestproject.R;

public class DrugListActivity extends AppCompatActivity implements SearchTypeListDialogFragment.Listener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_list);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.fabAdd)
     void onAddClick(){
        SearchTypeListDialogFragment.newInstance().show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onScanTypeClicked(int position) {
        Intent intent = new Intent(this,AddDrugActivity.class);
        if(position == 0){
            intent.putExtra("SEARCH_TYPE_FRAGMENT", Constants.ADD_BARCODE_TAG_NAME);
        }else{
            intent.putExtra("SEARCH_TYPE_FRAGMENT", Constants.ADD_NAME_TAG_NAME);
        }
        startActivity(intent);
    }

}
