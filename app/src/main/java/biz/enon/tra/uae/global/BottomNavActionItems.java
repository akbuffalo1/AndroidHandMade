package biz.enon.tra.uae.global;

/**
 * Created by ak-buffalo on 12.08.15.
 */
public enum BottomNavActionItems {
    HOME {
        @Override
        public String toString() {
            return "HOME";
        }
    }, INDEX {
        @Override
        public String toString() {
            return "INDEX";
        }
    }, CRM {
        @Override
        public String toString() {
            return "CRM";
        }
    }, SETTINGS {
        @Override
        public String toString() {
            return "Settings";
        }
    }
}
