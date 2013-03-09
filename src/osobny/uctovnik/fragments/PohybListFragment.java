package osobny.uctovnik.fragments;

import java.util.List;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.async.AsyncGraphTask.OnPohybyForGraphSelected;
import osobny.uctovnik.controllers.PohybController;
import osobny.uctovnik.datasources.PohybDataSource;
import osobny.uctovnik.dialogs.AddPohybDialog;
import osobny.uctovnik.dialogs.AddPrevodDialog;
import osobny.uctovnik.dialogs.PohybOdDoDialog;
import osobny.uctovnik.helpers.Constants;
import osobny.uctovnik.helpers.GlobalParams;
import osobny.uctovnik.objects.KategoriaObject;
import osobny.uctovnik.objects.PohybObject;
import osobny.uctovnik.objects.UcetObject;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class PohybListFragment extends BaseListFragment {
	
	public interface OnPohybSelectedListener {
		public void onPohybSelected();
		public void onPohybADU(); //onPohybAddedDeletedUpdated
	}
	
	private PohybController controller;
	private OnPohybSelectedListener onPohybSelectedListener;
	private OnPohybyForGraphSelected onPohybyForGraphSelected; // Listener z AsyncGraphTask
	private PohybOdDoDialog odDoDialog;
	
	private String ucetId = null;
	private String kategoriaId = null;
	private String where = null;
	
	public PohybListFragment() {
		super(R.layout.fragment_list_pohyb);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.controller = new PohybController(this);
		Intent intent = getActivity().getIntent();
		ucetId = intent.getStringExtra(getResources().getString(R.string.intent_msg_ucet_id));
		kategoriaId = intent.getStringExtra(getResources().getString(R.string.intent_msg_kategoria_id));
		nacitajPohyby();
	}
	
	public void setKategoriaId(String kategoriaId) {
		this.kategoriaId = kategoriaId;
		this.ucetId = null;
		nacitajPohyby();
	}
	
	@Override
	public void onItemUpdate() {
		onPohybSelectedListener.onPohybADU();
	}
	
	public void refresh() {
		this.controller.refreshAdapter();
	}
	
	public void refreshSum() {
		this.controller.fillSum();
	}
	
	@Override
	public void onItemAdd() {
		onPohybSelectedListener.onPohybADU();
	}
	@Override
	public void onItemDelete() {
		onPohybSelectedListener.onPohybADU();
	}
	
	public void setUcetId(String ucetId) {
		this.kategoriaId = null;
		this.ucetId = ucetId;
		nacitajPohyby();
	}
	
	public void showAll() {
		this.kategoriaId = null;
		this.ucetId = null;
		GlobalParams.datumDo = null;
		GlobalParams.datumOd = null;
		nacitajPohyby();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		/*if (activity instanceof OnPohybyChangedListener) {
			onPohybyChangedListener = (OnPohybyChangedListener) activity;
		} else {
			throw new ClassCastException(
					activity.toString()
							+ " must implemenet PohybListFragment.OnPohybSelectedListener");
		}
		*/
		onPohybSelectedListener = (OnPohybSelectedListener) activity;
		onPohybyForGraphSelected = (OnPohybyForGraphSelected) activity;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_pohyb, menu);
	}
	
	public List<KategoriaObject> getKategorie() {
		return controller.getKategorie();
	}
	
	public List<UcetObject> getUcty() {
		return controller.getUcty();
	}
	
	private void insert(DatePicker date, TimePicker time, String suma, String poznamka, Boolean isKredit, KategoriaObject kat, UcetObject ucet) {
		controller.insert(date, time, suma, poznamka, isKredit, kat, ucet);
	}
	
	private void edit(String id, DatePicker date, TimePicker time, String suma, String poznamka, Boolean isKredit, KategoriaObject kat, UcetObject ucet) {
		controller.update(id, date, time, suma, poznamka, isKredit, kat, ucet);
	}
	
	public PohybController getController() {
		return this.controller;
	}
	
	@Override
	public OnPohybSelectedListener getListener() {
		return onPohybSelectedListener;
	}
	
	public void onDialogPositiveClick(DialogFragment dialog, Boolean isPridanie) {
		AddPohybDialog pohybDialog = (AddPohybDialog) dialog;
		Dialog mDialog = pohybDialog.getDialog();
		TimePicker time = (TimePicker)mDialog.findViewById(R.id.add_pohyb_cas);
		DatePicker date = (DatePicker)mDialog.findViewById(R.id.add_pohyb_datum);
		String suma = ((EditText) mDialog.findViewById(R.id.add_pohyb_suma)).getText().toString();
		String poznamka = ((EditText) mDialog.findViewById(R.id.add_pohyb_poznamka)).getText().toString(); 
		Boolean isKredit = ((RadioButton) mDialog.findViewById(R.id.add_pohyb_rb_kredit)).isChecked();
		if (isPridanie) {
			insert(date, time, suma, poznamka, isKredit, pohybDialog.getSelectedKategoria(), pohybDialog.getSelectedUcet());
		} else {
			String id = ((TextView) dialog.getDialog().findViewById(
					R.id.add_pohyb_id)).getText().toString();
			edit(id, date, time, suma, poznamka, isKredit, pohybDialog.getSelectedKategoria(), pohybDialog.getSelectedUcet());
		}
	}
	
	public void showAdd(FragmentManager fragmentManager) {
		showDialog(fragmentManager, null);
	}
	
	public void showEdit(FragmentManager fragmentManager, PohybObject pohyb) {
		showDialog(fragmentManager, pohyb);
	}
	
	public void showOdDo(FragmentManager fragmentManager) {
		odDoDialog = new PohybOdDoDialog(GlobalParams.datumOd, GlobalParams.datumDo);
		odDoDialog.show(fragmentManager, getResources().getString(R.string.dialog_tag_pohyb_oddo));
	}
	
	public void showAddPrevod(FragmentManager fragmentManager) {
		PohybListFragment fragment = (PohybListFragment) fragmentManager.findFragmentById(R.id.fragment_pohyb_list);
		
		dialog = new AddPrevodDialog();
		
		if (fragment != null && fragment.isInLayout()) {
			List<KategoriaObject> kategorie = fragment.getKategorie();
			if (kategorie.isEmpty()) {
				Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.add_pohyb_err_kat), Toast.LENGTH_LONG).show();
				return;
			}
			List<UcetObject> ucty = fragment.getUcty();
			if (ucty.isEmpty()) {
				Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.add_pohyb_err_ucet), Toast.LENGTH_LONG).show();
				return;
			}
			((AddPrevodDialog)dialog).setUcty(ucty);
			((AddPrevodDialog)dialog).setKategorie(kategorie);
		}
		
		dialog.show(fragmentManager, getResources().getString(R.string.dialog_tag_prevod_add));
	}
	
	private void showDialog(FragmentManager fragmentManager, PohybObject pohyb) {
		PohybListFragment fragment = (PohybListFragment) fragmentManager.findFragmentById(R.id.fragment_pohyb_list);
		dialog = new AddPohybDialog(pohyb, this.ucetId == null ? null : Long.valueOf(this.ucetId), this.kategoriaId == null ? null :
			Long.valueOf(this.kategoriaId));
		
		if (fragment != null && fragment.isInLayout()) {
			List<KategoriaObject> kategorie = fragment.getKategorie();
			if (kategorie.isEmpty()) {
				Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.add_pohyb_err_kat), Toast.LENGTH_LONG).show();
				return;
			}
			List<UcetObject> ucty = fragment.getUcty();
			if (ucty.isEmpty()) {
				Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.add_pohyb_err_ucet), Toast.LENGTH_LONG).show();
				return;
			}
			((AddPohybDialog)dialog).setKategorie(kategorie);
			((AddPohybDialog)dialog).setUcty(ucty);
		}
		
		dialog.show(fragmentManager, getResources().getString(pohyb == null ? R.string.dialog_tag_pohyb_add : R.string.dialog_tag_pohyb_edit));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (odDoDialog != null && odDoDialog.isAdded()) {
			odDoDialog.dismiss();
		}
	}
	
	public void nastavOdDo(DialogFragment dialog) {
		odDoDialog = (PohybOdDoDialog) dialog;
		DatePicker datumOd = (DatePicker) odDoDialog.getDialog().findViewById(R.id.oddo_pohyb_datum_od);
		DatePicker datumDo = (DatePicker) odDoDialog.getDialog().findViewById(R.id.oddo_pohyb_datum_do);
		CheckBox datumOdCheck = (CheckBox) odDoDialog.getDialog().findViewById(R.id.oddo_pohyb_datum_od_check);
		CheckBox datumDoCheck = (CheckBox) odDoDialog.getDialog().findViewById(R.id.oddo_pohyb_datum_do_check);
		if (datumOdCheck.isChecked()) {
			GlobalParams.datumOd = Constants.getDatum(datumOd);
		} else {
			GlobalParams.datumOd = null;
		}
		if (datumDoCheck.isChecked()) {
			GlobalParams.datumDo = Constants.getDatum(datumDo);
		} else {
			GlobalParams.datumDo = null;
		}
		nacitajPohyby();
	}
	
	public void pridajPrevod(DialogFragment dialog) {
		AddPrevodDialog prevodDialog = (AddPrevodDialog) dialog;
		Dialog mDialog = prevodDialog.getDialog();
				
		String suma = ((EditText) mDialog.findViewById(R.id.add_prevod_suma)).getText().toString();
		UcetObject naUcet = prevodDialog.getUcetNa();
		UcetObject zUctu = prevodDialog.getUcetZ();
		KategoriaObject kategoria = prevodDialog.getSelectedKategoria(); 
		
		this.controller.insertPrevod(suma, zUctu, naUcet, kategoria);
	}
	
	public String getWhere() {
		return this.where;
	}
	
	private void nacitajPohyby() {
		StringBuilder where = new StringBuilder();
		if (GlobalParams.datumOd != null && GlobalParams.datumDo == null) {//vyplneny datum od
			where.append(PohybDataSource.DATUM + " >= \"" + Constants.getDatum(GlobalParams.datumOd) + "\"");
		} else if (GlobalParams.datumOd == null && GlobalParams.datumDo != null) { // vyplneny datum do
			where.append(PohybDataSource.DATUM + " <= \"" + Constants.getDatum(GlobalParams.datumDo) + "\"");
		} else if (GlobalParams.datumOd != null && GlobalParams.datumDo != null) { // vyplneny datum od aj do
			where.append(PohybDataSource.DATUM + " BETWEEN \"" + Constants.getDatum(GlobalParams.datumOd) + 
					"\" AND \"" + Constants.getDatum(GlobalParams.datumDo) + "\"");
		}
		
		if (ucetId != null) {
			where.append((where.length() == 0 ? "" : " AND ") + PohybDataSource.UCET_ID + "=" + ucetId); 
		} else if (kategoriaId != null) {
			where.append((where.length() == 0 ? "" : " AND ") + PohybDataSource.KATEGORIA_ID + "=" + kategoriaId);
		}
		this.where = where.toString();
		setListAdapter(controller.fillDataToList(this.where));
		refreshGraf();
	}
	
	public void refreshGraf() {
		controller.fillGraph(onPohybyForGraphSelected, where.toString());
	}
}