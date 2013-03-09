package osobny.uctovnik.helpers;

import osobny.uctovnik.datasources.BankaDataSource;
import osobny.uctovnik.datasources.KategoriaDataSource;
import osobny.uctovnik.datasources.PohybDataSource;
import osobny.uctovnik.datasources.UcetDataSource;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "osobnyUctovnik";
    public static final int DATABASE_VERSION = 1;
    
    public MySQLiteHelper(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(BankaDataSource.CREATE_STATEMENT);
    	db.execSQL(KategoriaDataSource.CREATE_STATEMENT);
    	db.execSQL(UcetDataSource.CREATE_STATEMENT);
    	db.execSQL(PohybDataSource.CREATE_STATEMENT);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	dropTables(db);
    }
    
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	dropTables(db);
    }
    
    public void dropTables(SQLiteDatabase db) {
    	db.execSQL("DROP TABLE IF EXISTS " + BankaDataSource.TABLE_NAME);
    	db.execSQL("DROP TABLE IF EXISTS " + KategoriaDataSource.TABLE_NAME);
    	db.execSQL("DROP TABLE IF EXISTS " + UcetDataSource.TABLE_NAME);
    	db.execSQL("DROP TABLE IF EXISTS " + PohybDataSource.TABLE_NAME);
    	onCreate(db);
    }
    
    public void clearTables(SQLiteDatabase db) {
    	db.delete(BankaDataSource.TABLE_NAME, null, null);
    	db.delete(KategoriaDataSource.TABLE_NAME, null, null);
    	db.delete(UcetDataSource.TABLE_NAME, null, null);
    	db.delete(PohybDataSource.TABLE_NAME, null, null);
    }
}
