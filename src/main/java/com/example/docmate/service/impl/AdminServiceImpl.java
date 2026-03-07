package com.example.docmate.service.impl;

import com.example.docmate.entity.RoleEntity;
import com.example.docmate.entity.UserEntity;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.RoleRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.payload.response.RoleResponse;
import com.example.docmate.repository.RoleRepository;
import com.example.docmate.repository.UserRepository;
import com.example.docmate.service.AdminService;
import com.example.docmate.utils.MyConstants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

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

    @Override
    public GlobalResponse changeStatus(UserRequest user, String userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
        userEntity.setStatus(user.getStatus());
        userRepository.save(userEntity);
        return GlobalResponseBuilder.buildSuccessResponse("User status changed");
    }
}
