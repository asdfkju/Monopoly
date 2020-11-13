	package Monopoly;
	import java.sql.*;
	import java.util.*;	
	public class DBsql {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String OtherUser = "";
		Scanner scan = new Scanner(System.in);
		boolean start=true;
		
		public void Connection() { //DB연결
			con=DBcon.DBConnection();
		}
		public void memberAdd() {
			Scanner scan = new Scanner(System.in);
			String sql = "insert into userlist values((select count(*)from userlist)+1,?,?,?,?)";
			try {
				System.out.println("ID");
				String id=scan.next();
				if(!MemberAddCheck(id)) {
				pstmt = con.prepareStatement(sql);
					pstmt.setString(1, id);
					pstmt.setInt(2, 0);
					pstmt.setInt(3, 0);
					pstmt.setInt(4, 1500);
					pstmt.executeUpdate();
					pstmt.close();
					MemberAddToast(id);
			}
				else {
					System.out.println("중복된 ID입니다.");
					memberAdd();
				}
			}
			catch(SQLException e) {
				System.out.println("DB접속을 먼저하세요.");
				Monopolymain.main(null);
			}
		}
		public void MemberAddToast(String id) {
			String sql ="select * from userlist where id=?";
			try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			rs=pstmt.executeQuery();
			while(rs.next()) {
				System.out.println(rs.getString("id")+"님의 회원번호는"+rs.getString("userno")+"입니다.");	
			}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		public boolean MemberAddCheck(String id) {
			String sql ="select * from userlist where id=?";
			try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			rs=pstmt.executeQuery();
			while(rs.next()) {
				return true;
			}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		public void AutoCommitOff() {//JDBC는 기본적으로 자동커밋, 허나 게임을 하고 끝냈을 때 기존 데이터를 건드면 안되니까
			try {
				con.setAutoCommit(false);
				System.out.println("자동 커밋 해제");
			} catch (Exception e) {
				System.out.println("DB연결을 먼저 하세요.");
				Monopolymain.main(null);
			}
		}
		public void Start() {
			String userA=FirstMemberSearch(); 
			String userB=SecondMemberSearch(); 
			int locationA = 1; 
			int locationB = 1; 
			int count=1; //몇턴을 진행했는지
			turnRepeat(userA,userB,locationA,locationB,count);
		}
		public String FirstMemberSearch() {
			String sql = "SELECT * FROM USERLIST where userno=? and id=?";
			String id=null;
			try {
			pstmt = con.prepareStatement(sql);
			System.out.println("1P의 회원번호를 입력하세요.");
			pstmt.setInt(1, scan.nextInt());
			System.out.println("1P의 ID를 입력하세요.");
			pstmt.setString(2, scan.next());
			rs = pstmt.executeQuery();
			while(rs.next()) {
				return rs.getString("id");
			}
			if(!rs.next()) {
				System.out.println("해당하는 회원이 없습니다.");
				Start();
			}
		}
			catch(Exception e) {
				e.printStackTrace();
			}
			return id;
	}
		public String SecondMemberSearch() { 
			String sql = "SELECT * FROM USERLIST where userno=? and id=?";
			String id=null;
			try {
			pstmt = con.prepareStatement(sql);
			System.out.println("2P의 회원번호를 입력하세요.");
			pstmt.setInt(1, scan.nextInt());
			System.out.println("2P의 ID를 입력하세요.");
			pstmt.setString(2, scan.next());
			String enter=scan.nextLine();
			rs = pstmt.executeQuery();
			while(rs.next()) {
				return rs.getString("id");
			}
			if(!rs.next()) {
				System.out.println("해당하는 회원이 없습니다.");
				Start();
			}
		}
			catch(Exception e) {
				e.printStackTrace();
			}
			return id;
	}
		public void turnRepeat(String userA, String userB, int locationA, int locationB, int count) {
			this.start=true;
			while (this.start) {
				gameInfo(count,userA,userB);
				locationA=TurnA(locationA,userA,userB);//A의 턴 시작
				if(!this.start) { //현재 필드의 값이 false이면 게임이 끝난것이므로
					ALose(userA,userB);
					break;
				}
				gameInfo(count,userA,userB);
				locationB=TurnB(locationB,userB,userA); //B의 턴 시작
				if(!this.start) {
					BLose(userA,userB);
					break;
				}
				count++;
				if(count>=20) {//턴수 20이면 게임 끝
					gameFinish(userA,userB);
					break;
				}
			}
			
		}
		public void map() {
			System.out.println("+-------+-------+--------+--------+");
			System.out.println("+   출      +   인      +   대        +   부        +");
			System.out.println("+   발      +   천      +   전        +   산        +");
			System.out.println("+-------+-------+--------+--------+");
			System.out.println("+   서      +                +   국        +");
			System.out.println("+       +                +   세        +");
			System.out.println("+   울      +                +   청        +");
			System.out.println("+-------+                +--------+");
			System.out.println("+   수      +                +   제        +");
			System.out.println("+       +                +   주        +");
			System.out.println("+   원      +                +   도        +");
			System.out.println("+-------+-------+--------+--------+");
			System.out.println("+   찬      +   대      +   광        +   독        +");
			System.out.println("+   스      +   구      +   주        +   도        +");
			System.out.println("+-------+-------+--------+--------+");
		}
		public void gameInfo(int count,String userA, String userB) {
			map();
			System.out.println("");
			System.out.println(count+"턴");
			CitySearchToast(userA);
			UserMoneySearchToast(userA);
			CitySearchToast(userB);
			UserMoneySearchToast(userB);
		}
		public void CitySearchToast(String user) {//1P,2P가 현재 소유중인 도시를 출력
			String sql = "SELECT * FROM MONOPOLY WHERE PROPERTY=?";
			try {
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, user);
				rs = pstmt.executeQuery();
				System.out.print(user+"의 소유 도시 : ");
				while (rs.next()) {
					System.out.print(rs.getString("city")+" ");
				}
				System.out.println("");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("DB와 연결되어 있지 않습니다. 연결을 먼저 하세요.");
				Monopolymain.main(null);
			}
		}
		public void UserMoneySearchToast(String user) { //A,B의 현재 잔액을 조회
			String sql = "SELECT MONEY FROM USERLIST WHERE id=?";
			try {
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, user);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					System.out.println(user + "님의 잔액: " + rs.getInt("money"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		public void BLose(String userA, String userB) {
			rollback(); //값이 바뀌어버린 DB값을 롤백
			WinScoreInsertDB(userA);
			LoseScoreInsertDB(userB);
			disConnection(); 
		}
		public void ALose(String userA, String userB) {
			rollback();
			WinScoreInsertDB(userB);
			LoseScoreInsertDB(userA);
			disConnection();
		}
		public void gameFinish(String userA, String userB) {
			System.out.println("시간 초과, 게임종료");
			if(UserMoneySearch(userA)>UserMoneySearch(userB)) {
				System.out.println(userA+"의 승리!!");
				BLose(userA,userB);
			}
			else if(UserMoneySearch(userA)<UserMoneySearch(userB)) {
				System.out.println(userB+"의 승리!!");
				ALose(userA,userB);
			}
			else {
				rollback(); 
				disConnection();
				System.out.println("무승부");
			}
		}
		public int dice() {
			int dice = (int) (Math.random() * 6) + 1;
			return dice;
		}
		public int TurnA(int locationA,String userA,String userB) {
			String id = userA;
			System.out.println("");
			System.out.println(id + "의 차례입니다.");
			System.out.println("주사위를 굴리시려면  엔터를 입력하세요.");
			String enter = scan.nextLine();
			boolean loading = true;
			try {
				while(loading) {
					System.out.print("■■■■■");
					Thread.sleep(300);
					System.out.print("■■■■■");
					Thread.sleep(300);
					System.out.println("■■■■■");
					loading = false;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			int dice = dice(); 
			locationA += dice;
			if (locationA >= 13) { // 최대 이동거리는 12인데 현재위치가 13보다 크거나 같아지는 경우
				UserMoneyBonus(id);
				locationA = locationA % 12;
				CityproPertyCheck(locationA, id,userB); 
				return locationA;
			} 
			else {
				CityproPertyCheck(locationA, id,userB);// A유저가 도시에 도착했을시 도시가 공백지인지,자신의 도시인지,타인의 도시인지 판단
			}
			return locationA;
		}
		public int TurnB(int locationB,String userB,String userA) {
			String otherid = userB;
			System.out.println("");
			System.out.println(otherid + "의 차례입니다.");
			System.out.println("주사위를 굴리시려면  엔터를 입력하세요.");
			String enter = scan.nextLine();
			boolean loading = true;
			try {
				while(loading) {
					System.out.print("■■■■■");
					Thread.sleep(300);
					System.out.print("■■■■■");
					Thread.sleep(300);
					System.out.println("■■■■■");
					loading = false;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			int dice = dice();
			locationB += dice;
			if (locationB >= 13) {
				UserMoneyBonus(otherid);
				locationB = locationB % 12;
				CityproPertyCheck(locationB, otherid,userA);
				return locationB;
			}
			else {
				CityproPertyCheck(locationB, otherid,userA);
			}
			return locationB;
		}
		public void CityproPertyCheck(int location,String userA,String userB) {
			String sql = "SELECT CITY,PRICE,PROPERTY FROM MONOPOLY WHERE CITYNO=?";  
			try {  
				if (userA.equals(userA)) {
					OtherUser = userB;
				} 
				else {
					OtherUser = userA;
				}
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, location);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int price=rs.getInt("price");
					if(location==1) {  
						return;   
					}
					else if (location == 5) {
						//국세청
						System.out.println(rs.getString("city")+"에 방문했습니다.");
						System.out.println(price + "를 지불합니다.");
						UserMoneyTax(price, userA,userB); 
					}
					else if (location == 10) { //찬스 
						System.out.println(rs.getString("city")+"에 방문했습니다.");
						Chance(userA);
					}
					else if ((rs.getString("property")).equals(userA)) { //내도시일때
						System.out.println("당신의 소유 도시인  "+rs.getString("city")+"에 방문했습니다.");
						return;
					} 
					else if ((rs.getString("property")).equals(OtherUser)) {// 상대도시일때
						System.out.print(rs.getString("city")+"에 방문했습니다.  ");
						System.out.println(rs.getString("property") + "의 소유지 입니다. " + price + "를 지불합니다.");
						UserMoneyPayment(price, location, userA,userB);
					} 
					else { //도시가 공백지일때
						System.out.println(rs.getString("city")+"에 방문했습니다.");
						System.out.println("이 " +rs.getString("city")+"는(은) 소유한 사람이없습니다.  가격: " + price);
						System.out.println("");
						System.out.println("1.구매|2.패스");
						System.out.println("");
						int input1 = scan.nextInt();
						switch (input1) {
						case 1:
							CityPurchase(price, userA,location);
							String enter=scan.nextLine();
							break;
						case 2:
							enter = scan.nextLine();
							break;						
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		public void Chance(String user) {
			int chance=(int)(Math.random()*3)+1;
			System.out.println("찬스 발동");
			if(chance==1) {
				boolean loading = true;
				try {
					while(loading) {
						System.out.print("■■■■■");
						Thread.sleep(200);
						System.out.print("■■■■■");
						Thread.sleep(100);
						System.out.println("■■■■■");
						loading = false;
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				System.out.println("당첨!! 10%의 금액이 가산됩니다.");
				String sql="update userlist set money=money+money/10 where id=?";
				try {
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, user);
				pstmt.executeUpdate();
				pstmt.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(chance==2) {
				System.out.println("가위바위보 미니게임 시작!");
				System.out.println("컴퓨터와 가위바위보를 하여 이기면 상금,지면 벌금");
				boolean win=minigame();
				if(win) {
					String sql="update userlist set money=money+1000 where id=?";
					try {
					pstmt = con.prepareStatement(sql);
					pstmt.setString(1, user);
					pstmt.executeUpdate();
					pstmt.close();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				if(!win) {
					String sql="update userlist set money=money-500 where id=?";
					try {
					pstmt = con.prepareStatement(sql);
					pstmt.setString(1, user);
					pstmt.executeUpdate();
					pstmt.close();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			if(chance==3) {
				boolean loading = true;
				try {
					while(loading) {
						System.out.print("■■■■■");
						Thread.sleep(200);
						System.out.print("■■■■■");
						Thread.sleep(100);
						System.out.println("■■■■■");
						loading = false;
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				System.out.println("꽝!! 10%의 금액이 차감됩니다.");
				String sql="update userlist set money=money-money/10 where id=?";
				try {
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, user);
				pstmt.executeUpdate();
				pstmt.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}	
			}
		}
		public boolean minigame() {
			Scanner scan = new Scanner(System.in);
			System.out.println("컴퓨터와 가위바위보를 진행하세요. 키워드는 가위,바위,보만 가능합니다.");
			String userinput = scan.next();
			boolean loading = true;
			try {
				while(loading) {
					System.out.print("■■■■■");
					Thread.sleep(300);
					System.out.print("■■■■■");
					Thread.sleep(200);
					System.out.println("■■■■■");
					loading = false;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			int computer=0;
			String computeroutput = null; 
			computer= (int) ((Math.random() * 3) +1);
			if(computer==1) {
				computeroutput = "가위";
			}
			else if(computer==2){
				computeroutput = "바위";
			}
			else if(computer==3) {
				computeroutput = "보";
			}
			if(!userinput.equals("가위")) {
				if(!userinput.equals("바위")) {
					if(!userinput.equals("보")) {
						if(!userinput.equals("종료")) {
							System.out.println("잘못 입력하셨습니다.");
							return false;
						}			
					}		
				}		
			}
			switch(computeroutput) {
			case "가위" :
				if(userinput.equals("가위")) {System.out.println("비겼습니다."); System.out.println("컴퓨터가 낸 것은? :"+ computeroutput); return false;}
				else if(userinput.equals("바위")) {System.out.println("이겼습니다."); System.out.println("컴퓨터가 낸 것은? :"+ computeroutput); return true;}
				else if(userinput.equals("보")) {System.out.println("졌습니다."); System.out.println("컴퓨터가 낸 것은? :"+ computeroutput); 	return false;}
				break;
			case "바위" :
				if(userinput.equals("가위")) {System.out.println("졌습니다."); System.out.println("컴퓨터가 낸 것은? :"+ computeroutput); return false;}
				else if(userinput.equals("바위")) {System.out.println("비겼습니다."); System.out.println("컴퓨터가 낸 것은? :"+ computeroutput); return false;}
				else if(userinput.equals("보")) {System.out.println("이겼습니다."); System.out.println("컴퓨터가 낸 것은? :"+ computeroutput); return true;}
				break;
			case "보" : 
				if(userinput.equals("가위")) {System.out.println("이겼습니다."); System.out.println("컴퓨터가 낸 것은? :"+ computeroutput); return true;}
				else if(userinput.equals("바위")) {System.out.println("졌습니다."); System.out.println("컴퓨터가 낸 것은? :"+ computeroutput); return false;}
				else if(userinput.equals("보")) {System.out.println("비겼습니다."); System.out.println("컴퓨터가 낸 것은? :"+ computeroutput); return false;}
				break;
			}		
			return false;
		}
		public void CityPurchase(int price, String user,int location) { //도시구매 메소드
			String sql = "SELECT MONEY FROM USERLIST WHERE id=?";
			try {
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, user);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int saveMoney = rs.getInt("money");
				if (saveMoney <= price) {
					System.out.println("잔액이 부족합니다");
				}
				if(saveMoney > price) {
					sql = "select * from monopoly where cityno=?";
					pstmt = con.prepareStatement(sql);
					pstmt.setInt(1, location);
					rs = pstmt.executeQuery();
					boolean loading = true;
					try {
						while(loading) {
							System.out.print("■■■■■");
							Thread.sleep(200);
							System.out.print("■■■■■");
							Thread.sleep(200);
							System.out.println("■■■■■");
							loading = false;
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					while(rs.next()) {
						System.out.println(user+"가 "+ rs.getString("city")+"를 구매하였습니다.");
					}
					sql = "UPDATE MONOPOLY SET PROPERTY=? WHERE PRICE=?"; 
					pstmt = con.prepareStatement(sql);
					pstmt.setString(1, user);
					pstmt.setInt(2, price);
					pstmt.executeUpdate();
					pstmt.close();
					sql = "UPDATE USERLIST SET MONEY=MONEY-? WHERE id=?";
					pstmt = con.prepareStatement(sql);
					pstmt.setInt(1, price);
					pstmt.setString(2, user);
					pstmt.executeUpdate();
					pstmt.close();
				}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		public void UserMoneyPayment(int price, int location, String userA, String userB) {
		String sql = "SELECT MONEY FROM USERLIST WHERE id=?"; 			                
			try {                                                            
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, userA);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int saveMoney = rs.getInt("money");
				if (saveMoney <= price) {
					if (userA.equals(userA)) { 
						OtherUser = userB;
					} 
					else {
						OtherUser = userA; 
					}
					boolean loading = true;
					try {
						while(loading) {
							System.out.print("■■■■■");
							Thread.sleep(400);
							System.out.print("■■■■■");
							Thread.sleep(400);
							System.out.println("■■■■■");
							loading = false;
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					System.out.println(userA+"님이 파산하셨습니다");
					System.out.println(OtherUser+"가 승리하였습니다.");
					System.out.println("");
					System.out.println("게임종료");
					System.out.println("");
					System.out.println("");
					this.start=false; //게임종료
				} 
				else if(saveMoney >= price) {
					sql = "UPDATE USERLIST SET MONEY=MONEY-? WHERE id=?";
					pstmt = con.prepareStatement(sql);
					pstmt.setInt(1, price);
					pstmt.setString(2, userA);
					pstmt.executeUpdate();
					pstmt.close();
					if (userA.equals(userA)) { 
						OtherUser = userB;
					} 
					else {
						OtherUser = userA; 
					}
					sql = "UPDATE USERLIST SET MONEY=MONEY+? WHERE id=?";
					pstmt = con.prepareStatement(sql);
					pstmt.setInt(1, price);
					pstmt.setString(2, OtherUser);
					pstmt.executeUpdate();
					pstmt.close();
					TakeOverCity(price,userA,userB,location);
				}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		public void TakeOverCity(int price,String userA,String userB,int location) {//인수유무 메소드
			int saveMoney=UserMoneySearch(userA);
			String city=citySearch(location);
			if(saveMoney >= price) {
				System.out.println("해당 도시를 인수하시겠습니까?");
				System.out.println("");
				System.out.println("1.네 2.아니오");
				int command=scan.nextInt();
				if(command==1) {
					String sql = "UPDATE USERLIST SET MONEY=MONEY-? WHERE id=?";
					try {
						pstmt = con.prepareStatement(sql);
						pstmt.setInt(1, price);
						pstmt.setString(2, userA);
						pstmt.executeUpdate();
						pstmt.close();
						if (userA.equals(userA)) { 
							OtherUser = userB;
						} 
						else {
							OtherUser = userA; 
						}
						sql = "UPDATE USERLIST SET MONEY=MONEY+? WHERE id=?";
						pstmt = con.prepareStatement(sql);
						pstmt.setInt(1, price);
						pstmt.setString(2, OtherUser);
						pstmt.executeUpdate();
						pstmt.close();
						sql = "UPDATE monopoly SET property=? WHERE cityno=?"; 
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, userA);
						pstmt.setInt(2, location);
						pstmt.executeUpdate();
						boolean loading = true;
						try {
							while(loading) {
								System.out.print("■■■■■");
								Thread.sleep(200);
								System.out.print("■■■■■");
								Thread.sleep(200);
								System.out.println("■■■■■");
								loading = false;
							}
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						System.out.println(userA+"가 " +city+" 를(을) 인수하였습니다.");
						String enter=scan.nextLine();
						pstmt.close();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					if(command==2) {
						return;
					}
				}
			}
		}
		public void UserMoneyBonus(String user) {
			String sql = "UPDATE USERLIST SET MONEY=MONEY+? WHERE id=?";
			try {
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, 200);
				pstmt.setString(2, user);
				pstmt.executeUpdate();
				pstmt.close();
				System.out.println("출발지점을 만나 보너스를 받습니다.");
				System.out.println("");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		public void UserMoneyTax(int price, String userA, String userB) {
			String sql = "SELECT MONEY FROM USERLIST WHERE id=?";
			try {
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, userA);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int saveMoney = rs.getInt("money");
				if (saveMoney <= price) {
					if (userA.equals(userA)) {
						OtherUser = userB;
					} else {
						OtherUser = userA;
					}
					boolean loading = true;
					try {
						while(loading) {
							System.out.print("■■■■■");
							Thread.sleep(400);
							System.out.print("■■■■■");
							Thread.sleep(400);
							System.out.println("■■■■■");
							loading = false;
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					System.out.println(userA+"님이 파산하셨습니다");
					System.out.println(OtherUser+"가 승리하였습니다.");
					System.out.println("");
					System.out.println("");
					System.out.println("게임종료");
					System.out.println("");
					System.out.println("");
					this.start=false; 
				} 
				else {
					sql = "UPDATE USERLIST SET MONEY=MONEY-? WHERE id=?";
					pstmt = con.prepareStatement(sql);
					pstmt.setInt(1, price);
					pstmt.setString(2, userA);
					pstmt.executeUpdate();
					pstmt.close();
				}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		public String citySearch(int location) {
			String sql = "SELECT * FROM MONOPOLY WHERE cityno=?";
			try {// 도시보유
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, location);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					return rs.getString("city");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		public int UserMoneySearch(String user) {
			String sql = "SELECT MONEY FROM USERLIST WHERE id=?";
			int returnmoney=0;
			try {// 잔액보유
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, user);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					returnmoney=rs.getInt("money");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return returnmoney;
		}
		public void disConnection() {
			try {
				System.out.println("");
				System.out.println("");
				System.out.println("접속종료");
				System.out.println("");
				System.out.println("");
				System.out.println("");
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		public void rollback() { //게임이 끝난뒤에 임시저장된 내용들을 롤백
			try {
				System.out.println("리셋");
				con.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		public void WinScoreInsertDB(String user) {
			String sql="update userlist set win=win+? where id=?";
			try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, 1);
			pstmt.setString(2, user);
			pstmt.executeUpdate();
			pstmt.close();
			}
			catch(Exception e) {
			e.printStackTrace();
			}
		}
		public void LoseScoreInsertDB(String user) {
			String sql="update userlist set lose=lose+? where id=?";
			try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, 1);
			pstmt.setString(2, user);
			pstmt.executeUpdate();
			pstmt.close();
			}
			catch(Exception e) {
			e.printStackTrace();
			}
		}
		public void ScoreSearch() { //전적 조회용
			String sql = "SELECT * FROM USERLIST where userno=?";
			try {
			pstmt = con.prepareStatement(sql);
			System.out.println("조회할 회원의 회원번호를 입력하세요.");
			int userno= scan.nextInt();
			pstmt.setInt(1,userno);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				System.out.print(rs.getString("id")+"의 전적 : "+rs.getInt("win")+"승"+rs.getInt("lose")+"패"+"\n"+"\n");	
			}
			else {
				System.out.println("해당하는 회원 정보가 없습니다.");
			}
		}
			catch(Exception e) {
				System.out.println("DB와 연결되어있지 않습니다.");
			}
		}
	}