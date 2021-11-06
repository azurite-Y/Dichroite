package org.zy.dichroite.fluorite.binding;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zy.dichroite.fluorite.interfaces.SqlSource;
import org.zy.dichroite.fluorite.interfaces.TypeHandler;
import org.zy.dichroite.fluorite.mapping.ParameterMap;
import org.zy.dichroite.fluorite.mapping.ParameterMapping;
import org.zy.dichroite.fluorite.session.Configuration;
import org.zy.dichroite.fluorite.session.MapperAnnotationParser.DefaultMapperAnnotation;
import org.zy.dichroite.fluorite.type.TypeHandlerRegistry;
import org.zy.fluorite.core.utils.Assert;
import org.zy.fluorite.core.utils.ReflectionUtils;
import org.zy.fluorite.core.utils.TypeUtils;

/**
 * @DateTime 2021年9月29日;
 * @author zy(azurite-Y);
 * @Description 默认sql封装类
 */
public class DefaultSqlSource implements SqlSource {
	private static final Logger logger = LoggerFactory.getLogger(DefaultSqlSource.class);

	private Configuration configuration;
	private DefaultMapperAnnotation mapperAnnotation;
	private ParameterMap parameterMap;
	private AbstractParameterBoundSqlBuilder builder;

	public DefaultSqlSource(Configuration configuration, ParameterMap parameterMap, DefaultMapperAnnotation mapperAnnotation) {
		this.configuration = configuration;
		this.mapperAnnotation = mapperAnnotation;
		this.parameterMap = parameterMap;
		builder = createParameterBoundSqlBuilder();
		builder.paraseStaticSql();
	}

	private AbstractParameterBoundSqlBuilder createParameterBoundSqlBuilder() {
		Class<?> paramterType = parameterMap.getType();
		AbstractParameterBoundSqlBuilder builder = null;
		/*
		 * 多参数则其 ParameterMap#type 为Mapper接口类型，而单参数则是参数本身的类型
		 */
		if (parameterMap.isOnlyParameter()) {
			if (TypeUtils.isDefaultType(paramterType)) {
				builder = new DefaultTypeParameterBoundSqlBuilder(configuration, mapperAnnotation, parameterMap);
			} else if (Map.class.isAssignableFrom(paramterType)) {
				builder = new MapParameterBoundSqlBuilder(configuration, mapperAnnotation, parameterMap);
			} else if (paramterType.isArray() || Collection.class.isAssignableFrom(paramterType)) {
				builder = new ArrayOrCollectionParameterBoundSqlBuilder(configuration, mapperAnnotation, parameterMap);
			} else {
				builder = new BeanMappingParameterBoundSqlBuilder(configuration, mapperAnnotation, parameterMap);
			}
		} else { // 多个参数根据参数索引下标关联参数值，在此时将索引下标保存到之前初始化为null的ParameterMapping#property之中
			builder = new MultivaluedParameterBoundSqlBuilder(configuration, mapperAnnotation, parameterMap);
		}
		return builder;
	}
	
	@Override
	public BoundSql getBoundSql(Map<Integer,Object> args) {
		return builder.builderBoundSql(args);
	}

	public static abstract class AbstractParameterBoundSqlBuilder {
		protected final String STARE_STR = "#{";
		protected final String END_STR = "}";
		
		protected DefaultMapperAnnotation mapperAnnotation;
		protected ParameterMap parameterMap;
		protected Configuration configuration;
		/**
		 *  解析参数之后的属性值集合，顺序代表参数值的装配顺序
		 */
		protected List<Object> parameterValueMappins = new ArrayList<>();
		/**
		 * 原始SQL中需要替换的片段集合
		 */
		protected List<String> replaceParts = new ArrayList<>();
		/**
		 * 已排序的 ParameterMapping 映射集
		 */
		protected List<ParameterMapping> sortedlist = new ArrayList<>();
		/**
		 * 装配参数值所需的 TypeHandler集合，顺序代表使用顺序
		 */
		protected List<TypeHandler<?>> typeHandlerList;
		
		protected String sql;
		
