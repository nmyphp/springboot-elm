package cn.enilu.elm.api.vo;

/**
 * Created  on 2017/12/29 0029.
 *
 * @author zt
 */
public class Constants {
    public static final String SESSION_ID = "login_user_session";

    public enum CityType {
        /**
         * 定位城市
         */
        guess,
        /**
         * 热门城市
         */
        hot,
        /**
         * 所有城市
         */
        group
    }
}
