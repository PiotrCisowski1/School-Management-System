package com.cisowski.schoolmanagement.model.request;

import lombok.Data;

@Data
public class AddressRequest {

    private String city;
    private String street;
    private String buildingNumber;
    private String voivodeship;
    private String zipCode;

}