		protected TypeHandlerRegistry typeHandlerRegistry;
		
		
		public AbstractParameterBoundSqlBuilder(Configuration configuration, DefaultMapperAnnotation mapperAnnotation, ParameterMap parameterMap) {
			super();
			this.mapperAnnotation = mapperAnnotation;
			this.parameterMap = parameterMap;
			this.configuration = configuration;
			this.sql = mapperAnnotation.getSql();
			this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			this.typeHandlerList = new ArrayList<>();
		}
		
		/**
		 * 解析SQL中可能存在的#{}
		 */
		public void paraseStaticSql() {
			String sql = mapperAnnotation.getSql();
			char[] sqlCharArray = sql.toCharArray();

			StringBuilder builder = new StringBuilder();
			StringBuilder expression = new StringBuilder();
			StringBuilder parameter = new StringBuilder();
			List<String> list = new ArrayList<>();

			// 尝试将sql自set分割
			int indexOf = sql.indexOf("set");
			if (indexOf == -1) {
				// 尝试将sql自where分割
				indexOf = sql.indexOf("where");
				indexOf+=5;
			} else {
				indexOf+=3;
			}

			builder.append(sqlCharArray, 0, indexOf);
			expression.append(sqlCharArray, indexOf, sql.length() - builder.length());

			char[] expressionArray = expression.toString().toCharArray();
			int start = 0;
			int end = 0;
			int len = 0;
			start = expression.indexOf(STARE_STR);
			end = expression.indexOf(END_STR);
			while(start > -1) { // start > -1：控制是否已检索sql中全部的"#{"，为-1则检索完成
				// 条件成立则代表sql中存在'#{'而没有'}' 
				Assert.isTrue(end != -1, "未找到结束的标识符'}',by sql：" + sql);

				parameter.append(expressionArray, start + 2, end - start - 2);

				list.add(parameter.toString());
				parameter.insert(0, STARE_STR).append(END_STR); // 在参数前后加上#{、} ==> #{parameter}
				replaceParts.add(parameter.toString());

				len = expression.length();
				expression.delete(0, len);
				parameter.delete(0, parameter.length());
				expression.append(expressionArray, end + 1 , len - end - 1);
				if (len - 1 > end) {
					expressionArray = expression.toString().toCharArray();
				}
				start = expression.indexOf(STARE_STR);
				end = expression.indexOf(END_STR);
			}
			sortParameterMappings(list);
//			builderBoundSql(sql,list);
		}

		/**
		 * 通过传入的list集合对 {@code ParameterMap#getParameterMappings() }中的数据进行排序。<br>
		 * 多参数使用参数索引下标关联参数值，而只有一个参数的话那么就这个参数的类型按情况解析构建 {@code ParameterMapping } 集合
		 * @param list
		 * @return 排序后的 {@code ParameterMap#getParameterMappings() } 集合
		 */
		protected void sortParameterMappings(List<String> list) {}
		
		/**
		 * 根据入参内容侯建BoundSql对象
		 * @param args
		 * @return 
		 */
		protected BoundSql builderBoundSql(Map<Integer,Object> args) {
			pretreatmentBuilderBoundSql(args);
			BoundSql boundSql = new BoundSql(configuration, sql, sortedlist, args);
			boundSql.setParameterValueMappins(parameterValueMappins);
			boundSql.setTypeHandlerList(typeHandlerList);
			return boundSql;
		}
		
		/**
		 * 构建BoundSql 之前的预处理方法</br>
		 * 执行的大致逻辑：
		 * 1.预编译需执行SQL
		 * 2.配置SQL注入属性集
		 * @param args
		 */
		protected abstract void pretreatmentBuilderBoundSql (Map<Integer,Object> args);
		
		/**
		 * 单步替换sql方法，子类可在其实现中自定义替换逻辑
		 * @param step - 当前单步计数
		 * @param count - 总计数，从零开始计数
		 * @param object - 当前  {@code ParameterMapping } 对象对应属性值
		 */
//		protected abstract void replaceSqlStep(int step, int count, Object object);
		
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
		
	}

	
	/**
	 * 唯一Map类型入参
	 * @author Azurite-Y
	 *
	 */
	public static class MapParameterBoundSqlBuilder extends AbstractParameterBoundSqlBuilder {

		public MapParameterBoundSqlBuilder(Configuration configuration, DefaultMapperAnnotation mapperAnnotation,
				ParameterMap parameterMap) {
			super(configuration, mapperAnnotation, parameterMap);
		}

