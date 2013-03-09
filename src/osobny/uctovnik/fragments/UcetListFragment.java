package osobny.uctovnik.fragments;

import java.util.List;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.controllers.UcetController;
import osobny.uctovnik.datasources.UcetDataSource;
import osobny.uctovnik.dialogs.AddUcetDialog;
import osobny.uctovnik.objects.BankaObject;
import osobny.uctovnik.objects.UcetObject;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class UcetListFragment extends BaseListFragment {

	public interface OnUcetSelectedListener {
		public void onUcetSelected(String id);
		public void onUcetAddedOrDeleted();
		public void onUcetEdited();
	}

	private OnUcetSelectedListener listener;
	private UcetController controller;
	private String selectedBanka;

	public UcetListFragment() {
		super(null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.controller = new UcetController(this);
		Intent intent = getActivity().getIntent();
		selectedBanka = intent.getStringExtra(getResources().getString(R.string.intent_msg_banka_id));
		setListAdapter(controller.fillDataToList(
				selectedBanka == null ? null : UcetDataSource.BANKA_ID + "=" + selectedBanka));
	}
	
	public void onBankaSelected(String bankaId) {
		selectedBanka = bankaId;
		getController().fillDataToList(UcetDataSource.BANKA_ID + "=" + selectedBanka);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnUcetSelectedListener) {
			listener = (OnUcetSelectedListener) activity;
		} else {
			throw new ClassCastException(
					activity.toString()
							+ " must implemenet UcetListFragment.OnUcetSelectedListener");
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_ucet, menu);
	}
	
	@Override
	public OnUcetSelectedListener getListener() {
		return listener;
	}
	
	/*
	 * Editovanie uctu -> refresh pohybov
	 * */
	@Override
	public void onItemUpdate() {
		listener.onUcetEdited();
	}
	
	/*
	 * Pridanie uctu -> refresh bank
	 * */
	@Override
	public void onItemAdd() {
		listener.onUcetAddedOrDeleted();
	}
	
	/*
	 * Odstranenie uctu -> refresh bank
	 * */
	@Override
	public void onItemDelete() {
		listener.onUcetAddedOrDeleted();
	}
	
	public List<BankaObject> getBanky() {
		return this.controller.getBanky();
	}
	
	public void refresh() {
		this.controller.refreshAdapter();
	}
	
	public UcetController getController() {
		return this.controller;
	}
	
	private void insert(String nazov, String cislo, String zost, String dispZost, String mena, BankaObject banka) {
		this.controller.insert(nazov, cislo, zost, dispZost, mena, banka);
	}
	
	private void edit(String id, String nazov, String cislo, String zost, String dispZost, String mena, BankaObject banka) {
		this.controller.update(id, nazov, cislo, zost, dispZost, mena, banka);
	}
	
	public void onDialogPositiveClick(DialogFragment dialog, boolean isPridanie) {
		AddUcetDialog addUcetFragment = ((AddUcetDialog) dialog); 
		Dialog ucetDialog = addUcetFragment.getDialog();
		String nazov = ((EditText) ucetDialog.findViewById(R.id.add_ucet_nazov)).getText().toString();
		String cislo = ((EditText) ucetDialog.findViewById(R.id.add_ucet_cislo)).getText().toString();
		String zostatok = ((EditText) ucetDialog.findViewById(R.id.add_ucet_zostatok)).getText().toString();
		String dispZost = ((EditText) ucetDialog.findViewById(R.id.add_ucet_disp_zostatok)).getText().toString();
		String mena = ((EditText) ucetDialog.findViewById(R.id.add_ucet_mena)).getText().toString();
		if (isPridanie) {
			insert(nazov, cislo, zostatok, dispZost, mena, addUcetFragment.getSelectedBanka());
		} else {
			String id = ((TextView) dialog.getDialog().findViewById(
					R.id.add_ucet_id)).getText().toString();
			edit(id, nazov, cislo, zostatok, dispZost, mena, addUcetFragment.getSelectedBanka());
		}
		
	}
	
	public void showAdd(FragmentManager fragmentManager) {
		showDialog(fragmentManager, null);
	}
	
	public void showEdit(FragmentManager fragmentManager, UcetObject ucet) {
		showDialog(fragmentManager, ucet);
	}
	
	public void showDialog(FragmentManager fragmentManager, UcetObject ucet) {
		UcetListFragment fragment = (UcetListFragment) fragmentManager.findFragmentById(R.id.fragment_ucet_list);
		dialog = new AddUcetDialog(ucet, this.selectedBanka == null ? null : Long.valueOf(this.selectedBanka));
		if (fragment != null && fragment.isInLayout()) {
			List<BankaObject> banky = fragment.getBanky();
			if (banky.isEmpty()) {
				Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.add_ucet_err_bank), Toast.LENGTH_LONG).show();
				return;
			}
			((AddUcetDialog)dialog).setBanky(banky);
		}
		dialog.show(fragmentManager, getResources().getString(ucet == null ?
				R.string.dialog_tag_ucet_add : R.string.dialog_tag_ucet_edit));
	}
}
