package org.legion.aegis.initializer;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.legion.aegis.common.consts.SystemConsts;
import org.legion.aegis.common.utils.SpringUtils;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;

@Controller
public class DataSourceInitializer {

    public void execute() {
        Connection connection = null;
        try {
            String scriptPath = SystemConsts.CLASSPATH + "db_full_mysql.sql";
            FileReader reader = new FileReader(new File(scriptPath));
            SqlSession sqlSession = SpringUtils.getBean(SqlSession.class);
            connection = sqlSession.getConfiguration().getEnvironment().getDataSource().getConnection();
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.setSendFullScript(true);
            scriptRunner.setStopOnError(true);
            scriptRunner.setAutoCommit(false);
            scriptRunner.runScript(reader);
            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e3) {
                e3.printStackTrace();
            }
        }

    }
}
