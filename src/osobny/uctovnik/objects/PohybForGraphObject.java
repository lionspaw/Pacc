package osobny.uctovnik.objects;

import java.util.Calendar;

public class PohybForGraphObject {
	private Calendar datCas;
	private Long suma;
	private Boolean kredit;
	
	public PohybForGraphObject(Calendar datCas, Long suma, Boolean kredit) {
		this.datCas = datCas;
		this.suma = suma;
		this.kredit = kredit;
	}

	public Calendar getDatCas() {
		return datCas;
	}

	public Long getSuma() {
		return suma;
	}

	public Boolean isKredit() {
		return kredit;
	}
	
}
