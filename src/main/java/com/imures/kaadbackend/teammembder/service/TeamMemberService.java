package com.imures.kaadbackend.teammembder.service;

import com.imures.kaadbackend.generalinfo.controller.request.GeneralInfoRequest;
import com.imures.kaadbackend.generalinfo.controller.response.GeneralInfoResponse;
import com.imures.kaadbackend.generalinfo.entity.GeneralInfo;
import com.imures.kaadbackend.generalinfo.mapper.GeneralInfoMapperImpl;
import com.imures.kaadbackend.generalinfo.repository.GeneralInfoRepository;
import com.imures.kaadbackend.generalinfo.service.GeneralInfoService;
import com.imures.kaadbackend.language.entity.Language;
import com.imures.kaadbackend.language.repository.LanguageRepository;
import com.imures.kaadbackend.teammembder.controller.request.TeamMemberRequest;
import com.imures.kaadbackend.teammembder.controller.response.TeamMemberResponse;
import com.imures.kaadbackend.teammembder.entity.TeamMember;
import com.imures.kaadbackend.teammembder.mapper.TeamMemberMapper;
import com.imures.kaadbackend.teammembder.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberMapper teamMemberMapper;
    private final GeneralInfoService generalInfoService;
    private final GeneralInfoMapperImpl generalInfoMapper;
    private final LanguageRepository languageRepository;
    private final GeneralInfoRepository generalInfoRepository;


    @Transactional
    public TeamMemberResponse createMember(TeamMemberRequest teamMemberRequest, MultipartFile image) throws IOException {
        TeamMember teamMember = new TeamMember();

        teamMember.setFirstName(teamMemberRequest.getFirstName());
        teamMember.setLastName(teamMemberRequest.getLastName());
        teamMember.setPriority(teamMemberRequest.getPriority());
        Optional.ofNullable(teamMemberRequest.getEmail()).ifPresent(teamMember::setEmail);
        Optional.ofNullable(teamMemberRequest.getPhone()).ifPresent(teamMember::setPhone);

        teamMember.setImgName(image.getName());
        teamMember.setImageData(image.getBytes());

        TeamMember transTeamMember = teamMemberRepository.save(teamMember);

        transTeamMember.setDescriptions(new ArrayList<>());
        for(GeneralInfoRequest generalInfoRequest : teamMemberRequest.getDescription()){

            generalInfoRequest.setCode("tm_" + generalInfoRequest.getCode() + transTeamMember.getId());

            GeneralInfo generalInfo = generalInfoService.create(generalInfoRequest);
            transTeamMember.addGeneralInfo(generalInfo);
        }

        TeamMember saved = teamMemberRepository.save(transTeamMember);

        return teamMemberMapper.fromEntityToResponse(saved);
    }

    @Transactional(readOnly = true)
    public TeamMemberResponse getMember(Long memberId, String languageCode) {
        TeamMember teamMember = teamMemberRepository
                .findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("TeamMember with id %s not found", memberId)));

        TeamMemberResponse teamMemberResponse = teamMemberMapper.fromEntityToResponse(teamMember);
        GeneralInfo description = getMemberGeneralInfo(teamMember, languageCode);

        GeneralInfoResponse descriptionResponse = generalInfoMapper.fromEntityToResponse(description);
        teamMemberResponse.setDescription(descriptionResponse);
        return teamMemberResponse;
    }

    @Transactional(readOnly = true)
    protected GeneralInfo getMemberGeneralInfo(TeamMember teamMember, String languageCode) {
        return teamMember.getDescriptions().stream()
                .filter(generalInfo -> generalInfo.getLanguage().getCode().equals(languageCode))
                .findFirst()
                .orElseGet(() -> teamMember.getDescriptions().stream()
                        .filter( generalInfo -> generalInfo.getLanguage().getDefaultLanguage())
                        .findFirst().orElseGet(()-> teamMember.getDescriptions().get(0))
                );
    }

    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getMembers(String languageCode) {

        List<TeamMember> members = teamMemberRepository.findAll();
        List<TeamMemberResponse> memberResponses = new ArrayList<>();
        for(TeamMember teamMember : members){
            TeamMemberResponse teamMemberResponse = teamMemberMapper.fromEntityToResponse(teamMember);
            teamMemberResponse.setDescription(
                    generalInfoMapper.fromEntityToResponse(getMemberGeneralInfo(teamMember, languageCode))
            );
            memberResponses.add(teamMemberResponse);
        }
        memberResponses.sort(Comparator.comparingLong(TeamMemberResponse::getId));
        return memberResponses;
    }

    @Transactional
    public TeamMemberResponse updateMember(TeamMemberRequest teamMemberRequest, MultipartFile image, Long memberId) throws IOException {
        TeamMember teamMember = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("TeamMember with id %s not found", memberId)));

        Optional.ofNullable(teamMemberRequest.getFirstName()).ifPresent(teamMember::setFirstName);
        Optional.ofNullable(teamMemberRequest.getLastName()).ifPresent(teamMember::setLastName);
        Optional.ofNullable(teamMemberRequest.getEmail()).ifPresent(teamMember::setEmail);
        Optional.ofNullable(teamMemberRequest.getPhone()).ifPresent(teamMember::setPhone);
        Optional.ofNullable(teamMemberRequest.getPriority()).ifPresent(teamMember::setPriority);

        if(image != null){
            teamMember.setImgName(image.getName());
            teamMember.setImageData(image.getBytes());
        }

       if(teamMemberRequest.getDescription() != null){
           for(GeneralInfoRequest generalInfoRequest : teamMemberRequest.getDescription()){
               Language language = languageRepository.findById(generalInfoRequest.getLanguageId())
                               .orElseThrow(()-> new EntityNotFoundException(String.format("Language with id: %s not found", generalInfoRequest.getLanguageId())));

               try{
                   GeneralInfo generalInfo =  generalInfoRepository.findByCodeAndLanguage_Code(
                           generalInfoRequest.getCode(), language.getCode()
                           )
                           .orElseThrow(()-> new EntityNotFoundException(String.format("General info with code: %s not found and language: %s", generalInfoRequest.getCode(), language.getCode())));
                   teamMember.getDescriptions().remove(generalInfo);
                   generalInfoService.delete(generalInfo.getId());
               }catch (EntityNotFoundException ignored){

               }
               finally {
                   GeneralInfo generalInfo = new GeneralInfo();
                   generalInfo.setLanguage(language);
                   generalInfo.setCode(generalInfoRequest.getCode());
                   generalInfo.setContent(generalInfoRequest.getContent());
                   teamMember.addGeneralInfo(generalInfo);
               }
           }
       }

        return teamMemberMapper.fromEntityToResponse(teamMemberRepository.save(teamMember));
    }

    @Transactional
    public void deleteMember(Long memberId) {
        TeamMember teamMember = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("TeamMember with id %s not found", memberId)));
        teamMemberRepository.delete(teamMember);
    }

    public byte[] getMemberPhoto(Long memberId) {
        return teamMemberRepository
                .findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("TeamMember with id %s not found", memberId)))
                .getImageData();
    }
}
