package ZGS.backend.controller;

import ZGS.backend.BaseResponse;
import ZGS.backend.dto.ContactDto;
import ZGS.backend.entity.Contact;
import ZGS.backend.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    // 前端使用者送出聯絡表單
    @PostMapping
    public ResponseEntity<BaseResponse<Contact>> create(@Valid @RequestBody ContactDto dto) {
        Contact saved = contactService.save(dto);
        return ResponseEntity.ok(BaseResponse.ok(saved));
    }

    // admin查詢所有聯絡資料
    @GetMapping
    public ResponseEntity<BaseResponse<List<Contact>>> getAll() {
        List<Contact> contacts = contactService.getAll();
        return ResponseEntity.ok(BaseResponse.ok(contacts));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable Long id) {
        contactService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("資料已刪除"));
    }

    @GetMapping("/paged")
    public ResponseEntity<BaseResponse<Page<Contact>>> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<Contact> contactPage = contactService.getPaged(PageRequest.of(page, size));
        return ResponseEntity.ok(BaseResponse.ok(contactPage));
    }

}