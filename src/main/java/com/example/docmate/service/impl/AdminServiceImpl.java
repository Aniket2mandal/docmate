package com.example.docmate.service.impl;

import com.example.docmate.entity.RoleEntity;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.RoleRequest;
import com.example.docmate.payload.response.RoleResponse;
import com.example.docmate.repository.RoleRepository;
import com.example.docmate.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

    public GlobalResponse createRole(RoleRequest role) {
        RoleEntity roleEntity = modelMapper.map(role, RoleEntity.class);
        roleRepository.save(roleEntity);
        return GlobalResponseBuilder.buildSuccessResponse("Role is Created");
    }

    public GlobalResponse getAllRole() {
        List<RoleEntity> roleEntityList = roleRepository.findAll();
        List<RoleResponse> roleResponseList = roleEntityList.stream()
                .map(role -> modelMapper.map(role, RoleResponse.class))
                .toList();
        return GlobalResponseBuilder.buildSuccessResponseWithData("All role fetched",roleResponseList);
    }
}
