    package me.snowlight.config.db;

import me.snowlight.domain.team.Team;
import me.snowlight.mapper.TeamMapper;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.util.Properties;

    public class MyBatisConfig {
    public void config() {
        Properties properties = new Properties();
        properties.setProperty("driver", "org.h2.Driver");
        properties.setProperty("url", "jdbc:h2:tcp://localhost/~/h2/jpa-member");
        properties.setProperty("username", "sa");
        properties.setProperty("password", "");

        UnpooledDataSourceFactory dataSourceFactory = new UnpooledDataSourceFactory();
        dataSourceFactory.setProperties(properties);

        DataSource dataSource = dataSourceFactory.getDataSource();
        Environment development = new Environment("development", new JdbcTransactionFactory(), dataSource);
        Configuration configuration = new Configuration(development);
        configuration.addMapper(TeamMapper.class);
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(configuration);

        try (SqlSession sqlSession = sessionFactory.openSession()) {
            TeamMapper mapper = sqlSession.getMapper(TeamMapper.class);
            Team byId = mapper.findById(1L);
            System.out.println(byId);
        }
    }
}
