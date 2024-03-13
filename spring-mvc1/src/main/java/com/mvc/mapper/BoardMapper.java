package com.mvc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mvc.entity.Board;

@Mapper
public interface BoardMapper {
	public List<Board> getList();
	public Board getOneContent(int idx);
	public void upCount(int idx);
	public void insertBoard(Board vo);
	public void boardUpdate(Board vo);
	public void deleteBoard(int idx);
}
