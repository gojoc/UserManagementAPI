package ro.deloittedigital.samekh.usermanagement.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = "password")
public class LoginRequest {
    @ApiModelProperty(example = "jane.doe@mail.com")
    private String email;

    @ApiModelProperty(example = "password")
    private String password;
}
