package org.lindberg.jpa.entitycloner.test.cloner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lindberg.jpa.entitycloner.persistence.EntityCloner;
import org.lindberg.jpa.entitycloner.test.model.Cliente.Cliente;
import org.lindberg.jpa.entitycloner.test.model.Pedido.Imposto;
import org.lindberg.jpa.entitycloner.test.model.Pedido.ItemPedido;
import org.lindberg.jpa.entitycloner.test.model.Pedido.Pedido;
import org.lindberg.jpa.entitycloner.test.model.Pedido.SituacaoPedido;
import org.lindberg.jpa.entitycloner.test.model.Produto.Produto;

public class EntityClonerIT {

	
	private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:base.sql'";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    @SuppressWarnings("unused")
	private static Connection connection;
    private static EntityManager entityManager;
    
    private static Produto prodCamisaLa;
    private static Produto prodCamisaGolaPolo;
    private static Cliente cliente;
    private static Pedido pedido;
	
    
	@BeforeClass
	public static void init() throws SQLException {
		
		createEntityManager();
		connection = getDBConnection();
		
		prodCamisaGolaPolo = new Produto(1, "Camisa Gola Polo", new Date());
		prodCamisaLa = new Produto(1, "Casaco de Lã", new Date());
		cliente = new Cliente(1, "Cliente A");
		
		pedido = new Pedido();
		pedido.setCliente(cliente);
		pedido.setDataCadastro(new Date());
		pedido.setDespachado(false);
		pedido.setSituacao(SituacaoPedido.Aberto);
		pedido.setValorPedido(100.00);
				
		List<ItemPedido> listaItens = new ArrayList<>();
		listaItens.add(new ItemPedido(prodCamisaGolaPolo, 10.0, 0.0));
		listaItens.add(new ItemPedido(prodCamisaLa, 8.0, 0.0));
		
		pedido.setItensPedido(listaItens);
		
		entityManager.getTransaction().begin();
		entityManager.persist(prodCamisaGolaPolo);
		entityManager.persist(prodCamisaLa);
		entityManager.persist(cliente);
		entityManager.persist(pedido);
		entityManager.getTransaction().commit();
		
	}
	
	@Test
	public void testCloneObjetoSimples() {
		
		Produto produto = (Produto) entityManager.createQuery("SELECT p FROM Produto p where p.id = :id").setParameter("id", prodCamisaGolaPolo.getId()).getResultList().get(0);
		
		@SuppressWarnings("rawtypes")
		HashMap<Class, List<String>> fieldsToIgnore = new HashMap<>();
		fieldsToIgnore.put(Produto.class, Arrays.asList("id"));
		
		EntityCloner<Produto> clonerProduto = new EntityCloner<Produto>(prodCamisaGolaPolo, fieldsToIgnore);
		Produto prodCamisa2 = clonerProduto.generateClone();
		
		entityManager.getTransaction().begin();
		entityManager.persist(prodCamisa2);
		entityManager.getTransaction().commit();
		
		Produto produto2 = (Produto) entityManager.createQuery("SELECT p FROM Produto p where p.id = :id").setParameter("id", prodCamisa2.getId()).getResultList().get(0);
		
		Produto prod1 = produto;
		Produto prod2 = produto2;
		
		assertThat(prod1.getId(), not(eq(prod2.getId())));
		assertThat(prod1.getCodigo(), equalTo(prod2.getCodigo()));
		assertThat(prod1.getDescricao(), equalTo(prod2.getDescricao()));
	}
	
	
	
	@Test
	public void testeCloneObjetoPedido() {
		Pedido pedBeforeClone = (Pedido) entityManager.createQuery("SELECT p FROM Pedido p where p.id = :id").setParameter("id", pedido.getId()).getResultList().get(0);
		
		@SuppressWarnings("rawtypes")
		HashMap<Class, List<String>> fieldsToIgnore = new HashMap<>();
		fieldsToIgnore.put(Pedido.class, Arrays.asList("id"));
		fieldsToIgnore.put(ItemPedido.class, Arrays.asList("id"));
		
		EntityCloner<Pedido> clonerPedido = new EntityCloner<Pedido>(pedBeforeClone, fieldsToIgnore);
		Pedido pedAfterClone = clonerPedido.generateClone();
		
		this.persistObject(pedAfterClone);
		
		Pedido pedAfterCloneDB = (Pedido) entityManager.createQuery("SELECT p FROM Pedido p where p.id = :id").setParameter("id", pedAfterClone.getId()).getResultList().get(0);
		
		assertThat(pedBeforeClone.getDataCadastro(), equalTo(pedAfterCloneDB.getDataCadastro()));
		assertThat(pedBeforeClone.getValorPedido(), equalTo(pedAfterCloneDB.getValorPedido()));
		assertThat(pedBeforeClone.isDespachado(), equalTo(pedAfterCloneDB.isDespachado()));
		
		//Propriedades Modificadas - Icms e IcmsSt
		assertThat(pedBeforeClone.getImposto().getIcms(), is(pedAfterCloneDB.getImposto().getIcms()));
		assertThat(pedBeforeClone.getImposto().getIcmsst(), is(pedAfterCloneDB.getImposto().getIcmsst()));
		assertThat(pedBeforeClone.getImposto().getIpi(), is(pedAfterCloneDB.getImposto().getIpi()));
		assertThat(pedBeforeClone.getImposto().getIss() , is(pedAfterCloneDB.getImposto().getIss()));
		
		for (int i = 0; i < pedBeforeClone.getItensPedido().size(); i++) {
			assertThat(pedBeforeClone.getItensPedido().get(i).getProduto().getId(), equalTo(pedAfterCloneDB.getItensPedido().get(i).getProduto().getId()));
			assertThat(pedBeforeClone.getItensPedido().get(i).getProduto().getCodigo(), equalTo(pedAfterCloneDB.getItensPedido().get(i).getProduto().getCodigo()));
			assertThat(pedBeforeClone.getItensPedido().get(i).getProduto().getDescricao(), equalTo(pedAfterCloneDB.getItensPedido().get(i).getProduto().getDescricao()));
		}
	}
	
