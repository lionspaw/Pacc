package osobny.uctovnik.dialogs;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.helpers.DialogListener;
import osobny.uctovnik.objects.BankaObject;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AddBankaDialog extends BaseDialog {

	BankaObject banka = null;

	@Override
	public void onAttach(Activity act) {
		super.onAttach(act);
		mListener = (DialogListener) act;
	}
	
	public AddBankaDialog(BankaObject banka) {
		this.banka = banka;
	}
	
	public AddBankaDialog() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.add_banka, null);
		if (this.banka != null) {
			nastavHodnoty(view);
		}
		
		Dialog dialog = getDialog(view, this.banka == null);
		dialog.setTitle(getResources().getString(R.string.add_banka_title));
		return dialog;
	}
	
	@Override
	protected void nastavHodnoty(View view) {
		TextView nazov = (TextView) view.findViewById(R.id.add_banka_nazov);
		TextView cislo = (TextView) view.findViewById(R.id.add_banka_cislo);
		TextView id = (TextView) view.findViewById(R.id.add_banka_id);
		nazov.setText(this.banka.getNazov());
		cislo.setText(this.banka.getCislo());
		id.setText(String.valueOf(this.banka.getId()));
	}
	
	@Override
	protected String spravneHodnoty() {
		TextView nazov = (TextView) getDialog().findViewById(R.id.add_banka_nazov);
		if ("".equals(nazov.getText().toString())) {
			return getResources().getString(R.string.add_banka_err_prazdny_nazov);
		}
		
		return null;
	}
}
