package osobny.uctovnik.datasources;

import java.util.ArrayList;
import java.util.List;

import osobny.uctovnik.helpers.MySQLiteHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/*
 * Abstraktna trieda, ktora obsahuje implementaciu spolocnych metod
 * */
public abstract class BaseDataSource<T> implements DataSource<T> {
	
	public static final String ID = "_id";
	private MySQLiteHelper dbHelper = null;
	protected SQLiteDatabase database = null;
	
	public BaseDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}
		
	/*
	 * Ked otvarame uz existujucu databazu, tak netreba open ani close!
	 * */
	public BaseDataSource(SQLiteDatabase database) { 
		this.database = database;
	}
	
	public void open() throws SQLException {
		if (database == null || !database.isOpen()) {
			database = dbHelper.getWritableDatabase();
		}
	}
	
	public void close() {
		if (database.isOpen()) {
			database.close();
		}
		dbHelper.close();
	}
	
	/*
	 * Ziskanie udajov (vsetky stlpce) podla zadanej where podmineky
	 * */
	public List<T> getAll(String where) {
		List<T> retVal = new ArrayList<T>();
		Cursor cursor = database.query(getTableName(), getAllColumns(),
				where, null, null, null, getOrder());
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			retVal.add(cursorToObject(cursor));
			cursor.moveToNext();
		}
		cursor.close();
		return retVal;		
	}
	
	public int getCount(String where) {
		Cursor cursor = database.rawQuery("SELECT COUNT(*) as pocet FROM " + getTableName() + (where == null ? "" : " WHERE " + where), null);
		cursor.moveToFirst();
		int pocet = cursor.getInt(cursor.getColumnIndex("pocet"));
		cursor.close();
		return pocet;
	}
	
	public int remove(String id) {
		return database.delete(getTableName(), ID + "=" + id, null);
	}
	
	/*
	 * Ziskanie zaznamu podla zadanej hodnoty id
	 * */
	public T get(Long id) {
		Cursor cursor = database.query(getTableName(), getAllColumns(),
				ID + "=" + id.toString(), null, null, null, null);
		cursor.moveToFirst();
		T retVal = null;
		if (!cursor.isAfterLast()) {
			retVal = cursorToObject(cursor);
		}
		cursor.close();
		return retVal;
	}
	
	@Override
	public String getOrder() {
		return ID + " DESC";
	}
}
