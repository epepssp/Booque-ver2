# 📚 💰 Booque ver2
<br>

<p align="center"><img width="700" alt="제222" src="https://github.com/epepssp/Booque-ver2/assets/118948099/a11d509d-b989-47a6-aec9-1a0ccdbfce2f"></p>
<br>

## 개요
**인원** 아이티윌 자바 134기 "하찮은 진정혜" 조 6인이 한 번 더 함께한 두번째 팀 프로젝트<br>
**일정** 2023년 1월 11일 ~ 2023년 2월 16일<br>
<br>

## 프로젝트 소개
유저간 중고거래 플랫폼 BOOQUE 장터 및 다양한 기능 추가하여 Booque ver1 빌드업!
 
<br>

## 사용 기술 및 개발환경
+ Java
+ Spring Boot
+ HTML
+ CSS
+ JavaScript
<br>

## 주요기능 소개(ver1과 비교하여 추가된 기능)
- 부끄장터
  - 중고책 거래 플랫폼
  - Socket을 이용한 유저간 실시간 채팅
  - 판매글 작성 -> 작성중인 글 임시 저장
- 알림
   - 댓글 알림
   - 키워드 알림 
- 외부 경로(로컬 폴더) 이용한 프로핊 사진 업로드 

<br>

## 나의 구현 기능
 + ### 외부 경로(로컬 폴더) 이미지 업로드 - 프로필 사진 변경
 <br>
 <div align="center"><img src="https://github.com/epepssp/Booque-ver2/assets/118948099/086e1692-d718-4703-888f-07dd7dbb9766" height="400" alt="프사"></div>
 <br> 

 + #### 설정
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

 + #### 구현
 > list.html 일부

 ```html

    <!-- (예진) 프로필 사진 업데이트 버튼 -->
    <span th:if="${ user.username } == ${ #authentication.name }" >
         <img onclick="document.getElementById('imageModal').style.display='block'" src="/images/im.png" width=22px; align="right" />
    </span>

    <!-- 프사 변경 모달 -->
    <div id ="imageModal" class="w3-modal">
        <div class="w3-modal-content" style="width: 350px; height: 180px;">
           <span onclick="document.getElementById('imageModal').style.display='none'" class="w3-button w3-display-topright">&times;</span>
           <div style="margin-left: 25x;" class="p-3" align="left"><small>프로필 사진 변경</small></div>
           <div align="center" class="m-3 pt-4 pb-4" style="border-top: 1px solid #DCDCDC; border-bottom: 1px solid #DCDCDC;">
               <form id="profileForm" enctype="multipart/form-data" method="post" action="/post/profile/imageUpdate">
                    <input type="hidden" id="id" name="id" th:value="${ user.id }"/>
                    <input style="display: inline-block;" type="file" name="file" id="file"/>
               </form>
           </div>
           <div class="mt-3 p-1">
               <button type="button" id="btnProfileUpdate" class="btn btn-primary">수정하기</button>
           </div>
        </div>
     </div>

 ```

 > imageUpload.js 

 ```javascript

     getImage();

     btnProfileUpdate.addEventListener('click', e => {

       const fileInput = document.querySelector('input[name="file"]');
       const file = fileInput.files[0];  // 사진 한 장
    
       const formData = new FormData();  // file로 전송할 수 없고 formData 타입으로 바꿔서 보내야
       formData.append('file', file); // "file"이 서버에서 파일을 받아오는 이름과 일치해야 함
       
       document.getElementById('imageModal').style.display = 'none';
  
       axios.post('/submit/image', formData)
            .then(response => { 
                getImage();
                console.log(response);
            })
            .catch(err => { console.log(err) })
      });
   
      function getImage(){  // 로그인 유저 프로필 이미지
        
        const id = document.querySelector('#id').value;
        const profileImageDiv = document.querySelector('#profileImageDiv');

        axios.get('/user/fileName/' + id)  
             .then(response => { 
                   let img = `<img src="/api/view/${response.data}" width=200px; />`; 
                   profileImageDiv.innerHTML = img;  // profileImageDiv에 프로필 사진 넣기
              })
             .catch(err => { console.log(err) })
       }
 ```

 > ImageUploadController. java 일부
 ```java

     @Value("${site.book.upload.path}") // 필요한 곳에 절대 경로(로컬 폴더) 값 주입하여 사용 
     private String imageFilePath;

     @PostMapping("/submit/image")
     public ResponseEntity<Integer> upload(@AuthenticationPrincipal UserSecurityDto userSecurityDto, MultipartFile file) 
            throws IllegalStateException, IOException {
        
        UUID uuid = UUID.randomUUID();  // 식별자
        String fileName = uuid + "_" + file.getOriginalFilename();
        
        File saveFile = new File(imageFilePath, fileName); // saveFile: 파일 껍데기(객체) 생성해서 경로+파일이름 저장
        file.transferTo(saveFile);
        
        User user = userRepository.findById(userSecurityDto.getId()).get();
        
        user.setFileName(fileName);
        userRepository.save(user);
        
       return ResponseEntity.ok(1);

     }
    
    
     @GetMapping("/api/view/{fileName}")
     public ResponseEntity<Resource> viewFile(@PathVariable String fileName) {
        log.info("viewFile(fileName={})", fileName);
        
        File file = new File(imageFilePath, fileName);
        
        String contentType = null;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
       
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", contentType);
        
        Resource resource = new FileSystemResource(file);
        
        return ResponseEntity.ok().headers(headers).body(resource);
     }
 ```
