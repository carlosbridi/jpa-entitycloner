package org.lindberg.jpa.entitycloner.util;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for reflection operations. 
 * 
 * @author Victor Lindberg (victorlindberg713@gmail.com)
 *
 */
public class ReflectionUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);
	
	public static void setValueByField(Field field, Object target, Object value){
		Method setterMethod = null;
		if (value != null)
		  setterMethod = getSetterMethod(target.getClass(), field.getName(), value.getClass(), true);
		
		if (setterMethod != null){
		   try {
		      setterMethod.invoke(target, value);
		   } catch (Exception e) {
		      throw new RuntimeException("Error invoking setter method for "+field.getName()+" in "+target.getClass(),e);
		   }
		}
		else{	
		   try {
			  makeAttributesAccessible(field);
			  field.set(target, value);
		   } catch (IllegalArgumentException ex) {
			  throw new RuntimeException(ex);
		   } catch (IllegalAccessException ex) {
			  throw new RuntimeException(ex);
		   }
	   }
	}
	
	
	public static Method getSetterMethod(Object target,String methodOrPropertyName,@SuppressWarnings("rawtypes") Class paramClassSet,boolean findInSuperClasses){
		return getSetterMethod(target.getClass(), methodOrPropertyName, paramClassSet,findInSuperClasses);
	}
	
	@SuppressWarnings("unchecked")
	public static Method getSetterMethod(@SuppressWarnings("rawtypes") Class clazz,String methodOrPropertyName,@SuppressWarnings("rawtypes") Class paramClassSet,boolean findInSuperClasses){
		methodOrPropertyName = getSetterMethodName(methodOrPropertyName);
		
		try {
			return clazz.getDeclaredMethod(methodOrPropertyName,paramClassSet);
		} catch (Exception ex) {
			if (findInSuperClasses && clazz != Object.class)
				return getSetterMethod(clazz.getSuperclass(), methodOrPropertyName, paramClassSet, findInSuperClasses);
		} 
		
		return null;
	}
	
	public static String getSetterMethodName(String fieldName){
		return getMethod("set", fieldName);
	}
	
	private static String getMethod(String prefix,String fieldName){
		if (StringUtils.startsWith(fieldName, prefix))
			return fieldName;
		
		String first = fieldName.substring(0,1);
		return prefix+fieldName.replaceFirst(first, first.toUpperCase());
	}
    
	public static void makeAttributesAccessible(Field... fields) {
		for (Field field : fields)
			field.setAccessible(true);
	}
	
	/**
     * Load Fields [Field] public, private, protected, default of a bean including inherited in a list.
     * 
     * @param clazz bean class
     * @param fieldList list to load fields
     * @param findInSuperClasses true whether to load the inherited fields and false otherwise.
     * @param setFieldsAsAccessible true if the fields should be setted as accessible.
     */
    public static void loadFields(@SuppressWarnings("rawtypes") Class clazz, List<Field> fieldList, boolean findInSuperClasses,
        boolean setFieldsAsAccessible) {
        if (clazz.equals(Object.class))
            return;

        for (Field field : clazz.getDeclaredFields()) {
            if (setFieldsAsAccessible)
                makeAttributesAccessible(field);
            fieldList.add(field);
        }

        if (findInSuperClasses)
            loadFields(clazz.getSuperclass(), fieldList, findInSuperClasses, setFieldsAsAccessible);
    }
    
    /**
     * Gets Fields [Field] public, private, protected, default of a bean including inherited.
     * 
     * @param target bean.
     * @param findInSuperClasses true whether to load the inherited fields and false otherwise.
     * @param setFieldsAsAccessible true if the fields should be setted as accessible. 
     */
    public static Field[] getFields(Object target, boolean findInSuperClasses, boolean setFieldsAsAccessible) {
        if (!(target instanceof Object))
            return target.getClass().getDeclaredFields();

        List<Field> campos = new ArrayList<Field>();
        loadFields(target.getClass(), campos, findInSuperClasses, setFieldsAsAccessible);
        return campos.toArray(new Field[campos.size()]);
    }
    

	/**
	 * Creates an instance.
	 * 
	 * @param <E> bean type expected.
	 * @param clazz bean class.
	 * @param args contructor arguments.
	 * @return bean instance.
	 * @throws EntityClonerException 
	 */
	@SuppressWarnings("unchecked")
	public static <E> E createInstance(Class<E> clazz,Object... args){
		try {
			return (E) ConstructorUtils.invokeConstructor(clazz, args);
		} catch (NoSuchMethodException e) {
			LOGGER.error("Construtor público não implementado para a classe: " + clazz.getName());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
