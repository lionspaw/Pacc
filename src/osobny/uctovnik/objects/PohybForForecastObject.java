package osobny.uctovnik.objects;

import java.util.Calendar;

/*
 * Objekt pre predpoved, obsahuje datum a sumu pohybov pre dany ucet
 * */
public class PohybForForecastObject {
	private Calendar datum;
	private Long suma;
	
	public PohybForForecastObject(Calendar datum, Long suma) {
		this.datum = datum;
		this.suma = suma;
	}

	public Calendar getDatum() {
		return datum;
	}

	public Long getSuma() {
		return suma;
	}
	
	
}
