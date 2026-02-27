package com.example.docmate.service.impl;

import com.example.docmate.entity.RoleEntity;
import com.example.docmate.enums.Role;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.repository.RoleRepository;
import com.example.docmate.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    public GlobalResponse createRole(Role name) {
        RoleEntity roleEntity =modelMapper.map(name, RoleEntity.class);
        roleRepository.save(roleEntity);
        return GlobalResponseBuilder.buildSuccessResponse("Role is Created");
    }
}
