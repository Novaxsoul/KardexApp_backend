package kardexapi.entities;

import java.io.Serializable;
import java.sql.Date;

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

import org.apache.johnzon.mapper.JohnzonConverter;



@Entity
@Table(name="kardex")
@NamedQuery(name="Kardex.findAll", query="SELECT k FROM Kardex k")
@NamedQuery(name="Kardex.findCount", query="SELECT count(k) FROM Kardex k")
public class Kardex implements Serializable{
	

	private static final long serialVersionUID = 1L;
	
	public Kardex() {
		
	}
	
	public Kardex(int id, int transType, int units, float unitCost, float totalCost, Date transDate, Product prod) {
		super();
		this.id = id;
		this.transType = transType;
		this.units = units;
		this.unitCost = unitCost;
		this.totalCost = totalCost;
		this.transDate = transDate;
		this.prod = prod;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="transType")
	private int transType;
	
	@Column(name="units")
	private int units;
	
	@Column(name="unitCost")
	private float unitCost;
	
	@Column(name="totalCost")
	private float totalCost;
	
	@Column(name="transDate")
	private java.sql.Date transDate;
	
	@ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Product prod;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTransType() {
		return transType;
	}

	public void setTransType(int transType) {
		this.transType = transType;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public float getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(float unitCost) {
		this.unitCost = unitCost;
	}

	public float getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(float totalCost) {
		this.totalCost = totalCost;
	}
	
	@JohnzonConverter(LocalDateConverter.class)
	public java.sql.Date getTransDate() {
		return transDate;
	}
	
	public void setTransDate(java.sql.Date transDate) {
		this.transDate = transDate;
	}

	public Product getProd() {
		return prod;
	}

	public void setProd(Product prod) {
		this.prod = prod;
	}
	
	
	
	
	
}
