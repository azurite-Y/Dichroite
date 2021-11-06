package org.zy.dichroite.fluorite.mapping;

import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.zy.dichroite.fluorite.annotation.BeanMapping;
import org.zy.dichroite.fluorite.annotation.Column;
import org.zy.dichroite.fluorite.session.Configuration;
import org.zy.fluorite.core.utils.Assert;

/**
 * @DateTime 2021年10月5日;
 * @author zy(azurite-Y);
 * @Description 参数集映射
 */
public class ParameterMap {
	/**
	 * 代表无参
	 */
	public static final ParameterMap EMPYT_PARAMETER_MAP = new ParameterMap(); 

	/**
	 * 方法全限定民
	 */
	private String id;
	/**
	 * 方法所属类
	 */
	private Class<?> type;
	/**
	 * 方法参数集
	 */
	private List<ParameterMapping> parameterMappings;
	/**
	 * 唯一入参类型是否就是返回值类型
	 */
	private boolean resultParameter;
	/**
	 * 是否已自动映射
	 */
	private static boolean autoMapped;
	/**
	 * 是否唯一参数
	 */
	private boolean onlyParameter;

	private ParameterMap() {
		parameterMappings = new ArrayList<>();
	}

	public static class Builder {
		private ParameterMap parameterMap = new ParameterMap();

		/**
		 * 复用之前的 ParameterMap对象
		 * @param parameterMap
		 */
		public Builder(ParameterMap parameterMap) {
			this(parameterMap.id,  parameterMap.type, parameterMap.parameterMappings, parameterMap.resultParameter);
			autoMapped = true;
		}

		public Builder(List<ParameterMapping> parameterMappings,boolean resultParameter) {
			this.parameterMap.parameterMappings = parameterMappings;
			this.parameterMap.resultParameter = resultParameter;
		}

		public Builder(String id, Class<?> type, List<ParameterMapping> parameterMappings,boolean resultParameter) {
			this(parameterMappings,resultParameter);
			parameterMap.id = id;
			parameterMap.type = type;
		}

		public Builder type(Class<?> type) {
			parameterMap.type = type;
			return this;
		}

		public Builder id(String id) {
			parameterMap.id = id;
			return this;
		}
		public Builder onlyParameter() {
			parameterMap.onlyParameter = true;
			return this;
		}

		public Builder resultParameter() {
			parameterMap.resultParameter = true;
			return this;
		}

		public ParameterMap build(Configuration configuration) {
			Assert.notNull(parameterMap.id , "'id'不能为空");
			Assert.notNull(parameterMap.type , "'type'不能为空");
			if (!autoMapped && parameterMap.onlyParameter) {
				// 更新参数来源类型
				parameterMap.type = parameterMap.parameterMappings.get(0).getJavaType();
				
				if (!isCollection()) {
					// 尝试解析Bean映射关系
					builderParameterMapping(configuration);
				}
			}
			return parameterMap;
		}

		/**
		 * 尝试解析单一参数之中可能存在的属性映射
		 * @param configuration 
		 */
		private void builderParameterMapping(Configuration configuration) {
			ParameterMapping parameterMapping = parameterMap.parameterMappings.get(0);
			if (parameterMap.getType().getAnnotation(BeanMapping.class) == null)  {
				return ;
			}

			List<ParameterMapping> list = new ArrayList<>();
			ParameterMode mode = parameterMapping.getMode();
			Class<?> type = parameterMapping.getJavaType();

			if (configuration.hasParameterMappings(type)) { // 尝试从缓存中获得 ResultMappings
				list = configuration.getParameterMappings(type);
			} else {
				Field[] fields = type.getDeclaredFields();
				for (Field field : fields) {
					// 忽略静态属性和标注@Transient的属性
					if (null != field.getAnnotation(Transient.class) || Modifier.isStatic(field.getModifiers())) {
						continue;
					}
					String fieldName = field.getName();
					Class<?> fieldType = field.getType();
					Column column = field.getAnnotation(Column.class);
					ParameterMapping mapping = new ParameterMapping(fieldName, fieldType, mode, null);
					if (null != column && !column.name().isEmpty()) {
						mapping.setColumnName(column.name());
						mapping.setLevel(column.level());
					}
					list.add(mapping);
				}
				/**
				 *  将参数类型更新为解析参数的类型
				 */
				parameterMap.type = type;
				configuration.addParameterMappings(type, list);
			}
			parameterMap.parameterMappings = list;
		}

		/**
		 * 判断唯一的参数是否是Collection或Map
		 * @return
		 */
		private boolean isCollection() {
			Class<?> paramterType = parameterMap.parameterMappings.get(0).getJavaType();
			return Map.class.isAssignableFrom(paramterType) || Collection.class.isAssignableFrom(paramterType);
		}
	}

	public String getId() {
		return id;
	}
	public Class<?> getType() {
		return type;
	}
	public boolean isResultParameter() {
		return resultParameter;
	}
	public List<ParameterMapping> getParameterMappings() {
		return parameterMappings;
	}
	public static ParameterMap getEmpytParameterMap() {
		return EMPYT_PARAMETER_MAP;
	}
	public static boolean isAutoMapped() {
		return autoMapped;
	}
	public boolean isOnlyParameter() {
		return onlyParameter;
	}
}
