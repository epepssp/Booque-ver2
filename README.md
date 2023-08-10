# 📚 💰 Booque ver2
<p align="center"><img width="800" alt="제222" src="https://github.com/epepssp/project2/assets/118948099/7f0f036d-f742-4a88-84e8-1eae05684373"></p>
<br>

## 개요
> #### 아이티윌 자바 134기 "하찮은 진정혜" 조 6인이 한 번 더 함께한 두번째 팀 프로젝트
> #### 개발기간: 2023년 1월 11일 ~ 2023년 2월 16일<br>

## 프로젝트 소개
- Booque ver1 빌드업(세세한 부분 수정보완)
- BOOQUE 장터: 유저간 중고책 거리 플랫폼 추가

## 사용 기술 및 개발환경
+ Java
+ Spring Boot
+ HTML
+ CSS
+ JavaScript

## 주요기능 소개
- Socket을 이용한 실시간 채팅 
- 댓글 and 알림 키워드 등록하고 알람 받기 
- 외부 경로(로컬 폴더) 이용한 사진 업로드 
- 작성중인 글 임시 저장 

## 구현 기능(담당)
 + ### 외부 경로(로컬 폴더) 이미지 업로드
  
 > SecurityConfig.java 추가

 ```java

    @Bean  // 로컬 폴터 이미지 불러오기 위한 config 추가
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()
                    );
        };
    }

 ```

 > application.properties 외부경로 위치 추가

 ``` application.properties

     site.book.upload.path=E:\\study\\images

 ```

 > ImageUploadController. java 일부
 ```java

    @Value("${site.book.upload.path}") // (예진) 이미지 저장할 절대 경로(로컬 폴더) 값 주입 
    private String imageFilePath; 

 ```

 > list.html 일부

 ```html
    <a th:href="@{ /myPage }"> <!-- 프로필 사진 클릭하면 마이페이지로 이동 -->
      <img th:src="${user.userImage}" width=200px; /><!-- (예진) 프로필 이미지-->
    </a>     

    <!-- (예진) 프로필 사진 업데이트 버튼 -->
    <span th:if="${ user.username } == ${ #authentication.name }" >
        <img onclick="imagePop()" src="/images/im.png" width=22px; align="right" />
    </span>

    <!-- 프사 이미지 변경 모달 -->
        <div class="modal" id ="imageModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                     <div class="modal-header">
                          <h5 class="modal-title">프로필 이미지</h5>
                          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="profileForm" enctype="multipart/form-data" method="post" action="/post/profile/imageUpdate">
                           <input type="hidden" id="id" name="id" th:value="${ user.id }"/>
                           <input type="file" name="file" id="file"/>
                        </form>
                    </div>
                    <div class="modal-footer">
                         <button type="button" id="btnProfileUpdate" class="btn btn-primary">수정하기</button>
                    </div>
                 </div>
            </div>
        </div>
 ```

 > imageUpload.js 

 ```javascript

    function tempImage() { // 현재 설정해 둔 프로필 이미지 정보 요청
        
        const id = document.querySelector('#id').value;
        console.log("tempImage: id", id);
        
        axios
        .get('/tempView/' + id)  
        .then(response => { viewImage(response.data) } )
        .catch(err => { console.log(err) })

     };
       
     function viewImage(data) { // 받은 대답으로 프로필 이미지 show

        const divProfileImage = document.querySelector('#divProfileImage');
            
        axios
        .get('/view/'+ data.fileName)  
        .then(response => { console.log('성공!!') } )
        .catch(err => { console.log(err) })
        
         let img ='';
         if(data.fileName) {
            img +=  `<img src="/view/${data.fileName}" width=200px />`;
         } else {
             // 설정한 프사 없을 경우 디폴트 이미지
             img +=  `<img src="/view/113163657.jpg" width=200px />`;
         }
          divProfileImage.innerHTML = img;
       };     


        // profile form HTML 요소를 찾음.
        const profileForm = document.querySelector('#profileForm');

        // 프로필 이미지 변경 버튼 찾아서 이벤트 리스너 등록
        const btnProfileUpdate = document.querySelector('#btnProfileUpdate');
        btnProfileUpdate.addEventListener('click', submitForm);
    
        function submitForm(event){
             event.preventDefault();
        
             const result = confirm('프로필 사진을 변경하시겠습니까?');
             if(result) {
                  profileForm.action = '/post/profile/imageUpdate';
                  profileForm.method= 'post';
                  profileForm.submit();
              }     
                 tempImage();  
       };

 ```

 > ImageUploadController. java 일부
 ```java

    @GetMapping("/view/{fileName}")  // 로컬 폴더 이미지 불러오기
    public ResponseEntity<Resource> viewUpdatedImage(@PathVariable String fileName) {
        
        File file = new File(imageFilePath, fileName);
        
        String contentType = null;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            log.error("{} : {}", e.getCause(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", contentType);
        
        Resource resource = new FileSystemResource(file);
       
        return ResponseEntity.ok().headers(headers).body(resource);
    }

 ```

 >  PostController. java 일부

 ```java
    @PostMapping("/profile/imageUpdate")  
    public String profileImageUpdate(Integer id, MultipartFile file, HttpServletRequest request) throws Exception{
        
        String referer = request.getHeader("referer");  // 현재 페이지 주소
        log.info("CurrentUrl ={}", referer);
        String urlTemp = referer.toString().substring(21);  // localhost:8888 뒷 부분만 잘라냄
        log.info("urlTemp ={}", urlTemp);  
        
        userService.write(id, file);
        
        return "redirect:"+urlTemp;  // 현재 페이지로 리다이렉트 
    }
 ```


 > UserService.java 일부

 ```java

    @Value("${site.book.upload.path}") // (예진) 절대 경로(외부 경로) 값 주입
    private String imageFilePath;

    public void write(Integer id, MultipartFile file) throws IllegalStateException, IOException {  // (예진) 프로필 이미지 업로드
  
        UUID uuid = UUID.randomUUID();  // 식별자
        String fileName = uuid + "_" + file.getOriginalFilename();

        File saveFile=new File(imageFilePath, fileName); // saveFile: 파일 껍데기(객체) 생성해서 경로+파일이름 저장
        file.transferTo(saveFile);
        
        User user = userRepository.findById(id).get();
        
        user.setFileName(fileName);
        user.setFilePath(imageFilePath+"/"+fileName);
        user.setUserImage("/view/"+fileName);

        userRepository.save(user);

    }
 ```
