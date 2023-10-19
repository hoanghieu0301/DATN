package com.poly.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "roles")
public class Role {
	@Id
	private String Role_id;
	private String Name;
	@JsonIgnore
	@OneToMany(mappedBy = "role")
	List<Authority> Authorities;
}
