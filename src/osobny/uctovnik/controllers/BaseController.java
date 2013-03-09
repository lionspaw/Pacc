package osobny.uctovnik.controllers;

import java.util.ArrayList;
import java.util.List;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.adapters.BaseAdapter;
import osobny.uctovnik.adapters.BaseAdapter.SwipableListItem;
import osobny.uctovnik.datasources.BaseDataSource;
import osobny.uctovnik.fragments.BaseListFragment;
import osobny.uctovnik.helpers.SwipeDetector;
import osobny.uctovnik.helpers.SwipeDetector.Action;
import osobny.uctovnik.objects.IdObject;
import android.view.View;

public abstract class BaseController<T extends BaseListFragment, O extends IdObject> implements SwipableListItem {

	protected T fragment; 
	protected View selectedItem = null; //vybrany zaznam
	protected View swipedItem = null;
	protected String where = null;
	
	public BaseController(T fragment) {
		this.fragment = fragment;
	}
	
	@Override
	public void itemClicked(View cItem) {
		if (selectedItem != null) { //uz mame ulozeny view
			if (!selectedItem.equals(cItem)) { //ak stary!=novy, tak staremu nastavime list_item_bg a novy ulozime ako selectedItem
				selectedItem.setBackgroundColor(selectedItem.getResources().getColor(R.color.list_item_bg));
				selectedItem = cItem;
			}			
		} else { //nemame ulozeny view
			selectedItem = cItem;
		}
		clicked();
	}
	
	@Override
	public void itemSwiped(View sItem, SwipeDetector.Action direction) {
		swipedItem = sItem;
		if (!swipedItem.equals(selectedItem)) {
			swipedItem.setBackgroundColor(swipedItem.getResources().getColor(R.color.list_item_bg));
		}
		if (direction == Action.RL) { // L <- R : delete
			delete();
		} else { // L -> R : edit
			edit();
		}
	} 
	
	public abstract void delete();
	
	public abstract void edit();
	
	public abstract void clicked();
	
	/*Synchronne metody*/
	public abstract BaseAdapter<O> getAdapter();
	public abstract BaseDataSource<O> getDataSource();
	
	public void syncFillList(String where) {
		List<O> retVal = new ArrayList<O>();
		getDataSource().open();
		retVal.addAll(getDataSource().getAll(where)); //selektovanie udajov
		getDataSource().close();
		getAdapter().clear();
		for (O item : retVal) { //Nie je mozne pouzivat adapter.addAll() -> je to od api 11.
			getAdapter().add(item);
		}
	}
	
	public void syncInsert(O object) {
		getDataSource().open();
		object.setId(getDataSource().create(object));
		getDataSource().close();
		getAdapter().add(object);
	}
	
	public void syncUpdate(O object) {
		getDataSource().open();
		getDataSource().edit(object);
		getDataSource().close();
	}
}
