package Monopoly;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBcon {
		public static Connection DBConnection() {
			Connection con = null;
			String user = "kimjinu"; 
			String password="1234"; 
			String url="jdbc:oracle:thin:@localhost:1521:xe"; //연동할 DB의 위치 저장. 만약 다른 컴이면 IP주소 필요.
			
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
					con=DriverManager.getConnection(url, user, password);
			}
		catch(Exception e) {
			System.out.println("접속 실패");
			e.printStackTrace();
		}
			System.out.println("접속 성공");	
			return con;
		}
	}
