package com.imures.kaadbackend.teammembder.service;

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
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberMapper teamMemberMapper;



    @Transactional
    public TeamMemberResponse createMember(TeamMemberRequest teamMemberRequest, MultipartFile image) throws IOException {
        TeamMember teamMember = new TeamMember();

        teamMember.setFirstName(teamMemberRequest.getFirstName());
        teamMember.setLastName(teamMemberRequest.getLastName());
        Optional.ofNullable(teamMemberRequest.getEmail()).ifPresent(teamMember::setEmail);
        Optional.ofNullable(teamMemberRequest.getPhone()).ifPresent(teamMember::setPhone);

        teamMember.setImgType(image.getContentType());
        teamMember.setImgName(image.getName());
        teamMember.setImageData(image.getBytes());

        TeamMember saved = teamMemberRepository.save(teamMember);
        return teamMemberMapper.fromEntityToResponse(saved);
    }

    @Transactional(readOnly = true)
    public TeamMemberResponse getMember(Long memberId) {
        TeamMember teamMember = teamMemberRepository
                .findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("TeamMember with id %s not found", memberId)));

        return teamMemberMapper.fromEntityToResponse(teamMember);
    }

    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getMembers() {
        return teamMemberRepository.findAll()
                .stream().map(teamMemberMapper::fromEntityToResponse)
                .toList();
    }

    @Transactional
    public TeamMemberResponse updateMember(TeamMemberRequest teamMemberRequest, MultipartFile image, Long memberId) throws IOException {
        TeamMember teamMember = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("TeamMember with id %s not found", memberId)));

        Optional.ofNullable(teamMemberRequest.getFirstName()).ifPresent(teamMember::setFirstName);
        Optional.ofNullable(teamMemberRequest.getLastName()).ifPresent(teamMember::setLastName);
        Optional.ofNullable(teamMemberRequest.getEmail()).ifPresent(teamMember::setEmail);
        Optional.ofNullable(teamMemberRequest.getPhone()).ifPresent(teamMember::setPhone);

        if(image != null){
            teamMember.setImgType(image.getContentType());
            teamMember.setImgName(image.getName());
            teamMember.setImageData(image.getBytes());
        }

        return teamMemberMapper.fromEntityToResponse(teamMemberRepository.save(teamMember));
    }

    @Transactional
    public void deleteMember(Long memberId) {
        TeamMember teamMember = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("TeamMember with id %s not found", memberId)));
        teamMemberRepository.delete(teamMember);
    }
}