		@Override
		protected void sortParameterMappings(List<String> list) {
			for (String name : list) { // 使用sql#{}内的内容作为其key从Map对象中获取参数
				if (name.isEmpty()) {
					throw new IllegalArgumentException("#{mapKey}使用于单个Map类型参数,其mapKey不能为空串，by：" + parameterMap.getId());
				}
				sortedlist.add(new ParameterMapping(name, parameterMap.getType(), null));
			}
		}

		@Override
		protected void pretreatmentBuilderBoundSql(Map<Integer, Object> args) {
			// 使用#{}内的内容作为其key从Map对象中获取参数
			Map<?,?> mapParameter = (Map<?,?>)args.get(0);
			StringBuilder builder = new StringBuilder();
			Class<?> parameClz = mapParameter.getClass();

			int count = sortedlist.size() - 1;
			ParameterMapping pm;
			Class<?> arrObjClaz = null;
			TypeHandler<Object> handler = null;
			for (int step = 0; step <= count; step++) {
				pm = sortedlist.get(step);
				Object object = mapParameter.get(pm.getProperty());
				if (!appendArrayCollectionSql(object,parameClz,builder)) {
					builder.append("?");
					
					if (arrObjClaz == null) {
						arrObjClaz = object.getClass();
						handler = typeHandlerRegistry.getTypeHandler(arrObjClaz);
					}
					
					this.parameterValueMappins.add(object);
					this.typeHandlerList.add(handler);
					
					sql = sql.replace(replaceParts.get(step), builder.toString());
				}
			}
		}
	}
	
	/**
	 * 唯一数组或集合类入参
	 * @author Azurite-Y
	 *
	 */
	public static class ArrayOrCollectionParameterBoundSqlBuilder extends AbstractParameterBoundSqlBuilder {

		public ArrayOrCollectionParameterBoundSqlBuilder(Configuration configuration, DefaultMapperAnnotation mapperAnnotation,
				ParameterMap parameterMap) {
			super(configuration, mapperAnnotation, parameterMap);
		}

		@Override
		protected void pretreatmentBuilderBoundSql(Map<Integer, Object> args) {
			/*
			 * 对于数组和Collection类型需要根据入参对象中元素个数编译SQL,而入参对象是数组类型的会被替换为一个List<Object>对象
			 */
			Object parameterObject = args.get(0);
			Class<?> parameClz = parameterObject.getClass();
			StringBuilder builder = new StringBuilder();

			appendArrayCollectionSql(parameterObject, parameClz, builder);
			
			sql = sql.replace(super.replaceParts.get(0), builder);
		}
	}
	
	/**
	 * 唯一被@BeanMapping 注解标注类入参
	 * @author Azurite-Y
	 *
	 */
	public static class BeanMappingParameterBoundSqlBuilder extends AbstractParameterBoundSqlBuilder {

		public BeanMappingParameterBoundSqlBuilder(Configuration configuration, DefaultMapperAnnotation mapperAnnotation,
				ParameterMap parameterMap) {
			super(configuration, mapperAnnotation, parameterMap);
		}

		@Override
		@SuppressWarnings("all")
		protected void sortParameterMappings(List<String> list) {
			List<ParameterMapping> parameterMappings = parameterMap.getParameterMappings();
			int len = parameterMappings.size();
			boolean used = false;
			for (String name : list) {
				used = false;
				for (int i = 0; i < len && !used && sortedlist.size() < len; i++) {
					ParameterMapping mapping = parameterMappings.get(i);
					if (((mapping.getColumnName() != null && name.equals(mapping.getColumnName())) || name.equals(mapping.getProperty()))) {
						sortedlist.add(mapping);
						used = true;
					} else if (i == len - 1) { // 遍历完属性集之后还是没有找到目标属性
						logger.error("未找到对应的属性定义，by property：" + name + " ,SQL：" + mapperAnnotation.getSql() + ", "+ parameterMap.getId());
					}
				}
			}
		}

