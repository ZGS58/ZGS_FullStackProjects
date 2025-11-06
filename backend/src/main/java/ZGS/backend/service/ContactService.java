package ZGS.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ZGS.backend.dto.ContactDto;
import ZGS.backend.entity.Contact;
import ZGS.backend.mapper.ContactMapper;
import ZGS.backend.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    public List<Contact> getAll() {
        return contactRepository.findAll();
    }

    public Contact save(ContactDto dto) {
        Contact contact = contactMapper.toEntity(dto);
        return contactRepository.save(contact);
    }

    public void delete(Long id) {
        contactRepository.deleteById(id);
    }

    public Page<Contact> getPaged(Pageable pageable) {
        return contactRepository.findAll(pageable);
    }
}