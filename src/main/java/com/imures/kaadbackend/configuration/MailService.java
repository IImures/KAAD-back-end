package com.imures.kaadbackend.configuration;

import com.imures.kaadbackend.contact.controller.response.ContactResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String username;

    @Value("${application.contact.mail}")
    private String contactMail;


    private final JavaMailSender javaMailSender;
    public void sendEmail(String toEmail,String subject,String body){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }

    public void sendContactEmail(ContactResponse response) {
        String subject = response.getFullName() + " prosi o pomoc z " + response.getSpecialization();

        String contactDetails = getContactDetails(response);

        String body = String.format(
                """
                        Potencjalny klient %S potrzebuje specjalisty w zakresie %S.\s
                        Preferuje kontakt przez %S i podał następujące dane kontaktowe: %s.
                        Obrany język kontaktu: %S""",
                response.getFullName(),
                response.getSpecialization(),
                response.getContactType(),
                contactDetails,
                response.getLanguage()
        );

        sendEmail(contactMail, subject, body);
    }

    private static String getContactDetails(ContactResponse response) {
        String contactDetails = "";
        if (response.getPhoneNumber() != null && !response.getPhoneNumber().isEmpty()) {
            contactDetails += response.getPhoneNumber();
        }
        if (response.getEmail() != null && !response.getEmail().isEmpty()) {
            if (!contactDetails.isEmpty()) {
                contactDetails += " i ";
            }
            contactDetails += response.getEmail();
        }
        if (contactDetails.isEmpty()) {
            contactDetails = "brak danych kontaktowych";
        }
        return contactDetails;
    }
}
