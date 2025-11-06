package ZGS.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import ZGS.backend.dto.UserDto;
import ZGS.backend.entity.Role;
import ZGS.backend.entity.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)       // 不從 DTO 複製角色
    @Mapping(target = "password", ignore = true)    // 不從 DTO 複製密碼
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    User toEntity(UserDto dto);

    @Mapping(target = "roleNames", source = "roles", qualifiedByName = "rolesToRoleNames")
    UserDto toDto(User entity);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    void updateEntityFromDto(UserDto dto, @MappingTarget User entity);

    @Named("rolesToRoleNames")
    default List<String> rolesToRoleNames(Set<Role> roles) {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}
