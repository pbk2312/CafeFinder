package CafeFinder.cafe.cafe.domain;

import lombok.Getter;

@Getter
public enum SeoulDistrict {
    GN("강남구"), GD("강동구"), GB("강북구"), GS("강서구"), GA("관악구"),
    GJ("광진구"), GR("구로구"), GC("금천구"), NW("노원구"), DB("도봉구"),
    DD("동대문구"), DJ("동작구"), MP("마포구"), SDM("서대문구"), SC("서초구"),
    SD("성동구"), SB("성북구"), SP("송파구"), YC("양천구"), YD("영등포구"),
    YS("용산구"), EP("은평구"), JR("종로구"), JG("중구"), JL("중랑구");


    private final String koreanName;

    SeoulDistrict(String koreanName) {
        this.koreanName = koreanName;
    }

}
