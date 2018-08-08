package org.lindberg.jpa.entitycloner.test.model.Pedido;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.lindberg.jpa.entitycloner.test.model.Cliente.Cliente;

@Entity
@SequenceGenerator(name="pedidoid_seq", sequenceName = "pedidoid_seq", allocationSize = 1, initialValue = 1)
public class Pedido {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedidoid_seq")
	private int id;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	private Double valorPedido;
	private boolean despachado;
	
	@Enumerated(EnumType.ORDINAL)
	private SituacaoPedido situacao;
	
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
	private List<ItemPedido> itensPedido = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;
	
	private Imposto imposto = new Imposto(0.0,0.0,0.0,0.0);
	
	public Pedido() {
		
	}
	
	public int getId() {
		return id;
	}
	
	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	public Double getValorPedido() {
		return valorPedido;
	}
	public void setValorPedido(Double valorPedido) {
		this.valorPedido = valorPedido;
	}
	public boolean isDespachado() {
		return despachado;
	}
	public void setDespachado(boolean despachado) {
		this.despachado = despachado;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Imposto getImposto() {
		return imposto;
	}

	public void setImposto(Imposto imposto) {
		this.imposto = imposto;
	}

	public List<ItemPedido> getItensPedido() {
		return itensPedido;
	}

	public void setItensPedido(List<ItemPedido> itensPedido) {
		this.itensPedido = itensPedido;
	}

	public SituacaoPedido getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoPedido situacao) {
		this.situacao = situacao;
	}
				
}
