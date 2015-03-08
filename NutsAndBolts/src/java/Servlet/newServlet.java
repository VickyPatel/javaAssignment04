/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import Connection.DBConnect;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author c0633648
 */
@Path("/products")
public class newServlet {

    @GET
    @Produces("application/json")
    public String doGet() {

        JSONArray json = new JSONArray();
        Connection conn = DBConnect.getConnection();

        try {

            String query = "SELECT * FROM product";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int total = rs.getMetaData().getColumnCount();
                JSONObject jsonObj = new JSONObject();

                for (int i = 0; i < total; i++) {
                    String name = rs.getMetaData().getColumnLabel(i + 1).toLowerCase();
                    Object value = rs.getObject(i + 1);
                    jsonObj.put(name, value);

                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(newServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        return json.toJSONString();
    }

    @POST
    @Path("{productID}")
    protected void doPost(String data) throws ParseException {
        JSONObject jsonData = (JSONObject) new JSONParser().parse(data);

        String name = (String) jsonData.get("name");
        String desc = (String) jsonData.get("description");
        long quant = (long) jsonData.get("quantity");

        doUpdate("INSERT INTO product (name,description,quantity) VALUES (?,?,?)", name, desc, quant);
    }

    @PUT
    @Path("{productID}")
    protected void doPut(@PathParam("productID") int productID, String str) throws ParseException {

        try {
            JSONObject jsonData = (JSONObject) new JSONParser().parse(str);
            
            String name = (String) jsonData.get("name");
            String desc = (String) jsonData.get("description");
            long quant = (long) jsonData.get("quantity");
            
            Connection conn = DBConnect.getConnection();
            String query = "UPDATE product SET name=\'" + name + "\',description=\'" + desc + "\',quantity=" + quant + " where productID= " + productID;
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(newServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @DELETE
    @Path("{productID}")
    protected void doDelete(@PathParam("productID") int id) {

        try {
            Connection conn = DBConnect.getConnection();

            String query = "DELETE FROM product where productID= " + id;
            PreparedStatement pst = conn.prepareStatement(query);
            pst.execute();

        } catch (SQLException ex) {
            Logger.getLogger(newServlet.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getResult(String query, String... parameter) {
        StringBuilder sb = new StringBuilder();
        JSONObject obj = new JSONObject();

        try (Connection conn = DBConnect.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(query);
            for (int i = 1; i <= parameter.length; i++) {
                pst.setString(i, parameter[i - 1]);
            }
            System.out.println(parameter.length);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                obj.put("productID", rs.getInt("productID"));
                obj.put("name", rs.getString("name"));
                obj.put("description", rs.getString("description"));
                obj.put("quantity", rs.getInt("quantity"));
                sb.append(obj.toJSONString());
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return sb.toString();
    }

    private void doUpdate(String query, String name, String desc, long quant) {

        ArrayList list = new ArrayList();
        list.add(name);
        list.add(desc);
        list.add(quant);

        try (Connection conn = DBConnect.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= list.size(); i++) {
                pstmt.setString(i, list.get(i - 1).toString());
            }
            pstmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(newServlet.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

}
