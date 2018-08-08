package org.lindberg.jpa.entitycloner.persistence;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang.StringUtils;
import org.lindberg.jpa.entitycloner.util.ReflectionUtil;

/**
 * 
 * Cloner of persistent entities. Clone a persistent entity and every tree of
 * relationships, based on jpa annotations.
 * 
 * @author Victor Lindberg (victorlindberg713@gmail.com)
 * 
 */
@SuppressWarnings("rawtypes")
public class EntityCloner<E> {

	/**
	 * Original entity to clone.
	 */
	private E entity;

	private HashMap<Class, List<String>> fieldsToIgnore = null;

	/**
	 * Create a EntityCloner.
	 * @param Entidade a ser clonada.
	 */
	public EntityCloner(E entity) {
		this.entity = entity;
	}

	/**
	 * Create a EntityCloner.
	 * 
	 * @param Entidade a ser clonada
	 * @param Mapa relacional de entidade x campos a serem ignorados.
	 * 
	 */
	public EntityCloner(E entity, HashMap<Class, List<String>> fieldsToIgnore) {
		this.entity = entity;
		this.fieldsToIgnore = fieldsToIgnore;
	}

	/**
	 * Gera um clone a partir da entidade original.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public E generateClone() {
		return (E) generateCopyToPersist(entity, new HashMap<Object, Object>());
	}

	/**
	 * Gera um clone a partir da entidade original.
	 * @param quantidadeClonar Quantidade de objetos a serem clonados a partir da entidade original.
	 * @return Lista com várias entidades clonadas.
	 */
	@SuppressWarnings("unchecked")
	public List<E> generateClone(int quantidadeClonar) {
		if (quantidadeClonar == 0) {
			quantidadeClonar = 1;
		} else if (quantidadeClonar < 0) {
			throw new RuntimeException("Quantidade a ser clonada deve ser maior que 0.");
		}
		
		List<E> listaObjetos = new ArrayList<>();
		for (int i = 0; i < quantidadeClonar-1; i++) {
			listaObjetos.add((E) generateCopyToPersist(entity, new HashMap<Object, Object>()));
		}
		
		return listaObjetos;
	}
	
