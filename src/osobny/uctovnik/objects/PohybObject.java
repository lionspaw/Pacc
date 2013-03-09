package osobny.uctovnik.objects;

import java.util.Calendar;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/*
 * Pohyb objekt - obsahuje udaje pohybu
 * */
@Root
public class PohybObject implements IdObject {
	
	@Attribute
	private Long id;
	@Element
	private Calendar datCas;
	@Element
	private Long suma;
	@Element(required=false)
	private String poznamka;
	@Element
	private Boolean kredit;
	
	private KategoriaObject kategoria;
	@Element
	private Long kategoriaId; //len kvoli import/export
	
	private UcetObject ucet;
	@Element
	private Long ucetId; //len kvoli import/export

	public PohybObject(){}
	
	public PohybObject(Long id, Calendar datCas, Long suma, String poznamka,
			Boolean kredit, KategoriaObject kategoria, UcetObject ucet) {
		this.id = id;
		this.datCas = datCas;
		this.suma = suma;
		this.poznamka = poznamka;
		this.kredit = kredit;
		this.kategoria = kategoria;
		this.kategoriaId = kategoria.getId();
		this.ucet = ucet;
		this.ucetId = ucet.getId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getKategoriaId() {
		return this.kategoriaId;
	}
	
	public Long getUcetId() {
		return this.ucetId;
	}

	public Calendar getDatCas() {
		return datCas;
	}

	public void setDatCas(Calendar datCas) {
		this.datCas = datCas;
	}

	public Long getSuma() {
		return suma;
	}

	public void setSuma(Long suma) {
		this.suma = suma;
	}

	public String getPoznamka() {
		return poznamka;
	}

	public void setPoznamka(String poznamka) {
		this.poznamka = poznamka;
	}

	public Boolean isKredit() {
		return kredit;
	}

	public void setKredit(Boolean kredit) {
		this.kredit = kredit;
	}

	public KategoriaObject getKategoria() {
		return kategoria;
	}

	public void setKategoria(KategoriaObject kategoria) {
		this.kategoria = kategoria;
	}

	public UcetObject getUcet() {
		return ucet;
	}

	public void setUcet(UcetObject ucet) {
		this.ucet = ucet;
	}

}
