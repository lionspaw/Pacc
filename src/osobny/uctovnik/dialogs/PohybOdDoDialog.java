package osobny.uctovnik.dialogs;

import java.util.Calendar;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.helpers.DialogListener;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class PohybOdDoDialog extends SherlockDialogFragment {

	private DialogListener mListener;
	private Calendar calDatumOd;
	private Calendar calDatumDo;
	
	@Override
	public void onAttach(Activity act) {
		super.onAttach(act);
		mListener = (DialogListener) act;
	}
	
	public PohybOdDoDialog(Calendar calOd, Calendar calDo) {
		calDatumOd = calOd;
		calDatumDo = calDo;
	}
	
	public PohybOdDoDialog() {
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.pohyb_od_do, null);
		Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(view);
		dialog.setTitle(getResources().getString(R.string.pohyb_oddo_title));
		
		final DatePicker datumOd = ((DatePicker) dialog.findViewById(R.id.oddo_pohyb_datum_od));
		final DatePicker datumDo = ((DatePicker) dialog.findViewById(R.id.oddo_pohyb_datum_do));

		if (VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			datumOd.setCalendarViewShown(false);
			datumDo.setCalendarViewShown(false);
		}		
		
		
		Button btnSet = (Button) dialog.findViewById(R.id.oddo_pohyb_ok);
		btnSet.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onDialogPositiveClick(PohybOdDoDialog.this);
				PohybOdDoDialog.this.getDialog().dismiss();
			}
		});
		
		Button btnBack = (Button) dialog.findViewById(R.id.oddo_pohyb_naspat);
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onDialogNegativeClick(PohybOdDoDialog.this);
				PohybOdDoDialog.this.getDialog().dismiss();
			}
		});
		
		CheckBox datumOdCheck = (CheckBox) dialog.findViewById(R.id.oddo_pohyb_datum_od_check);
		CheckBox datumDoCheck = (CheckBox) dialog.findViewById(R.id.oddo_pohyb_datum_do_check);
		datumOdCheck.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				datumOd.setEnabled(isChecked);
			}
		});
		datumDoCheck.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				datumDo.setEnabled(isChecked);
			}
		});
		
		if (calDatumOd == null) {
			datumOd.setEnabled(false);
			datumOdCheck.setChecked(false);
		} else {
			datumOdCheck.setChecked(true);
			datumOd.setEnabled(true);
			datumOd.updateDate(calDatumOd.get(Calendar.YEAR), 
					calDatumOd.get(Calendar.MONTH),
					calDatumOd.get(Calendar.DAY_OF_MONTH));
		}
		
		if (calDatumDo == null) {
			datumDo.setEnabled(false);
			datumDoCheck.setChecked(false);
		} else {
			datumDo.setEnabled(true);
			datumDoCheck.setChecked(true);
			datumDo.updateDate(calDatumDo.get(Calendar.YEAR), 
					calDatumDo.get(Calendar.MONTH),
					calDatumDo.get(Calendar.DAY_OF_MONTH));
		}
		
		return dialog;
	}
}
