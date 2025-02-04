package com.cisowski.schoolmanagement.service;

import com.cisowski.schoolmanagement.model.entity.Address;

import java.util.Collection;

public interface AddressService {
    Address addAddress(Address address);
    Address updateAddress(Address address);
    void deleteAddress(Integer addressId);
    Address findById(Integer addressId);
    Collection<Address> findAll();
}
