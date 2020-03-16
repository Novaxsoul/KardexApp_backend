package kardexapi.entities;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table(name="product")
@NamedQuery(name="Product.findAll", query="SELECT p FROM Product p inner join p.categ c where p.status <> false and c.status <> false")
@NamedQuery(name="Product.findCount", query="SELECT count(p) FROM Product p inner join p.categ c where p.status <> false and c.status <> false")
public class Product implements Serializable{

	public Product(int id, String name, int cant, Category categ, boolean status) {
		super();
		this.id = id;
		this.name = name;
		this.cant = cant;
		this.categ = categ;
		this.status = status;
	}

	private static final long serialVersionUID = 1L;
	
	public Product() {
		
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="cant")
	private int cant;
	
	@ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Category categ;
	
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

	public int getCant() {
		return cant;
	}

	public void setCant(int cant) {
		this.cant = cant;
	}

	public Category getCateg() {
		return categ;
	}

	public void setCateg(Category categ) {
		this.categ = categ;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
}
