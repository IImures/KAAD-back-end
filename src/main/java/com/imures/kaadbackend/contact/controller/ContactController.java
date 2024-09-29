package com.imures.kaadbackend.contact.controller;

import com.imures.kaadbackend.contact.controller.request.ContactRequest;
import com.imures.kaadbackend.contact.controller.response.ContactResponse;
import com.imures.kaadbackend.contact.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/contact")
@RequiredArgsConstructor
public class ContactController {


    private final ContactService contactService;


//    @GetMapping
//    public ResponseEntity<List<ContactResponse>> getAllContact() {
//        return ResponseEntity.ok().body(contactService.getAllContacts());
//    }

    @GetMapping
    public ResponseEntity<Page<ContactResponse>> getContacts(
            @RequestParam(
                    value = "page",
                    required = false,
                    defaultValue = "0"
            ) int page,
            @RequestParam(
                    value = "limit",
                    required = false,
                    defaultValue = "20"
            ) int limit,
            @RequestParam(
                    value = "sort",
                    required = false,
                    defaultValue = "createdAt"
            ) String sortBy,
            @RequestParam(
                    value ="types",
                    required = false
            ) String types
    ) {
        Pageable pageRequest = PageRequest.of(page, limit, Sort.by(sortBy));
        Page<ContactResponse> response;
        if(types==null || types.isEmpty()){
            response = contactService.findAll(pageRequest);
        }else{
            response = contactService.findAllWithContactTypes(pageRequest, types);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "{contactId}")
    public ResponseEntity<ContactResponse> getContactById(
            @PathVariable Long contactId
    ) {
        return ResponseEntity.ok().body(contactService.getContactById(contactId));
    }

    @PostMapping
    public ResponseEntity<Void> createContact(
            @RequestBody ContactRequest contactRequest
    ) throws BadRequestException {
        contactService.createContact(contactRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(path = "{contactId}")
    public ResponseEntity<ContactResponse> updateContact(
            @RequestBody ContactRequest contactRequest,
            @PathVariable Long contactId
    ){
        return ResponseEntity.ok(contactService.updateContact(contactRequest, contactId));
    }

    @DeleteMapping(path = "{contactId}")
    public ResponseEntity<Void> deleteContact(
            @PathVariable Long contactId
    ){
        contactService.deleteContact(contactId);
        return ResponseEntity.noContent().build();
    }


}
