package com.cybertek.service;

import com.cybertek.dto.RoleDTO;
import com.cybertek.exception.TicketingProjectException;

import java.util.List;

public interface RoleService {
    List<RoleDTO> listAllRoles();

    RoleDTO findById(long id) throws TicketingProjectException;


}
