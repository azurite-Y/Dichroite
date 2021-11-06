package org.zy.dichroite.fluorite.annotation;

/**
 * @DateTime 2021年9月30日;
 * @author zy(azurite-Y);
 * @Description 查询模板，指定在SQL拼接的过程中的逻辑
 */
public enum QueryTemplate {
	/**
	 * 并行， SQL拼接的判断对所有不为空值或空集的属性‘一视同仁’，若有值则纳入SQL查询中</br>
	 * if(property1) {...} if(property2) {...}
	 */
	parallel,
	/**
	 * 串行，SQL拼接的判断根据pojo类中设置的梯度顺序排列后再进行判断</br>
	 * if(property1) {...} else if(property2) {...} else {}
	 */
	serial
}
