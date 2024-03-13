package com.spring.mvc;


import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mvc.entity.Board;
import com.mvc.entity.Member;
import com.mvc.mapper.BoardMapper;
import com.mvc.mapper.MemberMapper;

@Controller
public class BoardController {
	
	@Autowired
	BoardMapper mapper;
	
	@Autowired
	MemberMapper memMapper;
	
	@ModelAttribute("cp")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}
	
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
	
	@GetMapping("/loginForm.do")
	public String loginForm() {
		return "loginForm";
	}
	
	@PostMapping("/loginPro.do")
	public String loginPro(Model model, Member mvo, HttpSession session) {
		System.out.println("id = " + mvo.getMemID());
		System.out.println("pw = " + mvo.getMemPassword());
		String id = mvo.getMemID();
		int check = memMapper.loginCheck(mvo);
		System.out.println("check = " + check);
		session.setAttribute("log", id);
		model.addAttribute("check" ,check);
		return "loginPro";
	}
	
	@GetMapping("/logoutPro.do")
	public String logoutPro(HttpSession session) {
		session.invalidate();
		return "logoutPro";
	}
	
	@GetMapping("/joinForm.do")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/joinPro.do")
	public String joinPro(Member mvo) {
		memMapper.join(mvo);
		return "template";
	}
}







