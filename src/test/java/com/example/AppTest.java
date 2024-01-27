package com.example;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.basePath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AppTest {

    String[] types = {"sentence", "paragraph", "title"};
    int[] numbers = {3, 101, 501, 0};
    String[] formats = {"json", "html"};

    @BeforeAll
    public static void setup(){
        baseURI = "https://fish-text.ru";
        basePath = "/get";
    }

    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void testFishTextAPIWithDefaultParams(){
        assertThat(countSentences(getResponseKeyTextDefault()), equalTo(numbers[0]));
    }

    @Test
    public void testFishTextAPIWithParagraftParam(){
        assertThat(countParagraphs(getResponseHtmlParagraths()), equalTo(numbers[0]));
    }

    @Test
    public void testFishTextAPIError(){
        assertThat(getResponseError(), equalTo("11"));
    }

    String getResponseKeyText(){
        return given().
                    param("type", types[0]).
                    param("number", numbers[0]).
                    param("format", formats[0]).
                when().
                    get(baseURI+basePath).
                then().
                    extract().jsonPath().getString("text");
    }

    String getResponseError(){
        return given().
                    param("type", types[0]).
                    param("number", numbers[2]).
                    param("format", formats[0]).
                when().
                    get(baseURI+basePath).
                then().
                    extract().jsonPath().getString("errorCode");
    }

    String getResponseKeyTextDefault(){
        return given().
                when().
                    get(baseURI+basePath).
                then().
                    extract().jsonPath().getString("text");
    }



    List<Object> getResponseHtmlParagraths(){
        String html = given().
        param("type", types[1]).
        param("number", numbers[0]).
        param("format", formats[1]).
        when().
        get(baseURI+basePath).asString()
        ;
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, html);
        List<Object> p = xmlPath.getList("html.body.p");
        return p;
    }
    
    int countSentences(String input){
        Pattern p = Pattern.compile("[.!?]");
        Matcher m = p.matcher(input);
        int countSentences = 0;
        while(m.find()) {
            countSentences++;
        }
        return countSentences;
    }
    
    int countParagraphs(List<Object> input){
        return input.size();
    }
}
