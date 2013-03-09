package osobny.uctovnik.activities;

import java.util.List;

import osobny.uctovnik.async.AsyncGraphTask.OnPohybyForGraphSelected;
import osobny.uctovnik.fragments.GrafFragment;
import osobny.uctovnik.fragments.PohybListFragment;
import osobny.uctovnik.helpers.DialogListener;
import osobny.uctovnik.objects.PohybForGraphObject;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PohybListActivity extends SherlockFragmentActivity
	implements DialogListener, PohybListFragment.OnPohybSelectedListener, OnPohybyForGraphSelected {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pohyb);
        initActionBarSherlock();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getSupportMenuInflater().inflate(R.menu.activity_list, menu);
        return true;
    }
    
    private void initActionBarSherlock() {
    	getSupportActionBar().setHomeButtonEnabled(true);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.menu_pohyb_add):
			showAddPohyb();
			break;
		case (R.id.menu_pohyb_od_do):
			showPohybOdDo();
			break;
		case (R.id.menu_show_all):
			showAll();
			break;
		case (R.id.menu_pohyb_prevod):
			showAddPrevod();
			break;
		case android.R.id.home:
			 Intent i = new Intent(this, MainMenuActivity.class);
			 i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 startActivity(i);
			 return true;
		default:
			break;
		}
		return true;
	}
	
	/*
	 * Pohyby changed: bud je zobrazeny 1 stlpec alebo 2
	 * ak 1 tak otvorime graf
	 * ak 2 tak prekreslim graf
	 * */
    @Override
	public void onPohybSelected() {
    	GrafFragment graf = (GrafFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_graf);
    	PohybListFragment pohyb = (PohybListFragment) getSupportFragmentManager()
    			.findFragmentById(R.id.fragment_pohyb_list);
		if (graf == null || !graf.isAdded()) {
			Intent intent = new Intent(this, GrafActivity.class);
			intent.putExtra(getResources().getString(R.string.intent_msg_list_pohyb), pohyb.getWhere());
			startActivity(intent);
		}
	}
    
    @Override
	public void onPohybyForGraphSelected(List<PohybForGraphObject> result) {
		GrafFragment grafFragment = (GrafFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_graf);
		if (grafFragment != null && grafFragment.isAdded()) {
			grafFragment.setPohyby(result);
		}
	}
	
	private void showAll() {
		PohybListFragment pohybFragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		if (pohybFragment != null && pohybFragment.isAdded()) {
			pohybFragment.showAll();
		}
	}
	
	private void showAddPrevod() {
		PohybListFragment pohybFragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		if (pohybFragment != null && pohybFragment.isAdded()) {
			pohybFragment.showAddPrevod(getSupportFragmentManager());
		}
	}
	
	public void showAddPohyb() {
		PohybListFragment fragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		fragment.showAdd(getSupportFragmentManager());
	}
	
	public void showPohybOdDo() {
		PohybListFragment fragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		fragment.showOdDo(getSupportFragmentManager());
	}
	
	@Override
	public void onPohybADU() {
		PohybListFragment pohyb = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		GrafFragment graf = (GrafFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_graf);
		
		if (pohyb != null && pohyb.isAdded()) {
			pohyb.refreshSum();
		}
		
		if (graf != null && graf.isAdded()) {
			pohyb.refreshGraf();
		}		
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		String tag = dialog.getTag();
		if (getResources().getString(R.string.dialog_tag_pohyb_add).equals(tag)) {
			PohybListFragment fragment = (PohybListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_pohyb_list);
			
			fragment.onDialogPositiveClick(dialog, true);
		} else if (getResources().getString(R.string.dialog_tag_pohyb_edit).equals(tag)) {
			PohybListFragment fragment = (PohybListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_pohyb_list);
			
			fragment.onDialogPositiveClick(dialog, false);
		} else if (getResources().getString(R.string.dialog_tag_pohyb_oddo).equals(tag)) {
			PohybListFragment fragment = (PohybListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_pohyb_list);
			fragment.nastavOdDo(dialog);
		} else if (getResources().getString(R.string.dialog_tag_prevod_add).equals(tag)) {
			PohybListFragment fragment = (PohybListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_pohyb_list);
			fragment.pridajPrevod(dialog);
		}
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		//Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
	}
}
