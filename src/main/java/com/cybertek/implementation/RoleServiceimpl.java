package com.cybertek.implementation;

import com.cybertek.dto.RoleDTO;
import com.cybertek.entity.Role;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.util.MapperUtil;
import com.cybertek.repository.RoleRepository;
import com.cybertek.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceimpl implements RoleService {

    private MapperUtil mapperUtil;
    private RoleRepository roleRepository;

    @Override
    public List<RoleDTO> listAllRoles() {
        List<Role> list = roleRepository.findAll();
        return list.stream()
                .map(obj ->mapperUtil.convert(obj,new RoleDTO()))
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO findById(long id) throws TicketingProjectException {
        Role role = roleRepository.findById(id).orElseThrow(()-> new TicketingProjectException("Role does not exist"));
        return mapperUtil.convert(role,new RoleDTO());
    }
}
