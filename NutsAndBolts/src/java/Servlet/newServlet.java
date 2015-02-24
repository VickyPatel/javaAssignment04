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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/**
 *
 * @author c0633648
 */
@WebServlet("/products")
public class newServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Content-Type", "text/plain-text");

        try (PrintWriter out = response.getWriter()) {

            if (!request.getParameterNames().hasMoreElements()) {
                out.println(getResult("SELECT * FROM product"));
            } else {
                int id = Integer.parseInt(request.getParameter("id"));
                System.out.println("hello");
                out.println(getResult("SELECT * FROM product WHERE productID = ?", String.valueOf(id)));
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Set<String> key = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (key.contains("name") && key.contains("description") && key.contains("quantity")) {
                String name = request.getParameter("name");
                String desc = request.getParameter("description");
                String quant = request.getParameter("quantity");

                doUpdate("INSERT INTO product (name,description,quantity) VALUES (?,?,?)", name, desc, quant);
                out.println("http://localhost:8080/NutsAndBolts/products?id="+doUpdate("SELECT LAST_INSERT_ID()"));
            } else {
                response.setStatus(500);
            }
        } catch (IOException ex) {
            Logger.getLogger(newServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        Set<String> key = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (key.contains("productID") && key.contains("name") && key.contains("description") && key.contains("quantity")) {
                String id = request.getParameter("productID");
                String name = request.getParameter("name");
                String desc = request.getParameter("description");
                String quant = request.getParameter("quantity");

                doUpdate("UPDATE product SET name=?,description=?,quantity=? where productID=? ", name, desc, quant, id);
                out.println("http://localhost:8080/NutsAndBolts/products?id=" + id);
            } else {
                response.setStatus(500);
            }
        } catch (IOException ex) {
            Logger.getLogger(newServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        Set<String> key = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (key.contains("productID")) {
                String id = request.getParameter("productID");

                doUpdate("DELETE FROM product where productID=? ", id);

            } else {
                response.setStatus(500);
            }
        } catch (IOException ex) {
            Logger.getLogger(newServlet.class.getName()).log(Level.SEVERE, null, ex);
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

    private int doUpdate(String query, String... parameter) {
        int change = 0;
        try (Connection conn = DBConnect.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= parameter.length; i++) {
                pstmt.setString(i, parameter[i - 1]);
            }
            change = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(newServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return change;
    }

}
