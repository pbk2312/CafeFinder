package CafeFinder.cafe.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "cafe") // Elasticsearch 인덱스 이름
@Getter
@Builder
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드 생성자
public class CafeInfoDocument {

    @Id // Elasticsearch 문서의 고유 식별자
    @Field(name = "cafe_code", type = FieldType.Keyword)
    private String cafeCode; // 카페 코드 (고유 식별자)

    @Field(name = "name", type = FieldType.Text)
    private String name; // 카페명

    @Field(name = "address", type = FieldType.Text)
    private String address; // 주소

    @Field(name = "district", type = FieldType.Keyword)
    private String district; // 행정구 (ex: DJ -> 동작구)

    @Field(name = "themes", type = FieldType.Keyword)
    private List<String> themes; // 카페 테마 리스트

    @Field(name = "review", type = FieldType.Double)
    private Double review; // 평균 평점

    @Field(name = "hours", type = FieldType.Text)
    private String hours; // 영업시간

    @Field(name = "phone", type = FieldType.Text)
    private String phone; // 전화번호 (nullable)

    @Field(name = "image_url", type = FieldType.Text)
    private String imageUrl; // 대표사진 URL
}
