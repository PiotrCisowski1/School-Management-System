package com.cisowski.schoolmanagement.users.common.service;

import com.cisowski.schoolmanagement.users.common.model.AddressEntity;

import java.util.Collection;

public interface AddressService {
    AddressEntity addAddress(AddressEntity address);
    AddressEntity updateAddress(AddressEntity address);
    void deleteAddress(Integer addressId);
    AddressEntity findById(Integer addressId);
    Collection<AddressEntity> findAll();
}
