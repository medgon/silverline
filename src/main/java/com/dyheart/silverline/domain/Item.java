package com.dyheart.silverline.domain;


import javax.persistence.*;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_description")
    private String itemDescription;
    
    @Column(name = "item_quantity")
    private Integer quantity; 
    
    @Column(name = "location")
    private String location;


    public Item() {

    }


	public Long getItemId() {
		return itemId;
	}


	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}


	public String getItemName() {
		return itemName;
	}


	public void setItemName(String itemName) {
		this.itemName = itemName;
	}


	public String getItemDescription() {
		return itemDescription;
	}


	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}


	public Integer getQuantity() {
		return quantity;
	}


	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	@Override
	public String toString() {
		return "Item [itemId=" + itemId + ", itemName=" + itemName + ", itemDescription=" + itemDescription
				+ ", quantity=" + quantity + ", location=" + location + "]";
	}

    
}
