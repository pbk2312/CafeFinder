package recipe.recipeshare.service.board;

import java.util.UUID;

public interface BoardService<T> {

    // 등록
    void register(T dto, UUID memberId);

    // 삭제
    void remove(T dto);

    // 수정
    void modify(T dto);

}
