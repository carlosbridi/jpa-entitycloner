package org.lindberg.jpa.entitycloner.test.model.Produto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



@Entity
@Table(name = "Produto")
@SequenceGenerator(name="produtoid_seq", sequenceName = "produtoid_seq", allocationSize = 1, initialValue = 1)
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produtoid_seq")
	private int id;
	private int codigo;
	private String descricao;
	
	@Column(name = "data_cadastro")
	@Temporal(TemporalType.DATE)
	private Date dataCadastro;
	
	public Produto() {
		
	}
	
	public Produto(int codigo, String descricao, Date dataCadastro) {
		super();
		this.codigo = codigo;
		this.descricao = descricao;
		this.dataCadastro = dataCadastro;
	}

	public int getCodigo() {
		return codigo;
	}
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
	
	
}
