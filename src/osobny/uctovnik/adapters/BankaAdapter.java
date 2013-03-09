package osobny.uctovnik.adapters;

import java.util.List;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.controllers.BankaController;
import osobny.uctovnik.objects.BankaObject;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*
 * Adapter pomocou ktoreho sa mapuju POJO objekty zoznamu do layoutu 
 * */
public class BankaAdapter extends BaseAdapter<BankaObject> {

	static class ViewHolder {
		public TextView nazov;
		public TextView cislo;
		public TextView id;
		public TextView pocetUctov;
	}
	
	private BankaController controller;
	
	public BankaAdapter(SwipableListItem swipeableListItem, Activity context, List<BankaObject> list) {
		super(swipeableListItem, context, R.layout.row_banka, list);
		controller = (BankaController) swipeableListItem;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(layoutId, null); //TODO EZ MIATT LEHETNEK WIDTH/HEIGHT PROBLEMAK
			addSwipe(rowView);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.nazov = (TextView) rowView.findViewById(R.id.banka_row_nazov);
			viewHolder.cislo = (TextView) rowView.findViewById(R.id.banka_row_cislo);
			viewHolder.id = (TextView) rowView.findViewById(R.id.banka_row_id);
			viewHolder.pocetUctov = (TextView) rowView.findViewById(R.id.banka_row_ucty_pocet);
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		BankaObject obj = list.get(position);
		holder.cislo.setText(obj.getCislo());
		holder.nazov.setText(obj.getNazov());
		holder.id.setText(obj.getId().toString());
		holder.pocetUctov.setText(getActivity().getResources().getString(R.string.row_banka_ucty) + " " 
				+ String.valueOf(controller.pocetUctov(obj.getId().toString())));
		return rowView;
	}
}

