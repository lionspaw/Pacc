package osobny.uctovnik.activities;

import java.util.List;

import osobny.uctovnik.async.AsyncGraphTask.OnPohybyForGraphSelected;
import osobny.uctovnik.fragments.GrafFragment;
import osobny.uctovnik.fragments.KategoriaListFragment;
import osobny.uctovnik.fragments.PohybListFragment;
import osobny.uctovnik.helpers.DialogListener;
import osobny.uctovnik.objects.PohybForGraphObject;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class KategoriaListActivity extends SherlockFragmentActivity implements
		KategoriaListFragment.OnKategoriaSelectedListener, DialogListener,
		PohybListFragment.OnPohybSelectedListener, OnPohybyForGraphSelected  {
	
	private String selectedKategoria = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kategoria);
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
	public void onKategoriaSelected(String id) {
		selectedKategoria = id;
		PohybListFragment pohybFragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		if (pohybFragment != null && pohybFragment.isAdded()) {
			pohybFragment.setKategoriaId(selectedKategoria);
		} else {
			Intent intent = new Intent(this, PohybListActivity.class);
			intent.putExtra(getResources().getString(R.string.intent_msg_kategoria_id), id);
			startActivity(intent);
		}		
	}
	
	/*
	 * Funkcia volana pri updatetnutie kategorie, aby boli zmeny zobrazene aj u pohyboch, ak su pohyby zobrazene
	 * */
	@Override
	public void onKategoriaUpdated() {
		PohybListFragment pohybFragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		if (pohybFragment != null && pohybFragment.isAdded()) {
			pohybFragment.refresh();
		}
	}
	
	/*
	 * Pohyb selected: bud su zobrazene 2 alebo 3 stlpce
	 * ak 2 tak otvorime PohybListActivity
	 * ak 3 tak nerobime nic
	 * */
	@Override
	public void onPohybSelected() {
		GrafFragment graf = (GrafFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_graf);
		if (graf == null || !graf.isAdded()) {
			Intent intent = new Intent(this, PohybListActivity.class);
			intent.putExtra(getResources().getString(R.string.intent_msg_kategoria_id), selectedKategoria);
			startActivity(intent);
		}
	}
	
	@Override
	public void onPohybADU() {
		KategoriaListFragment kategoria = (KategoriaListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_kategoria_list);
		PohybListFragment pohyb = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		GrafFragment graf = (GrafFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_graf);
		
		kategoria.refreshAdapter();
		if (pohyb != null && pohyb.isAdded()) {
			pohyb.refreshSum();
		}
		
		if (graf != null && graf.isAdded()) {
			pohyb.refreshGraf();
		}		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.menu_kategoria_add):
			showAddKategoria();
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
		case android.R.id.home:
			 Intent i = new Intent(this, MainMenuActivity.class);
			 i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 startActivity(i);
			 return true;
		case (R.id.menu_pohyb_prevod):
			showAddPrevod();
			break;
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
	
	public void showPohybOdDo() {
		PohybListFragment fragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		fragment.showOdDo(getSupportFragmentManager());
	}
	
	private void showAll() {
		selectedKategoria = null;
		KategoriaListFragment kategoriaFragment = (KategoriaListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_kategoria_list);
		if (kategoriaFragment != null && kategoriaFragment.isAdded()) {
			kategoriaFragment.getController().fillDataToList(null);
		}
		
		PohybListFragment pohybFragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		if (pohybFragment != null && pohybFragment.isAdded()) {
			pohybFragment.showAll();
		}
	}
	
	private void showAddKategoria() {
		KategoriaListFragment fragment = (KategoriaListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_kategoria_list);
		fragment.showAdd(getSupportFragmentManager());
	}
	
	private void showAddPohyb() {
		PohybListFragment fragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		fragment.showAdd(getSupportFragmentManager());
	}
	
	private void showAddPrevod() {
		PohybListFragment pohybFragment = (PohybListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_pohyb_list);
		if (pohybFragment != null && pohybFragment.isAdded()) {
			pohybFragment.showAddPrevod(getSupportFragmentManager());
		}
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		String tag = dialog.getTag();
		if (getResources().getString(R.string.dialog_tag_kategoria_add).equals(tag)) {
			KategoriaListFragment fragment = (KategoriaListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_kategoria_list);	
			fragment.onDialogPositiveClick(dialog, true);
		} else if(getResources().getString(R.string.dialog_tag_kategoria_edit).equals(tag)) {
			KategoriaListFragment fragment = (KategoriaListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_kategoria_list);	
			fragment.onDialogPositiveClick(dialog, false);
		} else if(getResources().getString(R.string.dialog_tag_pohyb_add).equals(tag)) {
			PohybListFragment fragment = (PohybListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_pohyb_list);
			fragment.onDialogPositiveClick(dialog, true);
		} else if(getResources().getString(R.string.dialog_tag_pohyb_edit).equals(tag)) {
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
