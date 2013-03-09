package osobny.uctovnik.activities;

import java.util.List;

import osobny.uctovnik.async.AsyncGraphTask.OnPohybyForGraphSelected;
import osobny.uctovnik.fragments.GrafFragment;
import osobny.uctovnik.fragments.PohybListFragment;
import osobny.uctovnik.fragments.UcetListFragment;
import osobny.uctovnik.helpers.DialogListener;
import osobny.uctovnik.objects.PohybForGraphObject;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class UcetListActivity extends SherlockFragmentActivity implements
		UcetListFragment.OnUcetSelectedListener, DialogListener,
		PohybListFragment.OnPohybSelectedListener, OnPohybyForGraphSelected {

	private String selectedUcet = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ucet);
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
		case (R.id.menu_ucet_add):
			showAddUcet();
			break;
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

	@Override
	public void onPohybyForGraphSelected(List<PohybForGraphObject> result) {
		GrafFragment grafFragment = (GrafFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_graf);
		if (grafFragment != null && grafFragment.isAdded()) {
			grafFragment.setPohyby(result);
		}
	}
	
	private void showAddUcet() {
		UcetListFragment fragment = (UcetListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_ucet_list);
		fragment.showAdd(getSupportFragmentManager());
	}
	
	private void showAddPrevod() {
		PohybListFragment pohybFragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		if (pohybFragment != null && pohybFragment.isAdded()) {
			pohybFragment.showAddPrevod(getSupportFragmentManager());
		}
	}

	private void showAll() {
		selectedUcet = null;
		UcetListFragment ucetFragment = (UcetListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_ucet_list);
		if (ucetFragment != null && ucetFragment.isAdded()) {
			ucetFragment.getController().fillDataToList(null);
		}

		PohybListFragment pohybFragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		if (pohybFragment != null && pohybFragment.isAdded()) {
			pohybFragment.showAll();
		}
	}
	
	@Override
	public void onPohybADU() {
		UcetListFragment ucet = (UcetListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_ucet_list);
		PohybListFragment pohyb = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		GrafFragment graf = (GrafFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_graf);
		
		ucet.refresh();
		if (pohyb != null && pohyb.isAdded()) {
			pohyb.refreshSum();
		}
		
		if (graf != null && graf.isAdded()) {
			pohyb.refreshGraf();
		}		
	}
	
	/*
	 * Funkcia je volana pri pridani alebo odstraneni uctu,
	 * akciu je treba vykonat len v bankalistactivity
	 * */
	@Override
	public void onUcetAddedOrDeleted() {
	}
	
	/*
	 * Funkcia je volana pri editovanie uctu,
	 * potrebujeme refresh pohybov
	 * */
	@Override
	public void onUcetEdited() {
		PohybListFragment pohybFragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		if (pohybFragment != null && pohybFragment.isAdded()) {
			pohybFragment.refresh();
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
	public void onUcetSelected(String id) {
		selectedUcet = id;
		PohybListFragment pohybFragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		if (pohybFragment != null && pohybFragment.isAdded()) {
			pohybFragment.setUcetId(selectedUcet);
		} else {
			Intent intent = new Intent(this, PohybListActivity.class);
			intent.putExtra(
					getResources().getString(R.string.intent_msg_ucet_id), id);
			startActivity(intent);
		}
	}

	/*
	 * Pohyby changed: bud su zobrazene 2 alebo 3 stlpce ak 2 tak otvorime
	 * PohybListActivity ak 3 tak prekreslim graf
	 */
	@Override
	public void onPohybSelected() {
		GrafFragment graf = (GrafFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_graf);
		if (graf == null || !graf.isAdded()) {
			Intent intent = new Intent(this, PohybListActivity.class);
			intent.putExtra(
					getResources().getString(R.string.intent_msg_ucet_id),
					selectedUcet);
			startActivity(intent);
		} else { // prekreslenie grafu
			//nie je treba, robi to AsyncGraphTask
		}
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		String tag = dialog.getTag();
		if (getResources().getString(R.string.dialog_tag_ucet_add).equals(tag)) {
			UcetListFragment fragment = (UcetListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_ucet_list);
			fragment.onDialogPositiveClick(dialog, true);
		} else if (getResources().getString(R.string.dialog_tag_ucet_edit)
				.equals(tag)) {
			UcetListFragment fragment = (UcetListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_ucet_list);
			fragment.onDialogPositiveClick(dialog, false);
		} else if (getResources().getString(R.string.dialog_tag_pohyb_add)
				.equals(tag)) {
			PohybListFragment fragment = (PohybListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_pohyb_list);
			fragment.onDialogPositiveClick(dialog, true);
		} else if (getResources().getString(R.string.dialog_tag_pohyb_edit)
				.equals(tag)) {
			PohybListFragment fragment = (PohybListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_pohyb_list);
			fragment.onDialogPositiveClick(dialog, false);
		} else if (getResources().getString(R.string.dialog_tag_pohyb_oddo)
				.equals(tag)) {
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
	}
}
