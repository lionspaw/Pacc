package osobny.uctovnik.adapters;

import java.util.List;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.controllers.KategoriaController;
import osobny.uctovnik.objects.KategoriaObject;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*
 * Adapter pomocou ktoreho sa mapuju POJO objekty zoznamu do layoutu 
 * */
public class KategoriaAdapter extends BaseAdapter<KategoriaObject> {

	static class ViewHolder {
		public TextView nazov;
		public View farba;
		public TextView id;
		public TextView sumDebet;
		public TextView sumKredit;
	}
	
	KategoriaController controller;
	
	public KategoriaAdapter(SwipableListItem swipeableListItem, Activity context, List<KategoriaObject> list) {
		super(swipeableListItem, context, R.layout.row_kategoria, list);
		controller = (KategoriaController) swipeableListItem;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(layoutId, parent, false); //TODO EZ MIATT LEHETNEK WIDTH/HEIGHT PROBLEMAK
			addSwipe(rowView);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.nazov = (TextView) rowView.findViewById(R.id.kategoria_row_nazov);
			viewHolder.farba = rowView.findViewById(R.id.kategoria_row_farba_view);
			viewHolder.id = (TextView) rowView.findViewById(R.id.kategoria_row_id);
			viewHolder.sumDebet = (TextView) rowView.findViewById(R.id.kategoria_row_sum_debet);
			viewHolder.sumKredit = (TextView) rowView.findViewById(R.id.kategoria_row_sum_kredit);
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		KategoriaObject obj = list.get(position);
		holder.nazov.setText(obj.getNazov());
		holder.id.setText(String.valueOf(obj.getId()));
		holder.farba.setBackgroundColor(obj.getFarba());
		controller.setSum(this, String.valueOf(obj.getId()), (TextView) rowView.findViewById(R.id.kategoria_row_sum_kredit), (TextView) rowView.findViewById(R.id.kategoria_row_sum_debet));
		
		return rowView;
	}
	
	/*
	 * Funkcia pomocou ktoreho MyAsyncSelectTask setuje sumu kreditov a debetov
	 * */
	public void setText(TextView view, String text) {
		view.setText(text);
	}
} 