<br>


 + ### 알림 (Notice) 
 + #### 새 댓글 알림 / 새 글 등록 키워드(BookId) 알림
   + ##### 새 댓글 알림: 댓글 작성 버튼 클릭할 때 새 댓글 알림 생성

   <div align="center"><img src="https://github.com/epepssp/Booque-ver2/assets/118948099/a81cd3f7-308d-4b45-abe3-a84ad1c32283" height="400" alt="댓글알림"></div>

   > postReply.javascript
   ```javascript

      // 댓글 작성 함수
      function registerNewReply() {
                  // 중략
            axios.post('/api/reply', data)
                 .then(response => {
                      alert('#  댓글 등록 성공');
                        
                      newReplyNotion(response.data);
                  })
                  .catch(error => {
                        console.log(error);
                  });
       }
   
       // 새 댓글 노티스 생성
       function newReplyNotion(data){ 
      
            axios.post('/notice', data)
                 .then(response => {
                      console.log('노티스 저장성공');
                  })         
                 .catch(error => {
                      console.log(error);
                  });
       }
     ```
   <br>
   
   + #### 새 글 등록 키워드(BookId) 알림
     <div align="center"><img src="https://github.com/epepssp/Booque-ver2/assets/118948099/ca090a59-a4e5-47f5-bf17-c18ada6ceba1" height="400" alt="키워드알림"></div>
     
     + ###### 검색어 기반 추천 리스트 제공 -> 알림 받고 싶은 키워드(BookId) 등록 
       
     > mainSearch.html
     > 
     ```html
     
          <!-- 메인창 검색 -->
          <input id="searchInput" name="mainKeyword" type="search" placeholder="검색어를 입력하세요!"  th:value="${ mainKeyword }"/>
             <button id="btnSearchL" style="border: none; background-color: white;">
                <i class="bi bi-search" style="font-size: 2rem;"></i>
             </button>


        <script>
            // search 버튼 이벤트 리스너 등록. 검색어(mainKeyword) 컨트롤러로 전달 
            const btnSearch = document.querySelector('#btnSearchL')
            btnSearch.addEventListener('click', function(){
                 const formSearch = document.querySelector('#formSearch')
            })
        </script>
     
     ```

     <br>
     
     ```java

        // BookRepository
            List<Book> findTop4ByBookNameIgnoreCaseContaining(String Keyword);
    
        // MarketController
        @GetMapping("/mainSearch")
        public void mainSearch(@AuthenticationPrincipal UserSecurityDto userDto ,String region,
                        String mainKeyword, Model model, String orderSlt , String status) {
     
             // 검색어(mainKeyword) 포함된 책 추천 리스트(4개) 넘겨줌
             // (예진) 키워드 포함된 책 제목 => 이런 중고책 찾으세요? => 알람 설정 할 수 있게
             List<Book> list4 = bookService.searchByBookName(mainKeyword);
             model.addAttribute("list4", list4);
        }
     
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
                   <div style="display: inline-block;"><!-- 키워드 등록 버튼 --> 
                       <span class="m-1" onclick="register(event);"><i class="bi bi-hand-index-fill" style="font-size: 21px;"></i></span>
                   </div> 
               </div>
            </div><!-- main div end -->
         </div>


        <script>
           function register(event) { // 키워드 알림 bookId 등록 함수
              const bookId = document.querySelector('#b-Id').value;
	
	       axios.get('/register/notice/'+bookId)
	            .then(response => {
	               alert('알림 등록 완료!'); })
	            .catch(err =>{
	                console.log(err); });
	    }
         </script>
      ```

      + ###### 새 글이 등록될 때 중고책 판매글의 BookId와 유저들이 등록해 둔 알림 키워드(BookId) 목록 비교 -> 일치하는 항목 있다면 키워드 알림 생성 

      > marketCreate.js
      ```javascript
      
         const btnSubmit = document.querySelector('#btnSubmit');
         btnSubmit.addEventListener('click', function () {
               
               checkBookId(bookId,usedBookId);
         }

         // 새 글 등록되면 생성해야 할 알림 있는지 체크
         // 해당 bookId 알림 설정 한 유저가 있다면 노티스 생성
        function checkBookId(bookId,usedBookId) {
              const data = { bookId : bookId, usedBookId : usedBookId }
      
              axios.post('/notice/check', data)
                   .then(response => {  console.log('성공') })
                   .catch(err => { alert(err) });
         };

      ```

      > NoticeRestController.java

      ```java
       
          @PostMapping("/notice/check")  // 설정할 키워드 알람 있는지 체크
          public ResponseEntity<Integer> checkContainBookId(@RequestBody NoticeDto noticeDto){
              List<User> users = userService.read();  // 유저 All
            
              for (User u : users) {
                 if(u.getNoticeBookId() == noticeDto.getBookId()) { // 유저가 알람 받기 등록한 bookId가 새로 작성된 중고 판매글 bookId와 같을때 -> 노티스 생성
         
                      NoticeDto dto = NoticeDto.builder().userId(u.getId()).bookId(noticeDto.getBookId()).usedBookId(noticeDto.getUsedBookId()).build();
                      return ResponseEntity.ok(noticeService.create(dto));
                 } 
             } 
              return ResponseEntity.ok(1);
           }
      ```

      
    + #### 상단바 알림 버튼 추가
      + ##### 알림 버튼 오른쪽 상단 뱃지 - 알림 갯수 표현
   
      > layout.html

      ```html
      
            <!-- 알림 버튼 -->
            <div class="w3-dropdown-hover w3-bar-item w3-right">
                <button class="w3-button" id="btnAlarm" style="color:white; margin-top:8px; margin-right:30px;">
                   <!-- (예진) 알림 아이콘 오른쪽 상단 빨간 뱃지 가운데 알림 갯수 뜨도록 -->
                    <i class="fa fa-bell-o"></i>
                    <span class="position-absolute top-10 right-10 translate-middle badge rounded-pill bg-danger" style="width: 24px;  height: 24px;">
                       <span id="noticeCount" class="position-absolute top-50 start-50 translate-middle" style="transform: translate(-50%, -50%); font-size: 15px;"></span>
                    </span>
                </button>
                <div class="w3-dropdown-content w3-card-4 w3-bar-block mb-2" style="top:66px; right:46px;">
                    <div id="divNotices" class="notices"></div><!-- (예진) 댓글 알림 리스트 보여줄 영역 -->
                </div>
             </div>
       ```
      
       + ##### 상단바 알림 버튼 Dropdown 리스트 - 알림 확인 / 삭제
        
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

	              <!-- 새 댓글 알림 클릭하면 해당 댓글로 이동. 알림 클릭시 확인한 것으로 간주하여 알림 삭제. -->
                     + `onclick="deleteNotice();" a href="/post/detail?postId=${ x.postId }&bookId=${ x.bookId }&replyId=${ x.replyId }">`
                     + '<input type="hidden" id="noticeId"  value="'+ x.noticeId +'" />'
                     + '내블로그) <img class="rounded-circle m-1" width="30" height="30" src="' + x.userImage + '" />'
                     + `<span class="under-line"><span class="fw-bold">${x.nickName}</span>님의 새 댓글!</span>`
                     + '</a></div>';
                     }
               
                   if(x.usedBookId){
                      str +=`<div><a style="font-size: 17px; text-align:left; padding-top:15px; color:#708090;" class="w3-bar-item w3-button"`

                      <!-- 새 글 키워드 알림 클릭하면 해당 글로 이동. 알림 클릭시 확인한 것으로 간주하여 알림 삭제. -->
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
      
        + ##### 댓글 리스트 새 댓글 백그라운드 컬러 효과 -> 로그인 유저가 알림 클릭 이동 후 해당 댓글 클릭시 백그라운드 컬러 사라짐(알림 확인한 것으로 간주)
       
        > postReply.js

        ```javascript

          function updateReplyList(data){  // 댓글 목록 
              const divReplies = document.querySelector('#replies');
              let str = '';

              for (let r of data){
                  str += '<div class="card border-dark mb-3 w-100" style="text-align: left;">';
            
                  if(r.replyId == repId) {   // 새 댓글
                      str +='<div class="bgColor" id="bgColorBtn" style="background-color: #e6f2ff;">';
                  }
                  if(r.replyId != repId) {   // 새 댓글 아닌
                      str +='<div class="bgColor" id="bgColorBtn">';
                  }
      
                  str +=`<div class="flex-shrink-0"><a href="/post/list?postWriter=${r.replyWriter}">`
                      + '<img class="rounded-circle m-2" width="45" height="45" src="' + r.userImage + '" alt="..." />'
                      + `<span class="fw-bold m-2">${r.nickName}</span></a></div>`
                      + '<div class="card-body text-dark">'
                      + '<p class="card-text">' + r.replyContent + '</p>'
                      + '<div><small style="color:gray;"> 작성시간: ' + '<span id="commentDate">' + r.createdTime + '</span>' + '</small></div>'
                  //     + '<div><small style="color:gray;"> 수정시간: ' + r.modifiedTime + '</small></div>'
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
          }
        
          // 새 댓글에 준 백그라운드 컬러 -> 한 번 클릭하면 없어짐
          const bg = document.querySelector('.bgColor');
        
          bg.addEventListener('click', function(){
             const divBg = document.getElementById('bgColorBtn');
             divBg.style.backgroundColor = 'white';
             divBg.removeAttribute('class');
          });
      
       ```
      
     + #### 알림 리스트
       + ##### 로그인 유저의 확인 하지 않은, 새로 생성된 모든 알림 
       > notice.js

      ```javascript
      
         const userId = document.querySelector('#userId2').innerText;
    
         if(userId){
            showNotice();  // 페이지마다 제일 먼저 showNotice() 실행 
         }
   
         function showNotice(){  // 로그인 한 유저의 알림 리스트 보여주는 함수
             axios.get('/showNotice/' + userId)  
                  .then(response => { 
                          updateNoticeList(response.data) } )
                  .catch(err => { console.log(err) });
         }    
      ```
      
      > NoticeService.java

      ```java
      
          public List<NoticeDto> readNotices(Integer userId) {  // 알림 받을 userId
        
               List<Notices> list = noticeRepository.findByUserIdOrderByNoticeIdDesc(userId);
               List<NoticeDto> noticeList = new ArrayList<>();
      
               for (Notices n : list) {
            
                   if(n.getUsedBookId() == null) {  // 새 댓글 알람인 경우
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
                
                } else { // 키워드 알람인 경우 
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
            return noticeList;    // 생성된 시간 순서대로 하나의 알람 리스트 만들어서 넘김
         }
     ```


     

     



