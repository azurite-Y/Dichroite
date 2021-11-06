package org.zy.dichroite.fluorite.interfaces;

import java.sql.Connection;

import org.zy.dichroite.fluorite.session.Configuration;

/**
 * @author zy(Azurite - Y);
 * @DateTime 2021/9/5;
 * @Description SqlSession工厂类
 */
public interface SqlSessionFactory {
	SqlSession openSession();

	SqlSession openSession(Connection connection);

	Configuration getConfiguration();
}

