/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mysqltooracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SaxDBWorker {

    private Connection con;
    private PreparedStatement stmt, alts, tcols, trct;
    private String insertQuery;
    private HashMap<String, String> tableColumns = new HashMap();
    private ResultSet res;
    public String tableName;
    public String nlsDateFormat = "YYYY-MM-DD HH24:MI:SS";
    public ArrayList<String> fieldName;
    public ArrayList<String> fieldData;
    public int rowFields;
    private final String dbusn = "sur2";
    private final String dbpwd = "sur2";
    private final String connString = "sefarmtest.gksm.local:1521/sefarm12_test";
    private boolean isEmpty = true;

    public SaxDBWorker(String table) {
        tableName = table;
        fieldName = new ArrayList();
        fieldData = new ArrayList();
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@" + connString, dbusn, dbpwd);
            con.setAutoCommit(false);
            alts = con.prepareCall("alter session set nls_date_format='" + nlsDateFormat + "'");
            alts.execute();
            alts.close();
            tcols = con.prepareStatement("select upper(column_name),data_type from user_tab_columns where upper(table_name)=upper('" + tableName + "')");
            res = tcols.executeQuery();
            System.out.println("Truncating tabe: " + tableName);
            trct = con.prepareCall("truncate table " + tableName + " reuse storage");
            trct.execute();
            trct.close();
            System.out.println("Table truncated");
            //System.out.println("Table columns:");
            while (res != null && res.next()) {
                tableColumns.put(res.getString(1), res.getString(2));
                //System.out.println(res.getString(1)+":"+res.getString(2));
            }
            if (tableColumns.size() == 0) {
                System.out.println("Table doesn't exists: " + tableName);
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
        } catch (Exception e) {
            System.exit(1);
        }
    }

    public void processRow() {
        isEmpty = true;
        for (int k = 1; k <= rowFields; k++) {
            try {
                stmt.setString(k, null);
            } catch (Exception e) {

            }
        }
        try {
            for (int i = 0; i < fieldName.size(); i++) {
                try {
                    if (fieldData.get(i).length() > 0) {
                        isEmpty = false;
                        switch ((tableColumns.containsKey(fieldName.get(i)))? tableColumns.get(fieldName.get(i)):"STRING" ) {
                            case "NUMBER":
                                stmt.setLong(i+1, Long.parseLong(fieldData.get(i)));
                                break;
                            case "FLOAT":
                                stmt.setFloat(i+1, Float.parseFloat(fieldData.get(i)));
                                break;
                            case "DATE":
                                stmt.setString(i+1, fieldData.get(i));
                                break;
                            case "CLOB":
                                //stmt.setBytes(bp, fieldData.get(i).getBytes("UTF-8"));
                                stmt.setString(i+1, fieldData.get(i));
                                break;
                            case "BLOB":
                                stmt.setBytes(i+1, fieldData.get(i).getBytes("UTF-8"));
                                break;
                            default:
                                stmt.setString(i+1, fieldData.get(i));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(15);
                }
            }
            if (!isEmpty) {
                stmt.execute();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void rollbackAll() {
        try {
            con.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void commitAll() {
        try {
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanup() {
        try {
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
