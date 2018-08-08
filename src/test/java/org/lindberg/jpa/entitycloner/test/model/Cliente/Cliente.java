package org.lindberg.jpa.entitycloner.test.model.Cliente;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(name="clienteid_seq", sequenceName = "clienteid_seq", allocationSize = 1, initialValue = 1)
public class Cliente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clienteid_seq")
	private int id;
	private int codigo;
	private String nome;
	
	public Cliente() {
		
	}
	
	public Cliente(int codigo, String nome) {
		super();
		this.codigo = codigo;
		this.nome = nome;
	}
		
	
	public int getCodigo() {
		return codigo;
	}
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
}
