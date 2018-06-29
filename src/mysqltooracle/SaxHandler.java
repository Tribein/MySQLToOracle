/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mysqltooracle;

import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHandler extends DefaultHandler {
    //public Long rowNum = 0L;
    private Stack<String> elementStack = new Stack<String>();
    private SaxDBWorker mysqlWorker; 
    String fieldName;
    String fieldData;
    int fieldPos;
    private String dbUSN,dbPWD,dbSTR;
    public SaxHandler(String username, String password, String connectionString){
        dbUSN=username;
        dbPWD=password;
        dbSTR=connectionString;
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        this.elementStack.push(qName);

        switch (qName.toLowerCase()) {
            case "table_data":
                //System.out.println("Table: "+attributes.getValue("name"));
                mysqlWorker = new SaxDBWorker(attributes.getValue("name"),dbUSN,dbPWD,dbSTR);
                System.out.println("Processing table: "+mysqlWorker.tableName);
            break;
            case "row":
                //System.out.println("New row");
                //rowNum++;
                fieldName="";
                fieldData="";
                fieldPos = 0;
            case "field":
                fieldName = attributes.getValue("name");
                if(fieldName != null){
                    //System.out.println("Field "+fieldPos+": "+fieldName);
                    mysqlWorker.fieldName.add(fieldPos, fieldName.toUpperCase());
                    mysqlWorker.fieldData.add(fieldPos, "");
                }
                
            break;
        }
    }

    public void endElement(String uri, String localName,
            String qName) throws SAXException {

        //this.elementStack.pop();

        switch(qName.toLowerCase()){
            case "table_data":
                System.out.println("Table done: "+mysqlWorker.tableName);
                //mysqlWorker.rollbackAll();
                mysqlWorker.commitAll();
                mysqlWorker.cleanup();
            break;
            
            case "row":
                //System.out.println("Submit row: "+rowNum.toString());
                mysqlWorker.processRow();
                mysqlWorker.fieldName.clear();
                mysqlWorker.fieldData.clear();
            break;
            case "field":
                mysqlWorker.fieldData.set(fieldPos, fieldData);
                fieldName="";
                fieldData="";
                fieldPos++;
            break;
        }
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {

        String value = new String(ch, start, length).trim();
        if (value.length() == 0) {
            return;
        }
        if (currentElement().equalsIgnoreCase("field") && fieldName != null && ! fieldName.isEmpty()) {
            fieldData += value;
        }
    }

    private String currentElement() {
        return this.elementStack.peek();
    }

    private String currentElementParent() {
        if (this.elementStack.size() < 2) {
            return null;
        }
        return this.elementStack.get(this.elementStack.size() - 2);
    }    
}
