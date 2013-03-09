package osobny.uctovnik.adapters;

import java.util.List;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.helpers.Constants;
import osobny.uctovnik.objects.UcetObject;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*
 * Adapter pomocou ktoreho sa mapuju POJO objekty zoznamu do layoutu 
 * */
public class UcetAdapter extends BaseAdapter<UcetObject> {

	static class ViewHolder {
		public TextView nazov;
		public TextView cislo;
		public TextView zostatok;
		public TextView dispZostatok;
		public TextView banka;
		public TextView id;
	}
	
	public UcetAdapter(SwipableListItem swipeableListItem, Activity context, List<UcetObject> list) {
		super(swipeableListItem, context, R.layout.row_ucet, list);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(layoutId, null);
			addSwipe(rowView);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.nazov = (TextView) rowView.findViewById(R.id.ucet_row_nazov);
			viewHolder.cislo = (TextView) rowView.findViewById(R.id.ucet_row_cislo);
			viewHolder.zostatok = (TextView) rowView.findViewById(R.id.ucet_row_zostatok);
			viewHolder.dispZostatok = (TextView) rowView.findViewById(R.id.ucet_row_disp_zostatok);
			viewHolder.banka = (TextView) rowView.findViewById(R.id.ucet_row_banka);
			viewHolder.id = (TextView) rowView.findViewById(R.id.ucet_row_id);
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		UcetObject obj = list.get(position);
		holder.nazov.setText(obj.getNazov());
		holder.cislo.setText(obj.getCisloUctu());
		holder.zostatok.setText(Constants.sumaToString(obj.getZostatok()));
		if (obj.getZostatok().equals(obj.getDispZostatok())) {
			holder.dispZostatok.setText(" " + obj.getMena());
		} else {
			holder.dispZostatok.setText(" (" + Constants.sumaToString(obj.getDispZostatok()) + ") " + obj.getMena());
		}
		holder.banka.setText(obj.getBanka().getNazov());
		holder.id.setText(obj.getId().toString());
		return rowView;
	}
}
