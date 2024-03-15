package kr.board.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

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
	
	@GetMapping("/memUpdateForm.do")
	public String updateForm() {
		return "member/memUpdateForm";
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
	public String memRegister(RedirectAttributes rttr, Member m, String memPassword1, String memPassword2 , HttpSession session) {
		
		System.out.println("m = " +m);
		System.out.println("=== memRegister ===");
		//m.setMemPassword(memPassword1);
		if(!m.nullValueCheck()) {
			rttr.addFlashAttribute("msgType" , " 실패 메세지 ");
			rttr.addFlashAttribute("msg", "모든 값을 넣어주세요");
			return "redirect:/member/memJoin.do";
		}
		if(!memPassword1.equals(memPassword2)) {
			rttr.addFlashAttribute("msgType" , " 실패 메세지 ");
			rttr.addFlashAttribute("msg", "패스워드 값이 서로 다릅니다");
			return "redirect:/member/memJoin.do";
		}
		
		m.setMemPassword(memPassword1);
		m.setMemProfile(""); // 사진이 없다는 의미
		
		int result = memberMapper.register(m);
		if(result == 1) {
			rttr.addFlashAttribute("msgType" , " 회원가입 성공 ");
			rttr.addFlashAttribute("msg", "회원가입 성공 했습니다");
			session.setAttribute("mvo", m);
			return "redirect:/";
		} else {
			rttr.addFlashAttribute("msgType" , " 실패 메세지 ");
			rttr.addFlashAttribute("msg", "회원가입 실패 했습니다");
			return "redirect:/member/memRegister.do";
		}
	}
	
	@GetMapping("/memRegisterCheck.do")
	public @ResponseBody int memRegisterCheck(String memID) {
		System.out.println("memRegisterCheck memID = " +memID);
		Member member = memberMapper.registerCheck(memID);
		return member == null ? 1 : 0;
	}
	
	@PostMapping("/memUpdate.do")
	public String memUpdate(RedirectAttributes rttr, Member member , String memPassword1 , String memPassword2, HttpSession session) {
		
		if(!member.nullValueCheck()) {
			rttr.addFlashAttribute("msgType" , " 실패 메세지 ");
			rttr.addFlashAttribute("msg", "모든 값을 넣어주세요");
			return "redirect:/member/memUpdateForm.do";
		}
		
		if(!memPassword1.equals(memPassword2)) {
			rttr.addFlashAttribute("msgType" , " 실패 메세지 ");
			rttr.addFlashAttribute("msg", "패스워드 값이 서로 다릅니다");
			return "redirect:/member/memUpdateForm.do";
		}
		member.setMemPassword(memPassword1);
		int result = memberMapper.memUpdate(member);
		System.out.println("m = " +member);
		System.out.println("result = " + result);
		if(result == 1) {
			rttr.addFlashAttribute("msgType" , " 성공 메세지 ");
			rttr.addFlashAttribute("msg", "회원정보 수정 완료");
			session.setAttribute("mvo", member);
			return "redirect:/";
		} else {
			rttr.addFlashAttribute("msgType" , " 실패 메세지 ");
			rttr.addFlashAttribute("msg", "회원정보 수정 실패");
			return "redirect:/member/memUpdateForm.do";
		}
	}
	
	
	// 회원 사진 등록 
	@GetMapping("/memImageForm.do")
	public String memImageForm() {
		return "/member/memImageForm";
	}
	
	@PostMapping("/memImageUpdate.do")
	public String memImageUpdate(HttpServletRequest request, HttpSession session, RedirectAttributes rttr) {
		MultipartRequest multi = null;
		int result = 0;
		int fileMaxSize = 10*1024*1024; // 10MB
		String savePath = request.getSession().getServletContext().getRealPath("resources/upload");
		Path uploadDirectory = Paths.get(savePath);
		if(!Files.exists(uploadDirectory)) { // 업로드 폴더 없으면 생성 
			try {
				Files.createDirectory(uploadDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// 이미지 업로드 
		try {
			multi = new MultipartRequest(request, savePath, fileMaxSize, "UTF-8" , new DefaultFileRenamePolicy());
			
			String memID = multi.getParameter("memID");
			Member mvo = memberMapper.getMember(memID);
			if(mvo == null) {
				return "redirect:/";
			}
			
			File file = multi.getFile("memProfile");
			if(file.exists()) {
				System.out.println("저장완료 ");
				System.out.println("저장 경로 " + savePath);
				
				String ext = file.getName().substring(file.getName().lastIndexOf(".")+1);
				ext = ext.toUpperCase(); // png, PNG, jpg, JPG 등등
				
				// 이미지 확장자가 아니면 되돌아가기
				if(!(ext.equals("PNG") || ext.equals("JPG"))) {
					
					rttr.addFlashAttribute("msgType" , " 실패 메세지 ");
					rttr.addFlashAttribute("msg", " 이미지 사진만 업로드 가능합니다 ");
					return "redirect:/member/memImageForm.do";
				} 
				
				String newProfile = file.getName();
				String oldProfile = mvo.getMemProfile(); // 기존 이미지 파일
				
				// 기존에 이미지 파일이 있다면 삭제
				File oldFile = new File(savePath + "/" + oldProfile);
				
				if(oldFile.exists()) {
					oldFile.delete();
				}
				
				mvo.setMemProfile(newProfile);
				result = memberMapper.memProfileUpdate(mvo);
				System.out.println("이미지 업로드 mvo = " +mvo);
			}
			
			// db 이미지 성공 후 
			if(result == 1) {
				session.setAttribute("mvo", mvo);
				
				rttr.addFlashAttribute("msgType" , " 성공 메세지 ");
				rttr.addFlashAttribute("msg", " 이미지 등록 성공 ");
				return "redirect:/";
			}
			
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		
		
		
		return "redirect:/";
	}
	
}
