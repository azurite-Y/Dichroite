package org.zy.dichroite.fluorite.binding;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zy.dichroite.fluorite.annotation.QueryTemplate;
import org.zy.dichroite.fluorite.interfaces.SqlSource;
import org.zy.dichroite.fluorite.interfaces.TypeHandler;
import org.zy.dichroite.fluorite.mapping.ParameterMap;
import org.zy.dichroite.fluorite.mapping.ParameterMapping;
import org.zy.dichroite.fluorite.mapping.SqlCommandType;
import org.zy.dichroite.fluorite.session.Configuration;
import org.zy.dichroite.fluorite.session.MapperAnnotationParser.DefaultMapperAnnotation;
import org.zy.dichroite.fluorite.type.TypeHandlerRegistry;
import org.zy.fluorite.core.utils.Assert;
import org.zy.fluorite.core.utils.CollectionUtils;
import org.zy.fluorite.core.utils.ObjectUtils;
import org.zy.fluorite.core.utils.ReflectionUtils;

/**
 * @DateTime 2021年9月29日;
 * @author zy(azurite-Y);
 * @Description 动态sql封装类.方法有动态sql注解及有唯一一个参数时才会启用动态sql的判断，也就是说动态sql的入参只能是包含字段映射的pojo对象
 */
public class DynamicSqlSource implements SqlSource {
//	private static final Logger logger = LoggerFactory.getLogger(DynamicSqlSource.class);

	private Configuration configuration;
	private DefaultMapperAnnotation mapperAnnotation;
	private ParameterMap parameterMap;

	public DynamicSqlSource(Configuration configuration, ParameterMap parameterMap, DefaultMapperAnnotation defaultMapperAnnotation) {
		this.configuration = configuration;
		this.mapperAnnotation = defaultMapperAnnotation;
		this.parameterMap = parameterMap;
	}

	@Override
	public BoundSql getBoundSql(Map<Integer,Object> args) {
		AbstractBoundSqlBuilder builder = null;
		SqlCommandType sqlCommandType = mapperAnnotation.getSqlCommandType();
		if (sqlCommandType.equals(SqlCommandType.INSERT)) {
			builder = new InsertValueBoundSqlBuilder(configuration, mapperAnnotation, parameterMap);
		} else if (sqlCommandType.equals(SqlCommandType.UPDATE)) {
			builder = new UpdateSetBoundSqlBuilder(configuration, mapperAnnotation, parameterMap);
		} else {
			builder = new QueryWhereBoundSqlBuilder(configuration, mapperAnnotation, parameterMap);
		}
		return builder.builderBoundSql(args);
	}

	public static abstract class AbstractBoundSqlBuilder {
		protected DefaultMapperAnnotation mapperAnnotation;
		protected ParameterMap parameterMap;
		protected BoundSql boundSql;
		protected Configuration configuration;
		/**
		 * 解析参数之后的属性值集合，顺序代表参数值的装配顺序
		 */
		protected List<Object> parameterValueMappins;
		protected List<Object> additionalParameterValueMappins;
		/**
		 * 装配参数值所需的 TypeHandler集合，顺序代表使用顺序
		 */
		protected List<TypeHandler<?>> typeHandlerList;
		protected List<TypeHandler<?>> additionalTypeHandlerList;
		/**
		 * 是否继续解析流程的控制符，由子类按需控制
		 */
		protected boolean processControl = true;

		protected TypeHandlerRegistry typeHandlerRegistry;		
		
		public AbstractBoundSqlBuilder(Configuration configuration, DefaultMapperAnnotation mapperAnnotation, ParameterMap parameterMap) {
			super();
			this.mapperAnnotation = mapperAnnotation;
			this.parameterMap = parameterMap;
			this.configuration = configuration;
			this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			this.typeHandlerList = new ArrayList<>();
			this.parameterValueMappins = new ArrayList<>();
			this.additionalParameterValueMappins = new ArrayList<>();
			this.additionalTypeHandlerList = new ArrayList<>();
		}