	@Test
	public void testeCloneManipulandoDadosEmbeddableClass() {
		Pedido pedBeforeClone = (Pedido) entityManager.createQuery("SELECT p FROM Pedido p where p.id = :id").setParameter("id", pedido.getId()).getResultList().get(0);
		
		@SuppressWarnings("rawtypes")
		HashMap<Class, List<String>> fieldsToIgnore = new HashMap<>();
		fieldsToIgnore.put(Pedido.class, Arrays.asList("id"));
		fieldsToIgnore.put(ItemPedido.class, Arrays.asList("id"));		
		fieldsToIgnore.put(Imposto.class, Arrays.asList("icms", "icmsst", "ipi"));
		
		EntityCloner<Pedido> clonerPedido = new EntityCloner<Pedido>(pedBeforeClone, fieldsToIgnore);
		Pedido pedAfterClone = clonerPedido.generateClone();
		
		this.persistObject(pedAfterClone);
		
		Pedido pedAfterCloneDB = (Pedido) entityManager.createQuery("SELECT p FROM Pedido p where p.id = :id").setParameter("id", pedAfterClone.getId()).getResultList().get(0);
		
		assertThat(pedido.getImposto().getIcms(), is(not(pedAfterCloneDB.getImposto().getIcms())));
		assertThat(pedido.getImposto().getIpi(), is(not(pedAfterCloneDB.getImposto().getIpi())));
		assertThat(pedido.getImposto().getIcmsst(), is(not(pedAfterCloneDB.getImposto().getIcmsst())));
		assertThat(pedido.getImposto().getIss(), is(pedAfterCloneDB.getImposto().getIss()));
	}
	

	@Test
	public void testCloneRelacionamentoManyToOne() {
		Pedido pedBeforeClone = (Pedido) entityManager.createQuery("SELECT p FROM Pedido p where p.id = :id").setParameter("id", pedido.getId()).getResultList().get(0);
		
		@SuppressWarnings("rawtypes")
		HashMap<Class, List<String>> fieldsToIgnore = new HashMap<>();
		fieldsToIgnore.put(Pedido.class, Arrays.asList("id"));
		fieldsToIgnore.put(ItemPedido.class, Arrays.asList("id"));
		fieldsToIgnore.put(Cliente.class, Arrays.asList("nome"));
		fieldsToIgnore.put(Produto.class, Arrays.asList("descricao"));
		
		EntityCloner<Pedido> cloner = new EntityCloner<Pedido>(pedBeforeClone, fieldsToIgnore);
		Pedido pedClone = cloner.generateClone();
		
		this.persistObject(pedClone);
		
		Pedido pedAfterCloneDB = (Pedido) entityManager.createQuery("SELECT p FROM Pedido p where p.id = :id").setParameter("id", pedClone.getId()).getResultList().get(0);
		
		//Cliente - Não deve modificar, pois é ManyToOne
		assertThat(pedido.getCliente().getCodigo(), equalTo(pedAfterCloneDB.getCliente().getCodigo()));
		assertThat(pedido.getCliente().getNome(), equalTo(pedAfterCloneDB.getCliente().getNome()));
		
		for (int i = 0; i < pedido.getItensPedido().size(); i++) {
			assertThat(pedido.getItensPedido().get(i).getProduto().getId(), equalTo(pedAfterCloneDB.getItensPedido().get(i).getProduto().getId()));
			assertThat(pedido.getItensPedido().get(i).getProduto().getCodigo(), equalTo(pedAfterCloneDB.getItensPedido().get(i).getProduto().getCodigo()));
			assertThat(pedido.getItensPedido().get(i).getProduto().getDescricao(), equalTo(pedAfterCloneDB.getItensPedido().get(i).getProduto().getDescricao()));
		}
	}

	private static Connection getDBConnection() {
		Connection dbConnection = null;
		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return dbConnection;
	}
	
	
	private <T> T persistObject(T object) {
		entityManager.getTransaction().begin();
		entityManager.persist(object);
		entityManager.getTransaction().commit();
		
		return object;
	}
	
	private static void createEntityManager() {
		
		if (!Optional.ofNullable(entityManager).isPresent()) {
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-entitycloner");
			entityManager = emf.createEntityManager();
		}
		
	}
	
}
