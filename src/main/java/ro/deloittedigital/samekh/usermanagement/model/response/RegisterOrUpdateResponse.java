package ro.deloittedigital.samekh.usermanagement.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RegisterOrUpdateResponse {
    @ApiModelProperty(example = "jane.doe@mail.com")
    private String email;

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
