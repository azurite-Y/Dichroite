package org.zy.dichroite.fluorite.mapping;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zy.dichroite.fluorite.annotation.BeanMapping;
import org.zy.dichroite.fluorite.annotation.Column;
import org.zy.dichroite.fluorite.annotation.MapKey;
import org.zy.dichroite.fluorite.session.Configuration;
import org.zy.fluorite.core.convert.ResolvableType;
import org.zy.fluorite.core.utils.Assert;
import org.zy.fluorite.core.utils.TypeUtils;

/**
 * @DateTime 2021年10月5日;
 * @author zy(azurite-Y);
 * @Description
 */
public class ResultMap {
	/**
	 * 代表无返回值
	 */
	public static final ResultMap EMPTY_RESULT_MAP = new ResultMap();
	private String id;
	private ResolvableType resultType;
	private List<ResultMapping> resultMappings;
	private MapKey mapKey;
	private boolean isManyMapResult;
	/**
	 * 结果集单行数据封装对象类型，以下为对应关系 </br>
	 * Integer、int					  - Integer </br>
	 * Object						  - Object </br>
	 * List<?>						  - Object </br>
	 * Map<String,String>			  - Map </br>
	 * Map<String,Teacher>			  - Teacher </br>
	 * Map<String,Map<String,Object>> - Map </br>
	 */
	private Class<?> resultRowObjectType;
	/**
	 * 结果集处理是所需创建的 对象类型，以下为对应关系 </br>
	 * Integer、int					  - Integer </br>
	 * Object						  - Object </br>
	 * List<?>						  - Object </br>
	 * Map<String,String>			  - String </br>
	 * Map<String,Teacher>			  - Object </br>
	 * Map<String,Map<String,Object>> - Map </br>
	 */
	private Class<?> resultObjectType;
	/**
	 * 已映射的字段集
	 */
	private Set<String> mappedColumns;

	/**
	 * 是否已自动映射(在ParameterMap中已解析则为true)
	 */
	private Boolean autoMapperd;
	
	private List<Object> ResultObject;
	
	private ResultMap() {
		resultMappings = new ArrayList<>();
		mappedColumns = new HashSet<>();
	}

	public static class Builder {
		private ResultMap resultMap = new ResultMap();

		/**
		 * 创建空集的ResultMap.Builder,默认为未解析
		 * @param id
		 * @param returnType
		 */
		public Builder(String id, ResolvableType returnType) {
			this(id, returnType, null, false);
		}
		public Builder(String id, ResolvableType returnType, List<ResultMapping> resultMappings) {
			this(id, returnType, resultMappings, true);
		}
		public Builder(String id, ResolvableType returnType, List<ResultMapping> resultMappings, Boolean autoMapperd) {
			resultMap.id = id;
			resultMap.resultMappings = resultMappings;
			resultMap.autoMapperd = autoMapperd;
			resultMap.resultType = returnType;
		}

		public Builder id(String id) {
			resultMap.id = id;
			return this;
		}
		
		public Builder mapKey(MapKey mapKey) {
			resultMap.mapKey = mapKey;
			return this;
		}

		public ResultMap build(Configuration configuration) {
			Assert.notNull(resultMap.id, "ResultMap对象必须指定'id");
			Assert.notNull(resultMap.resultType , "ResultMap对象必须指定'resultType'");

			Class<?> resultObjectTargetType = resultMap.resultType.resolve();
			resultMap.resultObjectType = resultObjectTargetType;

			if (TypeUtils.isDefaultType(resultObjectTargetType)) {
				resultMap.resultRowObjectType = resultObjectTargetType;
			} else if (Map.class.isAssignableFrom(resultObjectTargetType)) {
				if (resultMap.mapKey != null) {
					resultMap.isManyMapResult = true;
				}
				resultMap.resultRowObjectType = resultMap.resultType.getGenerics()[1].resolve();
			} else if (Collection.class.isAssignableFrom(resultObjectTargetType)) {
				resultMap.resultRowObjectType = resultMap.resultType.getGenerics()[0].resolve();
			} else {
				resultMap.resultRowObjectType = resultMap.resultObjectType;
			}
			if (!resultMap.autoMapperd && null != resultMap.resultRowObjectType.getAnnotation(BeanMapping.class)) {
				builderResultMapping(configuration);
			}
			return resultMap;
		}

		/**
		 * 尝试解析单一参数之中可能存在的属性映射
		 */
		private void builderResultMapping(Configuration configuration) {
			List<ResultMapping> list = new ArrayList<>();
			if (configuration.hasResultMappings(resultMap.resultRowObjectType)) { // 尝试从缓存中获得 ResultMappings
				list = configuration.getResultMappings(resultMap.resultRowObjectType);
			} else {
				Field[] fields = resultMap.resultRowObjectType.getDeclaredFields();
				for (Field field : fields) {
					String fieldName = field.getName();
					Class<?> fieldType = field.getType();
					Column column = field.getAnnotation(Column.class);
					ResultMapping resultMapping = new ResultMapping(fieldName, fieldType, null);
					if (null != column) {
						resultMapping.setColumnLabel(column.name());
					}
					list.add(resultMapping);
				}
			}
			configuration.addResultMappings(resultMap.resultRowObjectType, list);
			resultMap.resultMappings = list;
		}
	}

	public String getId() {
		return id;
	}
	public ResolvableType getResultType() {
		return resultType;
	}
	public List<ResultMapping> getResultMappings() {
		return resultMappings;
	}
	public MapKey getMapKey() {
		return mapKey;
	}
	public Class<?> getResultRowObjectType() {
		return resultRowObjectType;
	}
	public Class<?> getResultObjectType() {
		return resultObjectType;
	}
	public Set<String> getMappedColumns() {
		return mappedColumns;
	}
	public Boolean getAutoMapperd() {
		return autoMapperd;
	}
	public List<Object> getResultObject() {
		return ResultObject;
	}
	public void setResultObject(List<Object> resultObject) {
		ResultObject = resultObject;
	}
	public boolean isManyMapResult() {
		return isManyMapResult;
	}
}
