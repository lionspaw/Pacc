package osobny.uctovnik.dialogs;

import java.util.List;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.helpers.Constants;
import osobny.uctovnik.helpers.DialogListener;
import osobny.uctovnik.objects.BankaObject;
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

public class AddUcetDialog extends BaseDialog {

	private List<BankaObject> banky;
	private BankaObject selectedBanka;
	private UcetObject ucet;
	private Spinner spinnerBanky;
	private Long bankaId; //ak je vybrana banka pred pridanim uctu, tak v spinnery je vybrana ta banka
	
	public AddUcetDialog(UcetObject ucet, Long bankaId) {
		this.ucet = ucet;
		this.bankaId = bankaId;
	}
	
	public AddUcetDialog() {
	}
	
	@Override
	public void onAttach(Activity act) {
		super.onAttach(act);
		mListener = (DialogListener) act;
	}
	
	public void setBanky(List<BankaObject> banky) {
		this.banky = banky;
	}
	
	public BankaObject getSelectedBanka() {
		return this.selectedBanka;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.add_ucet, null);
		Dialog dialog = getDialog(view, this.ucet == null);
		spinnerBanky = (Spinner) view.findViewById(R.id.add_ucet_banky);
		ArrayAdapter<BankaObject> adapter = new ArrayAdapter<BankaObject>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, this.banky);
		spinnerBanky.setAdapter(adapter);
		if (this.ucet != null) { //editovanie
			this.nastavHodnoty(view);
		} else { //pridanie
			if (this.bankaId != null) { //je vybrana banka, nastavim do spinneru
				int index = 0;
				for (BankaObject ob : this.banky) {
					if (this.bankaId.equals(ob.getId())) {
						spinnerBanky.setSelection(index);
						break;
					}
					index++;
				}
			}
		}
		spinnerBanky.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				AddUcetDialog.this.selectedBanka = AddUcetDialog.this.banky.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
		dialog.setTitle(getResources().getString(R.string.add_ucet_title));
		return dialog;
	}
	
	@Override
	protected void nastavHodnoty(View view) {
		TextView nazov = (TextView) view.findViewById(R.id.add_ucet_nazov);
		TextView cislo = (TextView) view.findViewById(R.id.add_ucet_cislo);
		TextView zost = (TextView) view.findViewById(R.id.add_ucet_zostatok);
		TextView dispZost = (TextView) view.findViewById(R.id.add_ucet_disp_zostatok);
		TextView mena = (TextView) view.findViewById(R.id.add_ucet_mena);
		TextView id = (TextView) view.findViewById(R.id.add_ucet_id);
		
		id.setText(String.valueOf(this.ucet.getId()));
		nazov.setText(this.ucet.getNazov());
		cislo.setText(this.ucet.getCisloUctu());
		zost.setText(Constants.sumaToString(this.ucet.getZostatok()));
		dispZost.setText(Constants.sumaToString(this.ucet.getDispZostatok()));
		mena.setText(this.ucet.getMena());
		
		int index = 0;
		for (BankaObject ob : this.banky) {
			if (this.ucet.getBanka().getId().equals(ob.getId())) {
				spinnerBanky.setSelection(index);
				break;
			}
			index++;
		}
	}
	
	@Override
	protected String spravneHodnoty() {
		TextView nazov = (TextView) getDialog().findViewById(R.id.add_ucet_nazov);
		if ("".equals(nazov.getText().toString())) {
			return getResources().getString(R.string.add_ucet_err_prazdny_nazov);
		}
		
		String zost = ((TextView) getDialog().findViewById(R.id.add_ucet_zostatok)).getText().toString();
		String dispZost = ((TextView) getDialog().findViewById(R.id.add_ucet_disp_zostatok)).getText().toString();
		
		if (!"".equals(zost) && !(zost.matches(Constants.REG_CELE_CISLO) || zost.matches(Constants.REG_DESATINNE_CISLO))) {
			return getResources().getString(R.string.add_ucet_err_zly_zost);
		}
		
		if (!"".equals(dispZost) && !(dispZost.matches(Constants.REG_CELE_CISLO) || dispZost.matches(Constants.REG_DESATINNE_CISLO))) {
			return getResources().getString(R.string.add_ucet_err_zly_disp_zost);
		}
		
		return null;
	}
}