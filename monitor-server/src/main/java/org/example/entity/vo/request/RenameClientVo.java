package org.example.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RenameClientVo {

    @NotNull
    int id;

    @Length(min = 1, max = 10)
    String name;
}
