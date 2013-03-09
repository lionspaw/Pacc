package osobny.uctovnik.controllers;

import java.util.ArrayList;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.adapters.BankaAdapter;
import osobny.uctovnik.async.AsyncInsertTask;
import osobny.uctovnik.async.AsyncListTask;
import osobny.uctovnik.async.AsyncUpdateTask;
import osobny.uctovnik.datasources.BankaDataSource;
import osobny.uctovnik.datasources.UcetDataSource;
import osobny.uctovnik.fragments.BankaListFragment;
import osobny.uctovnik.helpers.GlobalParams;
import osobny.uctovnik.objects.BankaObject;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

public class BankaController extends BaseController<BankaListFragment, BankaObject> {

	private Activity context;
	private BankaAdapter adapter;
	private BankaDataSource dataSource;

	public BankaController(BankaListFragment fragment) {
		super(fragment);
		this.context = fragment.getActivity();
		this.adapter = new BankaAdapter(this, context, new ArrayList<BankaObject>());
		this.dataSource = new BankaDataSource(context);
	}
	
	@Override
	public BankaAdapter getAdapter() {
		return this.adapter;
	}
	
	@Override
	public BankaDataSource getDataSource() {
		return this.dataSource;
	}
	
	public BankaAdapter fillDataToList(String where) {
		this.where = where;
		refreshAdapter();
		return adapter;
	}
	
	public void refreshAdapter() {
		if (GlobalParams.isAsync) { //async nacitanie dat do zoznamu
			AsyncListTask<BankaObject> myAsyncTask = new AsyncListTask<BankaObject>(
					fragment, dataSource, adapter);
			myAsyncTask.execute(where);
		} else { //sync nacitanie dat do zoznamu
			syncFillList(where);
		}
	}

	public void insert(String nazov, String cislo) {
		BankaObject object = new BankaObject(null, cislo, nazov);
		if (GlobalParams.isAsync) {
			AsyncInsertTask<BankaObject> myAsyncTask = new AsyncInsertTask<BankaObject>(
					dataSource, adapter);
			myAsyncTask.execute(object);
		} else {
			syncInsert(object);
		}
		
	}
	
	public void update(String sId, String nazov, String cislo) {
		Long id = Long.decode(sId);
		BankaObject object = new BankaObject(id, cislo, nazov);
		if (GlobalParams.isAsync) {
			AsyncUpdateTask<BankaObject> myAsynchTask = new AsyncUpdateTask<BankaObject>(dataSource, adapter);
			myAsynchTask.execute(object);
		} else {
			syncUpdate(object);
		}
		int itemIndex = this.adapter.getIndexById(id);
		this.adapter.remove(this.adapter.getById(id));
		this.adapter.insert(object, itemIndex);
		this.fragment.onItemUpdate();
	}
	
	private Boolean hasUcet(String id) {
		return pocetUctov(id)>0;
	}
	
	//TODO je len synchronne
	public int pocetUctov(String id) {
		UcetDataSource ucetDataSource = new UcetDataSource(context);
		ucetDataSource.open();
		int pocet = ucetDataSource.getCount(UcetDataSource.BANKA_ID + "=" + id);
		ucetDataSource.close();
		return pocet;
	}
	
	@Override
	public void delete() {
		String sId = ((TextView) this.swipedItem.findViewById(R.id.banka_row_id)).getText().toString();
		if (hasUcet(sId)) { //banku nie je mozne odstranit, ma priradeny ucet
			Toast.makeText(this.fragment.getActivity(), this.fragment.getActivity().getResources()
					.getString(R.string.banka_rem_err_ucet), Toast.LENGTH_LONG).show();
			
		} else { //je mozne odstranit, nema ucet
			Long id = Long.decode(sId);
			BankaObject banka = adapter.getById(id);
			if (banka == null) {
				//ERROR NEEXISTUJE BANKA
				return;
			}
			dataSource.open();
			dataSource.remove(sId);
			dataSource.close();
			adapter.remove(banka);
			swipedItem = null;
		}
		
	}
	
	@Override
	public void edit() {
		String sId = ((TextView) this.swipedItem.findViewById(R.id.banka_row_id)).getText().toString();
		Long id = Long.decode(sId);
		BankaObject banka = adapter.getById(id);
		fragment.showEdit(fragment.getFragmentManager(), banka);
	}
	
	@Override
	public void clicked() {
		String id = ((TextView) this.selectedItem.findViewById(R.id.banka_row_id)).getText().toString();
		this.fragment.getListener().onBankaSelected(id);
	}
}
