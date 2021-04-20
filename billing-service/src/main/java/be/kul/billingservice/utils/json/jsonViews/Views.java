package be.kul.billingservice.utils.json.jsonViews;

public class Views {
    public static interface BillView {
        public static interface Basic {
        }
        public static interface Detail extends Basic {
        }
        public static interface Admin extends Detail {
        }
    }

}
