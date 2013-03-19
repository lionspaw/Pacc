package osobny.uctovnik.helpers;

import java.util.Calendar;

import android.os.Build;
import android.os.Build.VERSION;

public class GlobalParams {
	
	/*
	 * Globalne parametre na ulozenie datumu od a do pri filtrovani pohybov.
	 * Globalne z dovodu, ze pri zmene orientacie screenu sa atributy fragmentu PohybListFragment sa neulozili 
	 * (resp. boli vymazane datumy od - do)
	 * */
	public static Calendar datumOd = null;
	public static Calendar datumDo = null;
	public static boolean isAsync = VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2; 
	//Pre Android <= 3.2 robim vsetko synchronne
	
}
