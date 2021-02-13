package com.cybertek.mapper;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.entity.Project;
import com.cybertek.repository.ProjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    private ModelMapper modelMapper;
    private ProjectRepository projectRepository;

    public ProjectMapper(ModelMapper modelMapper, ProjectRepository projectRepository) {
        this.modelMapper = modelMapper;
        this.projectRepository = projectRepository;
    }

    public Project converterToEntity(ProjectDTO dto) {
        return modelMapper.map(dto, Project.class);
    }

    public ProjectDTO convertEntityToDTO(Project entity) {
        return modelMapper.map(entity, ProjectDTO.class);
    }
}
