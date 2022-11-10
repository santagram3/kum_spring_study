package com.example.news;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServlet;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/news")
public class NewsController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private NewsDAO dao;

	// 이거 임포트 안되서 앞에 붙어버림
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	//application.properties 에서 news.imgdir 를 가져오려면 
	// @Value 어노테이션으로 가져온다 
	@Value("${news.imgdir}")
	String fdir;
	
	@Autowired
	public NewsController(NewsDAO dao) {
		this.dao=dao;
	}

	@PostMapping("/add")
	public String addNews(@ModelAttribute News news , Model m ,@RequestParam("file") MultipartFile file  ) {
		
		System.out.println("\n==========1\n");
		try {
			// 저장 파일 객체 생성 
			File dest = new File(fdir+"/"+file.getOriginalFilename());
			System.out.println("fdir : "+ fdir);
			System.out.println("dest : "+ dest);
			// 파일 저장 
			file.transferTo(dest);
			System.out.println("\n==========2\n");
			// news /객체에 파일 이름 저장 
			// 1번 
			//news.setImg(fdir+dest.getName());
			// 2번 
			news.setImg("/img/"+dest.getName()); // 이게 정답 .. 
			// 3번 
			//news.setImg("img/"+dest.getName());
			// 3번 
			//news.setImg("img/"+dest.getName());
			
			//news.setImg(fdir+dest.getName());
			System.out.println("fdir+dest.getName() : "+fdir+dest.getName());
			System.out.println("dest.getName() : "+ dest.getName());
			dao.addNews(news);
		
		} catch (Exception e) {
			System.out.println("\n==========3\n");
			e.printStackTrace();
			logger.info("뉴스 추가 과정에서 문제가 발생!");
			m.addAttribute("error","뉴스가 정상적으로 등록되지 않았습니다.");
		}
		return "redirect:/news/list";
		
	}


	@GetMapping("/list")
	public String listNews(Model m) {
			System.out.println("\n\n===================\n\n");
		try {
			List<News> newslist = dao.getAll();
			m.addAttribute("newslist",newslist);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("뉴스 불러오기 과정에서 문제가 발생!");
			m.addAttribute("error","뉴스가 정상적으로 불러오지 않았습니다.");
		}
		
		return "newsList";
	}

	@GetMapping("/getNews")
	   public String getNews(@RequestParam("aid") int aid, Model model) {
		
		///getNews?aid=123
	      News n = null;
	      try {
	         n = dao.getNews(aid);
	         System.out.println("\n\n\n");
	         System.out.println(n.getImg());
	         System.out.println(n.getContent());
	         System.out.println(n.getTitle());
	         System.out.println("\n\n\n");
	      } catch (SQLException e) {
	         e.printStackTrace();
	      }
	      
	      model.addAttribute("news",n);
	      
	      return "newsView";

	   }
	
	@GetMapping("/delete/{aid}")
	   public String deleteNews(@PathVariable int aid, Model m) {
	      // localhost:8989/news/delete/
		System.out.println("123123");
	      try {
	         dao.delNews(aid);
	      }catch(SQLException e) {
	         e.printStackTrace();
	         logger.warn("뉴스 삭제 과정에서 문제 발생!!");
	         m.addAttribute("error","뉴스가 정상적으로 삭제되지 않았습니다!!");
	      }
	      return "redirect:/news/list";
	   }


}
