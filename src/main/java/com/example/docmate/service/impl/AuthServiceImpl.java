package com.example.docmate.service.impl;

import com.example.docmate.entity.RoleEntity;
import com.example.docmate.entity.UserEntity;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.repository.RoleRepository;
import com.example.docmate.repository.UserRepository;
import com.example.docmate.service.AuthService;
import com.example.docmate.utils.MyConstants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public GlobalResponse registerAdmin(UserRequest user){

        if(user.getRoleId() != null){
            RoleEntity roleEntity = roleRepository.findById(user.getRoleId())
                    .orElseThrow(()-> new GlobalException("Role "+MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
        }

        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        userRepository.save(userEntity);

        return GlobalResponseBuilder.buildSuccessResponse("User registered successfully");
    }
}