		@SuppressWarnings("all")
		protected void paraseParameterObject(Map<Integer,Object> args) {
			List<ParameterMapping> parameterMappings = sortParameterMappings(parameterMap.getParameterMappings());
			String sql = mapperAnnotation.getSql();
			Class<?> parameterType = parameterMap.getType();
			int count = parameterMappings.size() - 1;
			Object parameterObject = args.get(0);

			try {
				String usePropretyName = "";
				for (int step = 0; step <= count && processControl; step++) {
					ParameterMapping parameterMapping = parameterMappings.get(step);
					Class<?> javaType = parameterMapping.getJavaType();
					// 调用对应的getter方法获得参数值
					usePropretyName = parameterMapping.getProperty();
//					logger.info("usePropretyName：" + usePropretyName);

					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(usePropretyName, parameterType);
					Method readMethod = propertyDescriptor.getReadMethod();
					readMethod.setAccessible(true);
					Object obj = ReflectionUtils.invokeMethod(parameterObject, readMethod, null);
//					logger.info("usePropretyValue：" + obj);

					if(ObjectUtils.isDefaultValue(obj)) { // 排除未设值的初始属性
						// 尝试赋予初始值
						giveInitialValue(step, count, usePropretyName);
						continue;
					}

					String columnName = parameterMapping.getColumnName();
					if (columnName != null) {
						usePropretyName = columnName;
					}
					paraseParameterObjectStep(step, count, usePropretyName, obj);
				}
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 构建 BoundSql对象
		 * @param parameterObject - 调用方法的入参对象
		 * @return
		 */
		public BoundSql builderBoundSql(Map<Integer,Object> args) {
			paraseParameterObject(args);
			boundSql = new BoundSql(configuration, createSql(), parameterMap.getParameterMappings(), args);
			boundSql.setParameterValueMappins(parameterValueMappins);
			boundSql.setTypeHandlerList(typeHandlerList);
			return boundSql;
		}
		
		/**
		 * 
		 * @param builder
		 * @param parameObject
		 */
		protected boolean appendListSQL(StringBuilder builder, Object parameObject) {
			Collection<?> coll = null;
			if (parameObject.getClass().isArray()) {
				parameObject = CollectionUtils.asList(parameObject);
			}
			
			if (Collection.class.isAssignableFrom(parameObject.getClass())) {
				int len = 0;
				coll = (Collection<?>)parameObject;
				len = coll.size();
				builder.append(" in ");
				builder.append("(");
				Iterator<?> iterator = coll.iterator();
				Class<?> clz = null;
				TypeHandler<?> handler = null;
				for (int i = 0; i < len; i++) {
					builder.append("?");
					if (i < len - 1) {
						builder.append(",");
					}
					Object next = iterator.next();
					if (clz == null) {
						clz = next.getClass();
						handler = typeHandlerRegistry.getTypeHandler(clz);
					}
					additionalParameterValueMappins.add(next);
					additionalTypeHandlerList.add(handler);
				}
				builder.append(")");
				return true;
			}
			builder.append("=").append("?");
			return false;
		}

		/**
		 * 判断当前对象是否是数组或Collection类型，若是则追加对应的sql并配置属性集
		 * @param object - 根据子实现对应方法传入的对象而定，可能是mpaaer方法入菜对象亦或者是
		 * @param parameClz - mpaaer方法入菜对象类型
		 * @param builder
		 * @return
		 */
		protected boolean appendArrayCollectionSql(Object object,Class<?> parameClz,StringBuilder builder) {
			if (parameClz.isArray()) { // 若Map中有Array类型对象
				int len = Array.getLength(object) - 1;
				Class<?> arrObjClaz = null;
				TypeHandler<Object> handler = null;
				for (int i = 0; i <= len; i++) {
					Object arrObj = Array.get(object, i);
					if (arrObjClaz == null) {
						arrObjClaz = arrObj.getClass();
						handler = typeHandlerRegistry.getTypeHandler(arrObjClaz);
					}
					this.parameterValueMappins.add(arrObj);
					this.typeHandlerList.add(handler);
					builder.append("?");
					if (i < len) {
						builder.append(",");
					}
				}
				return true;
			} else if (Collection.class.isAssignableFrom(parameClz)) { // 若Map中有Collection类型对象
				Collection<?> coll = (Collection<?>)object;
				int len = coll.size() - 1;
				int i = 0;
				Class<?> arrObjClaz = null;
				TypeHandler<Object> handler = null;
				for (Iterator<?> iterator = coll.iterator(); iterator.hasNext();) {
					Object collObj = (Object) iterator.next();
					if (arrObjClaz == null) {
						arrObjClaz = collObj.getClass();
						handler = typeHandlerRegistry.getTypeHandler(arrObjClaz);
					}
					this.parameterValueMappins.add(collObj);
					this.typeHandlerList.add(handler);
					builder.append("?");
					if (i++ < len) {
						builder.append(",");
					}
				}
				return true;
			}
			return false;
		}
		
		/**
		 * 构建预编译的SQL
		 * @return
		 */
		protected abstract String createSql();

		/**
		 * 解析每一个 {@code ParameterMapping } 必备调用的方法
		 * @param count 总计数，从零开始计数
		 * @param step 当前单步计数
		 * @param usePropretyName - 当前使用的属性名或映射的字段名
		 * @param obj 当前解析的  {@code ParameterMapping } 对象对应属性值
		 */
		protected abstract void paraseParameterObjectStep(int step, int count, String usePropretyName, Object obj);

		/**
		 * 对 {@code ParameterMapping } 集合排序，主要应用于动态Select或Update语句
		 * @param list
		 * @return
		 */
		protected List<ParameterMapping> sortParameterMappings(List<ParameterMapping> list) {
			return list;
		}

		/**
		 * 尝试赋予初始值
		 * @param step
		 * @param count
		 * @param usePropretyName
		 */
		protected void giveInitialValue(int step, int count, String usePropretyName) {}
	}

	/**
	 * insert 语句
	 * @author Azurite-Y
	 * insert into student(id,name,sex) values(#{id},#{name},#{sex})
	 */
	public static class InsertValueBoundSqlBuilder extends AbstractBoundSqlBuilder {
		// 不管是否全字段插入与否都和设置插入字段集，sql的有效性由数据库验证
		private StringBuilder insertNames = new StringBuilder(); 
		private StringBuilder insertValues = new StringBuilder();
		/**
		 * 是否追加“,”
		 */
		private boolean appendSeparator;
		
		public InsertValueBoundSqlBuilder(Configuration configuration, DefaultMapperAnnotation mapperAnnotation, ParameterMap parameterMap) {
			super(configuration, mapperAnnotation, parameterMap);
		}

		@Override
		public void paraseParameterObjectStep(int step, int count, String usePropretyName, Object obj) {
			parameterValueMappins.add(obj);
			typeHandlerList.add(typeHandlerRegistry.getTypeHandler(obj.getClass()));
			
			if (appendSeparator) {
				insertNames.append(",");
				insertValues.append(",");
			} else {
				appendSeparator = true;
			}
			
			insertNames.append(usePropretyName);
			insertValues.append("?"); // 预占符
		}

		@Override
		public String createSql() {
			String sql = mapperAnnotation.getSql();
			sql = sql.replace(mapperAnnotation.getInsertPropretyNameLocationIndex().toString(), insertNames.toString());
			return sql.replace(mapperAnnotation.getInputParameterLocationIndex(), insertValues.toString());
		}
	}

	/**
	 * update 语句
	 * @author Azurite-Y
	 * update student set name = #{name} , sex = #{sex} where id = ${id} => update student set name = ? , sex = ? where id = ?
	 */
	public static class UpdateSetBoundSqlBuilder extends AbstractBoundSqlBuilder {
		private StringBuilder updateSets = new StringBuilder();
		private StringBuilder queryValues = new StringBuilder();
		/**
		 * 入参对象中默认值属性的个数
		 */
		private int defaultCount;
		/**
		 * 是否追加“,”
		 */
		private boolean appendSeparator;
		
		public UpdateSetBoundSqlBuilder(Configuration configuration, DefaultMapperAnnotation mapperAnnotation, ParameterMap parameterMap) {
			super(configuration, mapperAnnotation, parameterMap);
		}

		@Override
		public String createSql() {
			// 在转换器集合末尾追加参数值和对应的类型转换器
			this.typeHandlerList.addAll(this.additionalTypeHandlerList);
			this.parameterValueMappins.addAll(this.additionalParameterValueMappins);
			
			String sql = mapperAnnotation.getSql();
			sql = sql.replace(mapperAnnotation.getInputParameterLocationIndex().toString(), updateSets.toString());
			return sql.replace(mapperAnnotation.getQueryWhereName(), queryValues);
		}

		@Override
		public void paraseParameterObjectStep(int step, int count, String usePropretyName, Object obj) {
			// 先确定查询字段，为空则轮空继续判断下一个属性是否可用，若所有属性均不可用则报错
			if (queryValues.toString().isEmpty()) {
				 queryValues.append(usePropretyName).toString();
				 if (!super.appendListSQL(queryValues, obj)) {
					 this.additionalParameterValueMappins.add(obj);
					 this.additionalTypeHandlerList.add(typeHandlerRegistry.getTypeHandler(obj.getClass()));
				 }
			} else {
				// update 语句是进行单值更新，所以此处不展开集合容器对象
				parameterValueMappins.add(obj);
				typeHandlerList.add(typeHandlerRegistry.getTypeHandler(obj.getClass()));
				
				if (appendSeparator) { //
					updateSets.append(",");
				} else {
					appendSeparator = true;
				}
				
				updateSets.append(usePropretyName).append("=").append("?");
			}
		}	

		@Override
		protected List<ParameterMapping> sortParameterMappings(List<ParameterMapping> list) {
			list.sort(new Comparator<ParameterMapping>() {
				@Override
				public int compare(ParameterMapping pm1, ParameterMapping pm2) {
					// 降序排列
					return pm1.getLevel().compareTo(pm2.getLevel());
				}
			});
			// 排序之后末位元素为查询参数，其余的为被更新的参数
			return list;
		}

		@Override
		protected void giveInitialValue(int step, int count, String usePropretyName) {
			Assert.isTrue(defaultCount++ < count , "入参属性均为默认值，by " + parameterMap.getId() + "，若对数据没有查询或操作限制，请使用静态SQL");
		}
	}

	/**
	 * where 语句，其sql可能是select或delete语句
	 * @author Azurite-Y
	 * select * from student where id = #{id} =》 select * from student where id = ?
	 */
	public static class QueryWhereBoundSqlBuilder extends AbstractBoundSqlBuilder {
		private StringBuilder queryValues = new StringBuilder();
		private QueryTemplate queryTemplate;
		/**
		 * 入参对象中默认值属性的个数
		 */
		private int defaultCount;
		/**
		 * 是否开始追加 “and” 关键字
		 */
		private boolean appendEnd;
		
		public QueryWhereBoundSqlBuilder(Configuration configuration, DefaultMapperAnnotation mapperAnnotation,ParameterMap parameterMap) {
			super(configuration, mapperAnnotation, parameterMap);
			queryTemplate = mapperAnnotation.getMode();
		}

		@Override
		protected List<ParameterMapping> sortParameterMappings(List<ParameterMapping> list) {
			list.sort(new Comparator<ParameterMapping>() {
				@Override
				public int compare(ParameterMapping pm1, ParameterMapping pm2) {
					// 降序排列
					return pm1.getLevel().compareTo(pm2.getLevel());
				}
			});
			return list;
		}

		@Override
		protected void giveInitialValue(int step, int count, String usePropretyName) {
			// 在匹配到最后一个都还是默认值的话就启用默认查询
			if(defaultCount++ == count && queryValues.toString().isEmpty()) {
				String defaultQuerySql = mapperAnnotation.getDefaultQuerySql();
				Assert.isTrue(!defaultQuerySql.isEmpty(), "入参对象无可用属性，现使用默认查询SQL，但默认的查询sql不能为空串，by " + parameterMap.getId());
				queryValues.append(defaultQuerySql);
			}
		}

		@Override
		public void paraseParameterObjectStep(int step, int count, String usePropretyName, Object obj) {
//			parameterValueMappins.add(obj);
			if (appendEnd) {
				queryValues.append(" and");
			} else { // 在第一次确认追加之后后续均追加“and”关键字
				appendEnd = true;
			}
			queryValues.append(" ");
			queryValues.append(usePropretyName);
			if (!super.appendListSQL(queryValues, obj)) {
				this.parameterValueMappins.add(obj);
				this.typeHandlerList.add(this.typeHandlerRegistry.getTypeHandler(obj.getClass()));
			} else {
				this.parameterValueMappins.addAll(this.additionalParameterValueMappins);
				this.typeHandlerList.addAll(this.additionalTypeHandlerList);
				this.additionalParameterValueMappins.clear();
				this.additionalTypeHandlerList.clear();
			}
			
			
			if (queryTemplate.equals(QueryTemplate.serial)) {
				processControl = false; // 终止解析流程
				return;
			} 
		}

		@Override
		public String createSql() {
			return mapperAnnotation.getSql()
					.replace(mapperAnnotation.getQueryWhereName(), queryValues.toString());
		}
	}
}
