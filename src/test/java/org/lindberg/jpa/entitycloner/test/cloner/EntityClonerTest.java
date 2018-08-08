package org.lindberg.jpa.entitycloner.test.cloner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.lindberg.jpa.entitycloner.persistence.EntityCloner;
import org.lindberg.jpa.entitycloner.test.model.Cliente.Cliente;
import org.lindberg.jpa.entitycloner.test.model.Pedido.Imposto;
import org.lindberg.jpa.entitycloner.test.model.Pedido.ItemPedido;
import org.lindberg.jpa.entitycloner.test.model.Pedido.Pedido;
import org.lindberg.jpa.entitycloner.test.model.Pedido.SituacaoPedido;
import org.lindberg.jpa.entitycloner.test.model.Produto.Produto;

public class EntityClonerTest {

	private Pedido pedido = new Pedido();
	
	@Before
	public void init() {
		
		Produto prodCamisa = new Produto(1, "Camisa Gola Polo", new Date());
		Produto prodCasaco = new Produto(1, "Casaco de Lã", new Date());
		
		List<ItemPedido> itensPedido = new ArrayList<>();
		itensPedido.add(new ItemPedido(1, prodCamisa, 10.00, 2.0));
		itensPedido.add(new ItemPedido(2, prodCasaco, 20.00, 0.0));
		
		pedido.setDataCadastro(new Date());
		pedido.setValorPedido(1567.26);
		pedido.setDespachado(true);		
		pedido.setSituacao(SituacaoPedido.Aberto);
		pedido.setImposto(new Imposto(12.25,21.55,0.00,7.69));
		pedido.setCliente(new Cliente(1, "Teste"));
		pedido.setItensPedido(itensPedido);
	}
	 
	
	@Test
	public void testeCloneNewEntity() {
		EntityCloner<Pedido> cloner = new EntityCloner<Pedido>(pedido);
		Pedido pedClone = cloner.generateClone();
		
		assertThat(pedido, is(not(pedClone)));
	}
	
	@Test
	public void testClonePedido() {
		EntityCloner<Pedido> cloner = new EntityCloner<Pedido>(pedido);
		Pedido pedClone = cloner.generateClone();
		
		assertThat(pedido.getDataCadastro(), equalTo(pedClone.getDataCadastro()));
		assertThat(pedido.getValorPedido(), equalTo(pedClone.getValorPedido()));
		assertThat(pedido.isDespachado(), equalTo(pedClone.isDespachado()));
		assertThat(pedido.getSituacao(), equalTo(pedClone.getSituacao()));
		
		assertThat(pedido.getImposto().getIcms(), equalTo(pedClone.getImposto().getIcms()));
		assertThat(pedido.getImposto().getIpi(), equalTo(pedClone.getImposto().getIpi()));
		assertThat(pedido.getImposto().getIcmsst(), equalTo(pedClone.getImposto().getIcmsst()));
		assertThat(pedido.getImposto().getIss() , equalTo(pedClone.getImposto().getIss()));
		
		for (int i = 0; i < pedido.getItensPedido().size(); i++) {
			assertThat(pedido.getItensPedido().get(i).getId(), equalTo(pedClone.getItensPedido().get(i).getId()));
			assertThat(pedido.getItensPedido().get(i).getPreco(), equalTo(pedClone.getItensPedido().get(i).getPreco()));
			assertThat(pedido.getItensPedido().get(i).getDesconto(), equalTo(pedClone.getItensPedido().get(i).getDesconto()));
			
			assertThat(pedido.getItensPedido().get(i).getProduto().getId(), equalTo(pedClone.getItensPedido().get(i).getProduto().getId()));
			assertThat(pedido.getItensPedido().get(i).getProduto().getCodigo(), equalTo(pedClone.getItensPedido().get(i).getProduto().getCodigo()));
			assertThat(pedido.getItensPedido().get(i).getProduto().getDescricao(), equalTo(pedClone.getItensPedido().get(i).getProduto().getDescricao()));
		}
	
	}
	
	/**
	 * Realiza teste para verificar se não está alterando a referência do objeto clonado.
	 * Ou seja, se está instanciando um novo objeto.
	 */
	@SuppressWarnings("deprecation")
	@Test
	
