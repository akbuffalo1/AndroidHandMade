package biz.enon.tra.uae.global;

/**
 * Created by ak-buffalo on 11.08.15.
 */
public enum SmsService {
    REPORT {
        @Override
        public String toString() {
            return "Report SMS Number";
        }
    },
    BLOCK {
        @Override
        public String toString() {
            return "Block SMS Number";
        }
    }
}
