package site.book.project.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.domain.Notices;
import site.book.project.domain.Post;
import site.book.project.domain.User;
import site.book.project.dto.PostCreateDto;
import site.book.project.dto.PostListDto;
import site.book.project.dto.PostReadDto;
import site.book.project.dto.PostUpdateDto;
import site.book.project.dto.ReplyReadDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.UserRepository;
import site.book.project.service.BookService;
import site.book.project.service.NoticeService;
import site.book.project.service.PostService;
import site.book.project.service.ReplyService;
import site.book.project.service.UserService;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final BookService bookService;
    private final UserService userService;
    private final ReplyService replyService;
    private final UserRepository userRepository;
    private final NoticeService noticeService;   
    
    @Transactional(readOnly = true)
    @GetMapping("/list")
    public String list(@AuthenticationPrincipal UserSecurityDto userSecurityDto, String postWriter, Model model) {
     
        User user = null; 
        List<PostListDto> postList = new ArrayList<>();
        
        if (postWriter == null) {         
            user = userService.read(userSecurityDto.getId());        
            postList = postService.postDtoList(userSecurityDto.getId());
        } else if (postWriter != null) {
            user = userService.read(postWriter);
            postList = postService.postDtoList(user.getId());
        }
         
       
       // 포스트 create 날짜랑 오늘 날짜랑 같으면 new 하려고
        LocalDate now = LocalDate.now();
        String day= now.toString().substring(8);
        
        // (하은) post에 있는 bookId로 책 정보 넘기기
        List<Book> books = new ArrayList<>();
        
        if(postList.size() > 4 ) {
            for(int i=0; i < 4 ; i++) {
                Book book = bookService.read(postList.get(i).getBookId());
                books.add(book);
            }
            
        } else {
            for ( PostListDto p : postList) {
                
                Book book = bookService.read(p.getBookId());
                books.add(book);
            }
        }
                
            model.addAttribute("day", day);
            model.addAttribute("user", user);      
            model.addAttribute("list", postList);
            model.addAttribute("books", books);
                
        return "/post/list";
    }

    
    @PostMapping("/create")
    public String create(PostCreateDto dto, RedirectAttributes attrs) {
        log.info("create(dto ={})", dto);   
      
        Post entity = postService.create(dto); 
        log.info("과연 유저 id는 대체 어디서 들어온것? {}", dto.getUserId());
        // (홍찬) 리뷰순에서 사용할 것 - 글이 등록되기 전에
        // BookID에 해당하는 포스트 글이 1 증가시켜주기
        postService.countUpPostByBookId(dto.getBookId());
        
        attrs.addFlashAttribute("createdPostId", entity.getPostId());
        attrs.addFlashAttribute("userId", dto.getUserId());
        return "redirect:/post/list";
    }
    
    @Transactional(readOnly = true)
    @GetMapping({ "/detail", "/modify" })
    public void detail(@AuthenticationPrincipal UserSecurityDto userDto,
            Integer postId, String username ,Integer bookId, Integer replyId, Integer noticeId, Model model) {
        log.info("detail(postId= {}, bookId={}, postWriter={})", postId, bookId, username);
        log.info("리플아이디???={}",replyId);
        
        List<PostReadDto> recomList = postService.postRecomm(username, bookId);  // 1)
        
        Post p = postService.read(postId);
        Book b = bookService.read(bookId);
    
        if (username == null || userDto == null) { // 글 작성자와 유저가 다른 경우
            User u = userService.read(p.getUser().getId());
            Post entity = postService.read(postId); // 그 글의 조회수를 1올려줌.
            entity.update(postId, entity.getHit()+1);
            int hitCount = entity.getHit();
            model.addAttribute("hitCount", hitCount);
            model.addAttribute("user", u);
            
        } else { // 글 작성자와 유저가 같은경우
            User u = userService.read(username);
            int hitCount = postService.read(postId).getHit();
            model.addAttribute("hitCount", hitCount);
            model.addAttribute("user", u);
        }
        
        // (예진) 댓글 알림 클릭해서 들어가는 경우
        if(replyId != null) {
         
            model.addAttribute("replyId", replyId);
        }
        
        // (예진) 리플 작성칸 nickName 주기
        if(userDto != null) {
            String nick = userDto.getNickName();
            model.addAttribute("nick", nick);
        } 
    
         model.addAttribute("recomList",recomList );    // 2)
         model.addAttribute("post", p);
         model.addAttribute("book", b);
    }
   
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/delete")
    public String delete(Integer postId, RedirectAttributes attrs) {
        log.info("delete(postId={})",postId);
       
        replyService.deletePostIdWithAllReply(postId);
        postService.delete(postId);
        attrs.addFlashAttribute("deletedPostId", postId);
       
        // 삭제 완료 후에는 목록 페이지로 이동(redirect) - PRG 패턴
        return "redirect:/post/list";
    }
   
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update")
    public String update(PostUpdateDto dto) {
        log.info("update(dto={})", dto);
       
        postService.update(dto);
       
        // 포스트 수정 성공 후에는 상세 페이지로 이동(redirect)
        return "redirect:/post/detail?postId=" + dto.getPostId()+"&bookId="+ dto.getBookId();
    }
   
    @GetMapping("/search")
    public String search(String type, String keyword, Model model) {
        log.info("search(type={}, keyword={})", type, keyword);
       
        List<Post> list = postService.search(type, keyword);
        model.addAttribute("list", list);
       
        return "/post/list"; // list.html 파일
    }
    
    // (예진) 프로필 이미지 업로드
    @PostMapping("/profile/imageUpdate")  
    public String profileImageUpdate(Integer id, MultipartFile file, HttpServletRequest request) throws Exception{
        
        String referer = request.getHeader("referer");  // 현재 페이지 주소
        log.info("CurrentUrl ={}", referer);
        String urlTemp = referer.toString().substring(21);  // localhost:8888 뒷 부분만 잘라냄
        log.info("urlTemp ={}", urlTemp);  
        
        userService.write(id, file);
        
        return "redirect:"+urlTemp;  // 현재 페이지로 리다이렉트 
    }

    @PostMapping("/postIntroUpdate")  // (예진) postIntro 수정
    public String postIntroUpdate(Integer id, String postIntro, HttpServletRequest request) {
         log.info("포스트인트로!={} : {}",id,postIntro);
        
         String referer = request.getHeader("referer");  
         String urlTemp = referer.toString().substring(21);  
        
        User user = userService.read(id);
        log.info("변경 전: 포스트인트로 ={}", user.getPostIntro());
        user.setPostIntro(postIntro);
        log.info("변경 후: 포스트인트로 ={}", user.getPostIntro());
        
        userRepository.save(user);
        
        return "redirect:"+urlTemp;
    }
   
   
}