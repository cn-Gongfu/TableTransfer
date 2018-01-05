package yangb92;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * 
 * @author yangb
 *
 */
public class Main {

	private JDBCManager db = new JDBCManager();
	
	/**
	 * 数据迁移
	 * @param srcSql
	 * @param targetSql
	 * @param count
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void transation(String srcSql, String targetSql, int count) throws SQLException, ClassNotFoundException{
		PreparedStatement srcPrepareStatement = db.getSrcConection().prepareStatement(srcSql);
		ResultSet rs = srcPrepareStatement.executeQuery();
		
		Connection targetConection = db.getTargetConection();
		PreparedStatement targetPrepareStatement = targetConection.prepareStatement(targetSql);
		
		int flag = 0;
		while(rs.next()){
			for (int i = 1; i <= count; i++) {
				Object object = rs.getObject(i);
//				if(object != null)
//					System.out.println(object.getClass().toString());
//				else
//					System.out.println("NULL");
				
//				targetPrepareStatement.setObject(i, null);
				
				if(object instanceof java.sql.Date){
					targetPrepareStatement.setDate(i, rs.getDate(i));
//					System.out.println("Date 类型");
				}else if(object instanceof java.sql.Clob){
					targetPrepareStatement.setString(i,  rs.getString(i)); //教训: oracle Clob赋值要用String
//					System.out.println("Clob 类型");
				}else if(object instanceof java.sql.Timestamp){
					targetPrepareStatement.setDate(i, rs.getDate(i));
//					System.out.println("Timestamp 类型");
				}else if(object instanceof java.math.BigDecimal){
					targetPrepareStatement.setLong(i, rs.getLong(i));
				}else if(object instanceof String){
					targetPrepareStatement.setString(i, rs.getString(i));
				}else{
					targetPrepareStatement.setObject(i, object);
				}
				
			}
			System.out.println("[" + ++flag + "]");
			targetPrepareStatement.execute();
			targetPrepareStatement.clearParameters();
		}
		System.out.println("迁移完成,共迁移" + flag + "条数据");
		rs.close();
		srcPrepareStatement.close();
		targetPrepareStatement.close();
	}
	
	
	/**
	 * xml序列化
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public void xmlSerializable(String filePath) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException{
		//1, 创建解析器工厂
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        //2,根据解析器工厂创建解析器
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        //3,解析XML返回document
        Document document = documentBuilder.parse(filePath);
        //4,得到所有的name元素.
        NodeList nodelist = document.getElementsByTagName("TABLE");
        //5,返回集合,遍历集合,得到每一个那么元素
        //--遍历 getLength(); item();
        //--得到元素里面的值,使用getTextContent();
        for(int i=0;i<nodelist.getLength();i++){
             Node node = nodelist.item(i);
             Element talbe = (Element)node;
             String enable = talbe.getAttribute("enable");
             if(!enable.toLowerCase().equals("off")){
            	 String srcSql = null;
            	 String targetSql = null;
            	 int count = 0;
            	 NodeList childNodes = node.getChildNodes();
            	 for(int j=0;j<childNodes.getLength();j++){
            		 Node childNode = childNodes.item(j);
            		 String nodeName = childNode.getNodeName();
            		 if("SRC".equals(nodeName)){
            			 Element src = (Element)childNode;
            			 srcSql = src.getTextContent();
            		 }
            		 if("TARGET".equals(nodeName)){
            			 Element target = (Element)childNode;
            			 targetSql = target.getTextContent();
            			 String countStr = target.getAttribute("COUNT");
            			 count = Integer.parseInt(countStr);
            		 }
            	 }
            	 System.out.println("SRC:  " + srcSql);
            	 System.out.println("TARGET:  " + targetSql);
            	 System.out.println("COUNT: " + count);
            	 if(srcSql != null && targetSql != null && count != 0){
            		 transation(srcSql, targetSql, count); //执行数据迁移
            	 }else{
            		 System.err.println("配置文件有误!");
            	 }
            	 
             }
        }

	}
	
	public void jdbcInit(String filePath) throws IOException{
		Properties pop=new Properties();
        FileInputStream fis =new FileInputStream(filePath);
        pop.load(fis);
        JDBCManager.SRC_DBDRIVER = pop.getProperty("SRC_DBDRIVER");
        JDBCManager.SRC_DBURL = pop.getProperty("SRC_DBURL");
        JDBCManager.SRC_DBUSER = pop.getProperty("SRC_DBUSER");
        JDBCManager.SRC_DBPASS = pop.getProperty("SRC_DBPASS");
        
        JDBCManager.TARGET_DBDRIVER = pop.getProperty("TARGET_DBDRIVER");
        JDBCManager.TARGET_DBURL = pop.getProperty("TARGET_DBURL");
        JDBCManager.TARGET_DBUSER = pop.getProperty("TARGET_DBUSER");
        JDBCManager.TARGET_DBPASS = pop.getProperty("TARGET_DBPASS");
        fis.close();

	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, ParserConfigurationException, SAXException, IOException {
		System.out.println("------数据迁移程序------");
		Scanner scanner = new Scanner(System.in);
		System.out.println("请输入数据库配置文件路径:");
		String jdbcFile = scanner.nextLine();
//		String jdbcFile = "C:\\Users\\yangb\\Desktop\\online_data\\jdbc.properties";
		System.out.println("请输入SqlMapper文件路径:");
		String xmlFile = scanner.nextLine();
//		String xmlFile = "C:\\Users\\yangb\\Desktop\\online_data\\SqlMapper.xml";
		scanner.close();
		System.out.println("程序开始执行.");
		Main main = new Main();
		main.jdbcInit(jdbcFile);
		main.xmlSerializable(xmlFile);
		System.out.println("程序执行结束.");
		
//		String srcSql = "SELECT 	a.bod540 ID, 	c.bsc001 dept_id, 	1 object_type,  	a.BOD54J consult_type,  	1 open,  	a.aac003 name,  	a.aac004 gender,  	a.BOD54C age,  	a.aac030 phone,  	a.aae005 tel,  	a.aae015 email,  	a.bod54b card_code,  	a.aae007 zip_code,  	a.aae006 address,  	a.bod542 mail_title,  	a.bod543 mail_context,  	a.aae006 local,  	a.bod548 interact_code,  	888888 password,  	a.aae036 consult_time,  	b.bod555 reply_user_name,  	b.bod552 reply_context,  	b.bod557 reply_time, 	nvl2(b.bod557,1,0) reply_status, 	a.bod500 user_id FROM 	od54 a LEFT JOIN OD55 b ON a .bod540 = b.BOD540 LEFT JOIN SC01 c ON a .aae017 = c.bsc001	";
//		String targetSql = "INSERT INTO INTERACT_CONTENT (     id,     dept_id,     object_type,     consult_type,     open,     name,     gender,     age,     phone,     tel,     email,     card_code,     zip_code,     address,     mail_title,     mail_context,     local,     interact_code,     password,     consult_time,     reply_user_name,     reply_context,     reply_time,     reply_status,     user_id ) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//		new Main().transationInteract(srcSql, targetSql, 25);
	}
	
}