	public void testClonePedidoAlterandoClonado() {
		EntityCloner<Pedido> cloner = new EntityCloner<Pedido>(pedido);
		Pedido pedClone = cloner.generateClone();
		
		assertThat(pedido.getDataCadastro(), equalTo(pedClone.getDataCadastro()));
		assertThat(pedido.getValorPedido(), equalTo(pedClone.getValorPedido()));
		assertThat(pedido.isDespachado(), equalTo(pedClone.isDespachado()));
		assertThat(pedido.getSituacao(), equalTo(pedClone.getSituacao()));

		assertThat(pedido.getImposto().getIcms(), equalTo(pedClone.getImposto().getIcms()));
		assertThat(pedido.getImposto().getIpi(), equalTo(pedClone.getImposto().getIpi()));
		assertThat(pedido.getImposto().getIcmsst(), equalTo(pedClone.getImposto().getIcmsst()));
		assertThat(pedido.getImposto().getIss() , equalTo(pedClone.getImposto().getIss()));
		
		pedClone.setDataCadastro(new Date(2018,01,01));
		pedClone.setDespachado(false);
		pedClone.setValorPedido(15.00);
		pedClone.setSituacao(SituacaoPedido.Fechado);
		pedClone.getImposto().setIcms(2.0);
		pedClone.getImposto().setIcmsst(7.0);
		pedClone.getImposto().setIpi(8.0);
		pedClone.getImposto().setIss(0.0);				
		
		assertThat(pedido.getDataCadastro(), is(not(pedClone.getDataCadastro())));
		assertThat(pedido.getValorPedido(), is(not(pedClone.getValorPedido())));
		assertThat(pedido.isDespachado(), is(not(pedClone.isDespachado())));
		assertThat(pedido.getSituacao(), is(not(pedClone.getSituacao())));
		
		assertThat(pedido.getImposto().getIcms(), is(not(pedClone.getImposto().getIcms())));
		assertThat(pedido.getImposto().getIpi(), is(not(pedClone.getImposto().getIpi())));
		assertThat(pedido.getImposto().getIcmsst(), is(not(pedClone.getImposto().getIcmsst())));
		assertThat(pedido.getImposto().getIss() , is(not(pedClone.getImposto().getIss())));
	}
	
	
	@Test
	public void testClonePedidoIgnoreAllFields() {
		@SuppressWarnings("rawtypes")
		HashMap<Class, List<String>> fieldsToIgnore = new HashMap<>();
		fieldsToIgnore.put(Pedido.class, Arrays.asList("valorpedido", "despachado", "codigoCliente", "dataCadastro"));
		
		EntityCloner<Pedido> cloner = new EntityCloner<Pedido>(pedido, fieldsToIgnore);
		Pedido pedClone = cloner.generateClone();
		
		assertThat(pedido.getDataCadastro(), is(not(pedClone.getDataCadastro())));
		assertThat(pedido.getValorPedido(), is(not(pedClone.getValorPedido())));
		assertThat(pedido.isDespachado(), is(not(pedClone.isDespachado())));
	}
	
	@Test
	public void testClonePedidoWithoutFields() {
		@SuppressWarnings("rawtypes")
		HashMap<Class, List<String>> fieldsToIgnore = new HashMap<>();
		fieldsToIgnore.put(Pedido.class, Arrays.asList(""));
		
		EntityCloner<Pedido> cloner = new EntityCloner<Pedido>(pedido, fieldsToIgnore);
		Pedido pedClone = cloner.generateClone();
		
		assertThat(pedido.getDataCadastro(), equalTo(pedClone.getDataCadastro()));
		assertThat(pedido.getValorPedido(), equalTo(pedClone.getValorPedido()));
		assertThat(pedido.isDespachado(), equalTo(pedClone.isDespachado()));
	}
	
	
	@Test
	public void testClonePedidoIgnorandoCamposEntidadeFilha() {
		@SuppressWarnings("rawtypes")
		HashMap<Class, List<String>> fieldsToIgnore = new HashMap<>();
		fieldsToIgnore.put(Imposto.class, Arrays.asList("icms", "icmsst"));
		
		EntityCloner<Pedido> cloner = new EntityCloner<Pedido>(pedido, fieldsToIgnore);
		Pedido pedClone = cloner.generateClone();
		
		assertThat(pedido.getDataCadastro(), equalTo(pedClone.getDataCadastro()));
		assertThat(pedido.getValorPedido(), equalTo(pedClone.getValorPedido()));
		assertThat(pedido.isDespachado(), equalTo(pedClone.isDespachado()));
		
		//Propriedades Modificadas - Icms e IcmsSt
		assertThat(pedido.getImposto().getIcms(), is(not(pedClone.getImposto().getIcms())));
		assertThat(pedido.getImposto().getIcmsst(), is(not((pedClone.getImposto().getIcmsst()))));
		assertThat(pedido.getImposto().getIpi(), equalTo(pedClone.getImposto().getIpi()));
		assertThat(pedido.getImposto().getIss() , equalTo(pedClone.getImposto().getIss()));
	}
	
	
	@Test
	public void testCloneRelacionamentoManyToOne() {
		@SuppressWarnings("rawtypes")
		HashMap<Class, List<String>> fieldsToIgnore = new HashMap<>();
		fieldsToIgnore.put(Cliente.class, Arrays.asList("nome"));
		fieldsToIgnore.put(Produto.class, Arrays.asList("descricao"));
		
		EntityCloner<Pedido> cloner = new EntityCloner<Pedido>(pedido, fieldsToIgnore);
		Pedido pedClone = cloner.generateClone();
		
		//Cliente - Não deve modificar, pois é ManyToOne
		assertThat(pedido.getCliente().getCodigo(), equalTo(pedClone.getCliente().getCodigo()));
		assertThat(pedido.getCliente().getNome(), equalTo(pedClone.getCliente().getNome()));
		
		for (int i = 0; i < pedido.getItensPedido().size(); i++) {
			assertThat(pedido.getItensPedido().get(i).getProduto().getId(), equalTo(pedClone.getItensPedido().get(i).getProduto().getId()));
			assertThat(pedido.getItensPedido().get(i).getProduto().getCodigo(), equalTo(pedClone.getItensPedido().get(i).getProduto().getCodigo()));
			assertThat(pedido.getItensPedido().get(i).getProduto().getDescricao(), equalTo(pedClone.getItensPedido().get(i).getProduto().getDescricao()));
		}
	}
	
	
	@Test
	public void testCloneCollectionsIgnorandoCampos() {
		@SuppressWarnings("rawtypes")
		HashMap<Class, List<String>> fieldsToIgnore = new HashMap<>();
		fieldsToIgnore.put(ItemPedido.class, Arrays.asList("id","desconto"));
		
		EntityCloner<Pedido> cloner = new EntityCloner<Pedido>(pedido, fieldsToIgnore);
		Pedido pedClone = cloner.generateClone();
		
		for (int i = 0; i < pedido.getItensPedido().size(); i++) {
			assertThat(pedido.getItensPedido().get(i).getId(), is(not(pedClone.getItensPedido().get(i).getId())));
			assertThat(pedido.getItensPedido().get(i).getPreco(), equalTo(pedClone.getItensPedido().get(i).getPreco()));
			assertThat(pedido.getItensPedido().get(i).getDesconto(), is(not(pedClone.getItensPedido().get(i).getDesconto())));
			
			
			assertThat(pedido.getItensPedido().get(i).getProduto().getId(), equalTo(pedClone.getItensPedido().get(i).getProduto().getId()));
			assertThat(pedido.getItensPedido().get(i).getProduto().getCodigo(), equalTo(pedClone.getItensPedido().get(i).getProduto().getCodigo()));
			assertThat(pedido.getItensPedido().get(i).getProduto().getDescricao(), equalTo(pedClone.getItensPedido().get(i).getProduto().getDescricao()));
		}
	}
	
	
	@Test
	public void cloneQuantidadeNewEntity() {
		EntityCloner<Pedido> cloner = new EntityCloner<Pedido>(pedido);
		List<Pedido> listaPedidos = cloner.generateClone(10);
		
		for (int i = 0; i < listaPedidos.size(); i++) {
			Pedido pedClone = listaPedidos.get(i);
			
			assertThat(pedido, is(not(pedClone)));
		}
	}
	
