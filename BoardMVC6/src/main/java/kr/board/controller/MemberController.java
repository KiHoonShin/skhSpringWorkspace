package kr.board.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import kr.board.entity.AuthVO;
import kr.board.entity.Member;
import kr.board.entity.MemberUser;
import kr.board.mapper.MemberMapper;
import kr.board.security.MemberUserDetailsService;

// 이거 붙이면 /member 붙인채로 들어옴
@RequestMapping("/member")
@Controller
public class MemberController {

	@Autowired
	MemberMapper memberMapper;
	
	@Autowired
	PasswordEncoder pwEncoder; // 암호화 할 수 있는 객체
	
	@Autowired
	MemberUserDetailsService memberUserDetailsService;
	
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
	
//	@GetMapping("/memLogout.do")
//	public String memLogout(RedirectAttributes rttr, HttpSession session) {
//		session.invalidate();
//		rttr.addFlashAttribute("msgType" , " 로그아웃 성공 ");
//		rttr.addFlashAttribute("msg", "로그아웃 성공 했습니다");
//		return "redirect:/";
//	}
	
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
		
		m.setMemPassword(memPassword1); // 암호화 전 패스워드
		m.setMemProfile(""); // 사진이 없다는 의미
		
		String encyptPw = pwEncoder.encode(m.getMemPassword());
		System.out.println("encyptPw = " + encyptPw);
		
		m.setMemPassword(encyptPw);
		
		// 멤버 회원 추가 됨
		int result = memberMapper.register(m);
		
		if(result == 1) {
			
			// 멤버 생성 후 권한 테이블 생성
			List<AuthVO> list = m.getAuthList();
			for(AuthVO vo : list) {
				if(vo.getAuth() != null) {
					AuthVO auth = new AuthVO();
					auth.setMemID(m.getMemID());
					auth.setAuth(vo.getAuth());
					System.out.println("auth = " + auth);
					memberMapper.authInsert(auth);
				}
			}
			
			rttr.addFlashAttribute("msgType" , " 회원가입 성공 ");
			rttr.addFlashAttribute("msg", "회원가입 성공 했습니다");
			//session.setAttribute("mvo", m);
			return "redirect:/member/memLoginForm.do";
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
	
	
	// 회원 정보 수정
	
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
		System.out.println("update m = " +member);
		
		// 비번 암호화
		member.setMemPassword(pwEncoder.encode(member.getMemPassword()));
		
		int result = memberMapper.memUpdate(member);
		System.out.println("result = " + result);
		if(result == 1) {
			rttr.addFlashAttribute("msgType" , " 성공 메세지 ");
			rttr.addFlashAttribute("msg", "회원정보 수정 완료");
			
			// 기존 권한 다 삭제
			memberMapper.authDelete(member.getMemID());
			
			// 다시 새로운 권한 추가
			List<AuthVO> list = member.getAuthList();
			for(AuthVO vo : list) {
				if(vo.getAuth() != null) {
					AuthVO auth = new AuthVO();
					auth.setMemID(member.getMemID());
					auth.setAuth(vo.getAuth());
					memberMapper.authInsert(auth);
				}
			}
			
			// 회원 정보 업데이트 된 세션으로 재등록
			//session.setAttribute("mvo", member);
			
			// 새로운 인증 세션을 생성
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			MemberUser userAccount = (MemberUser)authentication.getPrincipal();
			SecurityContextHolder.getContext().setAuthentication(createNewAuthentication(authentication, userAccount.getMember().getMemID()));
			
			
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
				//session.setAttribute("mvo", mvo);
				
				// 새로운 인증 세션을 생성
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				MemberUser userAccount = (MemberUser)authentication.getPrincipal();
				SecurityContextHolder.getContext().setAuthentication(createNewAuthentication(authentication, userAccount.getMember().getMemID()));
				
				rttr.addFlashAttribute("msgType" , " 성공 메세지 ");
				rttr.addFlashAttribute("msg", " 이미지 등록 성공 ");
				return "redirect:/";
			}
			
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		return "redirect:/";
	}
	
	 protected Authentication createNewAuthentication(Authentication currentAuth, String username) {
        UserDetails newPrincipal = memberUserDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(newPrincipal, currentAuth.getCredentials(), newPrincipal.getAuthorities());
        newAuth.setDetails(currentAuth.getDetails());        
        return newAuth;
	 	}
}
