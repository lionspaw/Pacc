package osobny.uctovnik.datasources;

import android.database.Cursor;

/*
 * Interface pre DataSource objekty.
 * Obsahuje deklaraciu spolocnych metod
 * T je trieda z osobny.uctovnik.objects
 * */
public interface DataSource<T> {	
	T cursorToObject(Cursor cursor);
	Long create(T object);
	void edit(T object);
	String[] getAllColumns();
	String getTableName();
	String getOrder();
}
