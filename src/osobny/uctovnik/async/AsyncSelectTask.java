package osobny.uctovnik.async;

import osobny.uctovnik.adapters.BaseAdapter;
import osobny.uctovnik.adapters.KategoriaAdapter;
import osobny.uctovnik.adapters.PohybAdapter;
import osobny.uctovnik.async.AsyncSelectTask.SelectType;
import osobny.uctovnik.datasources.PohybDataSource;
import osobny.uctovnik.helpers.Constants;
import android.os.AsyncTask;
import android.widget.TextView;

public class AsyncSelectTask extends AsyncTask<SelectType, Void, Void> {

	public enum SELECT_TYPE {
		KATEGORIA_SUM_CR, KATEGORIA_SUM_DB, POHYB_SUM_CR, POHYB_SUM_DB
	}

	public class SelectType {

		public SelectType(SELECT_TYPE type, String id, TextView view) {
			this.type = type;
			this.id = id;
			this.view = view;
		}

		private SELECT_TYPE type;
		private String id; // id alebo where podmienka
		private TextView view;
	}

	private BaseAdapter adapter;

	public AsyncSelectTask(BaseAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	protected Void doInBackground(AsyncSelectTask.SelectType... objects) {
		for (final SelectType object : objects) {
			switch (object.type) {
			case KATEGORIA_SUM_CR:
			case KATEGORIA_SUM_DB:

				final PohybDataSource ds = new PohybDataSource(
						adapter.getContext());
				final KategoriaAdapter katAdapter = (KategoriaAdapter) adapter;
				ds.open();
				final String value;
				if (object.type == SELECT_TYPE.KATEGORIA_SUM_CR) {
					value = Constants.sumaToString(Long.valueOf(ds.getSumKat(
							object.id, true)));
				} else {
					value = Constants.sumaToString(Long.valueOf(ds.getSumKat(
							object.id, false)));
				}
				ds.close();

				adapter.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						katAdapter.setText(object.view, value);
					}
				});

				break;
				
			case POHYB_SUM_CR:
			case POHYB_SUM_DB:
				final PohybDataSource ds2 = new PohybDataSource(
						adapter.getContext());
				final PohybAdapter pohAdapter = (PohybAdapter) adapter;
				ds2.open();
				final String pohyb_sum;
				if (object.type == SELECT_TYPE.POHYB_SUM_CR) {
					pohyb_sum = Constants.sumaToString(Long.valueOf(ds2.getSumPohyb(object.id, true)));
				} else {
					pohyb_sum = Constants.sumaToString(Long.valueOf(ds2.getSumPohyb(object.id, false)));
				}
				ds2.close();

				adapter.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pohAdapter.setText(object.view, pohyb_sum);
					}
				});
				break;
			}
		}
		return null;
	}
}