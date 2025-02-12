package com.cisowski.schoolmanagement.users.common.model;

import lombok.Data;

@Data
public class AddressRequest {

    private String city;
    private String street;
    private String buildingNumber;
    private String voivodeship;
    private String zipCode;

}
