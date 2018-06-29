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

public class MySQLToOracle {
      
    static ArrayList < Thread> threadList = new ArrayList();
    
    public static void main(String[] args) throws Exception {
        String XMLFILENAME = args[3];
        /* DOM
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(new File(XMLFILENAME));
            NodeList nl = doc.getFirstChild().getChildNodes().item(1).getChildNodes();//mysqldump->database->table

            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeType()== Node.ELEMENT_NODE){
                    if(nl.item(i).getChildNodes().getLength() > 0){
                        threadList.add( new WorkThread(nl.item(i), args[0],args[1],args[2]) );
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
            SaxHandler handler   = new SaxHandler(args[0],args[1],args[2]);
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
