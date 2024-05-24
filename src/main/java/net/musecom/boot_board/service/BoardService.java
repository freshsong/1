package net.musecom.boot_board.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.musecom.boot_board.dto.BoardDto;
import net.musecom.boot_board.entity.BoardEntity;
import net.musecom.boot_board.repository.BoardRepository;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public void write(BoardDto bDto) {
        BoardEntity bEntity = BoardEntity.toBoardEntity(bDto);
        boardRepository.save(bEntity);
    }


    public List<BoardDto> findAll(){
        //column에 있는 모든 내용을 가져옴
        List<BoardEntity> bEntities = boardRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));

        //BoardDto 타입의 ArrayList를 만듦        
        List<BoardDto> bDtos = new ArrayList<>();

        //위에서 만든 ArrayList 박스에 column에서 가져온 내용을 하나하나 채움
        for(BoardEntity bEntity : bEntities) {
            bDtos.add(BoardDto.toBoardDto(bEntity));
        }
        return bDtos;
    }

    /* 게시글 조회수 증가 */
    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id); //repository 받아와 구현
    }

    /* 게시물 보기 */
    public BoardDto findById(Long id){
        //Optional 클래스 값이 있을수도, 없을수도 있는 타입
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
       if(optionalBoardEntity.isPresent()){

          BoardEntity boardEntity = optionalBoardEntity.get();
          BoardDto boardDto = BoardDto.toBoardDto(boardEntity);
          return boardDto;
          
       }else{
         return null;

       }
    }

    /* 게시물 수정 */
    public BoardDto update(BoardDto bDto){
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(bDto);
        boardRepository.save(boardEntity);
        return findById(bDto.getId()); //앞화면으로 되돌아가기 findbyid
    }

    /* 게시물 삭제 */
    public void delete(Long id){
        boardRepository.deleteById(id);
    }
    // select * from bbs limit page, pageLimit; 로 쓸수도 있어

    public Page<BoardDto> paging(Pageable pageable){
        int page = pageable.getPageNumber() - 1; //시작이 1이니 -1해서 0부터 시작하게
        int pageLimit = 10; //한 페이지에 보여지는 글의 개수
        // select * from bbs limit =0(page), 10(pagelimit);

        Page<BoardEntity> bEntities = 
            boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        /**
         * 1. 전체글 수
         * 2. db에 요청한 페이지 번호
         * 3. 요청 페이지의 해당 글
         * 4. 전체 페이지 수
         * 5. 한 페이지에 보여지는 글 수
         * 6. 이전 페이지 (true, false)
         * 7. 첫 페이지(true, false)
         * 8. 마지막 페이지(true, false)
         */
        System.out.println("1. 전체 글 수 : " + bEntities.getTotalElements() );
        System.out.println("2. db에 요청한 페이지 번호 : " + bEntities.getNumber());
        System.out.println("3. 요청 페이지의 해당 글 : " + bEntities.getContent());
        System.out.println("4. 전체 페이지 수 : " + bEntities.getTotalPages());
        System.out.println("5. 한 페이지에 보여지는 글 수 : " + bEntities.getSize());
        System.out.println("6. 이전 페이지 (true, false) : " + bEntities.hasPrevious());
        System.out.println("7. 첫 페이지(true, false) : " + bEntities.hasNext());
        System.out.println("8. 첫 페이지 : " + bEntities.isFirst());
        System.out.println("9. 마지막 페이지 : " + bEntities.isLast());
        //목록에서 보여줄 내용 id, bwriter, hits,title, createdTime
        Page<BoardDto> boardDtos = bEntities.map(board -> new BoardDto(board.getId(), board.getBwriter(), board.getHits(), board.getTitle(), board.getCreatedTime()))
        return null;
    }

    @Transactional
    public void delete(Long id, String bpass){
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);

        if(optionalBoardEntity.isPresent()){
            BoardEntity boardEntity = optionalBoardEntity.get();
            if(boardEntity.getPass().equals(bpass)){
                boardRepository.delete(boardEntity);
            }
            else{
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        }
    }
}



