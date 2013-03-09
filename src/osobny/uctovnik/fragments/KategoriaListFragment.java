package osobny.uctovnik.fragments;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.controllers.KategoriaController;
import osobny.uctovnik.dialogs.AddKategoriaDialog;
import osobny.uctovnik.objects.KategoriaObject;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class KategoriaListFragment extends BaseListFragment {
	
	public interface OnKategoriaSelectedListener {
		public void onKategoriaSelected(String id);
		public void onKategoriaUpdated();
	}
	
	private OnKategoriaSelectedListener listener;
	private KategoriaController controller;
	
	public KategoriaListFragment() {
		super(null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.controller = new KategoriaController(this);
		setListAdapter(controller.fillDataToList(null));
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_kategoria, menu);
	}
	
	public KategoriaController getController() {
		return this.controller;
	}
	
	public void refreshAdapter() {
		this.controller.refreshAdapter();
	}
	
	/*
	 * Refresh pri editovanie kategorie
	 * */
	@Override
	public void onItemUpdate() {
		this.listener.onKategoriaUpdated();
	}
	
	@Override
	public void onItemAdd() {
		//Na pridanie banky nie je volana ziadna akcia
	}
	@Override
	public void onItemDelete() {
		//na odstranenie banky nie je volana ziadna akcia
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnKategoriaSelectedListener) {
			listener = (OnKategoriaSelectedListener) activity;
		} else {
			throw new ClassCastException(
					activity.toString()
							+ " must implemenet KategoriaListFragment.OnKategoriaSelectedListener");
		}
	}
	
	@Override
	public OnKategoriaSelectedListener getListener() {
		return listener;
	}
	
	private void insert(String nazov, int farba) {
		controller.insert(nazov, farba);
	}
	
	private void edit(String id, String nazov, int farba) {
		controller.update(id, nazov, farba);
	}
	
	public void onDialogPositiveClick(DialogFragment dialog, boolean isPridanie) {
		AddKategoriaDialog kategoriaDialog = (AddKategoriaDialog) dialog;
		String nazov = ((EditText) kategoriaDialog.getDialog().findViewById(
				R.id.add_kategoria_nazov)).getText().toString(); 
		if (isPridanie) {
			insert(nazov, kategoriaDialog.getColor());
		} else {
			String id = ((TextView) dialog.getDialog().findViewById(
					R.id.add_kategoria_id)).getText().toString();
			edit(id, nazov, kategoriaDialog.getColor());
		}
		
	}
	
	public void showAdd(FragmentManager fragmentManager) {
		showDialog(fragmentManager, null);
	}
	
	public void showEdit(FragmentManager fragmentManager, KategoriaObject kategoria) {
		showDialog(fragmentManager, kategoria);
	}
	
	private void showDialog(FragmentManager fragmentManager, KategoriaObject kategoria) {
		dialog = new AddKategoriaDialog(kategoria);
		dialog.show(fragmentManager, getResources().getString(kategoria == null ? 
				R.string.dialog_tag_kategoria_add :R.string.dialog_tag_kategoria_edit));
	}
}
