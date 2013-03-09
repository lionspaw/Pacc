package osobny.uctovnik.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.widget.DatePicker;

public class Constants {
	
	public static String REG_NEGATIV = "\\-?";
	public static String REG_CELE_CISLO_POZ = "[0-9]+";
	public static String REG_DESATINNE_CISLO_POZ = "[0-9]+\\.[0-9]{1,2}";
	public static String REG_CELE_CISLO = Constants.REG_NEGATIV + Constants.REG_CELE_CISLO_POZ;
	public static String REG_DESATINNE_CISLO = Constants.REG_NEGATIV + Constants.REG_DESATINNE_CISLO_POZ;
	
	public static String getCas(Calendar cal) {
		StringBuffer retVal = new StringBuffer();
		String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
		retVal.append((hour.length() == 1 ? "0" : "") + hour + ":");
		String minute = String.valueOf(cal.get(Calendar.MINUTE));
		retVal.append((minute.length() == 1 ? "0" : "") + minute);
		return retVal.toString();
	}
	
	public static String getDatum(Calendar datum) {
		StringBuffer retVal = new StringBuffer();
		retVal.append(datum.get(Calendar.YEAR) + ".");
		String mesiac = String.valueOf(datum.get(Calendar.MONTH) + 1);
		retVal.append((mesiac.length() == 1 ? "0" : "") + mesiac + ".");
		String day = String.valueOf(datum.get(Calendar.DAY_OF_MONTH));
		retVal.append((day.length() == 1 ? "0" : "") + day);
		
		return retVal.toString();
	}
	
	public static String getDatumCas(Calendar cal) {
		StringBuffer retVal = new StringBuffer();
		retVal.append(getDatum(cal) + " ");
		retVal.append(getCas(cal));
		return retVal.toString();
	}
	
	public static Calendar getCalendarFromString(String str) throws ParseException {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		cal.setTime(sdf.parse(str));
		return cal;
	}
	
	public static Calendar getDateFromString(String str) throws ParseException {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		cal.setTime(sdf.parse(str));
		return cal;
	}
	
	public static Calendar getCalendar(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		return cal;
	}
	
	public static Calendar getDatum(DatePicker datePicker) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, datePicker.getYear());
		cal.set(Calendar.MONTH, datePicker.getMonth());
		cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
		return cal;
	}
	
	/*
	 * Funkcia vrati z numerickej hodnoty 123 string "1.23" 
	 */
	public static String sumaToString(Long suma) {
		if (suma % 100 == 0) { // cislo nema desatinnu cast
			return String.valueOf(suma / 100);
		} else {
			String inte = String.valueOf(suma / 100);
			String frac = suma < 0 ? String.valueOf(-1*suma % 100) : String.valueOf(suma % 100);
			return inte + "." + (frac.length() == 1 ? "0" : "") + frac;
		}
	}
	
	/*
	 * Funkcia vrati zo stringu "123.56" numericku hodnotu: 123*100 + 56
	 * */
	public static Long sumaToLong(String suma) {		
		if (suma.matches(Constants.REG_CELE_CISLO)) { //cele cislo
			return Long.valueOf(suma + "00");
		} else { //tvar \\-?[0-9]+\\.[0-9]+
			int positionOfFloatPont = suma.indexOf(".");
			Long inte = Long.valueOf(suma.substring(0, positionOfFloatPont));
			String tizedes = suma.substring(positionOfFloatPont + 1);
			Long frac = Long.valueOf(tizedes.length() == 1 ? tizedes + "0" : tizedes);
			return inte*100 + frac;
		}
	}
	
	public static Double sumaToDouble(Long suma) {
		Double retVal = Double.valueOf((suma / 100) + ((suma % 100) / 100d));
		return retVal;
	}
	
	public static boolean areDatesEqual(Calendar datum1, Calendar datum2) {
		if (datum1.get(Calendar.YEAR) == datum2.get(Calendar.YEAR) &&
			datum1.get(Calendar.MONTH) == datum2.get(Calendar.MONTH) &&
			datum1.get(Calendar.DAY_OF_MONTH) == datum2.get(Calendar.DAY_OF_MONTH)) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public static int daysBetween(Calendar odDatum, Calendar doDatum) {
		long diff = doDatum.getTimeInMillis() - odDatum.getTimeInMillis();
		return (int) (diff / (1000*60*60*24));
	}
}
