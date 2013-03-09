package osobny.uctovnik.helpers;

import osobny.uctovnik.activities.R;
import android.view.MotionEvent;
import android.view.View;

public class SwipeDetector implements View.OnTouchListener {

	public static enum Action {
		LR, RL, NONE
	}

	private final int len = 4; // (width / len) je potrebne na vykonanie swipe actionu
	
	private float downX, upX;
	private Action action = Action.NONE;

	public boolean swipeDetected() {
		return action != Action.NONE;
	}

	public Action getAction() {
		return action;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float deltaX;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			action = Action.NONE;
			v.setBackgroundColor(v.getResources().getColor(R.color.list_item_bg_selected));
			break;
			
		case MotionEvent.ACTION_MOVE:
			upX = event.getX();
			deltaX = downX - upX;
			if (Math.abs(deltaX) > v.getWidth() / len) {
				if (deltaX < 0) {
					v.setBackgroundColor(v.getResources().getColor(R.color.list_item_bg_edit));
					//Log.d("SWIPING", "L -> R");
				} else if (deltaX > 0) {
					v.setBackgroundColor(v.getResources().getColor(R.color.list_item_bg_delete));
					//Log.d("SWIPING", "L <- R");
				}
			} else {
				v.setBackgroundColor(v.getResources().getColor(R.color.list_item_bg_selected));
			}			
			break;
			
		case MotionEvent.ACTION_UP:
			v.setBackgroundColor(v.getResources().getColor(R.color.list_item_bg_selected));
			upX = event.getX();
			deltaX = downX - upX;
			if (Math.abs(deltaX) > v.getWidth() / len) {
				if (deltaX < 0) {
					action = Action.LR;
					//Log.d("SWIPE", "L -> R");
				} else if (deltaX > 0) {
					//Log.d("SWIPE", "L <- R");
					action = Action.RL;
				}
			}
			break;

		case MotionEvent.ACTION_CANCEL:
			v.setBackgroundColor(v.getResources().getColor(R.color.list_item_bg));
			action = Action.NONE;
			break;
		}
		return false;
	}
}