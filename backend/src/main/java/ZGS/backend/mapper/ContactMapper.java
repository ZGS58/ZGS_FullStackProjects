package ZGS.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ZGS.backend.dto.ContactDto;
import ZGS.backend.entity.Contact;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    ContactDto toDto(Contact contact);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Contact toEntity(ContactDto dto);

}