	@Test
	public void cloneQuantidadeNegativa() {
		EntityCloner<Pedido> cloner = new EntityCloner<Pedido>(pedido);
		try {
			@SuppressWarnings("unused")
			List<Pedido> listaPedidos = cloner.generateClone(-10);
		} catch (Exception e) {
			assertThat(e.getMessage(), equalTo("Quantidade a ser clonada deve ser maior que 0."));
		}
	}

	@Test
	public void testCloneQuantidade() {
		EntityCloner<Pedido> cloner = new EntityCloner<Pedido>(pedido);
		List<Pedido> listaPedidos = cloner.generateClone(10);
		
		for (int i = 0; i < listaPedidos.size(); i++) {
			Pedido pedClone = listaPedidos.get(i);
			
			assertThat(pedido.getDataCadastro(), equalTo(pedClone.getDataCadastro()));
			assertThat(pedido.getValorPedido(), equalTo(pedClone.getValorPedido()));
			assertThat(pedido.isDespachado(), equalTo(pedClone.isDespachado()));
			assertThat(pedido.getSituacao(), equalTo(pedClone.getSituacao()));

			assertThat(pedido.getImposto().getIcms(), equalTo(pedClone.getImposto().getIcms()));
			assertThat(pedido.getImposto().getIpi(), equalTo(pedClone.getImposto().getIpi()));
			assertThat(pedido.getImposto().getIcmsst(), equalTo(pedClone.getImposto().getIcmsst()));
			assertThat(pedido.getImposto().getIss() , equalTo(pedClone.getImposto().getIss()));
			
			
			for (int j = 0; j < pedido.getItensPedido().size(); j++) {
				assertThat(pedido.getItensPedido().get(j).getId(), equalTo(pedClone.getItensPedido().get(j).getId()));
				assertThat(pedido.getItensPedido().get(j).getPreco(), equalTo(pedClone.getItensPedido().get(j).getPreco()));
				assertThat(pedido.getItensPedido().get(j).getDesconto(), equalTo(pedClone.getItensPedido().get(j).getDesconto()));
				
				assertThat(pedido.getItensPedido().get(j).getProduto().getId(), equalTo(pedClone.getItensPedido().get(j).getProduto().getId()));
				assertThat(pedido.getItensPedido().get(j).getProduto().getCodigo(), equalTo(pedClone.getItensPedido().get(j).getProduto().getCodigo()));
				assertThat(pedido.getItensPedido().get(j).getProduto().getDescricao(), equalTo(pedClone.getItensPedido().get(j).getProduto().getDescricao()));
			}
		}
		
	}
	
}
