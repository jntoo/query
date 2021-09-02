package com.jntoo.db;

import java.sql.*;

public interface ConnectionConfig {
    public Connection getConn();
    public void closeConn(Connection connection);
}
