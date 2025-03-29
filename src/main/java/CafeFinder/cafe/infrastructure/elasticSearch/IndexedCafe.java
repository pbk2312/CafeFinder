package CafeFinder.cafe.infrastructure.elasticSearch;

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
@NoArgsConstructor
@AllArgsConstructor
public class IndexedCafe {

    @Id
    @Field(name = "cafe_code", type = FieldType.Keyword)
    private String cafeCode;

    @Field(name = "name", type = FieldType.Text)
    private String name;

    @Field(name = "address", type = FieldType.Text)
    private String address;

    @Field(name = "district", type = FieldType.Keyword)
    private String district;

    @Field(name = "themes", type = FieldType.Keyword)
    private List<String> themes;

    @Field(name = "average_rating", type = FieldType.Double)
    private Double averageRating;

    @Field(name = "review_count", type = FieldType.Integer)
    private Integer reviewCount;

    @Field(name = "hours", type = FieldType.Text)
    private String openingHours;

    @Field(name = "phone", type = FieldType.Text)
    private String phoneNumber;

    @Field(name = "image_url", type = FieldType.Text)
    private String imageUrl;

}
