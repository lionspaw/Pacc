package osobny.uctovnik.objects;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class UcetObject implements IdObject {
	@Attribute
	private Long id;
	@Element
	private String nazov;
	@Element(required=false)
	private String cislo;
	@Element
	private Long zostatok;
	@Element
	private Long dispZostatok;
	@Element
	private String mena;
	
	private BankaObject banka;
	@Element
	private Long bankaId;//len kvoli import/export

	public UcetObject(){}
	
	public UcetObject(Long id, String nazov, String cislo, Long zostatok,
			Long dispZostatok, String mena, BankaObject banka) {
		this.id = id;
		this.nazov = nazov;
		this.cislo = cislo;
		this.zostatok = zostatok;
		this.dispZostatok = dispZostatok;
		this.mena = mena;
		this.banka = banka;
		this.bankaId = banka.getId();
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

	public String getCisloUctu() {
		return cislo;
	}

	public void setCisloUctu(String cisloUctu) {
		this.cislo = cisloUctu;
	}

	public Long getZostatok() {
		return zostatok;
	}

	public void setZostatok(Long zostatok) {
		this.zostatok = zostatok;
	}

	public Long getDispZostatok() {
		return dispZostatok;
	}

	public void setDispZostatok(Long dispZostatok) {
		this.dispZostatok = dispZostatok;
	}

	public String getMena() {
		return mena;
	}

	public void setMena(String mena) {
		this.mena = mena;
	}

	public BankaObject getBanka() {
		return banka;
	}

	public void setBanka(BankaObject banka) {
		this.banka = banka;
	}
	
	public Long getBankaId() {
		return this.bankaId;
	}
	
	@Override
	public String toString() {
		return nazov;
	}
}