<br>




 + ### 알림(Notice)

> Notices.java 도메인 생성

```java
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name ="NOTICES")
@SequenceGenerator(name = "NOTICES_SEQ_GEN", sequenceName = "NOTICES_SEQ", initialValue = 1, allocationSize = 1)
public class Notices {
    // (예진) 새 댓글 알림, 키워드 알림 구현하기 위한 도메인

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTICES_SEQ_GEN")
    private Integer noticeId;
    
    // 새 댓글 알림
    private Integer userId;  // postWriter id; => 알림 받을 사람
    private Integer bookId;
    private Integer postId;
    private Integer replyId;
    
    // usedBookId 알림
    private Integer subscribedBookId;  // 유저가 알람 받기로 등록한 BookId
    private Integer usedBookId;  // usedBookPost의 id
}
```

1. 새 댓글 알림

알림 생성

> NoticeRestController.java 

```java
    // (예진) 포스트에 새 댓글이 달리면 알림(notice) 만들어짐
    // notice create
    @PostMapping("/notice")
    public ResponseEntity<Integer> newNotice(@RequestBody NoticeDto dto){
     
       Integer noticeId = noticeService.create(dto);
       
       return ResponseEntity.ok(noticeId);
    }
```

> NoticeService.java 

```java
  // 새 알림 생성
    public Integer create(NoticeDto dto) {
        
        Notices notice = null;
        if(dto.getUsedBookId() == null) {
             notice = Notices.builder()
                .userId(dto.getUserId())
                .bookId(dto.getBookId())
                .postId(dto.getPostId())
                .replyId(dto.getReplyId()).build();
        } else {
            notice = Notices.builder()
            .userId(dto.getUserId())
            .bookId(dto.getBookId())
            .usedBookId(dto.getUsedBookId()).build();
        }
           
        noticeRepository.save(notice);
        return notice.getNoticeId();
    }   
    
    public List<NoticeDto> readNotices(Integer userId) {  // 알림 받을 userId
        
        List<Notices> list = noticeRepository.findByUserIdOrderByNoticeIdDesc(userId);
        List<NoticeDto> noticeList = new ArrayList<>();
      
        for (Notices n : list) {
            
            if(n.getUsedBookId() == null) {
               PostReply r = replyService.readRep(n.getReplyId());
           
               NoticeDto dto= NoticeDto.builder()
                              .noticeId(n.getNoticeId())
                              .postId(n.getPostId())
                              .bookId(n.getBookId())
                              .userId(n.getUserId())
                              .replyId(n.getReplyId())
                              .userImage(r.getUser().getUserImage())
                              .nickName(r.getUser().getNickName())
                               .build();           
            
                noticeList.add(dto);
                
            } else {
                    UsedBook ub = usedBookService.read(n.getUsedBookId());
                    Book b = bookService.read(n.getBookId());
                
                    NoticeDto dto= NoticeDto.builder()
                        .noticeId(n.getNoticeId())
                        .bookId(n.getBookId())
                        .userId(n.getUserId())
                        .usedBookId(n.getUsedBookId())
                        .title(ub.getTitle())
                        .bookName(b.getBookName())
                        .bookImage(b.getBookImage())
                        .build();
                
                    noticeList.add(dto);
            }
            
        }
        
        return noticeList;
    }
```

