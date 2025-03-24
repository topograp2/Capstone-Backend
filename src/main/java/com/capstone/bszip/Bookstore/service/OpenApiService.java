package com.capstone.bszip.Bookstore.service;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.repository.BookstoreRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

import static com.capstone.bszip.Bookstore.domain.BookstoreCategory.*;

@Service
@RequiredArgsConstructor
public class OpenApiService {
    private final BookstoreRepository bookstoreRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL_CAFE = "http://api.kcisa.kr/openapi/API_CIA_090/request";
    private static final String API_URL_INDEP = "http://api.kcisa.kr/openapi/API_CIA_089/request";
    private static final String API_URL_CHILD = "http://api.kcisa.kr/openapi/service/CNV/API_CNV_037";

    @Value("${bookstore.cafe.key}")
    private String BOOKSTORE_CAFE_KEY;
    @Value("${bookstore.indep.key}")
    private String BOOKSTORE_INDEP_KEY;
    @Value("${bookstore.child.key}")
    private String BOOKSTORE_CHILD_KEY;

    //@PostConstruct
    public void saveCafeData() {
        String urlCafe = UriComponentsBuilder.fromHttpUrl(API_URL_CAFE)
                .queryParam("serviceKey", BOOKSTORE_CAFE_KEY)
                .queryParam("numOfRows", 10) //테스트로 10개만
                .queryParam("pageNo", 1)
                .build()
                .toString();
        Map<String, Object> responseCafe = restTemplate.getForObject(urlCafe, Map.class);
        System.out.println(responseCafe);
        List<Map<String, Object>> dataList = extractDataListFromResponse(responseCafe);

        for (Map<String, Object> item : dataList) {
            Bookstore bookstore = Bookstore.builder()
                    .name((String) item.get("TITLE"))
                    .bookstoreCategory(CAFE)
                    .phone((String) item.get("CONTACT_POINT"))
                    .hours((String) item.get("DESCRIPTION"))
                    .latitude(Double.valueOf(((String) item.get("COORDINATES")).split(" ")[0]))
                    .longitude(Double.valueOf(((String) item.get("COORDINATES")).split(" ")[1]))
                    .address((String) item.get("ADDRESS"))
                    .description((String) item.get("SUB_DESCRIPTION"))
                    .rating(0.0)
                    .build();
            bookstoreRepository.save(bookstore);
        }
    }

    //@PostConstruct
    public void saveIndepData() {
        String urlIndep = UriComponentsBuilder.fromHttpUrl(API_URL_INDEP)
                .queryParam("serviceKey", BOOKSTORE_INDEP_KEY)
                //.queryParam("numOfRows", 10) //테스트로 10개만
                .queryParam("pageNo", 1)
                .build()
                .toString();
        Map<String, Object> responseIndep = restTemplate.getForObject(urlIndep, Map.class);
        System.out.println(responseIndep);
        List<Map<String, Object>> dataList = extractDataListFromResponse(responseIndep);

        for (Map<String, Object> item : dataList) {
            Bookstore bookstore = Bookstore.builder()
                    .name((String) item.get("TITLE"))
                    .bookstoreCategory(INDEP)
                    .phone((String) item.get("CONTACT_POINT"))
                    .hours((String) item.get("DESCRIPTION"))
                    .keyword(((String)item.get("SUBJECT_KEYWORD")).split(",")[1])
                    .latitude(Double.valueOf(((String) item.get("COORDINATES")).split(",")[0]))
                    .longitude(Double.valueOf(((String) item.get("COORDINATES")).split(",")[1]))
                    .address((String) item.get("ADDRESS"))
                    .description((String) item.get("SUB_DESCRIPTION"))
                    .rating(0.0)
                    .build();
            bookstoreRepository.save(bookstore);
        }
    }
    //@PostConstruct
    public void saveChildData() {
        String urlChild = UriComponentsBuilder.fromHttpUrl(API_URL_CHILD)
                .queryParam("serviceKey", BOOKSTORE_CHILD_KEY)
                .queryParam("numOfRows", 10) //테스트로 10개만
                .queryParam("pageNo", 1)
                .build()
                .toString();
        Map<String, Object> responseChild = restTemplate.getForObject(urlChild, Map.class);
        System.out.println(responseChild);
        List<Map<String, Object>> dataList = extractDataListFromResponse(responseChild);

        for (Map<String, Object> item : dataList) {
            Bookstore bookstore = Bookstore.builder()
                    .name((String) item.get("FCLTY_NM"))
                    .bookstoreCategory(CHILD)
                    .phone("0" + (String) item.get("TEL_NO"))
                    .hours("평일개점마감시간" + convertDecimalToTime(item.get("WORKDAY_OPN_BSNS_TIME")) + "~" + convertDecimalToTime(item.get("WORKDAY_CLOS_TIME"))
                            + "토요일개점마감시간" + convertDecimalToTime(item.get("SAT_OPN_BSNS_TIME")) + "~" + convertDecimalToTime(item.get("SAT_CLOS_TIME"))
                            + "일요일개점마감시간" + convertDecimalToTime(item.get("SUN_OPN_BSNS_TIME")) + "~" + convertDecimalToTime(item.get("SUN_CLOS_TIME")))
                    .latitude(Double.valueOf((String)item.get("FCLTY_LA")))
                    .longitude(Double.valueOf((String)item.get("FCLTY_LO")))
                    .address((String) item.get("FCLTY_ROAD_NM_ADDR"))
                    .description((String) item.get("ADIT_DC"))
                    .rating(0.0)
                    .build();
            bookstoreRepository.save(bookstore);
        }
    }
    public static String convertDecimalToTime(Object decimalTime) {
        if (decimalTime == null) return "";
        double time = Double.parseDouble(decimalTime.toString());
        int totalMinutes = (int) Math.round(time * 24 * 60);
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }
    private List<Map<String, Object>> extractDataListFromResponse(Map<String, Object> response) {
        Map<String, Object> responseData = (Map<String, Object>) response.get("response");
        Map<String, Object> body = (Map<String, Object>) responseData.get("body");
        Map<String, Object> items = (Map<String, Object>) body.get("items");
        return (List<Map<String, Object>>) items.get("item");
    }
}
