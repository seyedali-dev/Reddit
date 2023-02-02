package com.project.helper.payload.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private @NotEmpty String username;
    private @NotEmpty @Email String email;

    @Pattern(regexp = "^" + //start of line
                      "(?=.*[0-9])" + //0-9 numbers
                      "(?=.*[a-z])" + //a-z letters
                      "(?=.*[A-Z])" + //A-Z letters
                      "(?=.*[!@#&()–[{}]:;',.?/*~$^+=<>])" + // one of the special characters
                      "." + // matches anything
                      "{3,20}" +// minimum and maximum length of password
                      "$" // end of line
            , message = "Pass must be between 3-20 characters & must contain a-z, A-Z, 0-9 and special characters.")
    private @NotEmpty String password;
}