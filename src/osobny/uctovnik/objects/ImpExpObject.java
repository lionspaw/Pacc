package osobny.uctovnik.objects;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


@Root(name="paccdata")
public class ImpExpObject {

	@ElementList
	private List<BankaObject> banky;
	
	@ElementList
	private List<UcetObject> ucty;
	
	@ElementList
	private List<KategoriaObject> kategorie;
	
	@ElementList
	private List<PohybObject> pohyby;

	public List<BankaObject> getBanky() {
		return banky;
	}

	public List<UcetObject> getUcty() {
		return ucty;
	}

	public List<KategoriaObject> getKategorie() {
		return kategorie;
	}

	public List<PohybObject> getPohyby() {
		return pohyby;
	}

	public void setBanky(List<BankaObject> banky) {
		this.banky = banky;
	}

	public void setUcty(List<UcetObject> ucty) {
		this.ucty = ucty;
	}

	public void setKategorie(List<KategoriaObject> kategorie) {
		this.kategorie = kategorie;
	}

	public void setPohyby(List<PohybObject> pohyby) {
		this.pohyby = pohyby;
	}
}
