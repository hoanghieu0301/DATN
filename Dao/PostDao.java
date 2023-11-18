package com.poly.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import com.poly.entity.Order;
import com.poly.entity.Post;


public interface PostDao extends   JpaRepository<Post, Integer>{
	
	
	
	@Query(value="Select  * from Posts where Posts.Status='true'   ",nativeQuery = true)
	List<Post> findAllStatus();

	@Query(value = "select * from Posts where Post_id like %:kw% or \r\n"
			+ " Post_Name like %:kw%  "  , nativeQuery = true)
	List<Post> finbyIdOrName(@Param("kw") String keyword);

	@Query(value="select * from Posts where CreateDate between ?1 and ?2 \\r\\n"
			
			+ "and Status like %?3%  \r\n"
			+ "post by Post_id desc",nativeQuery = true)
	List<Order> findByAllKeyWord(@DateTimeFormat(pattern="yyyy-MM-dd")Date MinDay , 
			@DateTimeFormat(pattern="yyyy-MM-dd")Date MaxDay ,
		@Param("Status")  String Status );
	
}
