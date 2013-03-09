package osobny.uctovnik.controllers;

import java.util.ArrayList;
import java.util.List;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.adapters.UcetAdapter;
import osobny.uctovnik.async.AsyncInsertTask;
import osobny.uctovnik.async.AsyncListTask;
import osobny.uctovnik.async.AsyncUpdateTask;
import osobny.uctovnik.datasources.BankaDataSource;
import osobny.uctovnik.datasources.PohybDataSource;
import osobny.uctovnik.datasources.UcetDataSource;
import osobny.uctovnik.fragments.UcetListFragment;
import osobny.uctovnik.helpers.Constants;
import osobny.uctovnik.helpers.GlobalParams;
import osobny.uctovnik.objects.BankaObject;
import osobny.uctovnik.objects.UcetObject;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

public class UcetController extends BaseController<UcetListFragment, UcetObject> {
	
	private Activity context;
	private UcetAdapter adapter;
	private UcetDataSource dataSource;
	
	public UcetController(UcetListFragment fragment){
		super(fragment);
		this.context = fragment.getActivity();
		this.adapter = new UcetAdapter(this, context, new ArrayList<UcetObject>());
		this.dataSource = new UcetDataSource(context);
	}
	
	@Override
	public UcetAdapter getAdapter() {
		return this.adapter;
	}
	
	@Override
	public UcetDataSource getDataSource() {
		return this.dataSource;
	}

	
	public UcetAdapter fillDataToList(String where) {
		this.where = where;
		refreshAdapter();
		return adapter;
	}
	
	public void refreshAdapter() {
		if (GlobalParams.isAsync) {
	    	AsyncListTask<UcetObject> myAsyncTask = new AsyncListTask<UcetObject>(fragment, dataSource, adapter);	
			myAsyncTask.execute(where);
		} else {
			syncFillList(where);
		}
	}
	
	public List<BankaObject> getBanky() {
		BankaDataSource bankaDataSource = new BankaDataSource(context);
		bankaDataSource.open();
		List<BankaObject> bankaObjects = bankaDataSource.getAll(null);
		bankaDataSource.close();
		return bankaObjects;
	}
	
	public void insert(String nazov, String cislo, String zost, String dispZost, String mena, BankaObject banka) {
		UcetObject object = getObjectFromInput(null, nazov, cislo, zost, dispZost, mena, banka);
		if (GlobalParams.isAsync) {
			AsyncInsertTask<UcetObject> myAsyncTask = new AsyncInsertTask<UcetObject>(
					dataSource, adapter);
			myAsyncTask.execute(object);
		} else {
			syncInsert(object);
		}
		this.fragment.onItemAdd();
	}
	
	private UcetObject getObjectFromInput(String sId, String nazov, String cislo, String sZost, String sDispZost, String mena, BankaObject banka) {
		Long id = sId == null ? null : Long.decode(sId);
		Long zost = "".equals(sZost) ? 0L : Constants.sumaToLong(sZost);
		Long dispZost = "".equals(sDispZost) ? zost : Constants.sumaToLong(sDispZost);
		
		return new UcetObject(id, nazov, cislo, zost, dispZost, mena, banka);
	}
	
	public void update(String sId, String nazov, String cislo, String zost, String dispZost, String mena, BankaObject banka) {
		UcetObject object = getObjectFromInput(sId, nazov, cislo, zost, dispZost, mena, banka);
		if (GlobalParams.isAsync) {
			AsyncUpdateTask<UcetObject> myAsynchTask = new AsyncUpdateTask<UcetObject>(dataSource, adapter);
			myAsynchTask.execute(object);
		} else {
			syncUpdate(object);
		}
		int itemIndex = this.adapter.getIndexById(object.getId());
		this.adapter.remove(this.adapter.getById(object.getId()));
		this.adapter.insert(object, itemIndex);
		this.fragment.onItemUpdate();
	}

	private Boolean hasPohyb(String id) {
		PohybDataSource pohybDataSource = new PohybDataSource(context);
		pohybDataSource.open();
		int pocet = pohybDataSource.getCount(PohybDataSource.UCET_ID + "=" + id);
		pohybDataSource.close();
		return pocet>0;
	}
	
	@Override
	public void delete() {
		String sId = ((TextView) this.swipedItem.findViewById(R.id.ucet_row_id)).getText().toString();
		if (hasPohyb(sId)) { //banku nie je mozne odstranit, ma priradeny ucet
			Toast.makeText(this.fragment.getActivity(), this.fragment.getActivity().getResources()
					.getString(R.string.ucet_rem_err_pohyb), Toast.LENGTH_LONG).show();
		} else { //je mozne odstranit, nema ucet
			Long id = Long.decode(sId);
			UcetObject ucet = adapter.getById(id);
			if (ucet == null) {
				//ERROR NEEXISTUJE ucet
				return;
			}
			dataSource.open();
			dataSource.remove(sId);
			dataSource.close();
			adapter.remove(ucet);
			swipedItem = null;
			this.fragment.onItemDelete();
		}
		
	}
	
	@Override
	public void edit() {
		String sId = ((TextView) this.swipedItem.findViewById(R.id.ucet_row_id)).getText().toString();
		Long id = Long.decode(sId);
		UcetObject ucet = adapter.getById(id);
		fragment.showEdit(fragment.getFragmentManager(), ucet);
	}
	
	@Override
	public void clicked() {
		String id = ((TextView) this.selectedItem.findViewById(R.id.ucet_row_id)).getText().toString();
		this.fragment.getListener().onUcetSelected(id);
	}
}
