package com.mvc.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.mvc.entity.Member;

@Mapper
public interface MemberMapper {
	public int loginCheck(Member mvo);
	public void join(Member mvo);
}
