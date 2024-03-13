package kr.board.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.board.entity.Member;
import kr.board.mapper.MemberMapper;

// 이거 붙이면 /member 붙인채로 들어옴
@RequestMapping("/member")
@Controller
public class MemberController {

	@Autowired
	MemberMapper memberMapper;
	
	@ModelAttribute("cp")
	public String getContextPath(Model model , HttpServletRequest request) {		
		model.addAttribute("cp", request.getContextPath());	
		return request.getContextPath();
	}
	
	@GetMapping("/memLoginForm.do")
	public String loginForm() {
		return "member/memLoginForm";
	}
	
	// model 객체는 request 객체를 forward 할 때만 값을 전달해준다
	// RedirectAttributes => redirect: 할 때 값을 들고 가고 새로 고침하면 값이 사라진다
	@PostMapping("/memLogin.do")
	public String memLogin(Member m , RedirectAttributes rttr, HttpSession session) {
		
		System.out.println("memLogin m = " + m);
		if(m.getMemID() == null || m.getMemID().equals("") ||
				m.getMemPassword() == null || m.getMemPassword().equals("")) {
			
			// addFlashAttribute -> 새로고침하면 사라짐
			rttr.addFlashAttribute("msgType" , " 로그인 실패 ");
			rttr.addFlashAttribute("msg", "모든 값을 넣어주세요");
			
			return "redirect:/member/memLoginForm.do";
		}
		
		Member mvo = memberMapper.memLogin(m);
		
		// 로그인 실패
		if(mvo == null) {
			rttr.addFlashAttribute("msgType" , " 로그인 실패 ");
			rttr.addFlashAttribute("msg", "회원 정보가 없습니다");
			
			return "redirect:/member/memLoginForm.do";
		}
		
		// 로그인 성공
		session.setAttribute("mvo", mvo);
		rttr.addFlashAttribute("msgType" , " 로그인 성공 ");
		rttr.addFlashAttribute("msg", "로그인 성공 했습니다");
		return "redirect:/";
	}
	
	@GetMapping("/memLogout.do")
	public String memLogout(RedirectAttributes rttr, HttpSession session) {
		session.invalidate();
		rttr.addFlashAttribute("msgType" , " 로그아웃 성공 ");
		rttr.addFlashAttribute("msg", "로그아웃 성공 했습니다");
		return "redirect:/";
	}
	
	@GetMapping("/memJoin.do")
	public String memJoin() {
		return "/member/join";
	}
	
	@PostMapping("/memRegister.do")					// @ModelAttribute			//@RequestParam(value="memPassword1")
	public String memRegister(RedirectAttributes rttr, Member m, String memPassword1, String memPassword2) {
		memberMapper.register(m);
		rttr.addFlashAttribute("msgType" , " 회원가입 성공 ");
		rttr.addFlashAttribute("msg", "회원가입 성공 했습니다");
		return "redirect:/";
	}
	
}