	protected Object generateCopyToPersist(Object entity, Map<Object, Object> entityCache) {
		Object cacheCopy = entityCache.get(entity);
		if (cacheCopy != null)
			return cacheCopy;

		Object entityCopy = ReflectionUtil.createInstance(entity.getClass());
			entityCache.put(entity, entityCopy);

			Field[] fields = ReflectionUtil.getFields(entity, true, true);

			for (Field entityField : fields) {

				if ((Optional.ofNullable(this.fieldsToIgnore).isPresent()) && (!this.fieldsToIgnore.isEmpty())
						&& this.ignoreField(entity.getClass().getName(), entityField.getType().getName(),
								entityField.getName()))
					continue;

				try {
					ReflectionUtil.makeAttributesAccessible(entityField);
					Object fieldValue = entityField.get(entity);
					if (isEntity(fieldValue) && ((!entityField.isAnnotationPresent(ManyToOne.class)))) { // ManyToOne não posso alterar os campos porque são apenas de ligação
						Object fieldValueCopy = null;
						if (fieldValue == null) {
							generateCopyToPersist(entityField, entityCache);
							entityCache.put(fieldValue, fieldValueCopy);
						} else {
							fieldValueCopy = generateCopyToPersist(fieldValue, entityCache);
							entityCache.put(fieldValue, fieldValueCopy);
						}
						Field oneToOneBack = getBackFieldRelationship(entityCopy, fieldValue, entityField,
								OneToOne.class, OneToOne.class);
						if (oneToOneBack != null) {
							ReflectionUtil.setValueByField(oneToOneBack, fieldValueCopy, entityCopy);
						}
						fieldValue = fieldValueCopy;
					} else if (fieldValue instanceof Collection) {
						boolean oneToManyRelationship = entityField.isAnnotationPresent(OneToMany.class);
						fieldValue = generateCopyCollectionToPersist(entityCopy, (Collection) fieldValue, entityField,
								oneToManyRelationship, entityCache);
					}

					entityField.set(entityCopy, fieldValue);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}

		return entityCopy;
	}

	protected boolean ignoreField(String className, String typeNameField, String fieldName) {

		Map<Class, List<String>> collect = fieldsToIgnore.entrySet().stream()
				.filter(x -> x.getKey().getName().equalsIgnoreCase(className) || x.getKey().getName().equalsIgnoreCase(typeNameField))
				.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

		if (!collect.isEmpty()) {
			Map.Entry<Class, List<String>> entry = collect.entrySet().iterator().next();
			List<String> lista = entry.getValue();
			Optional optField = lista.stream().filter(campo -> campo.equalsIgnoreCase(fieldName)).findFirst();
			return optField.isPresent();
		} else {
			return false;
		}
	}

	/**
	 * Gera uma copia da coleção de entidades pronta para inclusão.
	 * 
	 * @param entityCopy
	 *            copia da entidade onde a copia da coleção será setada.
	 * @param collection
	 *            coleção a ser copiada.
	 * @param entityField
	 * @param oneToManyRelationship
	 *            true se o campo corresponde é um atributo de relacionamento 1 x n
	 *            dentro do mapeamento.
	 * @param entityCache
	 *            cache das entidades que já foram copiados de modo que quando algum
	 *            bean for passado para ser copiado antes da copia for feita o cache
	 *            é verificado para ver se o bean ja foi copiado e assim essa
	 *            instancia de copia ser usada ao invés de fazer novamente a copia.
	 * @return copia da entidade pronta para para a inclusão.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	protected Collection generateCopyCollectionToPersist(Object entityCopy, Collection collection, Field entityField,
			boolean oneToManyRelationship, Map<Object, Object> entityCache)
			throws IllegalArgumentException, IllegalAccessException {

		Collection collectionCopy = null;
		if (collection instanceof List) {
			collectionCopy = new ArrayList();
		} else if (collection instanceof Set)
			collectionCopy = new HashSet();
		else 
			collectionCopy = (Collection) ReflectionUtil.createInstance(collection.getClass());
			

		if (oneToManyRelationship) {
			for (Object item : collection) {
				if (isEntity(item)) {
					Object itemCopy = generateCopyToPersist(item, entityCache);
					Field relationshipBackField = getBackFieldRelationship(entityCopy, itemCopy, entityField,
							OneToMany.class, ManyToOne.class);
					if (relationshipBackField == null)
						relationshipBackField = getBackFieldRelationship(entityCopy, itemCopy, entityField,
								OneToOne.class, OneToOne.class);

					if (relationshipBackField != null) {
						ReflectionUtil.makeAttributesAccessible(relationshipBackField);
						relationshipBackField.set(itemCopy, entityCopy);
					}
					collectionCopy.add(itemCopy);
				}

			}
		} else
			collectionCopy.addAll(collection);

		return collectionCopy;
	}

	protected Field getBackFieldRelationship(Object entityCopy, Object entity, Field forwardField,
			Class<? extends Annotation> forwardRelationshipType, Class<? extends Annotation> backRelationshipType) {
		Field[] fields = ReflectionUtil.getFields(entity, true, true);
		for (Field field : fields) {
			if (field.isAnnotationPresent(backRelationshipType)
					&& field.getType().isAssignableFrom(entityCopy.getClass())) {
				String mappedBy = null;
				if (forwardRelationshipType.equals(OneToMany.class)) {
					OneToMany oneToManyForward = forwardField.getAnnotation(OneToMany.class);
					if (oneToManyForward != null)
						mappedBy = oneToManyForward.mappedBy();
				} else if (forwardRelationshipType.equals(OneToOne.class)) {
					OneToOne oneToOneForward = forwardField.getAnnotation(OneToOne.class);
					if (oneToOneForward != null)
						mappedBy = oneToOneForward.mappedBy();
				}

				if (StringUtils.isNotBlank(mappedBy) && mappedBy.equals(field.getName()))
					return field;

			}
		}

		return null;
	}

	protected boolean isEntity(Object bean) {
		return bean != null && (bean.getClass().isAnnotationPresent(Entity.class)
				|| bean.getClass().isAnnotationPresent(Embeddable.class));
	}
}