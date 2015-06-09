/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;

/**
 *
 * @author c0654437
 */



@WebServlet(urlPatterns = {"/ProductServlet"})
public class ProductServlet extends HttpServlet {private Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String jdbc = "jdbc:mysql://localhost/javaproducts";
            conn = (Connection) DriverManager.getConnection(jdbc, "root", "");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    private String getResults(String query, String... params) {
        String result = new String();
        try (Connection conn = getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            JSONArray productArr = new JSONArray();
            while (rs.next()) {
                Map productMap = new LinkedHashMap();
                productMap.put("Pro_ID", rs.getInt("Pro_ID"));
                productMap.put("Pro_Name", rs.getString("Pro_Name"));
                productMap.put("Pro_Description", rs.getString("Pro_Description"));
                productMap.put("Pro_Quantity", rs.getInt("Pro_Quantity"));
                productArr.add(productMap);
            }
            result = productArr.toString();
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result.replace("},", "},\n");
    }

   
   @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            Connection conn = getConnection();
            if (keySet.contains("Pro_Name") && keySet.contains("Pro_Description") && keySet.contains("Pro_Quantity")) {
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `Products_Table`(`Pro_ID`, `Pro_Name`, `Pro_Description`, `Pro_Quantity`) "
                        + "VALUES (null, '" + request.getParameter("Pro_Name") + "', '"
                        + request.getParameter("Pro_Description") + "', "
                        + request.getParameter("Pro_Quantity") + ");"
                );
                try {
                    pstmt.executeUpdate();
                    request.getParameter("Pro_ID");
                    doGet(request, response);
                } catch (SQLException ex) {
                    Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
                    out.println("Data inserted Error while retriving data.");
                }
            } else {
                out.println("Error: Not enough data to input");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            Connection conn = getConnection();
            if (keySet.contains("Pro_ID") && keySet.contains("Pro_Name") && keySet.contains("Pro_Description") && keySet.contains("Pro_Quantity")) {
                PreparedStatement pstmt = conn.prepareStatement("UPDATE `product` SET `name`='"
                        + request.getParameter("Pro_Name") + "',`Pro_Description`='"
                        + request.getParameter("Pro_Description")
                        + "',`quantity`=" + request.getParameter("Pro_Quantity")
                        + " WHERE `productID`=" + request.getParameter("Pro_ID"));
                try {
                    pstmt.executeUpdate();
                    doGet(request, response); //shows updated row
                } catch (SQLException ex) {
                    Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
                    out.println("Error putting values.");
                }
            } else {
                out.println("Error: Not enough data to update");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            Connection conn = getConnection();
            if (keySet.contains("Pro_ID")) {
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `Product_Table` WHERE `Pro_ID`=" + request.getParameter("Pro_ID"));
                try {
                    pstmt.executeUpdate();
                } catch (SQLException ex) {
                    Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
                    out.println("Error in deleting the product.");
                }
            } else {
                out.println("Error: in data to delete");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
