/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mysqltooracle;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MysqlToOracle {
      
    static ArrayList < Thread> threadList = new ArrayList();
    
    public static void main(String[] args) throws Exception {
        String XMLFILENAME = args[0];
        /* DOM
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(new File(XMLFILENAME));
            NodeList nl = doc.getFirstChild().getChildNodes().item(1).getChildNodes();//mysqldump->database->table

            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeType()== Node.ELEMENT_NODE){
                    if(nl.item(i).getChildNodes().getLength() > 0){
                        threadList.add( new WorkThread(nl.item(i)) );
                        threadList.get(threadList.size()-1).run();
                    }
                }
                
            }
        */
        /* SAX */
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            InputStream xmlInput = new FileInputStream(XMLFILENAME);
            SAXParser  saxParser = factory.newSAXParser();
            SaxHandler handler   = new SaxHandler();
            saxParser.parse(xmlInput, handler);
            /*
            for(MySQLXMLTable table : handler.mysqlTables){
                System.out.println(table.tableName);
            }
            */
        } catch (Throwable err) {
            err.printStackTrace ();
        }           
    }
}
