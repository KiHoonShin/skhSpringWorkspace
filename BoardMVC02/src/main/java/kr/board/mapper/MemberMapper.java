package kr.board.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.board.entity.Member;

@Mapper
public interface MemberMapper {
	
	public Member memLogin(Member member);
	public void register(Member member);
	public Member registerCheck(String memID);
}
