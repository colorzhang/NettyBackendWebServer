package test.backend;

import java.io.InputStream;
import java.util.concurrent.Callable;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.TransactionIsolationLevel;

import test.backend.persistence.TestMapper;

public class Provider implements Callable<Object> {

	private final String path;
	
	private final Values values;

	private final static SqlSessionFactory sqlSessionFactory;
	static {
		InputStream configAsStream = Provider.class
				.getResourceAsStream("persistence/mybatis-config.xml");
		sqlSessionFactory = new SqlSessionFactoryBuilder()
				.build(configAsStream);
	}

	public Provider(String path, Values values) {
		this.path = path;
		this.values = values;
	}

	@Override
	public Object call() throws Exception {
		Object res = null;

//		try (SqlSession session = sqlSessionFactory
//				.openSession(TransactionIsolationLevel.REPEATABLE_READ)) {
//			TestMapper mapper = session.getMapper(TestMapper.class);
//			res = mapper.selectOne(1L);
//			session.commit();
//		}
		
		Integer duration = values.getAsInteger("duration");
		Thread.sleep(duration * 1000);
		res = "Slept for " + duration + " secs";

		return res;
		// throw new RuntimeException("Hello, Exception!");
	}
}
