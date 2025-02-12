package com.cisowski.schoolmanagement.users.parent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddParentResponse extends ParentDetailedResponse {

    private String password;

}
