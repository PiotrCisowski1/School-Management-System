package com.cisowski.schoolmanagement.services;

import com.cisowski.schoolmanagement.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.model.entity.Address;
import com.cisowski.schoolmanagement.repository.AddressRepository;
import com.cisowski.schoolmanagement.service.impl.AddressServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddressServiceTests {

    @Mock
    AddressRepository addressRepository;
    @InjectMocks
    AddressServiceImpl addressService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("addAddress - successful")
    public void addAddress_shouldReturnSavedAddress() throws IOException {
        Address targetAddress = objectMapper.readValue(
                new File("src/test/resources/Address.json"), Address.class);

        when(addressRepository.save(any(Address.class))).thenReturn(targetAddress);

        Address createdAddress = addressService.addAddress(targetAddress);

        verify(addressRepository, times(1)).save(targetAddress);
        Assert.assertNotNull(createdAddress);
        Assert.assertEquals(targetAddress, createdAddress);
    }
    @Test
    @DisplayName("updateAddress - successful")
    public void updateAddress_shouldReturnUpdatedEntity() throws IOException {
        Address savedAddress = objectMapper.readValue(
                new File("src/test/resources/Address.json"), Address.class);
        Address targetUpdatedAddress = savedAddress;
        targetUpdatedAddress.setCity("XYZ");

        when(addressRepository.findById(savedAddress.getId())).thenReturn(Optional.of(savedAddress));
        when(addressRepository.save(targetUpdatedAddress)).thenReturn(targetUpdatedAddress);

        Address updatedAddress = addressService.updateAddress(targetUpdatedAddress);

        Assert.assertNotNull(updatedAddress);
        Assert.assertEquals(targetUpdatedAddress,updatedAddress);
    }

    @Test(expected = EntityNotFoundException.class)
    @DisplayName("updateAddress with non existing object - should throw EntityNotFoundException")
    public void updateAddress_entityNotFoundEx() throws IOException{
        Address address = objectMapper.readValue(
                new File("src/test/resources/Address.json"), Address.class);

        when(addressRepository.findById(any())).thenReturn(Optional.empty());

        addressService.updateAddress(address);

        verify(addressRepository,times(1)).findById(any());
    }

    @Test
    @DisplayName("deleteAddress - successful")
    public void deleteAddress_shouldDeleteEntity() throws IOException{
        Address existingAddress = objectMapper.readValue(
                new File("src/test/resources/Address.json"), Address.class);

        when(addressRepository.findById(existingAddress.getId())).thenReturn(Optional.of(existingAddress));

        addressService.deleteAddress(existingAddress.getId());

        verify(addressRepository, times(1)).findById(existingAddress.getId());
        verify(addressRepository, times(1)).delete(existingAddress);
    }

    @Test(expected = EntityNotFoundException.class)
    @DisplayName("deleteAddress with non existing object - should throw EntityNotFoundException")
    public void deleteAddress_entityNotFoundEx() throws IOException{
        Integer adrId = 123;

        when(addressRepository.findById(any())).thenReturn(Optional.empty());

        addressService.deleteAddress(adrId);

        verify(addressRepository,times(1)).findById(any());
    }

    @Test
    @DisplayName("findAddressById - successful")
    public void findAddressById_shouldReturnEntity() throws IOException{
        Address existingAddress = objectMapper.readValue(
                new File("src/test/resources/Address.json"), Address.class);

        when(addressRepository.findById(any())).thenReturn(Optional.of(existingAddress));

        Address foundAddress = addressService.findById(existingAddress.getId());

        Assert.assertNotNull(foundAddress);
        Assert.assertEquals(existingAddress,foundAddress);
    }

    @Test(expected = EntityNotFoundException.class)
    @DisplayName("findAddressById with non existing ID - should throw EntityNotFoundException")
    public void findAddressById_entityNotFoundEx() {
        Integer adrId = 123;

        when(addressRepository.findById(any())).thenReturn(Optional.empty());

        addressService.findById(adrId);

        verify(addressRepository,times(1)).findById(any());
    }

    @Test
    @DisplayName("findAddresses - successful")
    public void findAddresses_shouldReturnCollectionOfAddress() {
        when(addressRepository.findAll()).thenReturn(Collections.emptyList());

        Collection<Address> addresses = addressService.findAll();

        verify(addressRepository, times(1)).findAll();
        Assert.assertNotNull(addresses);
        Assert.assertTrue(addresses.isEmpty());
    }






}
