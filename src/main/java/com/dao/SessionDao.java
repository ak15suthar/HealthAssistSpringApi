package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.bean.DoctorProfileBean;
import com.bean.UserBean;

@Repository
public class SessionDao {

	@Autowired
	JdbcTemplate stmt;

	// patient,pathology,pharma,doctor
	public int insertUser(UserBean userBean) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		String insertSql = "insert into users (email,password,firstname,lastname,gender,roleid,status,statusReason,otp) values(?,?,?,?,?,?,?,?,?)";
		 
		
	 
		stmt.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {

				PreparedStatement pstmt = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1,userBean.getEmail());
				pstmt.setString(2, userBean.getPassword());
				pstmt.setString(3,userBean.getFirstName());
				pstmt.setString(4,userBean.getLastName());
				pstmt.setString(5,userBean.getGender());
				pstmt.setInt(6,userBean.getRoleId());
				pstmt.setInt(7,userBean.getStatus());
				pstmt.setString(8,userBean.getStatusReason());
				pstmt.setString(9,userBean.getOtp());


				return pstmt;
			}
		}, keyHolder);

		int userId = (Integer) keyHolder.getKeys().get("userid");	
		userBean.setUserId(userId);
		return userBean.getUserId();
	}

	public void addDoctorProfile(DoctorProfileBean doctorProfileBean) {
		UserBean userBean = new UserBean();
		userBean.setEmail(doctorProfileBean.getEmail());
		userBean.setFirstName(doctorProfileBean.getFirstName());
		// 
		
		int userId  = insertUser(userBean);
		doctorProfileBean.setUserId(userId);
		stmt.update(
				"insert into doctorprofile (userid,qualification,specialization,experience,profilepic,about,registrationno) values "
						+ "(?,?,?,?,?,?,?)",
				doctorProfileBean.getUserId(), doctorProfileBean.getQualification(),
				doctorProfileBean.getExperience_in_year(), doctorProfileBean.getProfile_pic(),
				doctorProfileBean.getAbout(), doctorProfileBean.getRegistrationNo());
	}

	public List<UserBean> listSignup() {

		List<UserBean> signupBean = stmt.query("select * from users",
				BeanPropertyRowMapper.newInstance(UserBean.class));

		return signupBean;
	}

	public void updateSignup(UserBean signupBean) {
		stmt.update(
				"update users set email = ?,password = ?,firstname = ?,lastname = ?,gender = ?,roleid = ? where userid = ?",
				signupBean.getEmail(), signupBean.getPassword(), signupBean.getFirstName(), signupBean.getLastName(),
				signupBean.getGender(), signupBean.getRoleId(), signupBean.getUserId());

	}

	public void deleteSignup(int userId) {
		stmt.update("delete from users where userid = ?", userId);

	}

	public UserBean login(String email, String password) {

		UserBean signupBean = stmt.queryForObject("select * from users where email=? and password=?",
				new Object[] { email, password }, BeanPropertyRowMapper.newInstance(UserBean.class));

		return signupBean;
	}

}
