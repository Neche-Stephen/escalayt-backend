package com.sq022groupA.escalayt.payload.request;
import com.sq022groupA.escalayt.entity.model.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEditUserRequestDto {


    private Long id;

    private String email;

    private String username;

    private String jobTitle;

    private String password;

    /*
 employeeId: employeeId,
      email: emailRef.current.value,
      username: usernameRef.current.value,
      jobTitle: jobTitleRef.current.value,
      departmentId: departmentId,
      password: passwordRef.current.value,
 */
}
