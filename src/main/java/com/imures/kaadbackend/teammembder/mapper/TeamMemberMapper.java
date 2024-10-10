package com.imures.kaadbackend.teammembder.mapper;


import com.imures.kaadbackend.teammembder.controller.request.TeamMemberRequest;
import com.imures.kaadbackend.teammembder.controller.response.TeamMemberResponse;
import com.imures.kaadbackend.teammembder.entity.TeamMember;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TeamMemberMapper {

    TeamMemberResponse fromEntityToResponse(TeamMember entity);

    TeamMember fromRequestToEntity(TeamMemberRequest request);
}
