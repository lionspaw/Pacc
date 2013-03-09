package osobny.uctovnik.dialogs;

import java.util.Calendar;
import java.util.List;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.helpers.Constants;
import osobny.uctovnik.helpers.DialogListener;
import osobny.uctovnik.objects.KategoriaObject;
import osobny.uctovnik.objects.PohybObject;
import osobny.uctovnik.objects.UcetObject;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddPohybDialog  extends BaseDialog {

	private List<KategoriaObject> kategorie;
	private KategoriaObject selectedKategoria;
	private List<UcetObject> ucty;
	private UcetObject selectedUcet;
	private PohybObject pohyb;
	private Spinner spinnerKategorie;
	private Spinner spinnerUcty;
	private TimePicker timePicker;
	private Long ucetId;
	private Long kategoriaId;

	public AddPohybDialog(PohybObject pohyb, Long ucetId, Long kategoriaId) {
		this.pohyb = pohyb;
		this.ucetId = ucetId;
		this.kategoriaId = kategoriaId;
	}
	
	public AddPohybDialog() {
	}
	
	@Override
	public void onAttach(Activity act) {
		super.onAttach(act);
		mListener = (DialogListener) act;
	}
	
	public void setKategorie(List<KategoriaObject> kategorie) {
		this.kategorie = kategorie;
	}
	
	public KategoriaObject getSelectedKategoria() {
		return this.selectedKategoria;
	}
	
	public void setUcty(List<UcetObject> ucty) {
		this.ucty = ucty;
	}
	
	public UcetObject getSelectedUcet() {
		return this.selectedUcet;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.add_pohyb, null);
		timePicker = (TimePicker)view.findViewById(R.id.add_pohyb_cas);
		timePicker.setIs24HourView(true);
		if (VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			((DatePicker)view.findViewById(R.id.add_pohyb_datum)).setCalendarViewShown(false);
		}		
		spinnerKategorie = (Spinner) view.findViewById(R.id.add_pohyb_kategorie);
		ArrayAdapter<KategoriaObject> adapter = new ArrayAdapter<KategoriaObject>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, this.kategorie);
		spinnerKategorie.setAdapter(adapter);
		spinnerKategorie.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				AddPohybDialog.this.selectedKategoria = AddPohybDialog.this.kategorie.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
		
		spinnerUcty = (Spinner) view.findViewById(R.id.add_pohyb_ucty);
		ArrayAdapter<UcetObject> adapter2 = new ArrayAdapter<UcetObject>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, this.ucty);
		spinnerUcty.setAdapter(adapter2);
		spinnerUcty.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				AddPohybDialog.this.selectedUcet = AddPohybDialog.this.ucty.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
		if (this.pohyb != null) { //editovanie
			nastavHodnoty(view);
		} else { //nastavim hodinu v 0-23 formate
			Calendar cal = Calendar.getInstance();
			timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
			
			int index = 0;
			if (this.ucetId != null) { //ak bol vybrany ucet, tak nastavime v spinnery
				for (UcetObject ob : this.ucty) {
					if (this.ucetId.equals(ob.getId())) {
						spinnerUcty.setSelection(index);
						break;
					}
					index++;
				}
			} else if (this.kategoriaId != null) { // ak bola vybrana kategoria, tak nastavime v spinnery
				for (KategoriaObject ob : this.kategorie) {
					if (this.kategoriaId.equals(ob.getId())) {
						spinnerKategorie.setSelection(index);
						break;
					}
					index++;
				}
			}
		}
		
		Dialog dialog = getDialog(view, this.pohyb == null);
		dialog.setTitle(getResources().getString(R.string.add_pohyb_title));
		return dialog;
	}
	
	@Override
	protected void nastavHodnoty(View view) {
		TextView suma = (TextView) view.findViewById(R.id.add_pohyb_suma);
		TextView poznamka = (TextView) view.findViewById(R.id.add_pohyb_poznamka);
		TextView id = (TextView) view.findViewById(R.id.add_pohyb_id);
		id.setText(String.valueOf(this.pohyb.getId()));
		suma.setText(Constants.sumaToString(this.pohyb.getSuma()));
		poznamka.setText(this.pohyb.getPoznamka());
		int index = 0;
		for (UcetObject ob : this.ucty) {
			if (this.pohyb.getUcet().getId().equals(ob.getId())) {
				spinnerUcty.setSelection(index);
				break;
			}
			index++;
		}
		index = 0;
		for (KategoriaObject ob : this.kategorie) {
			if (this.pohyb.getKategoria().getId().equals(ob.getId())) {
				spinnerKategorie.setSelection(index);
				break;
			}
			index++;
		}
		
		if (this.pohyb.isKredit()) {
			((RadioGroup) view.findViewById(R.id.add_pohyb_rb)).check(R.id.add_pohyb_rb_kredit);
		} else {
			((RadioGroup) view.findViewById(R.id.add_pohyb_rb)).check(R.id.add_pohyb_rb_debet);
		}
		DatePicker datePicker = (DatePicker) view.findViewById(R.id.add_pohyb_datum);
        datePicker.updateDate(
        		this.pohyb.getDatCas().get(Calendar.YEAR),
        		this.pohyb.getDatCas().get(Calendar.MONTH),
        		this.pohyb.getDatCas().get(Calendar.DAY_OF_MONTH));
		timePicker.setCurrentHour(this.pohyb.getDatCas().get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(this.pohyb.getDatCas().get(Calendar.MINUTE));	
	}
	
	@Override
	protected String spravneHodnoty() {
		String suma = ((TextView) getDialog().findViewById(R.id.add_pohyb_suma)).getText().toString();
		if ("".equals(suma)) {
			return getResources().getString(R.string.add_pohyb_err_prazdna_suma);
		}
		
		if (!(suma.matches(Constants.REG_CELE_CISLO_POZ) || suma.matches(Constants.REG_DESATINNE_CISLO_POZ))) {
			return getResources().getString(R.string.add_pohyb_err_zla_suma);
		}
		
		return null;
	}
}