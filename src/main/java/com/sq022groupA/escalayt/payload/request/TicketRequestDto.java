package com.sq022groupA.escalayt.payload.request;

import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequestDto {

    private String title;

    private String location;

    private Priority priority;

    private String description;

    // New field for the file
    private MultipartFile file;

    private String fileTitle;

}
