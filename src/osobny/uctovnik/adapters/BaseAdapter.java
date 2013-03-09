package osobny.uctovnik.adapters;

import java.util.Iterator;
import java.util.List;

import osobny.uctovnik.helpers.SwipeDetector;
import osobny.uctovnik.objects.IdObject;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

public abstract class BaseAdapter<T extends IdObject> extends ArrayAdapter<T>  {
	protected final Activity context;
	protected List<T> list;
	protected final int layoutId;
	protected final SwipableListItem swipeableListItem;
	
	public interface SwipableListItem {
		public void itemSwiped(View swipedItem, SwipeDetector.Action direction);
		public void itemClicked(View clickedItem);
	}
	
	public BaseAdapter(SwipableListItem swipeableListItem, Activity context, int layoutId, List<T> list) {
		super(context, layoutId, list);
		this.context = context;
		this.list = list;
		this.layoutId = layoutId;
		this.swipeableListItem = swipeableListItem;
	}
	
	public void setList(List<T> list) {
		this.list = list;
	}
	
	public Activity getActivity() {
		return this.context;
	}
	
	/*
	 * funkcia getById() najde a vrati object podla id v zozname
	 * */
	public T getById(Long id) {
		for (Iterator<T> iterator = list.iterator(); iterator.hasNext(); ) {
			T obj = iterator.next();
			if (id.equals(obj.getId())) {
				return obj;
			}
		}
		return null;
	}
	
	public int getIndexById(Long id) {
		int index = -1;
		for (Iterator<T> iterator = list.iterator(); iterator.hasNext(); ) {
			index++;
			T obj = iterator.next();
			if (id.equals(obj.getId())) {
				return index;
			}
		}
		return -1;
	}
	
	public void addSwipe(View v) {
		final SwipeDetector swipeDetector = new SwipeDetector();
		v.setOnTouchListener(swipeDetector);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (swipeDetector.swipeDetected()){
					BaseAdapter.this.swipeableListItem.itemSwiped(v, swipeDetector.getAction());
                } else {
                	BaseAdapter.this.swipeableListItem.itemClicked(v);
                }
			}
		});
	}
}
