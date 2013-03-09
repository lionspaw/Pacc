package osobny.uctovnik.objects;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/*
 * Banka objekt - obsahuje cislo banky a nazov banky 
 * */
@Root
public class BankaObject implements IdObject {
	@Attribute
	private Long id;
	@Element(required=false)
	private String cislo;
	@Element
	private String nazov;

	public BankaObject(){}
	
	public BankaObject(Long id, String cislo, String nazov) {
		this.id = id;
		this.cislo = cislo;
		this.nazov = nazov;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCislo() {
		return cislo;
	}

	public void setCislo(String cislo) {
		this.cislo = cislo;
	}

	public String getNazov() {
		return nazov;
	}

	public void setNazov(String nazov) {
		this.nazov = nazov;
	}

	@Override
	public String toString() {
		return nazov;
	}
}
