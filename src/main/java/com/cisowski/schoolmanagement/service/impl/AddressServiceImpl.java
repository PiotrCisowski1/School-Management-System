package com.cisowski.schoolmanagement.service.impl;

import com.cisowski.schoolmanagement.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.model.entity.Address;
import com.cisowski.schoolmanagement.repository.AddressRepository;
import com.cisowski.schoolmanagement.service.AddressService;
import com.cisowski.schoolmanagement.utility.DbLogger;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional
    public Address addAddress(Address address) {
        String message = "Add Address for entity: " +address.toString();
        DbLogger.info(message);

        Address savedAddress = addressRepository.save(address);

        message = "Returning saved address: " +savedAddress.toString();
        DbLogger.info(message);

        return savedAddress;
    }

    @Override
    @Transactional
    public Address updateAddress(Address address) {
        String message = "Add Address for entity: " +address.toString();
        DbLogger.info(message);

        Optional<Address> existingAddress = addressRepository.findById(address.getId());
        if(existingAddress.isEmpty())
            throw new EntityNotFoundException(Address.class, "AddressId", address.getId().toString());

        Address updatedAddress = addressRepository.save(address);

        message = "Address updated successfully: " +address.toString();
        DbLogger.info(message);

        return updatedAddress;
    }

    @Override
    @Transactional
    public void deleteAddress(Integer addressId) {
        String message = "Starting delete Address function for AddressID: " + addressId.toString();
        DbLogger.info(message);

        Optional<Address> existingAddress = addressRepository.findById(addressId);
        if(existingAddress.isEmpty())
            throw new EntityNotFoundException(Address.class, "AddressId", addressId.toString());

        addressRepository.delete(existingAddress.get());

        message = String.format("Address with ID: %s, was deleted successfully", addressId);
        DbLogger.info(message);
    }

    @Override
    public Address findById(Integer addressId) {
        String message = "Searching for Address with ID: " + addressId.toString();
        DbLogger.info(message);

        Optional<Address> foundAddress = addressRepository.findById(addressId);
        if(foundAddress.isEmpty())
            throw new EntityNotFoundException(Address.class, "ID", addressId.toString());

        message = "Found Address with given ID: " + foundAddress.get().toString();
        DbLogger.info(message);
        return foundAddress.get();
    }

    @Override
    public Collection<Address> findAll() {
        String message = "Searching for all Address entities";
        DbLogger.info(message);

        return addressRepository.findAll();
    }

}
