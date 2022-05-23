package com.automate.df.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DmsAddress {
	
    public String city;
    public String state;
    public String street;
    public String country;
    public String houseNo;
    public String isRural;
    public String isUrban;
    public String pincode;
    public String village;
    public String district;
    public String latitude;
    public String longitude;
    public String addressType; 
    public String preferredBillingAddress;
    

}
