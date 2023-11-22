package com.poly.entity;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;


import lombok.Data;


@SuppressWarnings("serial")
@Data
@Entity 
@Table(name = "Products")
public class Product implements Serializable {
	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer Product_id;
	String Name;
	@Transient
	String Image1;
	@Transient
	String Image2 = null;
	@Transient
	String Image3 = null;
	@Transient
	String Image4 = null;
	@Transient
	String Image5 = null;
	@Transient
	String Image6 = null;
	@Transient
	String Image7 = null;
	@Transient
	String Detail ;
	@Transient
	String Description;
	@Transient
	String Chip;
	@Transient
	String Ram;
	@Transient
	String Rom;
	@Transient
	String Resolution;
	@Transient
	String Pin;
	Double Unit_price;
	Integer Quantity;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	Date Product_date = new Date();
	Double Distcount ;
	Boolean Status = false;
	Boolean Special = false;
	Boolean Lastest = false;
	@ManyToOne
	@JoinColumn(name = "Category_id")
	Category category;
	@ManyToOne
	@JoinColumn(name = "Trademark_id")
	Trademark trademark;
	@JsonIgnore
	@OneToMany(mappedBy = "product")
	List<OrderDetail> orderDetails;

//	@OneToMany(mappedBy = "product")
//	List<ImageProduct> imageProducts;
//
//
//	@OneToOne(mappedBy = "product")
//	private ProductDetail productDetail;
	
}
