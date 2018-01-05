package yangb92;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCManager {
	// 定义数据库驱动程序   
	public static String SRC_DBDRIVER = null;   
	// 定义数据库的连接地址   
	public static String SRC_DBURL = null;   
	//端口号后标识符可以通过在doc下运行lsnrctl status查看  default:1521
	// 数据库的连接用户名   
	public static String SRC_DBUSER = null;
	// 数据库的连接密码   
	public static String SRC_DBPASS = null;

	// 定义数据库驱动程序   
	public static String TARGET_DBDRIVER = null;
	// 定义数据库的连接地址   
	public static String TARGET_DBURL = null;
	//端口号后标识符可以通过在doc下运行lsnrctl status查看  default:1521
	// 数据库的连接用户名   
	public static String TARGET_DBUSER = null;
	// 数据库的连接密码   
	public static String TARGET_DBPASS = null;
	
	public Connection getSrcConection() throws SQLException, ClassNotFoundException{
		Class.forName(SRC_DBDRIVER);
		return DriverManager.getConnection(SRC_DBURL,SRC_DBUSER,SRC_DBPASS) ;  
	}
	
	public Connection getTargetConection() throws ClassNotFoundException, SQLException{
		Class.forName(TARGET_DBDRIVER);
		return DriverManager.getConnection(TARGET_DBURL,TARGET_DBUSER,TARGET_DBPASS) ;  
	}
}
