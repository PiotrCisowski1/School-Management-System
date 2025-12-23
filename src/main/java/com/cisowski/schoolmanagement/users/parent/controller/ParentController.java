package com.cisowski.schoolmanagement.users.parent.controller;

import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<AddParentResponse> addParent(@Valid @RequestBody ParentCreateRequest parentDto){
        String message = "Received Parent POST request for object: " + parentDto.toString();
        DbLogger.info(message);

        AddParentResponse savedParent = parentService.addParent(parentDto);
        return new ResponseEntity<>(savedParent, HttpStatus.CREATED);
    }

    @PatchMapping("/{parentId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<ParentDetailedResponse> updateParent(@Valid @RequestBody ParentPatchRequest request, @PathVariable Integer parentId){
        String message = String.format("Received Parent PUT request for object: %s", request.toString());
        DbLogger.info(message);

        ParentDetailedResponse updatedParent = parentService.updateParent(request, parentId);
        return new ResponseEntity<>(updatedParent, HttpStatus.OK);
    }

    @DeleteMapping("/{parentId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity deleteParent(@PathVariable Integer parentId){
        String message = String.format("Received Parent DELETE request for ID: %s", parentId);
        DbLogger.info(message);

        parentService.deleteUser(parentId);
        return new ResponseEntity(HttpStatusCode.valueOf(204));
    }

    @GetMapping("/{parentId}")
    @RequiresPermission(action = ResourceActionType.READ, resource = ResourceType.PARENT)
    public ResponseEntity<ParentDetailedResponse> getParent(@PathVariable Integer parentId){
        String message = String.format("Received Parent GET request for ID: %s", parentId);
        DbLogger.info(message);

        ParentDetailedResponse parentResponse = parentService.findById(parentId);
        return new ResponseEntity<>(parentResponse, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<Collection<ParentSummaryResponse>> getParents(){
        String message = "Received Parent GET request";
        DbLogger.info(message);

        Collection<ParentSummaryResponse> parentResponses = parentService.findAll();
        return new ResponseEntity<>(parentResponses, HttpStatus.OK);
    }
}