> postReply.js 일부

```javascript
 // (예진) 새 댓글 작성시 알림(notice) 만들어짐 - 댓글 작성 함수 then에 newReplyNotion 추가
    function newReplyNotion(data){
      
           axios.post('/notice', data)
           .then(response => {
             
           })         
            .catch(error => {
                    console.log(error);
           });
     
    }
```

유저의 알림 리스트 보여주기
> NoticeRestController.java 

```java
  // (예진) userId(postWriter/subscribedBookId) 알림 리스트(notice list) 불러오기
    @GetMapping("/showNotice/{userId}")
    public ResponseEntity<List<NoticeDto>> showAllNotices(@PathVariable Integer userId) {
 
         List<NoticeDto> list =noticeService.readNotices(userId);
       
        return ResponseEntity.ok(list);
    }  
```

> layout.html 일부

```html
  <!-- 알림 버튼 -->
            <div class="w3-dropdown-hover w3-bar-item w3-right">
                <button class="w3-button" id="btnAlarm" style="color:white; margin-top:8px; margin-right:30px;">
                
                    <!-- (예진) 알림 아이콘 오른쪽 상단에 알림 갯수 뜨도록 -->
                    <i class="fa fa-bell-o"></i><span class="position-absolute top-60 start-85 translate-middle badge rounded-pill bg-danger m-1" id="noticeCount"></span>
                </button>

                <input type="hidden" id="userId" th:value="${ userId }"/>
                
                 <div class="w3-dropdown-content w3-card-4 w3-bar-block mb-2" style="top:66px; right:46px;">

                     <!-- (예진) 댓글 알림 리스트 보여줄 영역 -->
                     <div id="divNotices" class="notices"></div>
                 </div>
            </div>
````

> notice.js

```javascript
    const userId = document.querySelector('#userId2').innerText;
    
    if(userId){
         console.log(userId);
         showNotice();      // 로그인 한 유저의 알림 리스트
    }
   
    function showNotice(){
        console.log(userId);
       
        axios
        .get('/showNotice/' + userId)  
        .then(response => { 
            updateNoticeList(response.data) } )
        .catch(err => { console.log(err) });
        
    }    
