package com.spring.mvc;


import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mvc.entity.Board;
import com.mvc.mapper.BoardMapper;

@Controller
public class BoardController {
	
	@Autowired
	BoardMapper mapper;
	
	@GetMapping("/")
	public String main() {
		return "template";
	}
	
	@GetMapping("/boardList.do")
	public String boardList(Model model) {
		ArrayList<Board> list = (ArrayList<Board>)mapper.getList();
		model.addAttribute("list", list);
		
		System.out.println("listSize = " + list.size());
		return "boardList";
	}
	
	@GetMapping("/boardContent.do")
	public String boardContent(int idx, Model model) {
		Board vo = mapper.getOneContent(idx);
		vo.setContent(vo.getContent().replace("\n", "<br/>"));
		mapper.upCount(idx);
		model.addAttribute("vo" , vo);
		return "boardContent";
	}
	
	@GetMapping("/boardForm.do")
	public String boardForm() {
		return "boardForm";
	}
	
	@PostMapping("/boardInsert.do")
	public String boardInsert(Board vo) {
		mapper.insertBoard(vo);
		return "redirect:/boardList.do";
	}
	
	@GetMapping("/boardUpdateForm.do/{idx}")
	public String updateForm(@PathVariable int idx, Model model) {
		Board vo = mapper.getOneContent(idx);
		model.addAttribute("vo" , vo);
		return "boardUpdate";
	}
	
	@PostMapping("/boardUpdate.do")
	public String boardUpdate(Board vo) {
		mapper.boardUpdate(vo);
		return "redirect:/boardList.do";
	}
	
	@GetMapping("/boardDelete.do/{idx}")
	public String deleteBoard(@PathVariable int idx) {
		mapper.deleteBoard(idx);
		return "redirect:/boardList.do";
	}
	
}







