package be.kul.carservice.utils.json.jsonViews;

public class Views {
    public static interface CarView {
        public static interface Basic {
        }
        public static interface Reserved extends Basic {
        }
        public static interface Ride extends Reserved {
        }
        public static interface Full extends Ride {
        }
    }

}
