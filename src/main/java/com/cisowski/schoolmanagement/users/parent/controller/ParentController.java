package com.cisowski.schoolmanagement.users.parent.controller;

import com.cisowski.schoolmanagement.common.annotation.SecurityResponses;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/parents")
@Tag(name = "Parents", description = "Operations related to parental accounts, including student-parent linking and access to children's academic progress.")
public class ParentController {

    private final ParentService parentService;

    public ParentController(ParentService parentService) {
        this.parentService = parentService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Create parent",
            description = "Adds new parent profile related with children. Response includes first generated password. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Created successfully")
    @ApiResponse(responseCode = "404", description = "Children not found")
    @ApiResponse(responseCode = "406", description = "Authority with given ID not found")
    @ApiResponse(responseCode = "409", description = "User with given email already exists")
    public ResponseEntity<AddParentResponse> addParent(@Valid @RequestBody ParentCreateRequest parentDto){
        String message = "Received Parent POST request for object: " + parentDto.toString();
        DbLogger.info(message);

        AddParentResponse savedParent = parentService.addParent(parentDto);
        return new ResponseEntity<>(savedParent, HttpStatus.CREATED);
    }

    @PatchMapping("/{parentId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Update parent",
            description = "Modify parent profile and relation with children. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Modification successfull")
    @ApiResponse(responseCode = "404", description = "Parent not found with ID")
    @ApiResponse(responseCode = "404", description = "Child not found with ID")
    @ApiResponse(responseCode = "406", description = "Child association removal - child not linked with parent")
    public ResponseEntity<ParentDetailedResponse> updateParent(@Valid @RequestBody ParentPatchRequest request, @PathVariable Integer parentId){
        String message = String.format("Received Parent PUT request for object: %s", request.toString());
        DbLogger.info(message);

        ParentDetailedResponse updatedParent = parentService.updateParent(request, parentId);
        return new ResponseEntity<>(updatedParent, HttpStatus.OK);
    }

    @DeleteMapping("/{parentId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Delete parent",
            description = "Remove parent with given ID and unlink children. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Removed successfully")
    @ApiResponse(responseCode = "404", description = "Not found with ID")
    public ResponseEntity deleteParent(@PathVariable Integer parentId){
        String message = String.format("Received Parent DELETE request for ID: %s", parentId);
        DbLogger.info(message);

        parentService.deleteUser(parentId);
        return new ResponseEntity(HttpStatusCode.valueOf(204));
    }

    @GetMapping("/{parentId}")
    @RequiresPermission(action = ResourceActionType.READ, resource = ResourceType.PARENT)
    @SecurityResponses
    @Operation(
            summary = "Find parent with ID",
            description = "Retrieves existing parent with given ID. Required authority level: Administrator, Teacher, Student, Parent")
    @ApiResponse(responseCode = "200", description = "Returns existing object")
    @ApiResponse(responseCode = "404", description = "Not found with ID")
    public ResponseEntity<ParentDetailedResponse> getParent(@PathVariable Integer parentId){
        String message = String.format("Received Parent GET request for ID: %s", parentId);
        DbLogger.info(message);

        ParentDetailedResponse parentResponse = parentService.findById(parentId);
        return new ResponseEntity<>(parentResponse, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Find all parents",
            description = "Retrieve all existing parents in the system. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns existing objects list (empty result as well)")
    public ResponseEntity<Collection<ParentSummaryResponse>> getParents(){
        String message = "Received Parent GET request";
        DbLogger.info(message);

        Collection<ParentSummaryResponse> parentResponses = parentService.findAll();
        return new ResponseEntity<>(parentResponses, HttpStatus.OK);
    }
}
