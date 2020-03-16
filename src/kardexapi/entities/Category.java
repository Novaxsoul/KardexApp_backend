package kardexapi.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="category")
@NamedQuery(name="Category.findAll", query="SELECT c FROM Category c where c.status <> false")
@NamedQuery(name="Category.findCount", query="SELECT count(c) FROM Category c where c.status <> false")
public class Category implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public Category() {
		
	}
	
	
	public Category(int id, String name, boolean status) {
		this.id = id;
		this.name = name;
		this.status = status;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="status")
	private boolean status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
	
}