```

알림 확인

> notice.js

```javascript

    function updateNoticeList(data){
        
         const noticeCount = document.querySelector('#noticeCount');
         let count = '';
         count += '<span style="color: white;">'+ data.length +'</span>';
         noticeCount.innerHTML = count;
        
        const divNotices = document.querySelector('#divNotices');
        let str ='';
        
         for (let x of data){ 
             
            if(x.replyId) {
              str +=`<div><a style="font-size: 17px; text-align:left; padding-top:15px; color:#708090;" class="w3-bar-item w3-button"`
                   // 알림 클릭하면 해당 댓글로 이동함. 
                  + `onclick="deleteNotice();" a href="/post/detail?postId=${ x.postId }&bookId=${ x.bookId }&replyId=${ x.replyId }">`
                  + '<input type="hidden" id="noticeId"  value="'+ x.noticeId +'" />'
                  + '내블로그) <img class="rounded-circle m-1" width="30" height="30" src="' + x.userImage + '" />'
                  + `<span class="under-line"><span class="fw-bold">${x.nickName}</span>님의 새 댓글!</span>`
                  + '</a></div>';
            }

           if(x.usedBookId){
             str +=`<div><a style="font-size: 17px; text-align:left; padding-top:15px; color:#708090;" class="w3-bar-item w3-button"`
                 + `onclick="deleteNotice();" a href=" /market/detail?usedBookId=${ x.usedBookId }">`
                 + '<input type="hidden" id="noticeId"  value="'+ x.noticeId +'" />'
                 + '부끄장터) <img class="rounded-circle m-1" width="30" height="30" src="' + x.bookImage + '" />'
                 + `<span class="fw-bold">${x.bookName}</span> 새 판매글!`
                 + '</a></div>';
          
            }  
        }
        
        divNotices.innerHTML = str;
 
    }
