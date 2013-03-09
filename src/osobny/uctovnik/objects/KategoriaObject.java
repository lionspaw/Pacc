package osobny.uctovnik.objects;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/*
 * Kategoria objekt - obsahuje nazov kategorie a farbu
 * */
@Root
public class KategoriaObject implements IdObject {
	@Attribute
	private Long id;
	@Element
	private String nazov;
	@Element
	private Integer farba;

	public KategoriaObject(){}
	
	public KategoriaObject(Long id, String nazov, Integer farba) {
		this.id = id;
		this.nazov = nazov;
		this.farba = farba;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNazov() {
		return nazov;
	}

	public void setNazov(String nazov) {
		this.nazov = nazov;
	}

	public Integer getFarba() {
		return farba;
	}

	public void setFarba(Integer farba) {
		this.farba = farba;
	}
	
	@Override
	public String toString() {
		return nazov;
	}
}
