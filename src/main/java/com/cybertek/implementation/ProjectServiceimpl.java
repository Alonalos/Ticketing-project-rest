package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.Project;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.mapper.MapperUtil;
import com.cybertek.mapper.ProjectMapper;
import com.cybertek.mapper.UserMapper;
import com.cybertek.repository.ProjectRepository;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceimpl implements ProjectService {

    private ProjectRepository projectRepository;
    private MapperUtil mapperUtil;
    private UserService userService;
    private TaskService taskService;
    private UserRepository userRepository;


    public ProjectServiceimpl(ProjectRepository projectRepository, MapperUtil mapperUtil, UserService userService, TaskService taskService, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.mapperUtil = mapperUtil;
        this.userService = userService;
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @Override
    public ProjectDTO getByProjectCode(String code) {
        Project project = projectRepository.findByProjectCode(code);
        return mapperUtil.convert(project,new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> list = projectRepository.findAll(Sort.by("projectCode"));
        return list.stream().map(obj -> mapperUtil.convert(obj, new ProjectDTO())).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO save(ProjectDTO dto) throws TicketingProjectException {
        Project foundProject=projectRepository.findByProjectCode(dto.getProjectCode());

        if(foundProject!=null){
            throw new TicketingProjectException("Project with this code already exist@");
        }

        Project obj=mapperUtil.convert(dto, new Project());

        Project createdProject=projectRepository.save(obj);

        return mapperUtil.convert(createdProject,new ProjectDTO());

    }

    @Override
    public ProjectDTO update(ProjectDTO dto) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(dto.getProjectCode());

        if(project==null){
            throw new TicketingProjectException("Project does not exist");
        }
        Project convertedProject = mapperUtil.convert(dto, new Project());
        Project updatedProject=projectRepository.save(convertedProject);
        return mapperUtil.convert(updatedProject,new ProjectDTO());
    }

    public void delete(String code) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(code);
        if(project==null){
            throw new TicketingProjectException("Project does not exist");
        }

        project.setIsDeleted(true);
        //project.setProjectCode(project.getProjectCode()+"-"+project.getId());

        projectRepository.save(project);

        taskService.deleteByProject(mapperUtil.convert(project,new ProjectDTO()));

    }

    @Override
    public ProjectDTO complete(String projectCode) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(projectCode);

        if(project==null){
            throw new TicketingProjectException("Project does not exist");
        }

        project.setProjectStatus(Status.COMPLETE);
        Project completedProject=projectRepository.save(project);
        return mapperUtil.convert(completedProject,new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() throws AccessDeniedException, TicketingProjectException {
        String id= SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentId=Long.parseLong(id);

        User user=userRepository.findById(currentId).orElseThrow(()->new TicketingProjectException("This manager does not exist"));

        List<Project>list=projectRepository.findByAssignedManager(user);

        if(list.size()==0){
            throw new TicketingProjectException("This manager does not have any projects assigned");
        }


        return list.stream().map(project -> {
            ProjectDTO obj = mapperUtil.convert(project,new ProjectDTO());
            obj.setUnfinishedTaskCounts(taskService.totalNonCompletedTasks(project.getProjectCode()));
            obj.setCompleteTaskCounts(taskService.totalCompletedTasks(project.getProjectCode()));
            return obj;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> readAllByAssignedManager(User user) {
        List<Project> list = projectRepository.findByAssignedManager(user);
        return list.stream().map(obj->mapperUtil.convert(obj,new ProjectDTO())).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedProjects() {

        return projectRepository.findAllByProjectStatusIsNot(Status.COMPLETE)
                .stream()
                .map(project->mapperUtil.convert(project,new ProjectDTO()))
                .collect(Collectors.toList());
    }

}
