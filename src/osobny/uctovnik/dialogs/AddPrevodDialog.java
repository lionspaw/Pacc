package osobny.uctovnik.dialogs;

import java.util.List;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.helpers.Constants;
import osobny.uctovnik.helpers.DialogListener;
import osobny.uctovnik.objects.KategoriaObject;
import osobny.uctovnik.objects.UcetObject;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class AddPrevodDialog extends BaseDialog {

	private List<UcetObject> ucty;
	private List<KategoriaObject> kategorie;
	private UcetObject selectedUcetZ;
	private UcetObject selectedUcetNa;
	private KategoriaObject selectedKategoria;
	private Spinner spinnerUctyZ;
	private Spinner spinnerUctyNa;
	private Spinner spinnerKategoria;

	public AddPrevodDialog() {
	}
	
	@Override
	public void onAttach(Activity act) {
		super.onAttach(act);
		mListener = (DialogListener) act;
	}
	
	public void setUcty(List<UcetObject> ucty) {
		this.ucty = ucty;
	}
	
	public void setKategorie(List<KategoriaObject> kategorie) {
		this.kategorie = kategorie;
	}
	
	public UcetObject getUcetZ() {
		return this.selectedUcetZ;
	}
	
	public UcetObject getUcetNa() {
		return this.selectedUcetNa;
	}
	
	public KategoriaObject getSelectedKategoria() {
		return this.selectedKategoria;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.add_prevod, null);
		ArrayAdapter<UcetObject> adapter = new ArrayAdapter<UcetObject>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, this.ucty);
		selectedUcetZ = this.ucty.get(0);
		selectedUcetNa = this.ucty.get(0);
		selectedKategoria = this.kategorie.get(0);
		
		spinnerUctyZ = (Spinner) view.findViewById(R.id.add_prevod_z);
		spinnerUctyZ.setAdapter(adapter);
		spinnerUctyZ.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				AddPrevodDialog.this.selectedUcetZ = AddPrevodDialog.this.ucty.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
		
		spinnerUctyNa = (Spinner) view.findViewById(R.id.add_prevod_na);
		spinnerUctyNa.setAdapter(adapter);
		spinnerUctyNa.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				AddPrevodDialog.this.selectedUcetNa = AddPrevodDialog.this.ucty.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});

		spinnerKategoria = (Spinner) view.findViewById(R.id.add_prevod_kategoria);
		ArrayAdapter<KategoriaObject> adapter2 = new ArrayAdapter<KategoriaObject>
		(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, this.kategorie);
		spinnerKategoria.setAdapter(adapter2);
		spinnerKategoria.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				AddPrevodDialog.this.selectedKategoria = AddPrevodDialog.this.kategorie.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
		
		Dialog dialog = getDialog(view, true);
		dialog.setTitle(getResources().getString(R.string.add_previd_title));
		return dialog;
	}
	
	@Override
	protected void nastavHodnoty(View view) {
	}
	
	@Override
	protected String spravneHodnoty() {
		if (this.selectedUcetZ.getId().equals(this.selectedUcetNa.getId())) {
			return getResources().getString(R.string.add_prevod_err_same_acc);
		}
		
		String suma = ((TextView) getDialog().findViewById(R.id.add_prevod_suma)).getText().toString();
		if ("".equals(suma)) {
			return getResources().getString(R.string.add_prevod_err_prazdna_suma);
		}
		
		if (!(suma.matches(Constants.REG_CELE_CISLO_POZ) || suma.matches(Constants.REG_DESATINNE_CISLO_POZ))) {
			return getResources().getString(R.string.add_prevod_err_zla_suma);
		}
		
		return null;
	}
}