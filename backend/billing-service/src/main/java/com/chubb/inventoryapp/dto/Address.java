package com.chubb.inventoryapp.dto;

import lombok.Data;

@Data
public class Address {
	private String line1;
    private String city;
    private String state;
    private String pincode;
}
