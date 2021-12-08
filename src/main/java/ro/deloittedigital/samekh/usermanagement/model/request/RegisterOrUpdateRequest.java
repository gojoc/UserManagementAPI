package ro.deloittedigital.samekh.usermanagement.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString(exclude = "password")
public class RegisterOrUpdateRequest {
    @ApiModelProperty(example = "jane.doe@mail.com")
    private String email;

    @ApiModelProperty(example = "password")
    private String password;

    @ApiModelProperty(example = "Jane")
    private String firstName;

    @ApiModelProperty(example = "Doe")
    private String lastName;

    @ApiModelProperty(example = "jane.doe")
    private String username;

    @ApiModelProperty(example = "female")
    private String gender;

    private LocalDate birthday;
}
