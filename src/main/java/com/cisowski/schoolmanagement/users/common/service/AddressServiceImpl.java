package com.cisowski.schoolmanagement.users.common.service;

import com.cisowski.schoolmanagement.users.common.model.AddressEntity;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.users.common.repository.AddressRepository;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
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
    public AddressEntity addAddress(AddressEntity address) {
        String message = "Add Address for entity: " +address.toString();
        DbLogger.info(message);

        AddressEntity savedAddress = addressRepository.save(address);

        message = "Returning saved address: " +savedAddress.toString();
        DbLogger.info(message);

        return savedAddress;
    }

    @Override
    @Transactional
    public AddressEntity updateAddress(AddressEntity address) {
        String message = "Add Address for entity: " +address.toString();
        DbLogger.info(message);

        Optional<AddressEntity> existingAddress = addressRepository.findById(address.getId());
        if(existingAddress.isEmpty())
            throw new EntityNotFoundException(AddressEntity.class, "AddressId", address.getId().toString());

        AddressEntity updatedAddress = addressRepository.save(address);

        message = "Address updated successfully: " +address.toString();
        DbLogger.info(message);

        return updatedAddress;
    }

    @Override
    @Transactional
    public void deleteAddress(Integer addressId) {
        String message = "Starting delete Address function for AddressID: " + addressId.toString();
        DbLogger.info(message);

        Optional<AddressEntity> existingAddress = addressRepository.findById(addressId);
        if(existingAddress.isEmpty())
            throw new EntityNotFoundException(AddressEntity.class, "AddressId", addressId.toString());

        addressRepository.delete(existingAddress.get());

        message = String.format("Address with ID: %s, was deleted successfully", addressId);
        DbLogger.info(message);
    }

    @Override
    public AddressEntity findById(Integer addressId) {
        String message = "Searching for Address with ID: " + addressId.toString();
        DbLogger.info(message);

        Optional<AddressEntity> foundAddress = addressRepository.findById(addressId);
        if(foundAddress.isEmpty())
            throw new EntityNotFoundException(AddressEntity.class, "ID", addressId.toString());

        message = "Found Address with given ID: " + foundAddress.get().toString();
        DbLogger.info(message);
        return foundAddress.get();
    }

    @Override
    public Collection<AddressEntity> findAll() {
        String message = "Searching for all Address entities";
        DbLogger.info(message);

        return addressRepository.findAll();
    }

}
