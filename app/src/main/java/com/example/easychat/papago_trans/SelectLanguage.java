package com.example.easychat.papago_trans;

import com.example.easychat.LoginPhoneNumberActivity;

public class SelectLanguage extends LoginPhoneNumberActivity{
        private String targetLang;
        private String sourceLang;
        private String message;
        private String countryCode;

        public SelectLanguage(String countryCode) {
                this.countryCode = countryCode;
        }

        public SelectLanguage(String sourceLang, String targetLang, String message) {
                this.sourceLang = sourceLang;
                this.targetLang = targetLang;
                this.message = message;
        }

        private static final String DEFAULT_LANG = "en"; // 기본 언어 코드 설정

        public String getSourceLang() {
                // sourceLang가 null이면 기본값을 반환하도록 설정
                return (sourceLang != null) ? sourceLang : DEFAULT_LANG;
        }

        public String getTargetLang() {
                // targetLang가 null이면 기본값을 반환하도록 설정
                return (targetLang != null) ? targetLang : DEFAULT_LANG;
        }
        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public String getCountryCode() {
                return countryCode;
        }

        public void setCountryCode(String countryCode) {
                this.countryCode = countryCode;
        }

//String languageCode = getLanguageCodeFromCountryCode(countryCode); // 국가코드에서 언어코드 가져오기, languageCode = 언어코드

        public String getLanguageCodeFromCountryCode(String countryCode) {
                switch (countryCode) {
                        case "+7":
                                return "ru"; //러시아
                        case "+33":
                                return "fr"; //프랑스
                        case "+34":
                                return "es"; //스페인
                        case "+39":
                                return "it"; //이탈리아
                        case "+49":
                                return "de"; //독일
                        case "+62":
                                return "id"; //인도네시아
                        case "66":
                                return "th"; //태국
                        case "+81":
                                return "ja"; //일본
                        case "+82":
                                return "ko"; //한국
                        case "+84":
                                return "vi"; //베트남
                        case "+86":
                                return "zh-CN"; //중국어 간체
                        default:
                                return "en"; //영국 "44", 미국 "1"
                }
        }
}
