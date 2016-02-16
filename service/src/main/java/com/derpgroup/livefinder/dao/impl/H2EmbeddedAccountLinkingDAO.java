package com.derpgroup.livefinder.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.jdbcx.JdbcDataSource;

import com.derpgroup.livefinder.dao.AccountLinkingDAO;
import com.derpgroup.livefinder.model.accountlinking.UserAccount;

public class H2EmbeddedAccountLinkingDAO implements AccountLinkingDAO {
  
  private Connection conn;
  private JdbcDataSource ds;
  
  public H2EmbeddedAccountLinkingDAO(){
    ds = new JdbcDataSource();
    ds.setURL("jdbc:h2:mem:");
    ds.setUser("sa");
    ds.setPassword("sa");
  }
  
  protected void init() throws SQLException {
    try {
      conn = ds.getConnection();
      setupFixtureData();
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  public void shutdown() throws SQLException{
    try {
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  protected ResultSet executeStatement(String sql) throws SQLException{
    Statement statement = conn.createStatement();
    
    try{
     boolean resultSetRows = statement.execute(sql);
     if(resultSetRows){
       return statement.getResultSet();
     }
    }catch(SQLException e){
      e.printStackTrace();
      throw e;
    }
    return null;
  }
  
  protected ResultSet executeQuery(String sql) throws SQLException{
    Statement statement = conn.createStatement();
    
    try{
      return statement.executeQuery(sql);
    }catch(SQLException e){
      e.printStackTrace();
      throw e;
    }
  }
  
  protected void setupFixtureData() throws SQLException{
    conn.setAutoCommit(false);
    String userTableCreation = "CREATE TABLE User(id varchar(255) PRIMARY KEY NOT NULL,"
        + "dateCreated TIMESTAMP NOT NULL DEFAULT(NOW()));";
    executeStatement(userTableCreation);
    
    String accountLinkTableCreation = "CREATE TABLE AccountLink("
        + "userId varchar(255),"
        + "externalUserId varchar(255),"
        + "externalSystemName varchar(64)"
        + ");";
    executeStatement(accountLinkTableCreation);
    
    String linkingTokenTableCreation = "CREATE TABLE LinkingToken("
        + "token UUID NOT NULL DEFAULT(RANDOM_UUID()),"
        + "userId varchar(255) NOT NULL,"
        + "dateCreated TIMESTAMP NOT NULL DEFAULT(NOW())"
        + ");";
    executeStatement(linkingTokenTableCreation);
    conn.commit();
    conn.setAutoCommit(true);
  }
  
  protected Connection getConn(){
    return conn;
  }

  @Override
  public UserAccount getUserByUserId(String alexaUserId) {
    String userSelect = "SELECT id FROM User WHERE id = '" + alexaUserId + "';";
    ResultSet response;
    try {
      response = executeQuery(userSelect);
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
    
    UserAccount user = new UserAccount();
    try {
      if(!response.first()){
        return null;
      }
      user.setUserId(response.getString("id"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return user;
  }

  @Override
  public UserAccount updateUser(UserAccount user) {

    String userCreate = "MERGE INTO User(id) KEY(id) VALUES('" + user.getUserId() + "');";

    try {
      executeStatement(userCreate);
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
    
    return getUserByUserId(user.getUserId());
  }

  @Override
  public String generateMappingTokenForUserId(String userId) {

    String linkingTokenCreate = "INSERT INTO LinkingToken(userId) VALUES('" + userId + "');";

    try {
      executeStatement(linkingTokenCreate);
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }

    ResultSet response;
    String linkingTokenRetrieve = "SELECT TOP 1 * FROM LinkingToken WHERE userId = '" + userId + "';";
       // + " ORDER BY dateCreated DESC";
    try {
      response = executeQuery(linkingTokenRetrieve);
      response.first();
      return response.getString("token");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String getUserIdByMappingToken(String token) {

    ResultSet response;
    String linkingTokenRetrieve = "SELECT TOP 1 userId FROM LinkingToken WHERE token = '" + token + "'"
        + " ORDER BY dateCreated DESC";
    try {
      response = executeQuery(linkingTokenRetrieve);
      response.first();
      return response.getString("userId");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void expireMappingToken(String token) {
    // TODO Auto-generated method stub

  }

  @Override
  public String generateAuthToken(String userId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getUserIdByAuthToken(String token) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void expireGrantedToken(String token) {
    // TODO Auto-generated method stub

  }

}