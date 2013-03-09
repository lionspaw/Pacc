package osobny.uctovnik.dialogs;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.helpers.DialogListener;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;

public abstract class BaseDialog extends SherlockDialogFragment {
	
	protected DialogListener mListener;
	
	public BaseDialog() {
	}
	
	public Dialog getDialog(View view, boolean isPridat) {
		Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(view);
		Button btnAdd = (Button) dialog.findViewById(R.id.dialog_add);
		btnAdd.setText(getResources().getString(isPridat ? R.string.pridat : R.string.modifikovat));
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String retVal = BaseDialog.this.spravneHodnoty();
				if (retVal == null) {
					mListener.onDialogPositiveClick(BaseDialog.this);
					BaseDialog.this.getDialog().dismiss();
				} else {
					Toast.makeText(getActivity(), retVal, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		Button btnBack = (Button) dialog.findViewById(R.id.dialog_naspat);
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onDialogNegativeClick(BaseDialog.this);
				BaseDialog.this.getDialog().dismiss();
			}
		});
		return dialog;
	}
	
	abstract protected String spravneHodnoty();
	abstract protected void nastavHodnoty(View view);
	
}
