package com.cisowski.schoolmanagement.users.parent.controller;

import com.cisowski.schoolmanagement.users.parent.model.AddParentResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentDetailedResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentSummaryResponse;
import com.cisowski.schoolmanagement.users.parent.service.ParentService;
import com.cisowski.schoolmanagement.users.parent.model.ParentCreateRequest;
import com.cisowski.schoolmanagement.users.parent.model.ParentPatchRequest;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/parents")
public class ParentController {

    private final ParentService parentService;

    public ParentController(ParentService parentService) {
        this.parentService = parentService;
    }

    @PostMapping
    public ResponseEntity<AddParentResponse> addParent(@Valid @RequestBody ParentCreateRequest parentDto){
        String message = "Received Parent POST request for object: " + parentDto.toString();
        DbLogger.info(message);

        AddParentResponse savedParent = parentService.addParent(parentDto);
        return new ResponseEntity<>(savedParent, HttpStatus.CREATED);
    }

    @PatchMapping("/{parentId}")
    public ResponseEntity<ParentDetailedResponse> updateParent(@Valid @RequestBody ParentPatchRequest request, @PathVariable Integer parentId){
        String message = String.format("Received Parent PUT request for object: %s", request.toString());
        DbLogger.info(message);

        ParentDetailedResponse updatedParent = parentService.updateParent(request, parentId);
        return new ResponseEntity<>(updatedParent, HttpStatus.OK);
    }

    @DeleteMapping("/{parentId}")
    public ResponseEntity deleteParent(@PathVariable Integer parentId){
        String message = String.format("Received Parent DELETE request for ID: %s", parentId);
        DbLogger.info(message);

        parentService.deleteUser(parentId);
        return new ResponseEntity(HttpStatusCode.valueOf(204));
    }

    @GetMapping("/{parentId}")
    public ResponseEntity<ParentDetailedResponse> getParent(@PathVariable Integer parentId){
        String message = String.format("Received Parent GET request for ID: %s", parentId);
        DbLogger.info(message);

        ParentDetailedResponse parentResponse = parentService.findById(parentId);
        return new ResponseEntity<>(parentResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<ParentSummaryResponse>> getParents(){
        String message = "Received Parent GET request";
        DbLogger.info(message);

        Collection<ParentSummaryResponse> parentResponses = parentService.findAll();
        return new ResponseEntity<>(parentResponses, HttpStatus.OK);
    }
}
