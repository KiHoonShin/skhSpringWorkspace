package kr.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.board.entity.Board;

@Mapper // 기본 생성자 -> setter()
public interface BoardMapper {
	public List<Board> getLists();
	public int boardInsert(Board board);
	public Board boardContent(int idx);
	public int boardDelete(int idx);
	public int boardUpdate(Board board);
	public int upCount(int idx);
}
