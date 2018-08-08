package org.lindberg.jpa.entitycloner.test.model.Pedido;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.lindberg.jpa.entitycloner.test.model.Produto.Produto;

@Entity(name = "itempedido")
@SequenceGenerator(name="itempedidoid_seq", sequenceName = "itempedidoid_seq", allocationSize = 1, initialValue = 1)
@Table(name = "itempedido")
public class ItemPedido {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itempedidoid_seq")
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "produto_id")
	private Produto produto;
	private Double preco;
	private Double desconto;
	
	@ManyToOne
	@JoinColumn(name = "pedido_id")
	private Pedido pedido;
	
	public ItemPedido() {};
	
	public ItemPedido(Produto produto, Double preco, Double desconto) {
		super();
		this.produto = produto;
		this.preco = preco;
		this.desconto = desconto;
	}
	
	public ItemPedido(int id, Produto produto, Double preco, Double desconto) {
		super();
		this.id = id;
		this.produto = produto;
		this.preco = preco;
		this.desconto = desconto;
	}
	
	
	public int getId() {
		return id;
	}
	public Double getPreco() {
		return preco;
	}
	public void setPreco(Double preco) {
		this.preco = preco;
	}
	public Double getDesconto() {
		return desconto;
	}
	public void setDesconto(Double desconto) {
		this.desconto = desconto;
	}
	public Produto getProduto() {
		return produto;
	}
	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}
	
}
