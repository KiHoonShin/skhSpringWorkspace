package kr.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.board.entity.Board;
import kr.board.mapper.BoardMapper;

// @Controller -> 리턴을 뷰 리졸버로 넘김
@RequestMapping("/boards")
@RestController	// 리턴 값에 @ResponseBody 넘겨줌
public class BoardController {

	@Autowired
	BoardMapper boardMapper;
	
	@GetMapping("")
	public List<Board> boardList(){
		List<Board> list = boardMapper.getLists();
		return list;
	}
	
	@PostMapping("") // @RequestBody -> json으로 받기 위함
	public String addBoard(Board board) {
		String msg = board.toString();
		int result = boardMapper.boardInsert(board);
		return result == 1 ? "게시글 추가 완료" : "게시글 추가 실패" +msg;
	}
	
	@GetMapping("/{idx}") // 쿼리스트링은 ?idx=1 -> @RequestParam(기본값)
	public Board getOneBoard(@PathVariable int idx){
		Board board = boardMapper.boardContent(idx); // 조회할 값이 없으면 null
		return board;
	}
	
	@DeleteMapping("/{idx}")
	public String deleteOneBoard(@PathVariable int idx) {
		int result = boardMapper.boardDelete(idx);
		return result == 1 ? "게시글 삭제 완료" : "게시글 삭제 실패";
	}
	
	@PutMapping("/{idx}")
	public String updateOneBoard(@RequestBody Board board , @PathVariable int idx) {
		String msg = board.toString();
		board.setIdx(idx);
		int result = boardMapper.boardUpdate(board);
		return result == 1 ? "업데이트 완료" : "업데이트 실패"+msg;
	}
	
}
