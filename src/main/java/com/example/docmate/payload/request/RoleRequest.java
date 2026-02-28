package com.example.docmate.payload.request;

import com.example.docmate.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequest {
    private Role name;
}
