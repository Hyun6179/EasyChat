package com.example.easychat.papago_trans;

import com.example.easychat.LoginPhoneNumberActivity;

public class SelectLanguage extends LoginPhoneNumberActivity{
        private Language sourceLang;
        private Language targetLang;
        private String message;
        private String countryCode;

        public SelectLanguage(String countryCode) {
                this.countryCode = countryCode;
        }

        public SelectLanguage(Language sourceLang, Language targetLang, String message) {
                this.sourceLang = sourceLang;
                this.targetLang = targetLang;
                this.message = message;
        }

        public Language getSourceLang() {
                return sourceLang;
        }

        public void setSourceLang(Language sourceLang) {
                this.sourceLang = sourceLang;
        }

        public Language getTargetLang() {
                return targetLang;
        }

        public void setTargetLang(Language targetLang) {
                this.targetLang = targetLang;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }


        String languageCode = getLanguageCodeFromCountryCode(countryCode); // 국가코드에서 언어코드 가져오기, languageCode = 언어코드

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
