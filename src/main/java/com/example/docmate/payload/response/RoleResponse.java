package com.example.docmate.payload.response;

import com.example.docmate.enums.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponse {
    private String id;
    private Role name;
}
