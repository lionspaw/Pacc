package osobny.uctovnik.controllers;

import java.util.ArrayList;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.adapters.KategoriaAdapter;
import osobny.uctovnik.async.AsyncInsertTask;
import osobny.uctovnik.async.AsyncListTask;
import osobny.uctovnik.async.AsyncSelectTask;
import osobny.uctovnik.async.AsyncSelectTask.SELECT_TYPE;
import osobny.uctovnik.async.AsyncSelectTask.SelectType;
import osobny.uctovnik.async.AsyncUpdateTask;
import osobny.uctovnik.datasources.KategoriaDataSource;
import osobny.uctovnik.datasources.PohybDataSource;
import osobny.uctovnik.fragments.KategoriaListFragment;
import osobny.uctovnik.helpers.Constants;
import osobny.uctovnik.helpers.GlobalParams;
import osobny.uctovnik.objects.KategoriaObject;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

public class KategoriaController extends BaseController<KategoriaListFragment, KategoriaObject> {

	private Activity context;
	private KategoriaAdapter adapter;
	private KategoriaDataSource dataSource;
	
	public KategoriaController(KategoriaListFragment fragment){
		super(fragment);
		this.context = fragment.getActivity();
		this.adapter = new KategoriaAdapter(this, context, new ArrayList<KategoriaObject>());
		this.dataSource = new KategoriaDataSource(context);
	}
	
	@Override
	public KategoriaAdapter getAdapter() {
		return this.adapter;
	}
	
	@Override
	public KategoriaDataSource getDataSource() {
		return this.dataSource;
	}
	
	public KategoriaAdapter fillDataToList(String where) {
		this.where = where;
		refreshAdapter();
		return adapter;
	}
	
	public void refreshAdapter() {
		if (GlobalParams.isAsync) {
			AsyncListTask<KategoriaObject> myAsyncTask = new AsyncListTask<KategoriaObject>(fragment, dataSource, adapter);
			myAsyncTask.execute(where);
		} else {
			syncFillList(where);
		}
	}
	
	public void insert(String nazov, int farba) {
		KategoriaObject object = new KategoriaObject(null, nazov, farba);
		if (GlobalParams.isAsync) {
			AsyncInsertTask<KategoriaObject> myAsyncTask = new AsyncInsertTask<KategoriaObject>(
					dataSource, adapter);
			myAsyncTask.execute(object);
		} else {
			syncInsert(object);
		}
	}
	
	public void update(String sId, String nazov, int farba) {
		Long id = Long.decode(sId);
		KategoriaObject object = new KategoriaObject(id, nazov, farba);
		if (GlobalParams.isAsync) {
			AsyncUpdateTask<KategoriaObject> myAsynchTask = new AsyncUpdateTask<KategoriaObject>(dataSource, adapter);
			myAsynchTask.execute(object);
		} else {
			syncUpdate(object);
		}
		int itemIndex = this.adapter.getIndexById(id);
		this.adapter.remove(this.adapter.getById(id));
		this.adapter.insert(object, itemIndex);
		this.fragment.onItemUpdate();
	}

	private Boolean hasPohyb(String id) {
		PohybDataSource pohybDataSource = new PohybDataSource(context);
		pohybDataSource.open();
		int pocet = pohybDataSource.getCount(PohybDataSource.KATEGORIA_ID + "=" + id);
		pohybDataSource.close();
		return pocet > 0;
	}
	
	public void setSum(KategoriaAdapter katAdapter, String id, TextView kredit, TextView debet) {
		if (GlobalParams.isAsync) {
			AsyncSelectTask myAsyncSelect = new AsyncSelectTask(katAdapter);
			SelectType cr = myAsyncSelect.new SelectType(SELECT_TYPE.KATEGORIA_SUM_CR, id, kredit);
			SelectType db = myAsyncSelect.new SelectType(SELECT_TYPE.KATEGORIA_SUM_DB, id, debet);
			myAsyncSelect.execute(cr, db);
		} else {
			final PohybDataSource pohybDataSource = new PohybDataSource(
					adapter.getContext());
			pohybDataSource.open();
			String valueCr = Constants.sumaToString(Long.valueOf(pohybDataSource.getSumKat(id, true)));
			String valueDb = Constants.sumaToString(Long.valueOf(pohybDataSource.getSumKat(id, false)));
			pohybDataSource.close();
			kredit.setText(valueCr);
			debet.setText(valueDb);
		}
	}
	
	@Override
	public void delete() {
		String sId = ((TextView) this.swipedItem.findViewById(R.id.kategoria_row_id)).getText().toString();
		if (hasPohyb(sId)) { //banku nie je mozne odstranit, kategoria ma priradnu kategoriu
			Toast.makeText(this.fragment.getActivity(), this.fragment.getActivity().getResources()
					.getString(R.string.kategoria_rem_err_pohyb), Toast.LENGTH_LONG).show();
		} else { //je mozne odstranit, nema pohyb
			Long id = Long.decode(sId);
			KategoriaObject kategoria = adapter.getById(id);
			if (kategoria == null) {
				//ERROR NEEXISTUJE kategoria?!
				return;
			}
			dataSource.open();
			dataSource.remove(sId);
			dataSource.close();
			adapter.remove(kategoria);
			swipedItem = null;
		}
		
	
	}
	
	@Override
	public void edit() {
		String sId = ((TextView) this.swipedItem.findViewById(R.id.kategoria_row_id)).getText().toString();
		Long id = Long.decode(sId);
		KategoriaObject kategoria = adapter.getById(id);
		fragment.showEdit(fragment.getFragmentManager(), kategoria);
	}
	
	@Override
	public void clicked() {
		String id = ((TextView) this.selectedItem.findViewById(R.id.kategoria_row_id)).getText().toString();
		this.fragment.getListener().onKategoriaSelected(id);
	}
}
