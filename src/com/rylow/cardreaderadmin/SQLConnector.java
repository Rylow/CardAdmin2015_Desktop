package com.rylow.cardreaderadmin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import com.rylow.Family.Family;
import com.rylow.SpecialCard.SpecialCard;
import com.rylow.Staff.Staff;
import com.rylow.Student.Student;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SQLConnector {
	
	public static Connection dbConnect(String db_connect_string,
            String db_userid,
            String db_password)
   {
      try {
         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
         Connection conn = DriverManager.getConnection(db_connect_string,
                  db_userid, db_password);
         return conn;

      } catch (Exception e) {
    	  
         e.printStackTrace();
         return null;
      }
      
   }
	
	private static int getCardType(Connection conn, int cardID){
		
		PreparedStatement psCardID;
		ResultSet rsCardID;
				
		try {
			
			psCardID = conn.prepareStatement("select * from Cards WHERE id = ?");
			psCardID.setInt(1, cardID);
			rsCardID = psCardID.executeQuery();
		    while (rsCardID.next()) {
		       return rsCardID.getInt(4);
		    }
		    
		    rsCardID.close();
		    psCardID.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 

		return 0;
		
	}
	
	public static TreeMap<Long, String> currentlyInSchoolReportFamily(Connection conn){
		
		PreparedStatement pstate, psFamily;
		ResultSet rs, rsFamily;
		Set<Integer> idSet = new HashSet<Integer>();
		Time timeIn, timeOut;
		TreeMap<Long, String> map = new TreeMap<Long, String>();
		String familyName = "";
				
		try {
			
			pstate = conn.prepareStatement("SELECT * FROM Visits WHERE CONVERT (date,InTime) = CONVERT (date, GETDATE()) ORDER BY id", ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = pstate.executeQuery();
			if (rs.isBeforeFirst()){
				while (rs.next()){
					
					idSet.add(rs.getInt(3));
					
				}
				
				for (int id : idSet){
					
					rs.afterLast();
					while(rs.previous()){
						
						if (rs.getInt(3) == id){
							
							timeIn = rs.getTime(6);
							timeOut = rs.getTime(7);
							
							if (timeOut.before(timeIn)){
								psFamily = conn.prepareStatement("SELECT * FROM Family WHERE id = ?");
								psFamily.setInt(1, rs.getInt(3));
								rsFamily = psFamily.executeQuery();
								
								if (rsFamily.next()){
									familyName = rsFamily.getString(2);
								}
								
								if (getCardType(conn, rs.getInt(2)) != 3)
									map.put(timeIn.getTime(), "Family " + familyName);
								else
									map.put(timeIn.getTime(), "Family " + familyName + " on Temporary Card");
								
								familyName = "";
								
								rsFamily.close();
								psFamily.close();

							}
							break;
						}
						
					}
					
				}
			}
			
			rs.close();
			
			
			
			pstate.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
		
	}
	
	public static TreeMap<Long, String> currentlyInSchoolReportStudents(Connection conn){
		
		PreparedStatement pstate, psFamily;
		ResultSet rs, rsFamily;
		Set<Integer> idSet = new HashSet<Integer>();
		Time timeIn, timeOut;
		TreeMap<Long, String> map = new TreeMap<Long, String>();
		String familyName = "";
				
		try {
			
			pstate = conn.prepareStatement("SELECT * FROM StudentAttendance WHERE CONVERT (date,TimeIN) = CONVERT (date, GETDATE()) ORDER BY id", ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = pstate.executeQuery();
			if (rs.isBeforeFirst()){
				while (rs.next()){
					
					idSet.add(rs.getInt(3));
					
				}
				
				for (int id : idSet){
					
					rs.afterLast();
					while(rs.previous()){
						
						if (rs.getInt(3) == id){
							
							timeIn = rs.getTime(6);
							timeOut = rs.getTime(7);
							
							if (timeOut.before(timeIn)){
								psFamily = conn.prepareStatement("SELECT * FROM Students WHERE id = ?");
								psFamily.setInt(1, rs.getInt(3));
								rsFamily = psFamily.executeQuery();
								
								if (rsFamily.next()){
									familyName = rsFamily.getString(2);
								}
								
								if (getCardType(conn, rs.getInt(2)) != 3)
									map.put(timeIn.getTime(), familyName);
								else
									map.put(timeIn.getTime(), familyName + " on Temporary Card");
								
								familyName = "";
								
								rsFamily.close();
								psFamily.close();

							}
							break;
						}
						
					}
					
				}
			}
			
			
			rs.close();
			pstate.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
		
	}
	
	public static TreeMap<Long, String> currentlyInSchoolReportStaff(Connection conn){
		
		PreparedStatement pstate, psFamily;
		ResultSet rs, rsFamily;
		Set<Integer> idSet = new HashSet<Integer>();
		Time timeIn, timeOut;
		TreeMap<Long, String> map = new TreeMap<Long, String>();
		String familyName = "";
				
		try {
			
			pstate = conn.prepareStatement("SELECT * FROM Attendance WHERE CONVERT (date,TimeIN) = CONVERT (date, GETDATE()) ORDER BY id", ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = pstate.executeQuery();
			if (rs.isBeforeFirst()){
				while (rs.next()){
					
					idSet.add(rs.getInt(3));
					
				}
				
				for (int id : idSet){
					
					rs.afterLast();
					while(rs.previous()){
						
						if (rs.getInt(3) == id){
							
							timeIn = rs.getTime(6);
							timeOut = rs.getTime(7);
							
							if (timeOut.before(timeIn)){
								psFamily = conn.prepareStatement("SELECT * FROM Staff WHERE id = ?");
								psFamily.setInt(1, rs.getInt(3));
								rsFamily = psFamily.executeQuery();
								
								if (rsFamily.next()){
									familyName = rsFamily.getString(2);
								}
								
								if (getCardType(conn, rs.getInt(2)) != 3)
									map.put(timeIn.getTime(), familyName);
								else
									map.put(timeIn.getTime(), familyName + " on Temporary Card");
								
								familyName = "";
								
								rsFamily.close();
								psFamily.close();

							}
							break;
						}
						
					}
					
				}
			}
			
			
			rs.close();
			pstate.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
		
	}
	
	
	
	//////////// FAMILIES////////////////////////////////
	
	protected static ObservableList<String> fillFamiliesList(Connection conn){

		PreparedStatement psFamily;
		ResultSet rsFamily;
		ObservableList<String> items =FXCollections.observableArrayList();
		
		try {
			
			psFamily = conn.prepareStatement("SELECT * FROM Family");
			rsFamily = psFamily.executeQuery();
			if (rsFamily.isBeforeFirst()){
				while (rsFamily.next()){
					items.add(rsFamily.getString(2));
				}
				
				FXCollections.sort(items);
			}
			
			rsFamily.close();
			psFamily.close();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		return items;
	}
	
	
	protected static Family findFamilyByName (String name){
		
		PreparedStatement psFamily;
		ResultSet rsFamily;
		Family family = new Family();
		Connection conn = null;

		
		
	    try {
	    	conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			psFamily = conn.prepareStatement("select * from Family where FamilyName = ?");
		    psFamily.setString(1, name);
		    rsFamily = psFamily.executeQuery();
		    
		    if (rsFamily.next()) {
		    	
		    	family.setFamilyID(rsFamily.getInt(1));
		    	family.setFamilyName(rsFamily.getString(2));
		    	
		    	family.setHolder1Name(rsFamily.getString(3));
		    	family.setHolder1Status(rsFamily.getString(5));
		    	
		    	family.setHolder2Name(rsFamily.getString(6));
		    	family.setHolder2Status(rsFamily.getString(8));
		    	
		    	family.setHolder3Name(rsFamily.getString(9));
		    	family.setHolder3Status(rsFamily.getString(11));
		    	
		    	family.setHolder4Name(rsFamily.getString(12));
		    	family.setHolder4Status(rsFamily.getString(14));
		    	
		    	family.setHolder1Picture(rsFamily.getString(4));
		    	family.setHolder2Picture(rsFamily.getString(7));
		    	family.setHolder3Picture(rsFamily.getString(10));
		    	family.setHolder4Picture(rsFamily.getString(13));
		    	
		    	family.setSecurityGroup(rsFamily.getInt(15));
		    	family.setActive(rsFamily.getBoolean(16));
		    	
		    	rsFamily.close();
		    	psFamily.close();
		    	
			}
			else{
			    	family = null; //Family not found
			}
		    
		} catch (SQLException e) {
			e.printStackTrace();
			family = null;
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return family;
	}
	
	protected static ObservableList<String> findCardsByFamily(Connection conn, int familyID){
		
		PreparedStatement psCardID;
		ResultSet rsCardID;
		ObservableList<String> items =FXCollections.observableArrayList();
		
		try {
			
			psCardID = conn.prepareStatement("select * from Cards where HolderID = ? AND HolderType = 1");
			psCardID.setInt(1, familyID);
			rsCardID = psCardID.executeQuery();
		    while (rsCardID.next()) {
		       items.add(rsCardID.getString(2));
		    }
		    
		    rsCardID.close();
		    psCardID.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	
		return items;
		
	}
	
	protected static Boolean doesFamilyExist(String familyName){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
			
			ps = conn.prepareStatement("select * from Family where FamilyName = ?");
			ps.setString(1, familyName);
			rs = ps.executeQuery();
			

			
			return rs.isBeforeFirst();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		} 
		finally{
			
			try {
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected static Boolean insertNewFamily(Family family){
		PreparedStatement ps;
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
		
			ps = conn.prepareStatement("INSERT INTO Family " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, family.getFamilyName());
			ps.setString(2, family.getHolder1Name());
			ps.setString(3, family.getHolder1Picture());
			ps.setString(4, family.getHolder1Status());
			ps.setString(5, family.getHolder2Name());
			ps.setString(6, family.getHolder2Picture());
			ps.setString(7, family.getHolder2Status());
			ps.setString(8, family.getHolder3Name());
			ps.setString(9, family.getHolder3Picture());
			ps.setString(10, family.getHolder3Status());
			ps.setString(11, family.getHolder4Name());
			ps.setString(12, family.getHolder4Picture());
			ps.setString(13, family.getHolder4Status());
			ps.setInt(14, family.getSecurityGroup());
			ps.setBoolean(15, family.getActive());
			ps.executeUpdate();
			
			ps.close();
			
			return true;
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected static Boolean updateFamilyProfile(Family family){
		PreparedStatement psUpdate;
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		try{
			psUpdate = conn.prepareStatement("UPDATE Family SET FamilyName = ?, Holder1Name = ?, Holder1Picture = ?, Holder1Status = ?,"
					+ " Holder2Name = ?, Holder2Picture = ?, Holder2Status = ?, Holder3Name = ?, Holder3Picture = ?, Holder3Status = ?,"
					+ " Holder4Name = ?, Holder4Picture = ?, Holder4Status = ?, SecurityGroup = ?, Active = ? WHERE id = ?");
			psUpdate.setString(1, family.getFamilyName());
			psUpdate.setString(2, family.getHolder1Name());
			psUpdate.setString(3, family.getHolder1Picture());
			psUpdate.setString(4, family.getHolder1Status());
			psUpdate.setString(5, family.getHolder2Name());
			psUpdate.setString(6, family.getHolder2Picture());
			psUpdate.setString(7, family.getHolder2Status());
			psUpdate.setString(8, family.getHolder3Name());
			psUpdate.setString(9, family.getHolder3Picture());
			psUpdate.setString(10, family.getHolder3Status());
			psUpdate.setString(11, family.getHolder4Name());
			psUpdate.setString(12, family.getHolder4Picture());
			psUpdate.setString(13, family.getHolder4Status());
			psUpdate.setInt(14, family.getSecurityGroup());
			psUpdate.setBoolean(15, family.getActive());
			psUpdate.setInt(16, family.getFamilyID());
			psUpdate.executeUpdate();
			
			psUpdate.close();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected static Boolean deleteFamilyRecord(int familyID){
		
		PreparedStatement psDelete;
		Connection conn;
		
		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try{
			psDelete = conn.prepareStatement("DELETE FROM Family WHERE id = ?");
			psDelete.setInt(1, familyID);
			psDelete.executeUpdate();
			
			psDelete.close();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
	}
	
	protected static int findFamilyIdByName(String familyName){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
			
			ps = conn.prepareStatement("select * from Family where FamilyName = ?");
			ps.setString(1, familyName);
			rs = ps.executeQuery();
			
			if (rs.next()){
			

				return rs.getInt(1);
				
			}
			else{

				return 0;
				
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		} 
		finally{
			
			try {
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected static String findFamilyNameById(int id){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
			
			ps = conn.prepareStatement("select * from Family where id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()){
			

				return rs.getString(2);
				
			}
			else{


				return "Not Found";
				
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return "Not Found";
		} 
		finally{
			
			try {
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected static Boolean addCardtoFamily(String cardNumber, int familyID){
		
		PreparedStatement ps;
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
		
			ps = conn.prepareStatement("INSERT INTO Cards " + "VALUES (?, ?, ?)");
			ps.setString(1, cardNumber);
			ps.setInt(2, familyID);
			ps.setInt(3, 1);
			ps.executeUpdate();
			
			ps.close();
			
			return true;
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected static Boolean isCardIsAlreadyAssigned(String cardNumber){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
			
			ps = conn.prepareStatement("select * from Cards where CardNumber = ?");
			ps.setString(1, cardNumber);
			rs = ps.executeQuery();
			

			
			return rs.isBeforeFirst();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		} 
		finally{
			
			try {
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	protected static Boolean removeCard(String cardNumber){
		
		PreparedStatement psDelete;
		Connection conn;
		
		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try{
			psDelete = conn.prepareStatement("DELETE FROM Cards WHERE CardNumber = ?");
			psDelete.setString(1, cardNumber);
			psDelete.executeUpdate();
			
			psDelete.close();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	
	///////////// STAFF //////////////////////////////
	
	protected static ObservableList<String> fillStaffList(Boolean attendanceReport){
		
		Connection conn = null;
		PreparedStatement psStaff;
		ResultSet rsStaff;
		ObservableList<String> items =FXCollections.observableArrayList();
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			if (attendanceReport)
				psStaff = conn.prepareStatement("SELECT * FROM Staff WHERE Enabled='True'");
			else
				psStaff = conn.prepareStatement("SELECT * FROM Staff");
			rsStaff = psStaff.executeQuery();
			if (rsStaff.isBeforeFirst()){
				while (rsStaff.next()){
					items.add(rsStaff.getString(2));
				}
				
				FXCollections.sort(items);
			}
			
			rsStaff.close();
			psStaff.close();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		return items;
	}
	
	protected static ObservableList<String> fillStaffListFilter(Boolean attendanceReport, String nameFilter){
		
		Connection conn = null;
		PreparedStatement psStaff;
		ResultSet rsStaff;
		ObservableList<String> items =FXCollections.observableArrayList();
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			if (attendanceReport){
				psStaff = conn.prepareStatement("SELECT * FROM Staff WHERE Enabled='True' AND Name LIKE LOWER(?)");
				psStaff.setString(1, "%" + nameFilter + "%");
			}
			else{
				psStaff = conn.prepareStatement("SELECT * FROM Staff WHERE Name LIKE LOWER(?))");
				psStaff.setString(1, "%" + nameFilter + "%");
			}
			rsStaff = psStaff.executeQuery();
			if (rsStaff.isBeforeFirst()){
				while (rsStaff.next()){
					items.add(rsStaff.getString(2));
				}
				
				FXCollections.sort(items);
			}
			
			rsStaff.close();
			psStaff.cancel();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		return items;
	}
	
	protected static ObservableList<String> fillStudentsList(Boolean attendanceReport){
		
		Connection conn = null;
		PreparedStatement psStaff;
		ResultSet rsStaff;
		ObservableList<String> items =FXCollections.observableArrayList();
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			if (attendanceReport)
				psStaff = conn.prepareStatement("SELECT * FROM Students WHERE Active='True'");
			else
				psStaff = conn.prepareStatement("SELECT * FROM Students");
			
			rsStaff = psStaff.executeQuery();
			if (rsStaff.isBeforeFirst()){
				while (rsStaff.next()){
					items.add(rsStaff.getString(2));
				}
				
				FXCollections.sort(items);
			}
			
			rsStaff.close();
			psStaff.close();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		return items;
	}
	
	protected static Staff findStaffByName (String name){
		
		PreparedStatement psStaff;
		ResultSet rsStaff;
		Staff staff = new Staff();
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
	    try {
	    	
	    	psStaff = conn.prepareStatement("select * from Staff where Name = ?");
	    	psStaff.setString(1, name);
	    	rsStaff = psStaff.executeQuery();
		    
		    if (rsStaff.next()) {
		    	
		    	staff.setId(rsStaff.getInt(1));
		    	staff.setName(rsStaff.getString(2));
		    	staff.setEmail(rsStaff.getString(3));
		    	staff.setRole(rsStaff.getString(4));
		    	staff.setPhoto(rsStaff.getString(5));
		    	staff.setExternal(rsStaff.getBoolean(6));
		    	staff.setActive(rsStaff.getBoolean(7));
		    	staff.setSecurityGroup(rsStaff.getInt(8));
		    	
			}
			else{
			    	staff = null; //Family not found
			}
		    
		    psStaff.close();
		    rsStaff.close();
		    
		} catch (SQLException e) {
			e.printStackTrace();
			staff = null;
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}

		
		return staff;
	}
	
	protected static Student findStudentByName (String name){
		
		PreparedStatement psStudent;
		ResultSet rsStudent;
		Student student = new Student();
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
	    try {
	    	
	    	psStudent = conn.prepareStatement("select * from Students where Name = ?");
	    	psStudent.setString(1, name);
	    	rsStudent = psStudent.executeQuery();
		    
		    if (rsStudent.next()) {
		    	
		    	student.setId(rsStudent.getInt(1));
		    	student.setName(rsStudent.getString(2));
		    	student.setPhoto(rsStudent.getString(3));
		    	student.setYear(rsStudent.getInt(4));
		    	student.setSecurityGroup(rsStudent.getInt(5));
		    	student.setActive(rsStudent.getBoolean(6));
		    	
		    	
			}
			else{
			    	student = null; //Family not found
			}
		    
		    rsStudent.close();
		    psStudent.close();
		    
		} catch (SQLException e) {
			e.printStackTrace();
			student = null;
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}

		
		return student;
	}
	
	

	protected static ObservableList<String> findCardsByStaff(int staffID){
	
		PreparedStatement psCardID;
		ResultSet rsCardID;
		ObservableList<String> items =FXCollections.observableArrayList();
		
		Connection conn;
	
		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
			
			psCardID = conn.prepareStatement("select * from Cards where HolderID = ? AND HolderType = 2");
			psCardID.setInt(1, staffID);
			rsCardID = psCardID.executeQuery();
		    while (rsCardID.next()) {
		       items.add(rsCardID.getString(2));
		    }
		    
		    rsCardID.close();
		    psCardID.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		return items;
	
	}
	
	protected static ObservableList<String> findCardsByStudent(int studentID){
		
		PreparedStatement psCardID;
		ResultSet rsCardID;
		ObservableList<String> items =FXCollections.observableArrayList();
		
		Connection conn;
	
		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
			
			psCardID = conn.prepareStatement("select * from Cards where HolderID = ? AND HolderType = 4");
			psCardID.setInt(1, studentID);
			rsCardID = psCardID.executeQuery();
		    while (rsCardID.next()) {
		       items.add(rsCardID.getString(2));
		    }
		    
		    rsCardID.close();
		    psCardID.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		return items;
	
	}
	
	protected static Boolean deleteStaffRecord(int staffID){
		
		PreparedStatement psDelete, psDeleteCards;
		Connection conn;
		
		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try{
			psDelete = conn.prepareStatement("DELETE FROM Staff WHERE id = ?");
			psDelete.setInt(1, staffID);
			psDelete.executeUpdate();
			
			psDeleteCards = conn.prepareStatement("DELETE FROM Cards WHERE HolderID = ? AND HolderType = 2");
			psDeleteCards.setInt(1, staffID);
			psDeleteCards.executeUpdate();
			
			psDelete.close();
			psDeleteCards.close();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected static Boolean deleteStudentRecord(int studentID){
		
		PreparedStatement psDelete, psDeleteCards;
		Connection conn;
		
		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try{
			psDelete = conn.prepareStatement("DELETE FROM Students WHERE id = ?");
			psDelete.setInt(1, studentID);
			psDelete.executeUpdate();
			
			psDeleteCards = conn.prepareStatement("DELETE FROM Cards WHERE HolderID = ? AND HolderType = 4");
			psDeleteCards.setInt(1, studentID);
			psDeleteCards.executeUpdate();
			
			psDelete.close();
			psDeleteCards.close();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected static Boolean doesStaffExist(String staffName){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
			
			ps = conn.prepareStatement("select * from Staff where Name = ?");
			ps.setString(1, staffName);
			rs = ps.executeQuery();
			
			return rs.isBeforeFirst();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		} 
		finally{
			
			try {
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	protected static Boolean insertNewStaff(Staff staff){
		PreparedStatement ps;
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
		
			ps = conn.prepareStatement("INSERT INTO Staff " + "VALUES (?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, staff.getName());
			ps.setString(2, staff.getEmail());
			ps.setString(3, staff.getRole());
			ps.setString(4, staff.getPhoto());
			ps.setBoolean(5, staff.getExternal());
			ps.setBoolean(6, staff.getActive());
			ps.setInt(7, staff.getSecurityGroup());
			ps.executeUpdate();
			
			ps.close();
			
			return true;
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	protected static Boolean insertNewStudent(Student student){
		PreparedStatement ps;
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
		
			ps = conn.prepareStatement("INSERT INTO Students " + "VALUES (?, ?, ?, ?, ?)");
			ps.setString(1, student.getName());
			ps.setString(2, student.getPhoto());
			ps.setInt(3, student.getYear());
			ps.setInt(4, student.getSecurityGroup());
			ps.setBoolean(5, student.getActive());
			
			ps.executeUpdate();
			
			ps.close();
			
			return true;
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	protected static Boolean updateStaffProfile(Staff staff){
		PreparedStatement psUpdate;
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		try{
			psUpdate = conn.prepareStatement("UPDATE Staff SET Name = ?, Email = ?, Role = ?, Photo = ?, [External] = ?, Enabled = ?, SecurityGroup = ? WHERE id = ?");

			psUpdate.setString(1, staff.getName());
			psUpdate.setString(2, staff.getEmail());
			psUpdate.setString(3, staff.getRole());
			psUpdate.setString(4, staff.getPhoto());
			psUpdate.setBoolean(5, staff.getExternal());
			psUpdate.setBoolean(6, staff.getActive());
			psUpdate.setInt(7, staff.getSecurityGroup());
			psUpdate.setInt(8, staff.getId());
			psUpdate.executeUpdate();
			
			psUpdate.close();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	protected static Boolean updateStudentProfile(Student student){
		PreparedStatement psUpdate;
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		try{
			psUpdate = conn.prepareStatement("UPDATE Students SET Name = ?, Photo = ?, Year = ?, SecurityGroup = ?, Active = ? WHERE id = ?");

			psUpdate.setString(1, student.getName());
			psUpdate.setString(2, student.getPhoto());
			psUpdate.setInt(3, student.getYear());
			psUpdate.setInt(4, student.getSecurityGroup());
			psUpdate.setBoolean(5, student.getActive());

			psUpdate.setInt(6, student.getId());
			psUpdate.executeUpdate();
			psUpdate.close();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	protected static int findStaffIdByName(String staffName){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
			
			ps = conn.prepareStatement("select * from Staff where Name = ?");
			ps.setString(1, staffName);
			rs = ps.executeQuery();
			
			if (rs.next()){
			
				return rs.getInt(1);
				
			}
			else{
				
				return 0;
				
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		} 
		finally{
			
			try {
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	protected static int findStudentIdByName(String studentName){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
			
			ps = conn.prepareStatement("select * from Students where Name = ?");
			ps.setString(1, studentName);
			rs = ps.executeQuery();
			
			if (rs.next()){
			
				return rs.getInt(1);
				
			}
			else{
				
				return 0;
				
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		} 
		finally{
			
			try {
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	protected static Boolean addCardtoStaff(String cardNumber, int staffID){
		
		PreparedStatement ps;
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
		
			ps = conn.prepareStatement("INSERT INTO Cards " + "VALUES (?, ?, ?)");
			ps.setString(1, cardNumber);
			ps.setInt(2, staffID);
			ps.setInt(3, 2);
			ps.executeUpdate();
			
			ps.close();
			
			return true;
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected static Boolean addCardtoStudent(String cardNumber, int studentID){
		
		PreparedStatement ps;
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
		
			ps = conn.prepareStatement("INSERT INTO Cards " + "VALUES (?, ?, ?)");
			ps.setString(1, cardNumber);
			ps.setInt(2, studentID);
			ps.setInt(3, 4);
			ps.executeUpdate();
			
			ps.close();
			
			return true;
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	///////////////// GUESTS //////////////////////////
	
	protected static ObservableList<GuestCard> fillCardCbox(int type){
		
		Connection conn = null;
		PreparedStatement ps;
		ResultSet rs;
		ObservableList<GuestCard> items =FXCollections.observableArrayList();
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			ps = conn.prepareStatement("SELECT * FROM Guests");
			rs = ps.executeQuery();
			if (rs.isBeforeFirst()){
				while (rs.next()){
					if (rs.getInt(3) == type)
						items.add(new GuestCard(rs.getString(2), rs.getInt(1)));
				}
				
				//FXCollections.sort(items);
			}
			
			rs.close();
			ps.close();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
		}
		
		return items;
		
		
		
	}
	
	protected static Boolean isGuestCardIssued(int guestCardID){
		
		Connection conn = null;
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			ps = conn.prepareStatement("SELECT * FROM Guests WHERE id = ?");
			ps.setInt(1, guestCardID);
			rs = ps.executeQuery();
			if (rs.isBeforeFirst()){
				while (rs.next()){
					if (rs.getInt(4) > 0)
						return true;
					else
						return false;
				}
				
				//FXCollections.sort(items);
			}
			
			rs.close();
			ps.close();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
		return true;
		
	}
	
	protected static int findIssuedGuestCard(int guestCardID){
		
		Connection conn = null;
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			ps = conn.prepareStatement("SELECT * FROM Guests WHERE id = ?");
			ps.setInt(1, guestCardID);
			rs = ps.executeQuery();
			if (rs.isBeforeFirst()){
				while (rs.next()){
					return rs.getInt(4);
				}
				
				//FXCollections.sort(items);
			}
			
			rs.close();
			ps.close();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
		return 0;
		
	}
	
	protected static int findGuestCardID(int globalCardID){
		
		Connection conn = null;
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			ps = conn.prepareStatement("SELECT * FROM Cards WHERE id = ?");
			ps.setInt(1, globalCardID);
			rs = ps.executeQuery();
			if (rs.isBeforeFirst()){
				while (rs.next()){
					return rs.getInt(3);
				}
				
				//FXCollections.sort(items);
			}
			
			rs.close();
			ps.close();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
		return 0;
		
	}
	
	protected static int findGlobalIDofGuestCard(int guestCardID){
		
		Connection conn = null;
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			ps = conn.prepareStatement("SELECT * FROM Cards WHERE HolderID = ? AND HolderType = 3");
			ps.setInt(1, guestCardID);
			rs = ps.executeQuery();
			if (rs.isBeforeFirst()){
				while (rs.next()){
					return rs.getInt(1);
				}
				
				//FXCollections.sort(items);
			}
			
			rs.close();
			ps.close();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
		return 0;
		
	}
	
	
	protected static Boolean releaseCard(int guestCardID, int type){
		
		Connection conn = null;
		PreparedStatement ps, pstate, psUpdate, psDoor;
		ResultSet rs;
		int globalCardID;
		
		
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			ps = conn.prepareStatement("SELECT * FROM Cards WHERE HolderID = ? AND HolderType = ?");
			ps.setInt(1, guestCardID);
			ps.setInt(2, 3);
			rs = ps.executeQuery();
			if (rs.next()){
				
				globalCardID = rs.getInt(1);
				
				if (type == 1){
					pstate = conn.prepareStatement("SELECT * FROM Visits WHERE CONVERT (date,InTime) = CONVERT (date, GETDATE()) AND CardID = ? ORDER BY id");
					pstate.setInt(1, globalCardID);
					rs = pstate.executeQuery();
				}
				else{
					if (type == 2){
						
						pstate = conn.prepareStatement("SELECT * FROM Attendance WHERE CONVERT (date,TimeIN) = CONVERT (date, GETDATE()) AND CardID = ? ORDER BY id");
						pstate.setInt(1, globalCardID);
						rs = pstate.executeQuery();
					}
					else{
						if (type == 3){
							
							pstate = conn.prepareStatement("SELECT * FROM GuestVisits WHERE TerminalOut = 0 AND CardID = ? ORDER BY id");
							pstate.setInt(1, globalCardID);
							rs = pstate.executeQuery();
							
						}
					}
				}
				if (!rs.isBeforeFirst() ) {    //If this is first record of the day for this card - ignore
					
					}
				else{
					
					Time timeIn = null, timeOut= null;
					Boolean last = false;
					int ident = 0;
					
					while(rs.next()){
						last = true;
						timeIn = rs.getTime(6);
						timeOut = rs.getTime(7);
						ident = rs.getInt(1);
					}
					
					if(last){

						if ((timeOut.getTime() - timeIn.getTime()) < 0){ 
							if (type == 1){
								psUpdate = conn.prepareStatement("UPDATE Visits SET OutTime = GETDATE(), TerminalIDOut = ? WHERE id = ?");
								psUpdate.setInt(1, 301);
								psUpdate.setInt(2, ident);
								psUpdate.executeUpdate();
								
								psUpdate.close();
							}
							else{
								if (type == 2){
									
									psUpdate = conn.prepareStatement("UPDATE Attendance SET TimeOUT = GETDATE(), TerminalOut = ? WHERE id = ?");
									psUpdate.setInt(1, 301);
									psUpdate.setInt(2, ident);
									psUpdate.executeUpdate();
									
									psUpdate.close();
									
								}
								else{
									if (type == 3){
										
										psUpdate = conn.prepareStatement("UPDATE GuestVisits SET TimeOUT = GETDATE(), TerminalOut = ? WHERE id = ?");
										psUpdate.setInt(1, 301);
										psUpdate.setInt(2, ident);
										psUpdate.executeUpdate();
										
										psDoor = conn.prepareStatement("DELETE FROM DoorSpecTempAccess WHERE HolderType = ? AND HolderID = ?");
										psDoor.setInt(1, 3);
										psDoor.setInt(2, guestCardID);						
										psDoor.executeUpdate();
										
										psDoor.close();
										psUpdate.close();
										
									}
								}
							}
							

							
						}
						else{
							//No unclosed last IN record - ignore

						}
					}
				}
				
				pstate = conn.prepareStatement("UPDATE Guests SET IssuedTo = ? WHERE id = ?"); //Case IN terminal - just insert new record
				pstate.setInt(1, 0);
				pstate.setInt(2, guestCardID);
				pstate.executeUpdate();
				
				rs.close();
				ps.close();
				
				pstate.close();
				
				
				return true;
				
			}
			
		} catch (SQLException e) {
		e.printStackTrace();
		return false;
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
		return false;
		
		
	}
	
	protected static Boolean assignFamilyGuestCard(int guestCardID, int familyID){
		
		Connection conn = null;
		PreparedStatement ps, pstate;
		ResultSet rs;
		int globalCardID;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			ps = conn.prepareStatement("SELECT * FROM Cards WHERE HolderID = ? AND HolderType = ?");
			ps.setInt(1, guestCardID);
			ps.setInt(2, 3);
			rs = ps.executeQuery();
			if (rs.next()){
				
				globalCardID = rs.getInt(1);
				
				pstate = conn.prepareStatement("INSERT INTO Visits " + "VALUES (?, ?, ?, 0, GetDate(), '')"); //Case IN terminal - just insert new record
				pstate.setInt(1, globalCardID);
				pstate.setInt(2, familyID);
				pstate.setInt(3, 301);
				pstate.executeUpdate();
				
				pstate = conn.prepareStatement("UPDATE Guests SET IssuedTo = ? WHERE id = ?"); //Case IN terminal - just insert new record
				pstate.setInt(1, familyID);
				pstate.setInt(2, guestCardID);
				pstate.executeUpdate();
				
				pstate.close();
				rs.close();
				ps.close();
				return true;
				
			}
			
			rs.close();
			ps.close();
			
			
		} catch (SQLException e) {
		e.printStackTrace();
		return false;
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
		return false;
		
		
	}
	
	public static ObservableList<String> currentlyInSchoolReportGuests(Connection conn){
		
		PreparedStatement pstate, psFamily, psStaff;
		ResultSet rs, rsFamily, rsStaff;
		Set<Integer> idSet = new HashSet<Integer>();
		Time timeIn, timeOut;
		
		ObservableList<String> items =FXCollections.observableArrayList();
				
		try {
			
			pstate = conn.prepareStatement("SELECT * FROM Guests WHERE IssuedTo <> 0 ORDER BY id", ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = pstate.executeQuery();
			while (rs.next()){
				if (rs.getInt(3) == 31){
					
					psFamily = conn.prepareStatement("SELECT * FROM Family WHERE id = ?");
					psFamily.setInt(1, rs.getInt(4));
					rsFamily = psFamily.executeQuery();
					
					if (rsFamily.next()){
						items.add("Family " + rsFamily.getString(2) + " on card#" + rs.getInt(1));
					}
					
					rsFamily.close();
					psFamily.close();
				}
				
				if (rs.getInt(3) == 32){
					
					psStaff = conn.prepareStatement("SELECT * FROM Staff WHERE id = ?");
					psStaff.setInt(1, rs.getInt(4));
					rsStaff = psStaff.executeQuery();
					
					if (rsStaff.next()){
						items.add(rsStaff.getString(2)  + " on card#" + rs.getInt(1));
					}
					
					rsStaff.close();
					psStaff.close();
					
				}
			}
			
			pstate = conn.prepareStatement("SELECT * FROM GuestVisits WHERE TerminalOUT = 0 ORDER BY id", ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = pstate.executeQuery();
			if (rs.isBeforeFirst()){
				while (rs.next()){
					
					idSet.add(rs.getInt(2));
					
				}
				
				for (int id : idSet){
					
					rs.afterLast();
					while(rs.previous()){
						
						if (rs.getInt(2) == id){
							
							timeIn = rs.getTime(6);
							timeOut = rs.getTime(7);
							
							if (timeOut.before(timeIn)){
								
								items.add(rs.getString(3) + " card#" + findGuestCardID(rs.getInt(2)));
								

							}
							break;
						}
						
					}
					
				}
			
			}
			
			rs.close();
			pstate.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return items;
		
	}
	
	public static TreeMap<Long, String> currentlyInSchoolReportOnlyGuests(){
		
		PreparedStatement pstate;
		ResultSet rs;
		Set<Integer> idSet = new HashSet<Integer>();
		Time timeIn, timeOut;
		TreeMap<Long, String> map = new TreeMap<Long, String>();
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			pstate = conn.prepareStatement("SELECT * FROM GuestVisits WHERE CONVERT (date,TimeIN) = CONVERT (date, GETDATE()) ORDER BY id", ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = pstate.executeQuery();
			if (rs.isBeforeFirst()){
				while (rs.next()){
					
					idSet.add(rs.getInt(2));
					
				}
				
				for (int id : idSet){
					
					rs.afterLast();
					while(rs.previous()){
						
						if (rs.getInt(2) == id){
							
							timeIn = rs.getTime(6);
							timeOut = rs.getTime(7);
							
							if (timeOut.before(timeIn)){
								
								map.put(timeIn.getTime(), rs.getString(3) + " card#" + findGuestCardID(rs.getInt(2)));
								

							}
							break;
						}
						
					}
					
				}
			
			}
			
			rs.close();
			pstate.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return map;
		
	}
	
	protected static String findStaffById(int id){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
			
			ps = conn.prepareStatement("select * from Staff where id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()){
			

				return rs.getString(2);
				
			}
			else{

				return "Not Found";
				
			}
			
			
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return "Not Found";
		} 
		finally{
			
			try {
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}

	protected static Boolean assignStaffGuestCard(int guestCardID, int staffID){
	
		Connection conn = null;
		PreparedStatement ps, pstate;
		ResultSet rs;
		int globalCardID;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			ps = conn.prepareStatement("SELECT * FROM Cards WHERE HolderID = ? AND HolderType = ?");
			ps.setInt(1, guestCardID);
			ps.setInt(2, 3);
			rs = ps.executeQuery();
			if (rs.next()){
				
				globalCardID = rs.getInt(1);
				
				pstate = conn.prepareStatement("INSERT INTO Attendance " + "VALUES (?, ?, ?, 0, GetDate(), '')"); //Case IN terminal - just insert new record
				pstate.setInt(1, globalCardID);
				pstate.setInt(2, staffID);
				pstate.setInt(3, 301);
				pstate.executeUpdate();
				
				pstate = conn.prepareStatement("UPDATE Guests SET IssuedTo = ? WHERE id = ?"); //Case IN terminal - just insert new record
				pstate.setInt(1, staffID);
				pstate.setInt(2, guestCardID);
				pstate.executeUpdate();
				
				rs.close();
				ps.close();
				pstate.close();
				
				return true;
				
			}
			
			} catch (SQLException e) {
			e.printStackTrace();
			return false;
			}
			finally{
				
				try {
					conn.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				
				
			}
		
			return false;
		
		
		}
	
	public static String findGuestCardHolder(int cardID){
		
		PreparedStatement pstate;
		ResultSet rs;
		Time timeIn, timeOut;
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			pstate = conn.prepareStatement("SELECT * FROM GuestVisits WHERE TerminalOut = 0 ORDER BY id", ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = pstate.executeQuery();
			rs.afterLast();
			while (rs.previous()){
				if (rs.getInt(2) == cardID){
					
					timeIn = rs.getTime(6);
					timeOut = rs.getTime(7);
					
					if (timeOut.before(timeIn)){
						
						return rs.getString(3);
						
					}
					break;
				}
				
				
			}

			rs.close();
			pstate.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return "Not Found";
		
	}
	
	protected static Boolean assignGuestGuestCard(int guestCardID, String guestName){
		
		Connection conn = null;
		PreparedStatement ps, pstate;
		ResultSet rs;
		int globalCardID;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			ps = conn.prepareStatement("SELECT * FROM Cards WHERE HolderID = ? AND HolderType = ?");
			ps.setInt(1, guestCardID);
			ps.setInt(2, 3);
			rs = ps.executeQuery();
			if (rs.next()){
				
				globalCardID = rs.getInt(1);
				
				pstate = conn.prepareStatement("INSERT INTO GuestVisits " + "VALUES (?, ?, ?, 0, GetDate(), '')"); //Case IN terminal - just insert new record
				pstate.setInt(1, globalCardID);
				pstate.setString(2, guestName);
				pstate.setInt(3, 301);
				pstate.executeUpdate();
				
				pstate = conn.prepareStatement("UPDATE Guests SET IssuedTo = ? WHERE id = ?"); //Case IN terminal - just insert new record
				pstate.setInt(1, 1);
				pstate.setInt(2, guestCardID);
				pstate.executeUpdate();
				
				rs.close();
				ps.close();
				pstate.close();
				
				return true;
				
			}
			
		} catch (SQLException e) {
		e.printStackTrace();
		return false;
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
		return false;
		
		
	}

	
	//////////////////////////////////////////////////////////////// Staff Attendance ///////////////////////////////////////////////////////////////////////////////////////////
	
	private static HashMap<Integer, String> getTerminals(){
		
		Connection conn = null;
		PreparedStatement psRecord;
		ResultSet rsRecord;
		HashMap<Integer, String> terminals = new HashMap<Integer, String>();
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");

			
			psRecord = conn.prepareStatement("SELECT * FROM Terminals");
			rsRecord = psRecord.executeQuery();
			
			while(rsRecord.next()){
				
				terminals.put(rsRecord.getInt(1), rsRecord.getString(2));
				
			}
			
			rsRecord.close();
			psRecord.close();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return terminals;
		
		
	}
		
	
	protected static ObservableList<StaffAttendanceRecord> getStaffAttendanceReport(String staffName){
		
		Connection conn = null;
		PreparedStatement psRecord;
		ResultSet rsRecord;
		ObservableList<StaffAttendanceRecord> items =FXCollections.observableArrayList();
		int id;
		HashMap<Integer, String> terminals = getTerminals();
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");

			id = findStaffIdByName(staffName);
			psRecord = conn.prepareStatement("SELECT * FROM Attendance WHERE StaffID = ? ORDER BY id");
			psRecord.setInt(1, id);
			rsRecord = psRecord.executeQuery();
			
			while(rsRecord.next()){
				
				if (rsRecord.getInt(4) != 0){
					items.add(new StaffAttendanceRecord(terminals.get(rsRecord.getInt(4)), rsRecord.getTimestamp(6)));
				}
				
				if (rsRecord.getInt(5) != 0){
					items.add(new StaffAttendanceRecord(terminals.get(rsRecord.getInt(5)), rsRecord.getTimestamp(7)));
				}
				
			}
			
			rsRecord.close();
			psRecord.close();
			
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return items;
		
		
	}
	
	protected static ObservableList<StaffAttendanceRecord> getStudentsAttendanceReport(String studentName){
		
		Connection conn = null;
		PreparedStatement psRecord;
		ResultSet rsRecord;
		ObservableList<StaffAttendanceRecord> items =FXCollections.observableArrayList();
		int id;
		HashMap<Integer, String> terminals = getTerminals();
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");

			id = findStudentIdByName(studentName);
			psRecord = conn.prepareStatement("SELECT * FROM StudentAttendance WHERE StudentID = ? ORDER BY id");
			psRecord.setInt(1, id);
			rsRecord = psRecord.executeQuery();
			
			while(rsRecord.next()){
				
				if (rsRecord.getInt(4) != 0){
					items.add(new StaffAttendanceRecord(terminals.get(rsRecord.getInt(4)), rsRecord.getTimestamp(6)));
				}
				
				if (rsRecord.getInt(5) != 0){
					items.add(new StaffAttendanceRecord(terminals.get(rsRecord.getInt(5)), rsRecord.getTimestamp(7)));
				}
				
			}
			
			rsRecord.close();
			psRecord.close();
			
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return items;
		
		
	}
	
	protected static ObservableList<StaffAttendanceRecord> getFamilyAttendanceReport(String familyName){
		
		Connection conn = null;
		PreparedStatement psRecord = null;
		ResultSet rsRecord = null;
		ObservableList<StaffAttendanceRecord> items =FXCollections.observableArrayList();
		int id;
		HashMap<Integer, String> terminals = getTerminals();
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");

			id = findFamilyIdByName(familyName);
			psRecord = conn.prepareStatement("SELECT * FROM Visits WHERE FamilyID = ? ORDER BY id");
			psRecord.setInt(1, id);
			rsRecord = psRecord.executeQuery();
			
			while(rsRecord.next()){
				
				if (rsRecord.getInt(4) != 0){
					items.add(new StaffAttendanceRecord(terminals.get(rsRecord.getInt(4)), rsRecord.getTimestamp(6)));
				}
				
				if (rsRecord.getInt(5) != 0){
					items.add(new StaffAttendanceRecord(terminals.get(rsRecord.getInt(5)), rsRecord.getTimestamp(7)));
				}
				
			}
			
			rsRecord.close();
			psRecord.close();
			
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return items;
		
		
	}
	
	protected static ObservableList<SecurityGroup> fillInSecurityBox(){
		
		Connection conn = null;
		PreparedStatement psRecord = null;
		ResultSet rsRecord = null;
		ObservableList<SecurityGroup> items =FXCollections.observableArrayList();

		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");

			psRecord = conn.prepareStatement("SELECT * FROM SecurityGroups ORDER BY id");
			rsRecord = psRecord.executeQuery();
			
			while(rsRecord.next()){
				
				items.add(new SecurityGroup(rsRecord.getInt(1), rsRecord.getString(2)));
				
			}
			
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				rsRecord.close();
				psRecord.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return items;
		
		
	}
	
	protected static ObservableList<Door> fillDoorList(){

		PreparedStatement psDoor = null;
		ResultSet rsDoor = null;
		ObservableList<Door> items =FXCollections.observableArrayList();
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			psDoor = conn.prepareStatement("SELECT * FROM Doors");
			rsDoor = psDoor.executeQuery();
			if (rsDoor.isBeforeFirst()){
				while (rsDoor.next()){
					items.add(new Door(rsDoor.getString(2), rsDoor.getInt(1)));
				}
				
				items.sort(new Comparator<Door>(){

				    @Override
				    public int compare(final Door o1, final Door o2){
				        // let your comparator look up your car's color in the custom order
				        return o1.getName().compareTo(o2.getName());
				    }
				});
			}
			
		} catch (SQLException e) {
		e.printStackTrace();
		}finally{
			
			try {
				rsDoor.close();
				psDoor.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return items;
	}
	
	protected static ObservableList<DoorSchedule> fillDoorSecGroupTable(int doorID){

		PreparedStatement psDoor = null;
		ResultSet rsDoor = null;
		ObservableList<DoorSchedule> items =FXCollections.observableArrayList();
		Connection conn = null;
		
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			psDoor = conn.prepareStatement("SELECT * FROM DoorPlan WHERE idDoor = ?");
			psDoor.setInt(1, doorID);
			rsDoor = psDoor.executeQuery();
			if (rsDoor.isBeforeFirst()){
				while (rsDoor.next()){
					items.add(new DoorSchedule(findDoorSecurityGroupById(conn, rsDoor.getInt(2)), formatIntToDate(rsDoor.getInt(5)), formatIntToDate(rsDoor.getInt(6)), findDoorScheduleById(conn, rsDoor.getInt(3))));
				}
				
				items.sort(new Comparator<DoorSchedule>(){

				    @Override
				    public int compare(final DoorSchedule o1, final DoorSchedule o2){
				        // let your comparator look up your car's color in the custom order
				        return o1.getName().compareTo(o2.getName());
				    }
				});
			}
			
		} catch (SQLException e) {
		e.printStackTrace();
		}finally{
			
			try {
				rsDoor.close();
				psDoor.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return items;
	}
	
	private static String formatIntToDate(int time){
		
		int diffHours = time / 60;
		int diffMinutes = time % 60;
		
		
		String since = "";
		
		if (diffHours > 9)
			since = since + diffHours + ":";
		else
			since = since + "0" + diffHours + ":";
		
		if (diffMinutes > 9)
			since = since + diffMinutes;
		else
			since = since + "0" + diffMinutes;
		
		return since;
		
		
	}
	
	protected static String findDoorSecurityGroupById(Connection conn, int sgID){

		PreparedStatement psDoor;
		ResultSet rsDoor;
		
		try {
			psDoor = conn.prepareStatement("SELECT * FROM SecurityGroups WHERE id = ?");
			psDoor.setInt(1, sgID);
			rsDoor = psDoor.executeQuery();
			if (rsDoor.isBeforeFirst()){
				if (rsDoor.next()){
					return rsDoor.getString(2);
				}

			}
			
			psDoor.close();
			rsDoor.close();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		return "Group not found";
	}
	
	protected static String findDoorScheduleById(Connection conn, int sgID){

		PreparedStatement psDoor;
		ResultSet rsDoor;
		
		try {
			
			psDoor = conn.prepareStatement("SELECT * FROM DoorSchedules WHERE id = ?");
			psDoor.setInt(1, sgID);
			rsDoor = psDoor.executeQuery();
			if (rsDoor.isBeforeFirst()){
				if (rsDoor.next()){
					return rsDoor.getString(2);
				}

			}
			
			rsDoor.close();
			psDoor.close();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		return "Schedule not found";
	}
	
	protected static int findSecGruopIDByName(Connection conn, String sgName){

		PreparedStatement psDoor = null;
		ResultSet rsDoor = null;
		
		try {
			
			psDoor = conn.prepareStatement("SELECT * FROM SecurityGroups WHERE GroupName = ?");
			psDoor.setString(1, sgName);
			rsDoor = psDoor.executeQuery();
			if (rsDoor.isBeforeFirst()){
				if (rsDoor.next()){
					return rsDoor.getInt(1);
				}

			}
			
			rsDoor.close();
			psDoor.close();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		return 0;
	}
	
	protected static int findDayTypeIDByName(Connection conn, String dtName){

		PreparedStatement psDoor = null;
		ResultSet rsDoor = null;
		
		try {
			
			psDoor = conn.prepareStatement("SELECT * FROM DoorSchedules WHERE ScheduleName = ?");
			psDoor.setString(1, dtName);
			rsDoor = psDoor.executeQuery();
			if (rsDoor.isBeforeFirst()){
				if (rsDoor.next()){
					return rsDoor.getInt(1);
				}

			}
			
			psDoor.close();
			rsDoor.close();
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		return 0;
	}
	
	protected static ObservableList<String> fillSecGroupBox(){

		PreparedStatement psDoor = null;
		ResultSet rsDoor = null;
		ObservableList<String> items =FXCollections.observableArrayList();
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			psDoor = conn.prepareStatement("SELECT * FROM SecurityGroups");
			rsDoor = psDoor.executeQuery();
			if (rsDoor.isBeforeFirst()){
				while (rsDoor.next()){
					items.add(rsDoor.getString(2));
				}
				
				FXCollections.sort(items);
			}
			
		} catch (SQLException e) {
		e.printStackTrace();
		}finally{
			
			try {
				rsDoor.close();
				psDoor.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return items;
	}
	
	protected static ObservableList<String> fillDayTypeBox(){

		PreparedStatement psDoor = null;
		ResultSet rsDoor = null;
		ObservableList<String> items =FXCollections.observableArrayList();
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			psDoor = conn.prepareStatement("SELECT * FROM DoorSchedules");
			rsDoor = psDoor.executeQuery();
			if (rsDoor.isBeforeFirst()){
				while (rsDoor.next()){
					items.add(rsDoor.getString(2));
				}
				
				FXCollections.sort(items);
			}
			
		} catch (SQLException e) {
		e.printStackTrace();
		}finally{
			
			try {
				rsDoor.close();
				psDoor.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return items;
	}
	
	protected static Boolean addNewDoorSgSchedule(DoorSchedule dsg, int id){

		PreparedStatement psDoor = null;
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			psDoor = conn.prepareStatement("INSERT INTO DoorPlan VALUES (?, ?, ?, ?, ?)");
			psDoor.setInt(1, findSecGruopIDByName(conn, dsg.getName()));
			psDoor.setInt(2, findDayTypeIDByName(conn, dsg.getDayType()));
			psDoor.setInt(3, id);
			psDoor.setInt(4, Integer.valueOf(dsg.getFrom()));
			psDoor.setInt(5, Integer.valueOf(dsg.getTo()));
			psDoor.executeUpdate();

			return true;
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}finally{
			
			try {
				psDoor.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected static Boolean removeDoorSgSchedule(DoorSchedule dsg, int id){

		PreparedStatement psDoor = null;
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			psDoor = conn.prepareStatement("DELETE FROM DoorPlan WHERE idSGroups = ? AND idDSchedule = ? AND idDoor = ? AND OpenTime = ? AND CloseTime = ?");
			psDoor.setInt(1, findSecGruopIDByName(conn, dsg.getName()));
			psDoor.setInt(2, findDayTypeIDByName(conn, dsg.getDayType()));
			psDoor.setInt(3, id);
			psDoor.setInt(4, Integer.valueOf(dsg.getFrom()));
			psDoor.setInt(5, Integer.valueOf(dsg.getTo()));
			
			System.out.println(dsg.getFrom() + "  " + dsg.getTo());
			
			psDoor.executeUpdate();

			return true;
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}finally{
			
			try {
				psDoor.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	private static String doorSpecPermDayList(String bitString){
		
		String dayList = "";
		
		int daySequence = Integer.parseInt(bitString, 2);
		
		if ((Integer.parseInt("1000000", 2) & daySequence) != 0)
			dayList += "Mon ";
		
		if ((Integer.parseInt("0100000", 2) & daySequence) != 0)
			dayList += "Tue ";
		
		if ((Integer.parseInt("0010000", 2) & daySequence) != 0)
			dayList += "Wed ";
		
		if ((Integer.parseInt("0001000", 2) & daySequence) != 0)
			dayList += "Thu ";
		
		if ((Integer.parseInt("0000100", 2) & daySequence) != 0)
			dayList += "Fri ";
		
		if ((Integer.parseInt("0000010", 2) & daySequence) != 0)
			dayList += "Sat ";
		
		if ((Integer.parseInt("0000001", 2) & daySequence) != 0)
			dayList += "Sun ";
		
		
		return dayList;
	}
	
	protected static ObservableList<DoorSchedule> fillDoorSpecPermSecGroupTable(int doorID){

		PreparedStatement psDoor = null;
		ResultSet rsDoor = null;
		ObservableList<DoorSchedule> items =FXCollections.observableArrayList();
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			psDoor = conn.prepareStatement("SELECT * FROM DoorSpecPermAccess WHERE DoorID = ?");
			psDoor.setInt(1, doorID);
			rsDoor = psDoor.executeQuery();
			if (rsDoor.isBeforeFirst()){
				while (rsDoor.next()){
					items.add(new DoorSchedule(findFamilyNameById(rsDoor.getInt(3)), formatIntToDate(rsDoor.getInt(5)), formatIntToDate(rsDoor.getInt(6)), doorSpecPermDayList(rsDoor.getString(4))));
				}
				
				items.sort(new Comparator<DoorSchedule>(){

				    @Override
				    public int compare(final DoorSchedule o1, final DoorSchedule o2){
				        // let your comparator look up your car's color in the custom order
				        return o1.getName().compareTo(o2.getName());
				    }
				});
			}
			
		} catch (SQLException e) {
		e.printStackTrace();
		}finally{
			
			try {
				rsDoor.close();
				psDoor.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return items;
	}
	
	protected static Boolean addNewSpecPermDoorSchedule(DoorSchedule dsg, int id){

		PreparedStatement psDoor = null;
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			psDoor = conn.prepareStatement("INSERT INTO DoorSpecPermAccess VALUES (?, ?, ?, ?, ?, ?)");
			psDoor.setInt(1, 1);
			psDoor.setInt(2, findFamilyIdByName(dsg.getName()));
			psDoor.setString(3, dsg.getDayType());
			psDoor.setInt(4, Integer.valueOf(dsg.getFrom()));
			psDoor.setInt(5, Integer.valueOf(dsg.getTo()));
			psDoor.setInt(6, id);
			psDoor.executeUpdate();

			return true;
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}finally{
			
			try {
				psDoor.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	protected static Boolean removeSpecPermDoorSchedule(DoorSchedule dsg, int id){

		PreparedStatement psDoor = null;
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			psDoor = conn.prepareStatement("DELETE FROM DoorSpecPermAccess WHERE HolderType = ? AND HolderID = ? AND DayOfWeek = ? AND DoorID = ? AND OpenTime = ? AND CloseTime = ?");
			psDoor.setInt(1, 1);
			psDoor.setInt(2, findFamilyIdByName(dsg.getName()));
			psDoor.setString(3, dsg.getDayType());
			psDoor.setInt(4, id);
			psDoor.setInt(5, Integer.valueOf(dsg.getFrom()));
			psDoor.setInt(6, Integer.valueOf(dsg.getTo()));
			
			System.out.println(findFamilyIdByName(dsg.getName()) + " " + dsg.getDayType() + " " + dsg.getFrom() + "  " + dsg.getTo() + " " + id);
			
			psDoor.executeUpdate();

			return true;
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}finally{
			
			try {
				psDoor.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected static ObservableList<String> fillGuestStaffList(){
		
		Connection conn = null;
		PreparedStatement psStaff = null, psStaff2 = null;
		ResultSet rsStaff = null, rsStaff2 = null;
		ObservableList<String> items =FXCollections.observableArrayList();
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			psStaff = conn.prepareStatement("SELECT * FROM Guests WHERE Type = 32 AND IssuedTo <> 0");
			rsStaff = psStaff.executeQuery();
			if (rsStaff.isBeforeFirst()){
				while (rsStaff.next()){
					psStaff2 = conn.prepareStatement("SELECT * FROM Staff WHERE id = ?");
					psStaff2.setInt(1, rsStaff.getInt(4));
					rsStaff2 = psStaff2.executeQuery();
					if (rsStaff2.next())
						items.add(rsStaff2.getString(2));
				}
				
				FXCollections.sort(items);
			}
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				psStaff.close();
				psStaff2.close();
				rsStaff.close();
				rsStaff2.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		return items;
	}
	
	protected static ObservableList<String> fillGuestFamiliesList(){
		
		Connection conn = null;
		PreparedStatement psStaff = null, psStaff2 = null;
		ResultSet rsStaff = null, rsStaff2 = null;
		ObservableList<String> items =FXCollections.observableArrayList();
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			psStaff = conn.prepareStatement("SELECT * FROM Guests WHERE Type = 31 AND IssuedTo <> 0");
			rsStaff = psStaff.executeQuery();
			if (rsStaff.isBeforeFirst()){
				while (rsStaff.next()){
					psStaff2 = conn.prepareStatement("SELECT * FROM Family WHERE id = ?");
					psStaff2.setInt(1, rsStaff.getInt(4));
					rsStaff2 = psStaff2.executeQuery();
					if (rsStaff2.next())
						items.add(rsStaff2.getString(2));
				}
				
				FXCollections.sort(items);
			}
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				rsStaff.close();
				rsStaff2.close();
				psStaff.close();
				psStaff2.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		return items;
	}
	
	public static ObservableList<String> fillGuestsList(){
		
		PreparedStatement pstate = null;
		ResultSet rs = null;
		Set<Integer> idSet = new HashSet<Integer>();
		Time timeIn, timeOut;
		ObservableList<String> map = FXCollections.observableArrayList();
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			pstate = conn.prepareStatement("SELECT * FROM GuestVisits WHERE CONVERT (date,TimeIN) = CONVERT (date, GETDATE()) ORDER BY id", ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = pstate.executeQuery();
			if (rs.isBeforeFirst()){
				while (rs.next()){
					
					idSet.add(rs.getInt(2));
					
				}
				
				for (int id : idSet){
					
					rs.afterLast();
					while(rs.previous()){
						
						if (rs.getInt(2) == id){
							
							timeIn = rs.getTime(6);
							timeOut = rs.getTime(7);
							
							if (timeOut.before(timeIn)){
								
								map.add(rs.getString(3) + " card#" + findGuestCardID(rs.getInt(2)));
								

							}
							break;
						}
						
					}
					
				}
			
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			
			try {
				rs.close();
				pstate.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return map;
		
	}
	
	protected static Boolean addNewSpecTempDoorSchedule(DoorSchedule dsg, int id){

		PreparedStatement psDoor = null;
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			

			try {
				psDoor = conn.prepareStatement("INSERT INTO DoorSpecTempAccess VALUES (?, ?, GetDate(), ?, ?)");
				psDoor.setInt(1, Integer.valueOf(dsg.getFrom()));
				
				int idH = 0;
				
				switch (Integer.valueOf(dsg.getFrom())){
				
				case 1 : idH = findFamilyIdByName(dsg.getName());
						 break;
				case 2 : idH = findStaffIdByName(dsg.getName());
						 break;
				case 3 : idH = Integer.valueOf(dsg.getName());
						 break;
				case 4 : idH = findStudentIdByName(dsg.getName());
				 break;
				
				}
				
				psDoor.setInt(2, idH);
				
				int diffHours = Integer.valueOf(dsg.getTo()) / 60;
				int diffMinutes = Integer.valueOf(dsg.getTo()) % 60;
				
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, diffHours);
				cal.set(Calendar.MINUTE, diffMinutes);
				java.sql.Timestamp sqlDate = new java.sql.Timestamp(cal.getTimeInMillis());
				psDoor.setTimestamp(3, sqlDate);
				psDoor.setInt(4, id);
				psDoor.executeUpdate();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}finally{
			
			try {
				psDoor.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected static ObservableList<DoorSchedule> fillDoorSpecTempSecGroupTable(int doorID){

		PreparedStatement psDoor = null;
		ResultSet rsDoor = null;
		ObservableList<DoorSchedule> items =FXCollections.observableArrayList();
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			psDoor = conn.prepareStatement("SELECT * FROM DoorSpecTempAccess WHERE DoorID = ?");
			psDoor.setInt(1, doorID);
			rsDoor = psDoor.executeQuery();
			if (rsDoor.isBeforeFirst()){
				while (rsDoor.next()){
					
					String name = "";
					String type = "";
					
					switch (rsDoor.getInt(2)){
					
						case 1 : name = findFamilyNameById(rsDoor.getInt(3));
								 type = "Family";
								 break;
						case 2 : name = findStaffById(rsDoor.getInt(3));
								 type = "Staff";
								 break;
						case 3 : name = findGuestCardHolder(findGlobalIDofGuestCard(rsDoor.getInt(3))) + " #" + rsDoor.getInt(3);
								 type = "Guest";
								 break;
						case 4 : name = findStudentById(rsDoor.getInt(3));
						 		 type = "Student";
						 break;
					
					
					}
					
					
					items.add(new DoorSchedule(name, rsDoor.getTimestamp(4).toString().substring(11, 16), rsDoor.getTimestamp(5).toString().substring(11, 16), type));
				}
				
				items.sort(new Comparator<DoorSchedule>(){

				    @Override
				    public int compare(final DoorSchedule o1, final DoorSchedule o2){
				        // let your comparator look up your car's color in the custom order
				        return o1.getName().compareTo(o2.getName());
				    }
				});
			}
			
		} catch (SQLException e) {
		e.printStackTrace();
		}finally{
			
			try {
				rsDoor.close();
				psDoor.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return items;
	}
	
	protected static Boolean removeDoorTempSchedule(DoorSchedule dsg, int id){

		PreparedStatement psDoor = null;
		Connection conn = null;
		
		try {
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			psDoor = conn.prepareStatement("DELETE FROM DoorSpecTempAccess WHERE HolderType = ? AND HolderID = ? AND DoorID = ?");
			psDoor.setInt(1, Integer.valueOf(dsg.getDayType()));
			
			int idH = 0;
			
			switch (Integer.valueOf(dsg.getDayType())){
			
			case 1 : idH = findFamilyIdByName(dsg.getName());
					 break;
			case 2 : idH = findStaffIdByName(dsg.getName());
					 break;
			case 3 : idH = Integer.valueOf(dsg.getName());
					 break;
			case 4 : idH = findStudentIdByName(dsg.getName());
			 		 break;
			
			}
			
			psDoor.setInt(2, idH);
			psDoor.setInt(3, id);
			
			psDoor.executeUpdate();
			
			return true;
			
			
		} catch (SQLException e) {

			e.printStackTrace();
			
			return false;
		}finally{
			
			try {
				psDoor.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	////////////////////////////// SPECIAL CARDS //////////////////////////////////////////////
	
	protected static ObservableList<SpecialCard> fillSpecCardList(){
		
		Connection conn;
		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		PreparedStatement psSpecCard = null;
		ResultSet rsSpecCard = null;
		ObservableList<SpecialCard> items =FXCollections.observableArrayList();
		
		try {
			
			psSpecCard = conn.prepareStatement("SELECT * FROM SpecialCards ORDER BY id");
			rsSpecCard = psSpecCard.executeQuery();
			if (rsSpecCard.isBeforeFirst()){
				while (rsSpecCard.next()){
					items.add(new SpecialCard(rsSpecCard.getInt(1), rsSpecCard.getString(2), rsSpecCard.getInt(3)));
				}
				
			}
			
		} catch (SQLException e) {
		e.printStackTrace();
		}
		finally{
			
			try {
				rsSpecCard.close();
				psSpecCard.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		return items;
	}
	
	protected static Boolean addCardtoSpecialProfile(String cardNumber, int specialID){
		
		PreparedStatement ps = null;
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
		
			ps = conn.prepareStatement("INSERT INTO Cards " + "VALUES (?, ?, ?)");
			ps.setString(1, cardNumber);
			ps.setInt(2, specialID);
			ps.setInt(3, 5);
			ps.executeUpdate();
			
			return true;
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected static ObservableList<String> findCardsBySpecialProfile(int specialID){
		
		PreparedStatement psCardID = null;
		ResultSet rsCardID = null;
		ObservableList<String> items =FXCollections.observableArrayList();
		
		Connection conn;
	
		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
			
			psCardID = conn.prepareStatement("select * from Cards where HolderID = ? AND HolderType = 5");
			psCardID.setInt(1, specialID);
			rsCardID = psCardID.executeQuery();
		    while (rsCardID.next()) {
		       items.add(rsCardID.getString(2));
		    }
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		finally{
			
			try {
				rsCardID.close();
				psCardID.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		return items;
	
	}
	
	protected static Boolean doesSpecialProfileExist(String profileName){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
			
			ps = conn.prepareStatement("select * from SpecialCards where Description = ?");
			ps.setString(1, profileName);
			rs = ps.executeQuery();
			
			return rs.isBeforeFirst();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		} 
		finally{
			
			try {
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	protected static Boolean insertNewSpecialProfile(SpecialCard card){
		PreparedStatement ps = null;
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
		
			ps = conn.prepareStatement("INSERT INTO SpecialCards " + "VALUES (?, ?)");
			ps.setString(1, card.getName());
			ps.setInt(2, card.getSecurityGroup());
			ps.executeUpdate();
			
			return true;
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	
	protected static Boolean updateSpecialProfile(SpecialCard card){
		PreparedStatement psUpdate = null;;
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		try{
			psUpdate = conn.prepareStatement("UPDATE SpecialCards SET Description = ?, SecurityGroup = ? WHERE id = ?");

			psUpdate.setString(1, card.getName());
			psUpdate.setInt(2, card.getSecurityGroup());
			psUpdate.setInt(3, card.getId());
			psUpdate.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally{
			
			try {
				psUpdate.close();
				conn.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	protected static Boolean deleteProfileRecord(int profileID){
			
			PreparedStatement psDelete = null;
			PreparedStatement psDeleteCards = null;
			Connection conn;
			
			conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
			
			try{
				psDelete = conn.prepareStatement("DELETE FROM SpecialCards WHERE id = ?");
				psDelete.setInt(1, profileID);
				psDelete.executeUpdate();
				
				psDeleteCards = conn.prepareStatement("DELETE FROM Cards WHERE HolderID = ? AND HolderType = 5");
				psDeleteCards.setInt(1, profileID);
				psDeleteCards.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			} 
			finally{
				
				try {
					psDelete.close();
					psDeleteCards.close();
					conn.close();
				} catch (SQLException e) {
					// 
					e.printStackTrace();
				}
				
			}
			
		}
	
	
	protected static String findStudentById(int id){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Connection conn;

		conn = dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		try {
			
			ps = conn.prepareStatement("select * from Students where id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()){
			
				return rs.getString(2);
				
			}
			else{
				
				return "Not Found";
				
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return "Not Found";
		} 
		finally{
			
			try {
				conn.close();
				rs.close();
				ps.close();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
			
		}
		
	}
	
}