```

> postReply.js

```javascript
  function updateReplyList(data){
        const divReplies = document.querySelector('#replies');
        let str = '';

       for (let r of data){
            str += '<div class="card border-dark mb-3 w-100" style="text-align: left;">';
       
            if(r.replyId == repId) {   // (예진) 클릭한 알림에 해당하는 댓글 -> 하늘색 백그라운드 효과로 표시
               str +='<div class="bgColor" id="bgColorBtn" style="background-color: #e6f2ff;">';
            }

            if(r.replyId != repId) {  
               str +='<div class="bgColor" id="bgColorBtn">';
            }      
            str +=`<div class="flex-shrink-0"><a href="/post/list?postWriter=${r.replyWriter}">`
                + '<img class="rounded-circle m-2" width="45" height="45" src="' + r.userImage + '" alt="..." />'
                + `<span class="fw-bold m-2">${r.nickName}</span></a></div>`
                + '<div class="card-body text-dark">'
                + '<p class="card-text">' + r.replyContent + '</p>'
                + '<div><small style="color:gray;"> 작성시간: ' + '<span id="commentDate">' + r.createdTime + '</span>' + '</small></div>'
    //            + '<div><small style="color:gray;"> 수정시간: ' + r.modifiedTime + '</small></div>'
                + '</div>';
                
            if(r.replyWriter == loginUser){
               str += '<div class="card-footer">'
                   + `<button type="button" class="btnModifies btn btn-outline-primary" data-rid="${r.replyId}">수정</button>`
                   + '</div>';
            }
            
            str += '</div>';

            str +='</div>';
         }
        
        divReplies.innerHTML = str;
        
        
        // (예진) 새 댓글에 하늘색 백그라운드 컬러 효과 -> 댓글 클릭시 효과 사라짐
        const bg = document.querySelector('.bgColor');
        
        bg.addEventListener('click', function(){
        const divBg = document.getElementById('bgColorBtn');
        divBg.style.backgroundColor = 'white';
        divBg.removeAttribute('class');
        
    });
```
알림 삭제

> NoticeRestController.java

```java
@DeleteMapping("/notice/delete/{noticeId}")
    public void deleteNotice(@PathVariable Integer noticeId){ 
       //알림 클릭하면 확인한 것으로 보고 알림 삭제 

        noticeService.delete(noticeId);
        noticeRepository.deleteById(noticeId);    
   }
```

2. 키워드 알림

알림 받을 키워드 등록

검색어가 포함된 책 리스트를 4개까지 보여주고
알림 받고 싶은 책 제목(키워드) 등록 할 수 있음

> MarketController.java 

```java
    // (예진) 키워드 포함된 책 제목 => 이런 중고책 찾으세요? 
    List<Book> list4 = bookService.searchByBookName(mainKeyword);

    model.addAttribute("list4", list4);
```

> BookRepository.java 

```java
    // (예진) 부끄장터 제목에 검색 키워드 포함된 책 리스트중 4개만
    List<Book> findTop4ByBookNameIgnoreCaseContaining(String Keyword);
```

> mainSearch.html 일부

```html
<div style="margin-bottom: 40px;"> <!-- (예진) 키워드 알림 등록 -->
    <h6 class="mb-1 fw-bold" style="margin-left: 40px; font-size: 15px; font-style: italic;">&nbsp;이런 책 찾으시나요?</h6>
    <h6 style="margin-left: 40px; font-size: 13px; font-style: italic;">&nbsp;원하는 책 클릭하고, 새 글 알림 받아보세요!</h6>

    <div class="rounded" th:each="x : ${ list4 }" style="margin-left: 40px; border: 1px solid silver; display: inline-block;"><!-- main div  -->
            <div style="margin: 10px;">
                <div style="display: inline-block; vertical-align: top; margin-right: 10px;">
                    <a th:href="@{ /detail?id={bookId} (bookId = ${ x.bookId })}"><img th:src="${ x.bookImage }" style="width: 60px;" /></a>
                    <input type="hidden" id="b-Id" th:value="${ x.bookId }"/>
                </div>
              <div style="text-align: left; display: inline-block;">
                     <div class="d-inline-flex px-1 my-1  border rounded text-secondary" style="font-size: 10px;">  
                         <span th:text="${ x.bookgroup }"></span><span> / </span><span th:text="${ x.category }"></span> 
                     </div>
                     <div style="font-size: 13px; width: 145px;" class="fw-bold text-truncate"  th:text="${ x.bookName }"></div> 
                     <div style="font-size: 12px;"><span th:text ="${ '저자: ' + x.author }"></span> </div> 
                     <div style="font-size: 12px;"><span th:text ="${ x.publisher + ' 출판' }"></span></div>  
               </div> 
               <div style="display: inline-block;">
                     <span class="m-1" onclick="register(event);"><i class="bi bi-hand-index-fill" style="font-size: 21px;"></i></span>
               </div> 
            </div>
    </div><!-- main div end -->
</div>

<script>
function register(event) {

	const bookId = document.querySelector('#b-Id').value;
	
	  axios
	   .get('/register/notice/'+bookId)
	   .then(response => {
	       alert('알림 등록 완료!'); })
	   .catch(err =>{
	       console.log(err); });
	}
</script>
```

> NoticeRestController.java 

```java
   // (예진) 키워드 알림 받을 BookId 등록
    @GetMapping("/register/notice/{bookId}")
    public ResponseEntity<Integer> registerBookId(@PathVariable Integer bookId, @AuthenticationPrincipal UserSecurityDto dto) {
        
       User user = userService.read(dto.getId());
       user.setNoticeBookId(bookId);
       userRepository.save(user);
        
       return ResponseEntity.ok(1);
    }
```

키워드 알림 생성

> marketCreate.js 일부

```javascript
    //(예진) 새 포스트 등록시 생성해야 할 노티스 있는지 체크
       function checkBookId(bookId,usedBookId) {
           
        const data = { bookId : bookId, usedBookId : usedBookId }
      
        axios
        .post('/notice/check', data)
        .then(response => {  console.log('성공') })
        .catch(err => { alert(err) });
    
       };
```

> NoticeRestController.java

```java
// (예진) usedBook 새 글 등록 될 때 마다 해당 북아이디 알림 설정한 유저가 있는지 체크
    // 있다면 노티스 생성  
    @PostMapping("/notice/check")
    public ResponseEntity<Integer> checkContainBookId(@RequestBody NoticeDto noticeDto){
        log.info("체크데이터={}:{}", noticeDto.getBookId(), noticeDto.getUsedBookId());
        
        Integer bookId = noticeDto.getBookId();
        Integer usedBookId = noticeDto.getUsedBookId();
        
        List<User> users = userService.read();
            
        for (User u : users) {
            if(u.getNoticeBookId() == bookId) {
               Integer uId = u.getId();  
            
               NoticeDto dto = NoticeDto.builder().userId(uId).bookId(bookId).usedBookId(usedBookId).build();
               Integer noticeId = noticeService.create(dto);
            
               return ResponseEntity.ok(noticeId);
            } 
        } 
          
        return ResponseEntity.ok(1);
  }
```
