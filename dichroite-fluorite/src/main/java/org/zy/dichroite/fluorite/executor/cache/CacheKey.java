package org.zy.dichroite.fluorite.executor.cache;

import java.io.Serializable;
import java.util.Map;

/**
 * @DateTime 2021年9月27日;
 * @author zy(azurite-Y);
 * @Description
 */
@SuppressWarnings("serial")
public class CacheKey implements Serializable {
	/** 参数 */
	private Map<Integer,Object> methodParame;

	/** 方法全限定名 */
	private String methodSignature;

	public CacheKey(String methodSignature, Map<Integer,Object> methodParame) {
		super();
		this.methodSignature = methodSignature;
		this.methodParame = methodParame;
	}

	public Map<Integer,Object> getMethodParame() {
		return methodParame;
	}
	public void setMethodParame(Map<Integer,Object> methodParame) {
		this.methodParame = methodParame;
	}
	public String getMethodSignature() {
		return methodSignature;
	}
	public void setMethodSignature(String methodSignature) {
		this.methodSignature = methodSignature;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((methodParame == null) ? 0 : methodParame.hashCode());
		result = prime * result + ((methodSignature == null) ? 0 : methodSignature.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CacheKey other = (CacheKey) obj;
		if (methodParame == null) {
			if (other.methodParame != null)
				return false;
		} else if (!methodParame.equals(other.methodParame))
			return false;
		if (methodSignature == null) {
			if (other.methodSignature != null)
				return false;
		} else if (!methodSignature.equals(other.methodSignature))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CacheKey [methodParame=" + methodParame + ", methodSignature=" + methodSignature + "]";
	}
}
