/* 
 * Copyright (C) 2018 Tribein
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mysqltooracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WorkThread extends Thread {

    String tableName;
    Node workNode, tempNode;
    NodeList rowsNode;
    NodeList fields;
    Connection con;
    PreparedStatement stmt, alts, tcols, trct;
    String insertQuery;
    int rowFields;
    String vvv;
    HashMap<String, String> tableColumns = new HashMap();
    ResultSet res;
    private String dbUSN,dbPWD,dbSTR;

    public WorkThread(Node n, String username, String password, String connectionString) {
        workNode    = n;
        dbUSN       = username;
        dbPWD       = password;
        dbSTR       = connectionString;        
        tableName   = workNode.getAttributes().getNamedItem("name").getNodeValue();
    }

    @Override
    public void run() { 
        System.out.println("Starting thread for table " + tableName);
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@" + dbSTR, dbUSN, dbPWD);
            con.setAutoCommit(false);
            prepareSchema();
        } catch (Exception e) {
            System.exit(1);
        }

        int bp;
        String fieldName;
        rowsNode = workNode.getChildNodes();
        System.out.println("Processing "+ rowsNode.getLength()+" nodes");
        for (int i = 0; i < rowsNode.getLength(); i++) {
            if (rowsNode.item(i).getNodeType() == Node.ELEMENT_NODE) {

                fields = rowsNode.item(i).getChildNodes();

                for (int k = 1; k <= rowFields; k++) {
                    try {
                        stmt.setString(k, null);
                    } catch (Exception e) {

                    }
                }
                bp = 0;
                for (int j = 0; j < fields.getLength(); j++) {
                    if (fields.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        //System.out.print(fields.item(j).getTextContent().trim() + "\t");
                        bp++;
                        try {

                            vvv = fields.item(j).getTextContent().trim();
                            if (vvv.length() > 0) {
                                fieldName = fields.item(j).getAttributes().getNamedItem("name").getNodeValue().toUpperCase();
                                switch ((tableColumns.containsKey(fieldName)) ? tableColumns.get(fieldName) : "STRING") {
                                    case "NUMBER":
                                        stmt.setLong(bp, Long.parseLong(vvv));
                                    break;
                                    case "FLOAT":
                                        stmt.setFloat(bp, Float.parseFloat(vvv));
                                    break;
                                    case "DATE":
                                        stmt.setString(bp, vvv);
                                    break;
                                    case "CLOB":
                                        //stmt.setBytes(bp, vvv.getBytes("UTF-8"));
                                        stmt.setString(bp, vvv);
                                    break;
                                    case "BLOB":
                                        stmt.setBytes(bp, vvv.getBytes("UTF-8"));
                                    break;                           
                                    default:
                                        stmt.setString(bp, vvv);
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                }
                if (bp > 1) {
                    try {
                        stmt.execute();
                    } catch (Exception e) {
                        System.out.println("Error: " + tableName + " " + e.getMessage());
                        //e.printStackTrace();
                    }
                }
                //System.out.println("");
            }
        }
        try {
            con.commit();
            System.out.println("Commit");
            stmt.close();
            con.close();
        } catch (Exception e) {

        }
        System.out.println("Finishing thread for table " + tableName);
    }
    
    private void prepareSchema() throws Exception {
            alts = con.prepareCall("alter session set nls_date_format='YYYY-MM-DD HH24:MI:SS'");
            alts.execute();
            alts.close();
            tcols = con.prepareStatement("select upper(column_name),data_type from user_tab_columns where upper(table_name)=upper('" + tableName + "')");
            res = tcols.executeQuery();
            System.out.println("Truncating tabe: "+tableName);
            trct = con.prepareCall("truncate table "+tableName+" reuse storage");
            trct.execute();
            trct.close();
            System.out.println("Table truncated");
            //System.out.println("Table columns:");
            while (res != null && res.next()) {
                tableColumns.put(res.getString(1), res.getString(2));
                //System.out.println(res.getString(1)+":"+res.getString(2));
            }
            if(tableColumns.size()==0){
                System.out.println("Table doesn't exists: "+tableName);
                System.exit(5);
            }   
        ArrayList<String> iq = new ArrayList();
        
        rowFields = tableColumns.size();
        for (int i = 0; i < rowFields; i++) {
            iq.add("?");
        }
        
        insertQuery = "insert into " + tableName + " values(" + String.join(" , ", iq) + ")";
        //System.out.println(insertQuery);
        try {
            stmt = con.prepareStatement(insertQuery);
        } catch (Exception e) {
            System.exit(2);
        }            
    }
}
