package osobny.uctovnik.adapters;

import java.util.List;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.helpers.Constants;
import osobny.uctovnik.objects.PohybObject;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*
 * Adapter pomocou ktoreho sa mapuju POJO objekty zoznamu do layoutu 
 * */
public class PohybAdapter extends BaseAdapter<PohybObject> {

	static class ViewHolder {
		public TextView datCas;
		public TextView suma;
		public TextView poznamka;
		public TextView kredit;
		public View kategoria;
		public TextView ucet;
		public TextView id;
	}
	
	public PohybAdapter(SwipableListItem swipeableListItem, Activity context, List<PohybObject> list) {
		super(swipeableListItem, context, R.layout.row_pohyb, list);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(layoutId, null);
			addSwipe(rowView);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.datCas = (TextView) rowView.findViewById(R.id.pohyb_row_dat_cas);
			viewHolder.suma = (TextView) rowView.findViewById(R.id.pohyb_row_suma);
			viewHolder.poznamka = (TextView) rowView.findViewById(R.id.pohyb_row_poznamka);
			viewHolder.kategoria = (View) rowView.findViewById(R.id.pohyb_row_farba_view);
			viewHolder.ucet = (TextView) rowView.findViewById(R.id.pohyb_row_ucet_nazov);
			viewHolder.id = (TextView) rowView.findViewById(R.id.pohyb_row_id);
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		PohybObject obj = list.get(position);
		holder.datCas.setText(Constants.getDatumCas(obj.getDatCas()));
		holder.suma.setText(Constants.sumaToString(obj.getSuma()) + " " + obj.getUcet().getMena());
		holder.suma.setTextColor(context.getResources().getColor(obj.isKredit() ? R.color.kredit : R.color.debet));
		holder.poznamka.setText(obj.getPoznamka());		
		holder.kategoria.setBackgroundColor(obj.getKategoria().getFarba());
		holder.ucet.setText(obj.getUcet().getNazov());
		holder.id.setText(obj.getId().toString());
		return rowView;
	}
	
	public void setText(TextView view, String text) {
		view.setText(view.getResources().getString(R.string.suma) + " " + text);
	}
}