		@Override
		@SuppressWarnings("all")
		protected void pretreatmentBuilderBoundSql(Map<Integer, Object> args) {
			// 通过属性编辑器获取sql注入值
			Object parameterObject = args.get(0);
			String property = null;
			StringBuilder builder = new StringBuilder();
			
			int len = sortedlist.size();
			ParameterMapping pm = null;
			Class<?> arrObjClaz = null;
			TypeHandler<Object> handler = null;
			for (int i = 0; i < len; i++,builder.delete(0, builder.length())) { // 每次迭代都清空builder内容
				pm = sortedlist.get(i);
				property = pm.getProperty();
				PropertyDescriptor propertyDescriptor;
				try {
					propertyDescriptor = new PropertyDescriptor(property, parameterObject.getClass());
					Method readMethod = propertyDescriptor.getReadMethod();
					Object obj = ReflectionUtils.invokeMethod(parameterObject, readMethod, null);
					if (!appendArrayCollectionSql(obj,pm.getJavaType(),builder)) {
						builder.append("?");
						
						if (arrObjClaz == null) {
							arrObjClaz = obj.getClass();
							handler = typeHandlerRegistry.getTypeHandler(arrObjClaz);
						}
						
						this.parameterValueMappins.add(obj);
						this.typeHandlerList.add(handler);
						
						sql = sql.replace(replaceParts.get(i), builder.toString());
					}
				} catch (IntrospectionException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 唯一默认类型入参
	 * @author Azurite-Y
	 *
	 */
	public static class DefaultTypeParameterBoundSqlBuilder  extends AbstractParameterBoundSqlBuilder  {

		public DefaultTypeParameterBoundSqlBuilder(Configuration configuration, DefaultMapperAnnotation mapperAnnotation,
				ParameterMap parameterMap) {
			super(configuration, mapperAnnotation, parameterMap);
		}

		@Override
		protected void sortParameterMappings(List<String> list) {
			sortedlist.add(new ParameterMapping(null, parameterMap.getType(), null));
		}

		@Override
		protected void pretreatmentBuilderBoundSql(Map<Integer, Object> args) {
			sql = sql.replace(replaceParts.get(0), "?");
			parameterValueMappins.add(args.get(0));
		}
	}
	
	/**
	 * 多个任意类型入参
	 * @author Azurite-Y
	 *
	 */
	public static class MultivaluedParameterBoundSqlBuilder  extends AbstractParameterBoundSqlBuilder  {

		public MultivaluedParameterBoundSqlBuilder(Configuration configuration, DefaultMapperAnnotation mapperAnnotation,
				ParameterMap parameterMap) {
			super(configuration, mapperAnnotation, parameterMap);
		}

		@Override
		protected void sortParameterMappings(List<String> list) {
			List<ParameterMapping> parameterMappings = parameterMap.getParameterMappings();
			for (String name : list) {
				Integer index = null;
				try {
					index = Integer.parseInt(name);
				} catch (NumberFormatException e) {
					logger.error("多个参数根据参数索引下标关联参数值,当前下标不是数字类型，by name：" + name);
					e.printStackTrace();
				}
				Assert.isTrue(index > -1, "多参数使用参数索引下标关联参数值,索引从0开始计数, by index：" + index);
				ParameterMapping pm = parameterMappings.get(index);
				pm.setProperty(index.toString());
				sortedlist.add(pm);
			}
		}

		@Override
		protected void pretreatmentBuilderBoundSql (Map<Integer, Object> args) {
			// 多个参数根据参数索引下标关联参数值
			int len = sortedlist.size();
			StringBuilder builder = new StringBuilder();
			ParameterMapping pm;
			Class<?> arrObjClaz = null;
			TypeHandler<Object> handler = null;
			for (int i = 0; i < len; i++,builder.delete(0, builder.length())) {
				pm = sortedlist.get(i);
				Object object = args.get(Integer.parseInt(pm.getProperty()));
				if (!appendArrayCollectionSql(object,pm.getJavaType(),builder)) {
					builder.append("?");
					
					if (arrObjClaz == null) {
						arrObjClaz = object.getClass();
						handler = typeHandlerRegistry.getTypeHandler(arrObjClaz);
					}
					
					this.parameterValueMappins.add(object);
					this.typeHandlerList.add(handler);
					
					sql = sql.replace(replaceParts.get(i), builder.toString());
				}
			}
		}
	}
}
