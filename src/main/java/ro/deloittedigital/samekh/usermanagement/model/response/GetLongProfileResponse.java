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
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GetLongProfileResponse extends GetShortProfileResponse {
    @ApiModelProperty(example = "jane.doe@mail.com")
    private String email;

    @ApiModelProperty(example = "female")
    private String gender;

    private LocalDate birthday;

    @Builder
    public GetLongProfileResponse(String email, String firstName, String lastName, String gender, LocalDate birthday) {
        super(firstName, lastName);
        this.email = email;
        this.gender = gender;
        this.birthday = birthday;
    }
}
