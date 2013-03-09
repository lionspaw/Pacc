package osobny.uctovnik.fragments;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.controllers.BankaController;
import osobny.uctovnik.dialogs.AddBankaDialog;
import osobny.uctovnik.objects.BankaObject;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class BankaListFragment extends BaseListFragment {

	public interface OnBankaSelectedListener {
		public void onBankaSelected(String id);
		public void onBankaEdited();
	}

	private OnBankaSelectedListener listener;
	private BankaController controller;

	public BankaListFragment() {
		super(null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.controller = new BankaController(this);		
		setListAdapter(controller.fillDataToList(null));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_banka, menu);
	}
	
	public BankaController getController() {
		return this.controller;
	}
	
	/*
	 * Refresh pri editovanie banky
	 * */
	@Override
	public void onItemUpdate() {
		this.listener.onBankaEdited();
	}
	
	@Override
	public void onItemAdd() {
		//Na pridanie banky nie je volana ziadna akcia
	}
	@Override
	public void onItemDelete() {
		//na odstranenie banky nie je volana ziadna akcia
	}
	
	public void refresh() {
		this.controller.refreshAdapter();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnBankaSelectedListener) {
			listener = (OnBankaSelectedListener) activity;
		} else {
			throw new ClassCastException(
					activity.toString()
							+ " must implemenet BankaListFragment.OnBankaSelectedListener");
		}
	}
	
	@Override
	public OnBankaSelectedListener getListener() {
		return listener;
	}
	
	private void insert(String nazov, String cislo) {
		controller.insert(nazov, cislo);
	}
	
	private void edit(String id, String nazov, String cislo) {
		controller.update(id, nazov, cislo);
	}
	
	public void onDialogPositiveClick(DialogFragment dialog, Boolean isPridanie) {
		String nazov = ((EditText) dialog.getDialog().findViewById(
				R.id.add_banka_nazov)).getText().toString();
		String cislo = ((EditText) dialog.getDialog().findViewById(
				R.id.add_banka_cislo)).getText().toString();
		if (isPridanie) { // pridanie
			insert(nazov, cislo);
		} else { //editovanie
			String id = ((TextView) dialog.getDialog().findViewById(
					R.id.add_banka_id)).getText().toString();
			edit(id, nazov, cislo);
		}
	}
	
	public void showAdd(FragmentManager fragmentManager) {
		showDialog(fragmentManager, null);
	}
	
	public void showEdit(FragmentManager fragmentManager, BankaObject banka) {
		showDialog(fragmentManager, banka);
	}
	
	private void showDialog(FragmentManager fragmentManager, BankaObject banka) {
		dialog = new AddBankaDialog(banka);
		dialog.show(fragmentManager, getResources().getString(banka == null ? R.string.dialog_tag_banka_add : R.string.dialog_tag_banka_edit));
	}
}
