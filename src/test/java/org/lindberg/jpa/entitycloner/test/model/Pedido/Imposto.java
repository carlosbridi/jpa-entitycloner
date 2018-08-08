package org.lindberg.jpa.entitycloner.test.model.Pedido;

import javax.persistence.Embeddable;

@Embeddable
public class Imposto {

	private Double icms;
	private Double icmsst;
	private Double ipi;
	private Double iss;
	
	public Imposto() {};
	
	public Imposto(Double icms, Double icmsst, Double ipi, Double iss) {
		super();
		this.icms = icms;
		this.icmsst = icmsst;
		this.ipi = ipi;
		this.iss = iss;
	}
	public Double getIcms() {
		return icms;
	}
	public void setIcms(Double icms) {
		this.icms = icms;
	}
	public Double getIcmsst() {
		return icmsst;
	}
	public void setIcmsst(Double icmsst) {
		this.icmsst = icmsst;
	}
	public Double getIpi() {
		return ipi;
	}
	public void setIpi(Double ipi) {
		this.ipi = ipi;
	}
	public Double getIss() {
		return iss;
	}
	public void setIss(Double iss) {
		this.iss = iss;
	}
	
	
	
	
}
