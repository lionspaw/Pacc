package osobny.uctovnik.dialogs;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.helpers.ColorPickerDialog;
import osobny.uctovnik.helpers.ColorPickerDialog.OnColorChangedListener;
import osobny.uctovnik.helpers.DialogListener;
import osobny.uctovnik.objects.KategoriaObject;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddKategoriaDialog extends BaseDialog implements
		OnColorChangedListener {

	int color;
	RelativeLayout colorBg;
	KategoriaObject kategoria = null;

	public AddKategoriaDialog(KategoriaObject kateg) {
		this.kategoria = kateg;
	}

	public AddKategoriaDialog() {
	}
	
	@Override
	public void onAttach(Activity act) {
		super.onAttach(act);
		mListener = (DialogListener) act;
	}

	@Override
	public void colorChanged(int color) {
		colorBg.setBackgroundColor(color);
		this.color = color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return this.color;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		color = getResources().getColor(R.color.base_kategoria);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.add_kategoria, null);
		if (this.kategoria != null) {
			nastavHodnoty(view);
		}
		colorBg = (RelativeLayout) view
				.findViewById(R.id.add_kategoria_color_bg);
		colorBg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ColorPickerDialog colorPicker = new ColorPickerDialog(
						getActivity(), AddKategoriaDialog.this,
						AddKategoriaDialog.this.color);
				colorPicker.show();
			}
		});

		Dialog dialog = getDialog(view, this.kategoria == null);
		dialog.setTitle(getResources().getString(R.string.add_kategoria_title));
		return dialog;
	}

	@Override
	protected void nastavHodnoty(View view) {
		TextView nazov = (TextView) view.findViewById(R.id.add_kategoria_nazov);
		View farba = (View) view.findViewById(R.id.add_kategoria_color_bg);
		TextView id = (TextView) view.findViewById(R.id.add_kategoria_id);

		this.color = this.kategoria.getFarba();
		nazov.setText(this.kategoria.getNazov());
		id.setText(String.valueOf(this.kategoria.getId()));
		farba.setBackgroundColor(this.color);
	}

	@Override
	protected String spravneHodnoty() {
		TextView nazov = (TextView) getDialog().findViewById(
				R.id.add_kategoria_nazov);
		if ("".equals(nazov.getText().toString())) {
			return getResources()
					.getString(R.string.add_kategoria_err_prazdny_nazov);
		}

		return null;
	}

}
