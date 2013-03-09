package osobny.uctovnik.activities;

import osobny.uctovnik.fragments.BankaListFragment;
import osobny.uctovnik.fragments.UcetListFragment;
import osobny.uctovnik.helpers.DialogListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class BankaListActivity extends SherlockFragmentActivity implements
		DialogListener, BankaListFragment.OnBankaSelectedListener,
		UcetListFragment.OnUcetSelectedListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bank);
		initActionBarSherlock();
	}

	
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_list, menu);
		return true;
	}

	private void initActionBarSherlock() {
    	getSupportActionBar().setHomeButtonEnabled(true);
    }
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.menu_banka_add):
			showAddBanka();
			break;
		case (R.id.menu_ucet_add):
			showAddUcet();
			break;
		case (R.id.menu_show_all):
			showAll();
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
	 * Banka item selected
	 * Ked je UcetListFragment zobrazeny tak selektujeme ucty banky
	 * inak otvorim UcetListActivity
	 * */
	@Override
	public void onBankaSelected(String id) {
		UcetListFragment ucetFragment = (UcetListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_ucet_list);
		if (ucetFragment != null && ucetFragment.isAdded()) {
			ucetFragment.onBankaSelected(id);
		} else {
			Intent intent = new Intent(this, UcetListActivity.class);
			intent.putExtra(getResources().getString(R.string.intent_msg_banka_id), id);
			startActivity(intent);
		}
	}
	
	/*
	 * Funkcia je volana pri update banky, ak je zobrazeny fragment uctov, tak treba refresh
	 * */
	@Override
	public void onBankaEdited() {
		UcetListFragment ucetFragment = (UcetListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_ucet_list);
		if (ucetFragment != null && ucetFragment.isAdded()) {
			ucetFragment.refresh();
		}
	}
	
	/*
	 * Funkcia je volana pri pridani alebo odstraneni uctu, zoznam bank je treba refresnut
	 * */
	@Override
	public void onUcetAddedOrDeleted() {
		BankaListFragment bankaFragment = (BankaListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_banka_list);
		bankaFragment.refresh();
	}
	
	@Override
	public void onUcetEdited() {
		
	}
	
	public void showAll() {
		BankaListFragment bankaFragment = (BankaListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_banka_list);
		if (bankaFragment != null && bankaFragment.isAdded()) {
			bankaFragment.getController().fillDataToList(null);
		}
		
		UcetListFragment ucetFragment = (UcetListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_ucet_list);
		if (ucetFragment != null && ucetFragment.isAdded()) {
			ucetFragment.getController().fillDataToList(null);
		}
	}

	@Override
	public void onUcetSelected(String id) {
		Intent intent = new Intent(this, PohybListActivity.class);
		intent.putExtra(getResources().getString(R.string.intent_msg_ucet_id), id);
		startActivity(intent);
	}

	public void showAddBanka() {
		BankaListFragment fragment = (BankaListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_banka_list);
		fragment.showAdd(getSupportFragmentManager());
	}
	
	public void showAddUcet() {
		UcetListFragment fragment = (UcetListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_ucet_list);
		fragment.showAdd(getSupportFragmentManager());
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		String tag = dialog.getTag();
		if (getResources().getString(R.string.dialog_tag_banka_add).equals(tag)) { //Pridanie banky
			BankaListFragment fragment = (BankaListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_banka_list);
			fragment.onDialogPositiveClick(dialog, true);
		} else if (getResources().getString(R.string.dialog_tag_banka_edit).equals(tag)) { //editovanie banky
			BankaListFragment fragment = (BankaListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_banka_list);
			fragment.onDialogPositiveClick(dialog, false);
		} else if (getResources().getString(R.string.dialog_tag_ucet_add).equals(tag)) { //Pridanie uctu
			UcetListFragment fragment = (UcetListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_ucet_list);
			fragment.onDialogPositiveClick(dialog, true);
		} else if (getResources().getString(R.string.dialog_tag_ucet_edit).equals(tag)) { //editovanie uctu
			UcetListFragment fragment = (UcetListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_ucet_list);
			fragment.onDialogPositiveClick(dialog, false);
		}
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		//Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
	}
}
